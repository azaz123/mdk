
package org.mdk.protocol.mysql.packet;

import java.io.IOException;

import org.mdk.protocol.mysql.Capabilities;
import org.mdk.base.sharebuffer.ShareBuffer;
import org.mdk.util.BufferUtil;
/**
 * From client to server during initial handshake.
 * <p>
 * <pre>
 * Bytes                        Name
 * -----                        ----
 * 4                            client_flags
 * 4                            max_packet_size
 * 1                            charset_number
 * 23                           (filler) always 0x00...
 * n (Null-Terminated String)   user
 * n (Length Coded Binary)      scramble_buff (1 + x bytes)
 * n (Null-Terminated String)   databasename (optional)
 *
 * &#64;see http://forge.mysql.com/wiki/MySQL_Internals_ClientServer_Protocol#Client_Authentication_Packet
 * </pre>
 *
 * @author hrz
 */
public class AuthPacket extends MySQLPacket {
    private static final byte[] FILLER = new byte[23];

    public long clientFlags;
    public long maxPacketSize;
    public int charsetIndex;
    public byte[] extra;// from FILLER(23)
    public String user;
    public byte[] password;
    public String database;

    public void read(ShareBuffer byteBuffer) throws IOException {
    	buftool.setBuffer(byteBuffer);
        packetLength = (int) buftool.readFixInt(3);
        packetId = buftool.readByte();
        clientFlags = buftool.readFixInt(4);
        maxPacketSize = buftool.readFixInt(4);
        charsetIndex = buftool.readByte();
        buftool.skip(23);
        user = buftool.readNULString();
        password = buftool.readLenencBytes();
        if ((clientFlags & Capabilities.CLIENT_CONNECT_WITH_DB) != 0) {
            database = buftool.readNULString();
        }
    }

    public void write(ShareBuffer buffer) {
    	buftool.setBuffer(buffer);
    	this.write(buffer, calcPacketSize());
    }
    public void write(ShareBuffer buffer, int pkgSize) {
    	buftool.setBuffer(buffer);
    	buftool.writeFixInt(3, pkgSize);
    	buftool.writeByte(packetId);
    	buftool.writeFixInt(4, clientFlags);
    	buftool.writeFixInt(4, maxPacketSize);
    	buftool.writeByte((byte) charsetIndex);
    	buftool.writeBytes(FILLER);
        if (user == null) {
        	buftool.writeByte((byte) 0);
        } else {
        	buftool.writeNULString(user);
        }
        if (password == null) {
        	buftool.writeByte((byte) 0);
        } else {
        	buftool.writeLenencBytes(password);
        }
        if (database == null) {
        	buftool.writeByte((byte) 0);
        } else {
        	buftool.writeNULString(database);
        }
    }

    @Override
    public int calcPacketSize() {
        int size = 32;//4+4+1+23;
        size += (user == null) ? 1 : user.length() + 1;
        size += (password == null) ? 1 : BufferUtil.getLength(password);
        size += (database == null) ? 1 : database.length() + 1;
        return size;
    }

    @Override
    protected String getPacketInfo() {
        return "MySQL Authentication Packet";
    }

}
