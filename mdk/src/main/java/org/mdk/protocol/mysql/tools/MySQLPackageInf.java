package org.mdk.protocol.mysql.tools;

public class MySQLPackageInf {
	public int pkgType;
	public boolean crossBuffer;
	public int startPos;
	public int endPos;
	public int pkgLength;
	/**
	 * half packet+remainsbytes = whole packet
	 */
	public int remainsBytes;
	@Override
	public String toString() {
		return "MySQLPackageInf [pkgType=" + pkgType + ", crossBuffer=" + crossBuffer + ", startPos=" + startPos
				+ ", endPos=" + endPos + ", pkgLength=" + pkgLength + ", remainsBytes=" + remainsBytes + "]";
	}
}
