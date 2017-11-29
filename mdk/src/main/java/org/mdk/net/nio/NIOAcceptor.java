package org.mdk.net.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;








public class NIOAcceptor extends Reactor {
    public ReactorContext Context;
    private ServerSocketChannel ServerChannel;
    
    public NIOAcceptor(ReactorContext Context) throws IOException {
    	bindContext(Context);
		this.Context = Context;
	}
    
    private void openServerChannel(Selector selector, String bindIp, int bindPort)
			throws IOException {
		final ServerSocketChannel serverChannel = ServerSocketChannel.open();
		final InetSocketAddress isa = new InetSocketAddress(bindIp, bindPort);
		serverChannel.bind(isa);
		serverChannel.configureBlocking(false);
		serverChannel.register(selector, SelectionKey.OP_ACCEPT);
	}
    
    public void startServerChannel(String ip, int port)throws IOException {
    	
		if (ServerChannel != null && ServerChannel.isOpen())
			return;
		openServerChannel(Context.selector, ip, port);
	}
    
    public void stopServerChannel(boolean clusterServer) {
		if (ServerChannel != null && ServerChannel.isOpen()) {
			try {
				ServerChannel.close();
			} catch (IOException e) {

			}
		}
	}
    
    private void accept(SocketChannel socketChannel) throws IOException {
        Reactor r = getReactor();
        r.acceptNewSocketChannel(socketChannel);
	}

	private Reactor getReactor(){
		// 获取一个reactor对象
		return new Reactor();
	}
	
	public Selector getSelector() {
		return this.Context.selector;
	}
	
	protected void processAcceptKey(SelectionKey curKey) throws IOException {
		ServerSocketChannel serverSocket = (ServerSocketChannel) curKey.channel();
		// 接收通道，设置为非阻塞模式
		final SocketChannel socketChannel = serverSocket.accept();		
		accept(socketChannel);
	}
	
	@SuppressWarnings("unchecked")
	protected void processReadKey(SelectionKey curKey) throws IOException {
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
}
