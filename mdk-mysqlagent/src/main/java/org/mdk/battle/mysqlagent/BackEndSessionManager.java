package org.mdk.battle.mysqlagent;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collection;

import org.mdk.battle.mysqlagent.Handle.FrontEndDefaultHandle;
import org.mdk.net.nio.NIOHandler;
import org.mdk.net.nio.ReactorContext;
import org.mdk.net.nio.Session;
import org.mdk.net.nio.SessionManager;
import org.mdk.battle.mysqlagent.respool.*;
import org.mdk.battle.mysqlagent.beans.*;

public class BackEndSessionManager implements SessionManager<BackEndSession> {
	public static final BackEndSessionManager INSTANCE = new BackEndSessionManager();
	private ArrayList<BackEndSession> allSessions = new ArrayList<BackEndSession>();
	private Dspool<RunTimeMysqlMetaBeans,BackEndSession> sessionpool  = new Dspool<RunTimeMysqlMetaBeans,BackEndSession>(100);
	@Override
	public BackEndSession createSession(ReactorContext Context, SocketChannel channel, boolean isAcceptedCon)
			throws IOException {
		// TODO Auto-generated method stub
		BackEndSession BeSession = new BackEndSession(Context,channel);
		allSessions.add(BeSession);
		return BeSession;
	}

	@Override
	public Collection<BackEndSession> getAllSessions() {
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

	}
	
	public Dspool<RunTimeMysqlMetaBeans,BackEndSession> getSessionPool() {
		// TODO Auto-generated method stub
        return sessionpool;
	}

}
