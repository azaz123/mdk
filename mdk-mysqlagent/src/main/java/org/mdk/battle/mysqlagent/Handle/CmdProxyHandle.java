package org.mdk.battle.mysqlagent.Handle;

import java.io.IOException;
import java.nio.channels.SelectionKey;

import org.mdk.battle.mysqlagent.*;
import org.mdk.battle.mysqlagent.attr.CmdAttr;
import org.mdk.net.nio.NIOHandler;
import org.mdk.protocol.mysql.tools.MysqlPrase;
import org.mdk.protocol.mysql.tools.bufferHelper;

public class CmdProxyHandle implements NIOHandler<FrontEndSession> {
	public static final CmdProxyHandle INSTANCE = new CmdProxyHandle();

	public void onSocketRead(final FrontEndSession session) throws IOException {

	    
	    
	}

	public void onSocketClosed(FrontEndSession session, boolean normal) {

	}

	@Override
	public void onSocketWrite(FrontEndSession session) throws IOException {
		session.writeToChannel();

	}

	@Override
	public void onConnect(SelectionKey curKey, FrontEndSession session, boolean success, String msg)
			throws IOException {
		throw new java.lang.RuntimeException("not implemented ");
	}

	@Override
	public void onWriteFinished(FrontEndSession session) throws IOException {
                 //nothing to do,because this handle don't do any write action
		if(!session.currentCmdInfo.CmdAttrMap.containsKey(CmdAttr.CMD_ATTR_RESULTSET_ACCEPT_OVER.getKey())){
			//last packet
			session.setCurNIOHandler(FrontEndDefaultHandle.INSTANCE);
			session.sessionBuffer.flip();
			session.change2ReadOpts();
		}else{
			session.sessionBuffer.flip();
			session.currentCmdInfo.Context.BSession.change2ReadOpts();
		}
		
	}
}
