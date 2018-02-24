package org.mdk.battle.mysqlagent.task;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;

import org.mdk.battle.mysqlagent.BackEndSession;
import org.mdk.battle.mysqlagent.attr.CmdAttr;
import org.mdk.battle.mysqlagent.cmd.CmdStatus;
import org.mdk.battle.mysqlagent.protocol.AckPacketPraser;
import org.mdk.net.nio.NIOHandler;
import org.mdk.protocol.mysql.packet.MySQLPacket;
import org.mdk.protocol.mysql.tools.MysqlPrase;


public class PassThroughTask extends AbstractTask implements NIOHandler<BackEndSession> {

	@Override
	public void Excute() throws IOException {
		// TODO Auto-generated method stub
		CmdInfo.Context.BSession.setCurNIOHandler(this);
		setShareBuffer();
		CmdInfo.Context.BSession.sessionBuffer.flip();

		CmdInfo.Context.BSession.sessionBuffer.readIndex = CmdInfo.Context.BSession.sessionBuffer.writeIndex;
		try {
			CmdInfo.Context.BSession.writeToChannel();
		} catch (IOException e) {
			CmdInfo.Context.BSession.close(false, e.getMessage());
		}
	}
	
	@Override
	public void onSocketRead(BackEndSession session) throws IOException {
		//passthrough is forever success
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
		//revertShareBuffer();
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
