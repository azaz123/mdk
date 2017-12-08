package org.mdk.battle.mysqlagent.task;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;

import org.mdk.battle.mysqlagent.BackEndSession;
import org.mdk.net.nio.NIOHandler;
import org.mdk.protocol.mysql.packet.MySQLPacket;
import org.mdk.protocol.mysql.tools.MysqlPrase;


public class PassThroughTask extends AbstractTask implements NIOHandler<BackEndSession> {

	@Override
	public void Excute() throws IOException {
		// TODO Auto-generated method stub
		Context.BSession.setCurNIOHandler(this);
		setShareBuffer();
		Context.BSession.sessionBuffer.flip();

		Context.BSession.sessionBuffer.readIndex = Context.BSession.sessionBuffer.writeIndex;
		try {
			Context.BSession.writeToChannel();
		} catch (IOException e) {
			Context.BSession.close(false, e.getMessage());
		}
	}
	
	@Override
	public void onSocketRead(BackEndSession session) throws IOException {
		//passthrough is forever success
		session.readFromChannel();
		finished(true);
	}

	@Override
	protected void finished(boolean success) throws IOException {
		// TODO Auto-generated method stub
		revertShareBuffer();
		if(super.isLastTask){
		    super.currentcmd.OnCmdResponse(super.Context,success);
		}else{
		    if(success){
		        super.nextTask.Excute();
		    }else{
		        super.currentcmd.OnCmdResponse(super.Context,success);
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
