
package org.mdk.protocol.mysql.packet;



import org.mdk.base.sharebuffer.ShareBuffer;



/**
 * From server to client in response to command, if error.
 * 
 * <pre>
 * Bytes                       Name
 * -----                       ----
 * 1                           field_count, always = 0xff
 * 2                           errno
 * 1                           (sqlstate marker), always '#'
 * 5                           sqlstate (5 characters)
 * n                           message
 * 
 * &#64;see http://forge.mysql.com/wiki/MySQL_Internals_ClientServer_Protocol#Error_Packet
 * </pre>
 * 
 * @author mycat
 */
public class ErrorPacket extends MySQLPacket {
   
    private static final byte SQLSTATE_MARKER = (byte) '#';
    private static final byte[] DEFAULT_SQLSTATE = "HY000".getBytes();

    public byte pkgType = MySQLPacket.ERROR_PACKET;
    public int errno;
    public byte mark = SQLSTATE_MARKER;
    public byte[] sqlState = DEFAULT_SQLSTATE;
    public String message;

  
    public void read(ShareBuffer byteBuffer) {
    	buftool.setBuffer(byteBuffer);
        packetLength =(int) buftool.readFixInt(3);
        packetId =buftool.readByte();
        pkgType =buftool.readByte();
        errno = (int) buftool.readFixInt(2);
        if ((byteBuffer.writeIndex - byteBuffer.readIndex) >0 && (buftool.getByte(byteBuffer.readIndex) == SQLSTATE_MARKER)) {
        	buftool.skip(1);
            sqlState = buftool.readBytes(5);
        }
        message = buftool.readNULString();
    }

    public void write(ShareBuffer buffer){
    	buftool.setBuffer(buffer);
    	buftool.writeFixInt(3,calcPacketSize());
    	buftool.writeByte(packetId);
    	buftool.writeByte(pkgType);
    	buftool.writeFixInt(2,errno);
    	buftool.writeByte(mark);
    	buftool.writeBytes(sqlState);
        if (message != null) {
        	buftool.writeNULString(message);

        }
    }

    @Override
    public int calcPacketSize() {
        int size = 9;// 1 + 2 + 1 + 5
        if (message != null) {
            size += message.length()+1;
        }
        return size;
    }

    @Override
    protected String getPacketInfo() {
        return "MySQL Error Packet";
    }

}
