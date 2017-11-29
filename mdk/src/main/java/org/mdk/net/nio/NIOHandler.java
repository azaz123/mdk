package org.mdk.net.nio;

import java.io.IOException;
import java.nio.channels.SelectionKey;


public interface NIOHandler<T extends Session> {

	void onConnect(SelectionKey curKey, T session, boolean success, String msg) throws IOException;

	void onSocketRead(T session) throws IOException;

	void onSocketWrite(T session) throws IOException;

	public void onWriteFinished(T s) throws IOException;

	void onSocketClosed(T session,boolean normal);
}

