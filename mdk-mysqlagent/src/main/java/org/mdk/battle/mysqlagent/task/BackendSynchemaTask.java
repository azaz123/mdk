package org.mdk.battle.mysqlagent.task;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;

import org.mdk.net.nio.*;
import org.mdk.protocol.mysql.packet.*;
import org.mdk.protocol.mysql.tools.MysqlPrase;



import org.mdk.battle.mysqlagent.*;
import org.mdk.battle.mysqlagent.cmd.CmdStatus;



public class BackendSynchemaTask extends AbstractTask implements NIOHandler<BackEndSession> {
    private String databases;
    
	public void SetDatabase(String databases){
		this.databases = databases;
	}
	@Override
	public void Excute() throws IOException {
		// TODO Auto-generated method stub
		setShareBuffer();
		CmdInfo.Context.BSession.sessionBuffer.reset();
		CmdInfo.Context.BSession.setCurNIOHandler(this);
		//String databases = AllConfigure.INSTANCE.MysqlBeans.get("firstmysql").GetDatabase();
		CmdInfo.Context.BSession.sessionBuffer.reset();
		CommandPacket packet = new CommandPacket();
		packet.packetId = 0;
		packet.command = MySQLPacket.COM_INIT_DB;
		packet.arg = databases.getBytes();
		packet.write(CmdInfo.Context.BSession.sessionBuffer);
		CmdInfo.Context.BSession.sessionBuffer.flip();
		CmdInfo.Context.BSession.sessionBuffer.readIndex = CmdInfo.Context.BSession.sessionBuffer.writeIndex;
		try {
			CmdInfo.Context.BSession.writeToChannel();
		}catch(ClosedChannelException e){
			CmdInfo.Context.BSession.close(false, e.getMessage());
			e.printStackTrace();
			this.finished(false);
			return;
		}  catch (Exception e) {
			e.printStackTrace();
			this.finished(false);
		}
	}
	
	@Override
	public void onSocketRead(BackEndSession session) throws IOException {
		session.sessionBuffer.reset();
		
		try {
    		if (!session.readFromChannel()){
    			return;
    		}
		}catch(ClosedChannelException e){
			session.close(false, e.getMessage());
			return;
		}catch (IOException e) {
			e.printStackTrace();
			this.finished(false);
			return;
		}
		
    	switch (MysqlPrase.resolveMySQLPackage(session.sessionBuffer,
				session.curMSQLPackgInf, false)) {
		case Full:
			if(session.curMSQLPackgInf.pkgType == MySQLPacket.OK_PACKET){
				this.finished(true);
			}else if(session.curMSQLPackgInf.pkgType == MySQLPacket.ERROR_PACKET){
	            this.finished(false);
			}
			break;
		default:
			return;
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
