package org.mdk.net.nio;

import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;
import java.io.IOException; 

import org.mdk.base.sharebuffer.ShareBuffer;




public class netBuffer extends ShareBuffer {
	public netBuffer(ByteBuffer buf){
    	super(buf);
    }
	
	public boolean writeFinished() {
		return readIndex==readMark;
	}
	
	public int readChannel(SocketChannel channel){
		int readed = 0;
		try{
			if ( writeIndex > (buf.capacity() * 1 / 3) ) 
				compact();
			startWrite();
			readed = channel.read(buf);
			if (readed == -1) {
				throw new ClosedChannelException();
			} else if (readed == 0) {

			}
			completeWrite(readed);
		}catch(IOException e){
			e.printStackTrace();
		}
		return readed;
	}
	
	public int writeChannel(SocketChannel channel){
		int writed = 0;
		try{
			startRead2();
			writed = channel.write(buf);
			completeRead2(writed);
			if (!buf.hasRemaining()) {
				if (readIndex > buf.capacity() * 2 / 3) {
					compact();
				} else {
					buf.limit(buf.capacity());
				}
			} else {
				
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		return writed;
	}
}
