package org.mdk.battle.mysqlagent.Handle;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.util.ArrayList;

import org.mdk.net.nio.NIOHandler;
import org.mdk.protocol.mysql.tools.bufferHelper;
import org.mdk.battle.mysqlagent.BackEndSession;
import org.mdk.battle.mysqlagent.BackEndSessionManager;
import org.mdk.battle.mysqlagent.FrontEndSession;
import org.mdk.battle.mysqlagent.cmd.DefaultSingleCmd;
import org.mdk.battle.mysqlagent.cmd.PassThroughCmd;
import org.mdk.battle.mysqlagent.protocol.*;
import org.mdk.battle.mysqlagent.task.*;
import org.mdk.protocol.mysql.*;
import org.mdk.protocol.mysql.packet.*;
import org.mdk.protocol.mysql.tools.*;
import org.mdk.battle.mysqlagent.*;


public class FrontEndDefaultHandle implements NIOHandler<FrontEndSession> {
	public static final FrontEndDefaultHandle INSTANCE = new FrontEndDefaultHandle();
	private bufferHelper buffertool = new bufferHelper();
	public void onSocketRead(final FrontEndSession session) throws IOException {
		System.out.println("FrontEndDefaultHandle onSocketRead ");
	    session.readFromChannel();
	    //shutdown frontend io event selector,because we need wait feedback packet
	    session.clearReadWriteOpts();
	    switch(MysqlPrase.resolveMySQLPackage(session.sessionBuffer, session.curMSQLPackgInf,false)){
		 case Full:
			break;
		 case LongHalfPacket:
		 case ShortHalfPacket:
			 session.sessionBuffer.readMark = session.sessionBuffer.readIndex;
			return;
		}
	    ArrayList<BackEndSession> allbsession = (ArrayList<BackEndSession>)BackEndSessionManager.INSTANCE.getAllSessions();
	    BackEndSession bsession = allbsession.get(0);
	    AbstractTask HeadTask = MysqlTaskChainManager.INSTANCE.CreateTaskChain(PassThroughCmd.INSTANCE, session,bsession , 0);
	    HeadTask.Excute();
	    return;
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
		}
}
