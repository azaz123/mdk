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
    private int currentReactorIndex = 0;
    private int maxcurrentReactorIndex;
    
    public NIOAcceptor(ReactorContext Context,int maxreactornum) throws IOException {
    	bindContext(Context);
		this.Context = Context;
		maxcurrentReactorIndex = maxreactornum;
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
		// get reactor object
		
		Reactor rReactor = nioRuntime.INSTANCE.reactorThreads[currentReactorIndex];
		currentReactorIndex++;
		if(currentReactorIndex == maxcurrentReactorIndex){
			currentReactorIndex = 0;
		}
		
		return rReactor;
	}
	
	public Selector getSelector() {
		return this.Context.selector;
	}
	
	protected void processAcceptKey(SelectionKey curKey) throws IOException {
		System.out.println("NIOACCEPTOR processAcceptKey");
		ServerSocketChannel serverSocket = (ServerSocketChannel) curKey.channel();
		// recv accept
		final SocketChannel socketChannel = serverSocket.accept();	
		socketChannel.configureBlocking(false);
		accept(socketChannel);
	}
	
	@SuppressWarnings("unchecked")
	protected void processReadKey(SelectionKey curKey) throws IOException {
		
		Session session = (Session) curKey.attachment();
		session.getCurNIOHandler().onSocketRead(session);
	}

	@SuppressWarnings("unchecked")
	protected void processWriteKey(SelectionKey curKey) throws IOException {
		
		Session session = (Session) curKey.attachment();
		session.getCurNIOHandler().onSocketWrite(session);
	}
}
