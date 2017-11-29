package org.mdk.net.nio;



public class nioRuntime {
	public static final nioRuntime INSTANCE = new nioRuntime();
	private Reactor[] reactorThreads;
	private int ReactorNum = 0;
	private ReactorContext[] Rc;
	private NIOAcceptor acceptor;
	public void setReactorInfo(ReactorContext[] Rc,int Num){
		this.Rc = Rc;
		ReactorNum = Num;
	}
	
	public void setAcceptor(NIOAcceptor acceptor){
		this.acceptor = acceptor;
	}
	
	public void start(){
		acceptor.start();
		for(int i=0;i < ReactorNum;i++){
			reactorThreads[i] = new Reactor();
			reactorThreads[i].bindContext(Rc[i]);
			reactorThreads[i].start();
		}
	}
}
