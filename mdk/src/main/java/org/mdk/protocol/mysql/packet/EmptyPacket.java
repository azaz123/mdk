
package org.mdk.protocol.mysql.packet;



import org.mdk.base.sharebuffer.ShareBuffer;



/**
 * @author mycat暂时只发现在load data infile时用到
 */
public class EmptyPacket extends MySQLPacket {
    public static final byte[] EMPTY = new byte[] { 0, 0, 0,3 };

    @Override
    public int calcPacketSize() {
        return 0;
    }

    @Override
    protected String getPacketInfo() {
        return "MySQL Empty Packet";
    }

	@Override
	public void write(ShareBuffer buffer) {
		throw new java.lang.RuntimeException("not implmented ,leader want you ");
		
	}

}