package org.mdk.base.sharebuffer;
import java.nio.ByteBuffer;

public class ShareBuffer {
    protected final ByteBuffer buf;
    private boolean inReading = false;
    public int writeIndex;
    public int readIndex;
    public int readMark;
    public ShareBuffer(ByteBuffer buf){
    	this.buf = buf;
    }
    
    public boolean isInReading() {
		return inReading;
	}

	public boolean isInWriting() {
		return inReading == false;
	}
    
    public void flip(){
    	inReading = !inReading;
    }
    
    public void reset(){
    	writeIndex = readIndex = readMark = 0;
    	inReading = false;
    	buf.clear();
    }
    
    public void compact(){
    	this.buf.position(readMark);
    	this.buf.limit(writeIndex);
    	this.buf.compact();
    	readIndex -= readMark;
    	readMark = 0;
    	writeIndex = this.buf.position();
    }
    
    public void startWrite(){
    	this.buf.position(writeIndex);
    }
    
    public void completeWrite(int totalcounts){
    	writeIndex +=totalcounts;
    }
    
    public void startRead1(){
    	this.buf.position(readIndex);
    	this.buf.limit(writeIndex);
    }
    
    public void completeRead1(int totalcounts){
    	readIndex +=totalcounts;
    	if(readIndex > writeIndex){
    		readIndex = writeIndex;
    	}
    }
    
    public void startRead2(){
    	this.buf.position(readMark);
    	this.buf.limit(readIndex);
    }
    
    public void completeRead2(int totalcounts){
    	readMark +=totalcounts;
    	if(readMark > readIndex){
    		readMark = readIndex;
    	}
    }
    
}
