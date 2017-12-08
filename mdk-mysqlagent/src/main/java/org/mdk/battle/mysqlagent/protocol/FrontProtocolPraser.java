package org.mdk.battle.mysqlagent.protocol;

import org.mdk.net.nio.netBuffer;
import org.mdk.protocol.mysql.tools.bufferHelper;

public class FrontProtocolPraser {
	public static final FrontProtocolPraser INSTANCE = new FrontProtocolPraser();
	private bufferHelper buffertool = new bufferHelper();
    public  FrontEndPacketType Praser(netBuffer buf){
    	buffertool.setBuffer(buf);
    	int nStart = buf.readIndex;
    	int nEnd = buf.writeIndex;
    	for(int i = nStart ;i<=nEnd;i++){
    		if(buffertool.getByte(i) == ';'){
    			return FrontEndPacketType.Full;
    		}
    	}
    	return FrontEndPacketType.ShortHalfPacket;
    }
}
