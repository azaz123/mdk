package org.mdk.net.nio;

import java.util.concurrent.atomic.AtomicInteger;

public class nioRuntime {
	public static final nioRuntime INSTANCE = new nioRuntime();
	private AtomicInteger sessionId = new AtomicInteger(1);
	public Reactor[] reactorThreads;
	private int ReactorNum = 0;
	public ReactorContext[] Rc;
	private NIOAcceptor acceptor;
	public void setReactorInfo(ReactorContext[] Rc,int Num){
		this.Rc = Rc;
		ReactorNum = Num;
	}
	
	public void setAcceptor(NIOAcceptor acceptor){
		this.acceptor = acceptor;
	}
	
	public void start(){
		reactorThreads = new Reactor[ReactorNum];
		acceptor.start();
		
		for(int i=0;i < ReactorNum;i++){
			reactorThreads[i] = new Reactor();
			reactorThreads[i].bindContext(Rc[i]);
			reactorThreads[i].start();
		}
		
	}
	
	public int genSessionId() {
		int val = sessionId.incrementAndGet();
		if (val < 0) {
			synchronized (sessionId) {
				if (sessionId.get() < 0) {
					int newValue = 1;
					sessionId.set(newValue);
					return newValue;
				} else {
					return sessionId.incrementAndGet();
				}
			}
		}
		return val;
	}
	
}
