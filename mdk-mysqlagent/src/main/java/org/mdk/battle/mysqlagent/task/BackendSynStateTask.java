package org.mdk.battle.mysqlagent.task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;

import org.mdk.net.nio.*;
import org.mdk.protocol.mysql.packet.*;
import org.mdk.protocol.mysql.tools.MysqlPrase;
import org.mdk.battle.mysqlagent.*;
import org.mdk.battle.mysqlagent.cmd.CmdStatus;



public class BackendSynStateTask extends AbstractTask implements NIOHandler<BackEndSession> {
    private int syncCmdNum = 0;
    
    @Override
    public void onSocketRead(BackEndSession session) throws IOException {
        session.sessionBuffer.reset();        
		try {
    		if (!session.readFromChannel()){
    			return;
    		}
		}catch(ClosedChannelException e){
			session.close(false, e.getMessage());
			this.finished(false);
			return;
		}catch (IOException e) {
			e.printStackTrace();
			this.finished(false);
			return;
		}
        
        boolean isAllOK = true;
        while (syncCmdNum >0) {
        	switch (MysqlPrase.resolveMySQLPackage(session.sessionBuffer,
    				session.curMSQLPackgInf, true)) {
			case Full:
				if(session.curMSQLPackgInf.pkgType == MySQLPacket.ERROR_PACKET){
					isAllOK = false;
					syncCmdNum = 0;
				}
				break;
			default:
				return;
        	}
        	syncCmdNum --;
        }

        if (isAllOK) {
            finished(true);
        } else {
            finished(false);
        }
    }
    


	    
	@Override
	public void Excute() throws IOException {
		// TODO Auto-generated method stub
		setShareBuffer();
		CmdInfo.Context.BSession.setCurNIOHandler(this);
		netBuffer netBuf = CmdInfo.Context.BSession.sessionBuffer;
        netBuf.reset();
        QueryPacket queryPacket = new QueryPacket();
        queryPacket.packetId = 0;
        
        queryPacket.sql = "";
        queryPacket.sql += "SET names " + "utf8" + ";";
        syncCmdNum++;
        if (syncCmdNum > 0) {
            queryPacket.write(netBuf);

            netBuf.flip();
            netBuf.readIndex = netBuf.writeIndex;
            try {
            	CmdInfo.Context.BSession.writeToChannel();
			}catch(ClosedChannelException e){
				e.printStackTrace();
				CmdInfo.Context.BSession.close(false, e.getMessage());
				this.finished(false);
				return;
			} catch (Exception e) {
				e.printStackTrace();
				this.finished(false);
				
			}
        }else{
        	finished(true);
        }
	}

	@Override
	protected void finished(boolean success) throws IOException {
		// TODO Auto-generated method stub
		revertShareBuffer();
		if(super.isLastTask){
        	super.CmdInfo.currentcmd.OnCmdResponse(super.CmdInfo,success);
        }else{
        	if(success){
        		super.nextTask.Excute();
        	}else{
        		super.CmdInfo.currentcmd.OnCmdResponse(super.CmdInfo,success);
        	}
        	
        }
	}
	
	public void onConnect(SelectionKey theKey, BackEndSession userSession, boolean success, String msg)
			throws IOException {

	}
	
	@Override
	public void onSocketClosed(BackEndSession userSession, boolean normal) {
	}

	@Override
	public void onSocketWrite(BackEndSession session) throws IOException {
		session.writeToChannel();

	}

	@Override
	public void onWriteFinished(BackEndSession s) throws IOException {
		if(ifUseShareBuffer()){
			s.sessionBuffer.flip();
		}else{
			s.sessionBuffer.reset();
		}
		s.change2ReadOpts();

	}

}
