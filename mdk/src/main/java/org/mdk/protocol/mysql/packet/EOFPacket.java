
package org.mdk.protocol.mysql.packet;



import org.mdk.base.sharebuffer.ShareBuffer;

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
 * @author hrz
 */
public class EOFPacket extends MySQLPacket {
	public byte pkgType = MySQLPacket.EOF_PACKET;
	public int warningCount;
	public int status = 2;

	public void write(ShareBuffer buffer) {
		buftool.setBuffer(buffer);
		buftool.writeFixInt(3, calcPacketSize());
		buftool.writeByte(packetId);
		buftool.writeLenencInt(pkgType);
		buftool.writeFixInt(2, warningCount);
		buftool.writeFixInt(2, status);
	}

	public void read(ShareBuffer buffer) {
		buftool.setBuffer(buffer);
		packetLength = (int) buftool.readFixInt(3);
		packetId = buftool.readByte();
		pkgType = (byte) buftool.readByte();
		warningCount = (int) buftool.readFixInt(2);
		status = (int) buftool.readFixInt(2);
	}

	@Override
	public int calcPacketSize() {
		return 5;// 1+2+2;
	}

	@Override
	protected String getPacketInfo() {
		return "MySQL EOF Packet";
	}

}
