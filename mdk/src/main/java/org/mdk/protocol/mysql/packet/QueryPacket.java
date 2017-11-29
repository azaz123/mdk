package org.mdk.protocol.mysql.packet;



import org.mdk.base.sharebuffer.ShareBuffer;


public class QueryPacket extends MySQLPacket {
    public String sql;
    private byte pkgType = MySQLPacket.COM_QUERY;

    @Override
    public int calcPacketSize() {
        return sql.length() + 1;
    }

    @Override
    protected String getPacketInfo() {
        return "A COM_QUERY packet:" + sql;
    }

    @Override
    public void write(ShareBuffer buffer) {
    	buftool.setBuffer(buffer);
    	buftool.writeFixInt(3, calcPacketSize());
    	buftool.writeByte(packetId);
    	buftool.writeByte(pkgType);
    	buftool.writeFixString(sql);
    }
}
