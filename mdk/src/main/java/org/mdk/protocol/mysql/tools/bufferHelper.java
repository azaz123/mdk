package org.mdk.protocol.mysql.tools;

import org.mdk.base.sharebuffer.ShareBuffer;;

public class bufferHelper {
	
	private ShareBuffer buffer;
	
	public void setBuffer(ShareBuffer buf){
		this.buffer = buf;
	}
	
	public void skip(int step) {
		buffer.readIndex += step;
	}

	
	public void writeBytes(byte[] bytes) {
		this.writeBytes(bytes.length, bytes);
	}

	public long readFixInt(int length) {
		long val = getInt(buffer.readIndex, length);
		buffer.readIndex += length;
		return val;
	}

	public long getFixInt(int index,int length){
		return getInt(index, length);
	}

	public long readLenencInt() {
		int index = buffer.readIndex;
		long len = getInt(index, 1) & 0xff;
		if (len < 251) {
			buffer.readIndex += 1;
			return getInt(index, 1);
		} else if (len == 0xfc) {
			buffer.readIndex += 2;
			return getInt(index + 1, 2);
		} else if (len == 0xfd) {
			buffer.readIndex += 3;
			return getInt(index + 1, 3);
		} else {
			buffer.readIndex += 8;
			return getInt(index + 1, 8);
		}
	}

	public long getInt(int index, int length) {
		buffer.getBuffer().position(index);
		long rv = 0;
		for (int i = 0; i < length; i++) {
			byte b = buffer.getBuffer().get();
			rv |= (((long) b) & 0xFF) << (i * 8);
		}
		return rv;
	}

	public byte[] getBytes(int index, int length) {
		buffer.getBuffer().position(index);
		byte[] bytes = new byte[length];
		buffer.getBuffer().get(bytes);
		return bytes;
	}

	public byte getByte(int index) {
		buffer.getBuffer().position(index);
		byte b = buffer.getBuffer().get();
		return b;
	}

	public String getFixString(int index, int length) {
		byte[] bytes = getBytes(index, length);
		return new String(bytes);
	}

	public String readFixString(int length) {
		byte[] bytes = getBytes(buffer.readIndex, length);
		buffer.readIndex += length;
		return new String(bytes);
	}

	public String getLenencString(int index) {
		int strLen = (int) getLenencInt(index);
		int lenencLen = getLenencLength(strLen);
		byte[] bytes = getBytes(index + lenencLen, strLen);
		return new String(bytes);
	}

	public String readLenencString() {
		int strLen = (int) getLenencInt(buffer.readIndex);
		int lenencLen = getLenencLength(strLen);
		byte[] bytes = getBytes(buffer.readIndex + lenencLen, strLen);
		buffer.readIndex += strLen + lenencLen;
		return new String(bytes);
	}

	public String getVarString(int index, int length) {
		return getFixString(index, length);
	}

	public String readVarString(int length) {
		return readFixString(length);
	}
	
	public String getNULString(int index) {
		int strLength = 0;
		int scanIndex = index;
		while (scanIndex < buffer.writeIndex) {
			if (getByte(scanIndex++) == 0) {
				break;
			}
			strLength++;
		}
		byte[] bytes = getBytes(index, strLength);
		return new String(bytes);
	}

	public String readNULString() {
		String rv = getNULString(buffer.readIndex);
		buffer.readIndex += rv.getBytes().length + 1;
		return rv;
	}

	public void putFixInt(int index, int length, long val) {
		int index0 = index;
		for (int i = 0; i < length; i++) {
			byte b = (byte) ((val >> (i * 8)) & 0xFF);
			putByte(index0++, b);
		}
	}

	public void writeFixInt(int length, long val) {
		putFixInt(buffer.writeIndex, length, val);
		buffer.writeIndex += length;
	}

	public void putLenencInt(int index, long val) {
		if (val < 251) {
			putByte(index, (byte) val);
		} else if (val >= 251 && val < (1 << 16)) {
			putByte(index, (byte) 0xfc);
			putFixInt(index + 1, 2, val);
		} else if (val >= (1 << 16) && val < (1 << 24)) {
			putByte(index, (byte) 0xfd);
			putFixInt(index + 1, 3, val);
		} else {
			putByte(index, (byte) 0xfe);
			putFixInt(index + 1, 8, val);
		}
	}

	public void writeLenencInt(long val) {
		if (val < 251) {
			putByte(buffer.writeIndex++, (byte) val);
		} else if (val >= 251 && val < (1 << 16)) {
			putByte(buffer.writeIndex++, (byte) 0xfc);
			putFixInt(buffer.writeIndex, 2, val);
			buffer.writeIndex += 2;
		} else if (val >= (1 << 16) && val < (1 << 24)) {
			putByte(buffer.writeIndex++, (byte) 0xfd);
			putFixInt(buffer.writeIndex, 3, val);
			buffer.writeIndex += 3;
		} else {
			putByte(buffer.writeIndex++, (byte) 0xfe);
			putFixInt(buffer.writeIndex, 8, val);
			buffer.writeIndex += 8;
		}
	}

	public void putFixString(int index, String val) {
		putBytes(index, val.getBytes());
	}

	public void writeFixString(String val) {
		putBytes(buffer.writeIndex, val.getBytes());
		buffer.writeIndex += val.getBytes().length;
	}

	public void putLenencString(int index, String val) {
		this.putLenencInt(index, val.getBytes().length);
		int lenencLen = getLenencLength(val.getBytes().length);
		this.putFixString(index + lenencLen, val);
	}

	public void writeLenencString(String val) {
		putLenencString(buffer.writeIndex, val);
		int lenencLen = getLenencLength(val.getBytes().length);
		buffer.writeIndex += lenencLen + val.getBytes().length;
	}

	public void putVarString(int index, String val) {
		putFixString(index, val);
	}

	public void writeVarString(String val) {
		writeFixString(val);
	}

	public void putBytes(int index, byte[] bytes) {
		putBytes(index, bytes.length, bytes);
	}

	public void putBytes(int index, int length, byte[] bytes) {
		buffer.getBuffer().position(index);
		buffer.getBuffer().put(bytes);
	}

	public void putByte(int index, byte val) {
		buffer.getBuffer().position(index);
		buffer.getBuffer().put(val);
	}

	public void putNULString(int index, String val) {
		putFixString(index, val);
		putByte(val.getBytes().length + index, (byte) 0);
	}

	public void writeNULString(String val) {
		putNULString(buffer.writeIndex, val);
		buffer.writeIndex += val.getBytes().length + 1;
	}

	public byte[] readBytes(int length) {
		byte[] bytes = this.getBytes(buffer.readIndex, length);
		buffer.readIndex += length;
		return bytes;
	}

	public void writeBytes(int length, byte[] bytes) {
		this.putBytes(buffer.writeIndex, length, bytes);
		buffer.writeIndex += length;
	}

	public void writeLenencBytes(byte[] bytes) {
		putLenencInt(buffer.writeIndex, bytes.length);
		int offset = getLenencLength(bytes.length);
		putBytes(buffer.writeIndex + offset, bytes);
		buffer.writeIndex += offset + bytes.length;
	}

	public void writeByte(byte val) {
		this.putByte(buffer.writeIndex, val);
		buffer.writeIndex++;
	}

	public byte readByte() {
		byte val = getByte(buffer.readIndex);
		buffer.readIndex++;
		return val;
	}

	public byte[] getLenencBytes(int index) {
		int len = (int) getLenencInt(index);
		return getBytes(index + getLenencLength(len), len);
	}

	/**
	 * 获取lenenc占用的字节长度
	 *
	 * @param lenenc
	 *            值
	 * @return 长度
	 */
	public static int getLenencLength(int lenenc) {
		if (lenenc < 251) {
			return 1;
		} else if (lenenc >= 251 && lenenc < (1 << 16)) {
			return 3;
		} else if (lenenc >= (1 << 16) && lenenc < (1 << 24)) {
			return 4;
		} else {
			return 9;
		}
	}

	
	public long getLenencInt(int index) {
		long len = getInt(index, 1) & 0xff;
		if (len == 0xfc) {
			return getInt(index + 1, 2);
		} else if (len == 0xfd) {
			return getInt(index + 1, 3);
		} else if (len == 0xfe) {
			return getInt(index + 1, 8);
		} else {
			return len;
		}
	}

	public byte[] readLenencBytes() {
		int len = (int) getLenencInt(buffer.readIndex);
		byte[] bytes = getBytes(buffer.readIndex + getLenencLength(len), len);
		buffer.readIndex += getLenencLength(len) + len;
		return bytes;
	}

	public void putLenencBytes(int index, byte[] bytes) {
		putLenencInt(index, bytes.length);
		int offset = getLenencLength(bytes.length);
		putBytes(index + offset, bytes);
	}
}
