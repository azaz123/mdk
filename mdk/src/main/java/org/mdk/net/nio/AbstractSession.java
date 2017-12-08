package org.mdk.net.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import org.mdk.net.nio.NIOHandler;

import org.mdk.net.nio.BufferPool;
import org.mdk.net.nio.netBuffer;
import org.mdk.protocol.mysql.tools.bufferHelper;



import org.mdk.net.nio.SessionManager;

public class AbstractSession implements Session {
	public bufferHelper buftool = new bufferHelper();
	public netBuffer sessionBuffer;
	private NIOHandler nioHandler;
	private int sessionId;
	public String addr;
	public SocketChannel channel;
	public SelectionKey channelKey;
	// Session是否关闭
	private boolean closed;
	public  ReactorContext Context;
	public byte[] seed;
	
	public AbstractSession(ReactorContext Context, SocketChannel channel) throws IOException {
		this(Context, channel, SelectionKey.OP_READ);
	}
	
	public AbstractSession(ReactorContext Context, SocketChannel channel, int socketOpt)
			throws IOException {
		
		this.Context = Context;
		this.channel = channel;
		InetSocketAddress clientAddr = (InetSocketAddress) channel.getRemoteAddress();
		this.addr = clientAddr.getHostString() + ":" + clientAddr.getPort();
		System.out.println("AbstractSession");
		SelectionKey socketKey = channel.register(Context.selector, socketOpt, this);
		System.out.println("AbstractSession end");
		this.channelKey = socketKey;
		this.sessionBuffer = new netBuffer(Context.bufPool.allocByteBuffer());
		this.sessionId = nioRuntime.INSTANCE.genSessionId();
		
	}
	
	public int getSessionId() {
		return sessionId;
	}
	
	public boolean readFromChannel() throws IOException {
		
		int readed = sessionBuffer.readChannel(channel);
		return readed > 0;
	}
	
	public int writeToChannel() throws IOException {
		int writed = sessionBuffer.writeChannel(channel);
		checkWriteFinished();
		return writed;
	}
	
	public netBuffer allocNewProxyBuffer() {
		return new netBuffer(this.Context.bufPool.allocByteBuffer());
	}
	
	public void recycleAllocedBuffer(netBuffer curFrontBuffer) {
		if (curFrontBuffer != null) {
			this.Context.bufPool.recycleBuf(curFrontBuffer.getBuffer());
		}
	}
	
	protected void checkWriteFinished() throws IOException {
		if (!this.sessionBuffer.writeFinished()) {
			this.change2WriteOpts();
		} else {
			writeFinished();
		}
	}
	
	public void change2ReadOpts() {
		int intesOpts = this.channelKey.interestOps();
		
	    channelKey.interestOps(SelectionKey.OP_READ);
	}

	public void clearReadWriteOpts() {
		this.channelKey.interestOps(0);
	}

	public void change2WriteOpts() {
		int intesOpts = this.channelKey.interestOps();
		channelKey.interestOps(SelectionKey.OP_WRITE);
	}
	
	public void writeFinished() throws IOException {
		this.getCurNIOHandler().onWriteFinished(this);

	}
	
	public String sessionInfo() {
		return " [" + this.addr + ']';
	}
	
	public boolean isChannelOpen() {
		return channel != null && channel.isConnected();
	}
	
	public netBuffer getNetBuffer() {
		return sessionBuffer;
	}
	
	protected void closeSocket(SocketChannel channel, boolean normal, String msg) {
		if (channel == null) {
			return;
		}
		try {
			channel.close();
		} catch (IOException e) {
		}
	}
	
	@Override
	public SocketChannel channel() {
		// TODO Auto-generated method stub
		return this.channel;
	}

	@Override
	public boolean isClosed() {
		// TODO Auto-generated method stub
		return closed;
	}

	@Override
	public <T extends Session> SessionManager<T> getMySessionManager() {
		// TODO Auto-generated method stub
		return (SessionManager<T>) Context.sessionMan;
	}

	@Override
	public NIOHandler getCurNIOHandler() {
		return nioHandler;
	}

	public void setCurNIOHandler(NIOHandler curNioHandler) {
		this.nioHandler = curNioHandler;
	}

	@Override
	public void close(boolean normal, String hint) {
		// TODO Auto-generated method stub
		if (!this.isClosed()) {
			this.closed = true;
			closeSocket(channel, normal, hint);
			this.Context.bufPool.recycleBuf(sessionBuffer.getBuffer());
            this.Context.allSessions.remove(this);
		} else {

		}
	}

}
