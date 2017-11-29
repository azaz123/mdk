package org.mdk.net.nio;

import java.io.IOException;
import java.net.ConnectException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;



public class Reactor extends Thread {
    public ReactorContext Context;
    ConcurrentLinkedQueue<Runnable> pendingJobsTmp;
    

    public void bindContext(ReactorContext Context){
    	this.Context = Context;
    	pendingJobsTmp = Context.pendingJobs;
    }
    
    public void acceptNewSocketChannel(final SocketChannel socketChannel) throws IOException {
    	pendingJobsTmp.offer(() -> {
			try {
				Session session = Context.sessionMan.createSession(Context,socketChannel, true);
				Context.allSessions.add(session);
			} catch (Exception e) {

			}
		});
	}
    
    public void addNIOJob(Runnable job) {
    	pendingJobsTmp.offer(job);
	}
    
    private void processNIOJob() {
		Runnable nioJob = null;
		while ((nioJob = pendingJobsTmp.poll()) != null) {
			try {
				nioJob.run();
			} catch (Exception e) {
				
			}
		}

	}
    
    protected void processAcceptKey(SelectionKey curKey) throws IOException {

	}
    
    @SuppressWarnings("unchecked")
	protected void processConnectKey(SelectionKey curKey) throws IOException {
		Session session = (Session) curKey.attachment();
		try {
			if (((SocketChannel) curKey.channel()).finishConnect()) {
				session.getCurNIOHandler().onConnect(curKey, session, true, null);
			}

		} catch (ConnectException ex) {
			session.getCurNIOHandler().onConnect(curKey, session, false, ex.getMessage());
		}
	}
    
    @SuppressWarnings("unchecked")
	protected void processReadKey( SelectionKey curKey) throws IOException {
		// only from cluster server socket
		Session session = (Session) curKey.attachment();
		session.getCurNIOHandler().onSocketRead(session);
	}

	@SuppressWarnings("unchecked")
	protected void processWriteKey(SelectionKey curKey) throws IOException {
		// only from cluster server socket
		Session session = (Session) curKey.attachment();
		session.getCurNIOHandler().onSocketWrite(session);
	}
	
	public void run() {
		long ioTimes = 0;
		while (true) {
			try {
				Context.selector.select(Context.SELECTOR_TIMEOUT);
				final Set<SelectionKey> keys = Context.selector.selectedKeys();
				// logger.info("handler keys ,total " + selected);
				if (keys.isEmpty()) {
					if (!pendingJobsTmp.isEmpty()) {
						ioTimes = 0;
						this.processNIOJob();
					}
					continue;
				} else if ((ioTimes > 5) & !pendingJobsTmp.isEmpty()) {
					ioTimes = 0;
					this.processNIOJob();
				}
				ioTimes++;
				for (final SelectionKey key : keys) {
					Session curSession = (Session) key.attachment();
					try {
						int readdyOps = key.readyOps();
						// 如果当前收到连接请求
						if ((readdyOps & SelectionKey.OP_ACCEPT) != 0) {
							processAcceptKey(key);
						}
						// 如果当前连接事件
						else if ((readdyOps & SelectionKey.OP_CONNECT) != 0) {
							this.processConnectKey(key);
						} else if ((readdyOps & SelectionKey.OP_READ) != 0) {
							this.processReadKey(key);

						} else if ((readdyOps & SelectionKey.OP_WRITE) != 0) {
							this.processWriteKey(key);
						}
					} catch (Exception e) {
						key.cancel();
						if (curSession != null) {
							curSession.close(false, "Socket IO err:" + e);
							Context.allSessions.remove(curSession);
							curSession = null;
						}
					}
				}
				keys.clear();
			} catch (IOException e) {

			}

		}

	}
    
}
