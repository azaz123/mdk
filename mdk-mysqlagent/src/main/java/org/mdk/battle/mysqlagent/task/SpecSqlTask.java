package org.mdk.battle.mysqlagent.task;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;

import org.mdk.net.nio.NIOHandler;
import org.mdk.protocol.mysql.packet.QueryPacket;
import org.mdk.battle.mysqlagent.*;
import org.mdk.battle.mysqlagent.attr.CmdAttr;
import org.mdk.battle.mysqlagent.cmd.CmdStatus;
import org.mdk.battle.mysqlagent.protocol.AckPacketPraser;

public class SpecSqlTask extends AbstractTask implements NIOHandler<BackEndSession> {
    private String sql;
    
    public void SetSql(String sql){
    	this.sql = sql;
    }
    
	@Override
	public void Excute() throws IOException {
		// TODO Auto-generated method stub
		setShareBuffer();
		CmdInfo.Context.BSession.sessionBuffer.reset();
		CmdInfo.Context.BSession.setCurNIOHandler(this);
		QueryPacket packet = new QueryPacket();
		packet.packetId = 0;
		packet.sql = sql;
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
		AckPacketPraser.Excute(CmdInfo);
	    if(!CmdInfo.CmdAttrMap.containsKey(CmdAttr.CMD_ATTR_RESULTSET_ACCEPT_OVER.getKey())){
		    //last packet
			revertShareBuffer();
		}
	    if(CmdInfo.CmdAttrMap.containsKey(CmdAttr.CMD_ATTR_RUN_STATUS.getKey())) {
	    	if(CmdInfo.CmdAttrMap.get(CmdAttr.CMD_ATTR_RUN_STATUS.getKey()).equals(CmdStatus.OVER_NORMAL)) {
	    		finished(true);
	    	}else {
	    		finished(false);
	    	}
	    }else {
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
