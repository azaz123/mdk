package org.mdk.protocol.mysql.tools;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.mdk.protocol.mysql.tools.*;
import org.mdk.protocol.mysql.*;
import org.mdk.net.nio.*;

public class MysqlPrase {
	public static bufferHelper buftool = new bufferHelper();
	public static CurrPacketType resolveMySQLPackage(netBuffer netBuf, MySQLPackageInf curPackInf, boolean markReaded)
			throws IOException {
		
		buftool.setBuffer(netBuf);
		ByteBuffer buffer = netBuf.getBuffer();
		// read offset
		int offset = netBuf.readIndex;
		// read limit
		int limit = netBuf.writeIndex;
		// read totallen
		int totalLen = limit - offset;
		if (totalLen == 0) { 
			return CurrPacketType.ShortHalfPacket;
		}

	
		// check if whole header
		if (!ParseUtil.validateHeader(offset, limit)) {
			// recv short half packet;
			return CurrPacketType.ShortHalfPacket;
		}

		// prase pkglength
		int pkgLength = ParseUtil.getPacketLength(buffer, offset);
		
		// prase packetType
		int packetType = -1;

		// is resultset packet
		if (pkgLength <= 7) {
			int index = offset + ParseUtil.msyql_packetHeaderSize;

			long len = buftool.getInt(index, 1) & 0xff;
			// if length < 251 , get default length
			if (len < 251) {
				packetType = (int) len;
			} else if (len == 0xfc) {
				// check if short half packet
				if (!ParseUtil.validateResultHeader(offset, limit, 2)) {
					// recv short half packet
					return CurrPacketType.ShortHalfPacket;
				}
				packetType = (int) buftool.getInt(index + 1, 2);
			} else if (len == 0xfd) {

				// check if short half packet
				if (!ParseUtil.validateResultHeader(offset, limit, 3)) {
					// recv short half packet
					return CurrPacketType.ShortHalfPacket;
				}

				packetType = (int) buftool.getInt(index + 1, 3);
			} else {
				// check if short half packet
				if (!ParseUtil.validateResultHeader(offset, limit, 8)) {
					// recv short half packet
					return CurrPacketType.ShortHalfPacket;
				}

				packetType = (int) buftool.getInt(index + 1, 8);
			}
		} else {
			// prase packetType
			packetType = buffer.get(offset + ParseUtil.msyql_packetHeaderSize);
		}

		// set packet type
		curPackInf.pkgType = packetType;
		// set packet length
		curPackInf.pkgLength = pkgLength;
		// set offset
		curPackInf.startPos = offset;

		curPackInf.crossBuffer = false;

		curPackInf.remainsBytes = 0;
		// recv whole head,but body is not whole packet 
		if ((offset + pkgLength) > limit) {
			curPackInf.endPos = limit;
			return CurrPacketType.LongHalfPacket;
		} else {
			// whole packet
			curPackInf.endPos = curPackInf.pkgLength + curPackInf.startPos;
			if (markReaded) {
				netBuf.readIndex = curPackInf.endPos;
			}
			return CurrPacketType.Full;
		}
	}
}
