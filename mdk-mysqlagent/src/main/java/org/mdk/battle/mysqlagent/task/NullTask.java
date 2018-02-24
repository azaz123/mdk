package org.mdk.battle.mysqlagent.task;

import java.io.IOException;
import java.nio.channels.SelectionKey;

import org.mdk.net.nio.NIOHandler;
import org.mdk.battle.mysqlagent.BackEndSession;
import org.mdk.battle.mysqlagent.attr.CmdAttr;
import org.mdk.battle.mysqlagent.protocol.AckPacketPraser;

public class NullTask extends AbstractTask implements NIOHandler<BackEndSession> {

	@Override
	public void Excute() throws IOException {
		// TODO Auto-generated method stub
		setShareBuffer();
		super.CmdInfo.currentcmd.OnCmdResponse(super.CmdInfo,true);
	}
    
	@Override
	public void onSocketRead(BackEndSession session) throws IOException {
		
	}
	
	@Override
	protected void finished(boolean success) throws IOException {
		// TODO Auto-generated method stub

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
