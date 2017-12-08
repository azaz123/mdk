package org.mdk.battle.mysqlagent;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collection;

import org.mdk.battle.mysqlagent.Handle.*;
import org.mdk.battle.mysqlagent.cmd.DefaultSingleCmd;
import org.mdk.net.nio.NIOHandler;
import org.mdk.net.nio.ReactorContext;
import org.mdk.net.nio.Session;
import org.mdk.net.nio.SessionManager;
import org.mdk.battle.mysqlagent.task.*;


public class FrontEndSessionMaganer implements SessionManager<FrontEndSession> {
	public static final FrontEndSessionMaganer INSTANCE = new FrontEndSessionMaganer();
	private ArrayList<FrontEndSession> allSessions = new ArrayList<FrontEndSession>();
	@Override
	public FrontEndSession createSession(ReactorContext Context, SocketChannel channel, boolean isAcceptedCon)
			throws IOException {
		// TODO Auto-generated method stub
		System.out.println("FrontEndSessionMaganer createSession");
		FrontEndSession fSession = new FrontEndSession(Context,channel);
		AbstractTask AuthTask = MysqlTaskChainManager.INSTANCE.CreateTaskChain(DefaultSingleCmd.INSTANCE, fSession,null , 2);
		AuthTask.Excute();
		allSessions.add(fSession);
		return null;
	}

	@Override
	public Collection<FrontEndSession> getAllSessions() {
		// TODO Auto-generated method stub
		return allSessions;
	}

	@Override
	public NIOHandler getDefaultSessionHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeSession(Session session) {
		// TODO Auto-generated method stub
		allSessions.remove(session);
	}

}
