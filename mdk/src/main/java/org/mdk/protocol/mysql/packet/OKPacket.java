
package org.mdk.protocol.mysql.packet;



import org.mdk.base.sharebuffer.ShareBuffer;
import org.mdk.util.BufferUtil;

/**
 * From Server To Client, at the end of a series of Field Packets, and at the
 * end of a series of Data Packets.With prepared statements, EOF Packet can also
 * end parameter information, which we'll describe later.
 * 
 * <pre>
 * Bytes                 Name
 * -----                 ----
 * 1                     field_count, always = 0xfe
 * 2                     warning_count
 * 2                     Status Flags
 * 
 * &#64;see http://forge.mysql.com/wiki/MySQL_Internals_ClientServer_Protocol#EOF_Packet
 * </pre>
 * 
 * @author mycat
 */
public class OKPacket extends MySQLPacket {
	public byte pkgType = MySQLPacket.OK_PACKET;

	public static final byte FIELD_COUNT = 0x00;
	public static final byte[] OK = new byte[] { 7, 0, 0, 1, 0, 0, 0, 2, 0, 0, 0 };

	public byte fieldCount = FIELD_COUNT;
	public long affectedRows;
	public long insertId;
	public int serverStatus;
	public int warningCount;
	public byte[] message;

	public void write(ShareBuffer buffer) {
		buftool.setBuffer(buffer);
		buftool.writeFixInt(3, calcPacketSize());
		buftool.writeByte(packetId);
		buftool.writeLenencInt(fieldCount);
		buftool.writeLenencInt(affectedRows);
		buftool.writeLenencInt(insertId);
		buftool.writeFixInt(2, serverStatus);
		buftool.writeFixInt(2, warningCount);
		if (message != null) {
			buftool.writeLenencString(new String(message));
		}
	}

	public void read(ShareBuffer buffer) {
		buftool.setBuffer(buffer);
		int index = buffer.readIndex;
		packetLength = (int) buftool.readFixInt(3);
		packetId = buftool.readByte();
		fieldCount = buftool.readByte();
		affectedRows = buftool.readLenencInt();
		insertId = buftool.readLenencInt();
		serverStatus = (int) buftool.readFixInt(2);
		warningCount = (int) buftool.readFixInt(2);
		if (index + packetLength + MySQLPacket.packetHeaderSize - buffer.readIndex > 0) {
			int msgLength = index + packetLength + MySQLPacket.packetHeaderSize - buffer.readIndex;
			this.message = buftool.getBytes(buffer.writeIndex, msgLength);
			buffer.readIndex += msgLength;
		}
	}

	@Override
	public int calcPacketSize() {
		int i = 1;
		i += BufferUtil.getLength(affectedRows);
		i += BufferUtil.getLength(insertId);
		i += 4;
		if (message != null) {
			i += BufferUtil.getLength(message);
		}
		return i;
	}

	@Override
	protected String getPacketInfo() {
		return "MySQL OK Packet";
	}

}
