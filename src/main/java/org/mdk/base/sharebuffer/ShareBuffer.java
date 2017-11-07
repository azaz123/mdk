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
    
    public byte readbyte(){
    	byte rVal = getbyte(readIndex);
    	readIndex++;
    	return rVal;
    }
    
    public ShareBuffer writebyte(byte val){
    	putbyte(writeIndex,val);
    	writeIndex++;
    	return this;
    }
    
    private byte getbyte(int index){
    	this.buf.position(index);
    	return this.buf.get();
    }
    
    private ShareBuffer putbyte(int index,byte val){
    	this.buf.position(index);
    	this.buf.put(val);
    	return this;
    }
}
