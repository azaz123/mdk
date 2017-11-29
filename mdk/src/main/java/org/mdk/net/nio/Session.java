package org.mdk.net.nio;

import java.nio.channels.SocketChannel;



public interface Session {

	public SocketChannel channel();

	boolean isClosed();

	public <T extends Session> SessionManager<T> getMySessionManager();

	public NIOHandler getCurNIOHandler();

	void close(boolean normal,String hint);

}

