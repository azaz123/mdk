package org.mdk.net.nio;

import java.io.IOException;
import java.nio.channels.Selector;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.mdk.net.nio.Session;

import org.mdk.net.nio.BufferPool;
import org.mdk.net.nio.SessionManager;



public class ReactorContext<T extends Session> {
	public final static long SELECTOR_TIMEOUT = 100;
	protected final SessionManager<T> sessionMan;
	public final Selector selector;
	public final BufferPool bufPool;
	public ConcurrentLinkedQueue<Runnable> pendingJobs = new ConcurrentLinkedQueue<Runnable>();
	public LinkedList<T> allSessions = new LinkedList<T>();
	
	public ReactorContext(int bufPoolSize,SessionManager man) throws IOException {
		this.bufPool = new BufferPool(bufPoolSize);
		this.selector = Selector.open();
		sessionMan = man;
	}
	
}
