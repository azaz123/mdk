
package org.mdk.protocol.mysql.packet;



import org.mdk.base.sharebuffer.ShareBuffer;

/**
 * From server to client during initial handshake.
 * <p>
 * <pre>
 * Bytes                        Name
 * -----                        ----
 * 1                            protocol_version
 * n (Null-Terminated String)   server_version
 * 4                            thread_id
 * 8                            scramble_buff
 * 1                            (filler) always 0x00
 * 2                            server_capabilities
 * 1                            server_language
 * 2                            server_status
 * 13                           (filler) always 0x00 ...
 * 13                           rest of scramble_buff (4.1)
 *
 * &#64;see http://forge.mysql.com/wiki/MySQL_Internals_ClientServer_Protocol#Handshake_Initialization_Packet
 * </pre>
 *
 * @author hrz
 */
public class HandshakePacket extends MySQLPacket {
    private static final byte[] FILLER_13 = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    public byte protocolVersion;
    public byte[] serverVersion;
    public long threadId;
    public byte[] seed;
    public int serverCapabilities;
    public byte serverCharsetIndex;
    public int serverStatus;
    public byte[] restOfScrambleBuff;


    public void read(ShareBuffer buffer) {
    	buftool.setBuffer(buffer);
        packetLength = (int) buftool.readFixInt(3);
        packetId = buftool.readByte();
        protocolVersion = buftool.readByte();
        serverVersion = buftool.readNULString().getBytes();
        threadId = buftool.readFixInt(4);
        seed = buftool.readNULString().getBytes();
        serverCapabilities = (int) buftool.readFixInt(2);
        serverCharsetIndex = buftool.readByte();
        serverStatus = (int) buftool.readFixInt(2);
        buftool.skip(13);
        restOfScrambleBuff = buftool.readNULString().getBytes();
    }

    public void write(ShareBuffer buffer) {
    	buftool.setBuffer(buffer);
    	int pkgSize=calcPacketSize();
    	//进行将握手包，写入至ProxyBuffer中,将write的opt指针进行相应用修改
    	
    	buftool.writeFixInt(3, pkgSize);
    	buftool.writeByte(packetId);
    	buftool.writeByte(protocolVersion);
    	buftool.writeNULString(new String(serverVersion));
    	buftool.writeFixInt(4, threadId);
    	buftool.writeNULString(new String(seed));
    	buftool.writeFixInt(2, serverCapabilities);
    	buftool.writeByte(serverCharsetIndex);
    	buftool.writeFixInt(2, serverStatus);
    	buftool.writeBytes(FILLER_13);
    	buftool.writeNULString(new String(restOfScrambleBuff));
    }

    @Override
    public int calcPacketSize() {
        int size = 1;
        size += serverVersion.length;// n
        size += 5;// 1+4
        size += seed.length;// 8
        size += 19;// 1+2+1+2+13
        size += restOfScrambleBuff.length;// 12
        size += 1;// 1
        return size;
    }

    @Override
    protected String getPacketInfo() {
        return "MySQL Handshake Packet";
    }

}
