package org.mdk.net.nio;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Collection;


public interface SessionManager<T extends Session> {


	public T createSession(ReactorContext Context, SocketChannel channel,
			boolean isAcceptedCon) throws IOException;

	public Collection<T> getAllSessions();
	public NIOHandler getDefaultSessionHandler();
	public void removeSession(Session session);

}
