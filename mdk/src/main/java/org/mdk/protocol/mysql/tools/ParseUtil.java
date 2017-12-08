package org.mdk.protocol.mysql.tools;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.mdk.util.CharTypes;


public final class ParseUtil {
	public final static int msyql_packetHeaderSize = 4;
	public final static int mysql_packetTypeSize = 1;
	
	public final static int mysql_packetHeader_length = 3;
	public final static int mysql_packetHeader_type   = 1;
	

	public static final boolean validateHeader(final long offset, final long position) {
		return offset + msyql_packetHeaderSize + mysql_packetTypeSize <= position;

	}


	public static final int getPacketLength(ByteBuffer buffer, int offset) throws IOException {
		int length = buffer.get(offset) & 0xff;
		length |= (buffer.get(++offset) & 0xff) << 8;
		length |= (buffer.get(++offset) & 0xff) << 16;
		return length + msyql_packetHeaderSize;
	}


	public static final boolean validateResultHeader(final long offset, final long position, final int bitLength) {
		return offset + msyql_packetHeaderSize + mysql_packetTypeSize + bitLength <= position;
	}

	public static boolean isEOF(char c) {
		return (c == ' ' || c == '\t' || c == '\n' || c == '\r' || c == ';');
	}

	public static long getSQLId(String stmt) {
		int offset = stmt.indexOf('=');
		if (offset != -1 && stmt.length() > ++offset) {
			String id = stmt.substring(offset).trim();
			try {
				return Long.parseLong(id);
			} catch (NumberFormatException e) {
			}
		}
		return 0L;
	}

	/**
	 * <code>'abc'</code>
	 * 
	 * @param offset
	 *            stmt.charAt(offset) == first <code>'</code>
	 */
	private static String parseString(String stmt, int offset) {
		StringBuilder sb = new StringBuilder();
		loop: for (++offset; offset < stmt.length(); ++offset) {
			char c = stmt.charAt(offset);
			if (c == '\\') {
				switch (c = stmt.charAt(++offset)) {
				case '0':
					sb.append('\0');
					break;
				case 'b':
					sb.append('\b');
					break;
				case 'n':
					sb.append('\n');
					break;
				case 'r':
					sb.append('\r');
					break;
				case 't':
					sb.append('\t');
					break;
				case 'Z':
					sb.append((char) 26);
					break;
				default:
					sb.append(c);
				}
			} else if (c == '\'') {
				if (offset + 1 < stmt.length() && stmt.charAt(offset + 1) == '\'') {
					++offset;
					sb.append('\'');
				} else {
					break loop;
				}
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/**
	 * <code>"abc"</code>
	 * 
	 * @param offset
	 *            stmt.charAt(offset) == first <code>"</code>
	 */
	private static String parseString2(String stmt, int offset) {
		StringBuilder sb = new StringBuilder();
		loop: for (++offset; offset < stmt.length(); ++offset) {
			char c = stmt.charAt(offset);
			if (c == '\\') {
				switch (c = stmt.charAt(++offset)) {
				case '0':
					sb.append('\0');
					break;
				case 'b':
					sb.append('\b');
					break;
				case 'n':
					sb.append('\n');
					break;
				case 'r':
					sb.append('\r');
					break;
				case 't':
					sb.append('\t');
					break;
				case 'Z':
					sb.append((char) 26);
					break;
				default:
					sb.append(c);
				}
			} else if (c == '"') {
				if (offset + 1 < stmt.length() && stmt.charAt(offset + 1) == '"') {
					++offset;
					sb.append('"');
				} else {
					break loop;
				}
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/**
	 * <code>AS `abc`</code>
	 * 
	 * @param offset
	 *            stmt.charAt(offset) == first <code>`</code>
	 */
	private static String parseIdentifierEscape(String stmt, int offset) {
		StringBuilder sb = new StringBuilder();
		loop: for (++offset; offset < stmt.length(); ++offset) {
			char c = stmt.charAt(offset);
			if (c == '`') {
				if (offset + 1 < stmt.length() && stmt.charAt(offset + 1) == '`') {
					++offset;
					sb.append('`');
				} else {
					break loop;
				}
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/**
	 * @param aliasIndex
	 *            for <code>AS id</code>, index of 'i'
	 */
	public static String parseAlias(String stmt, final int aliasIndex) {
		if (aliasIndex < 0 || aliasIndex >= stmt.length()) {
			return null;
		}
		switch (stmt.charAt(aliasIndex)) {
		case '\'':
			return parseString(stmt, aliasIndex);
		case '"':
			return parseString2(stmt, aliasIndex);
		case '`':
			return parseIdentifierEscape(stmt, aliasIndex);
		default:
			int offset = aliasIndex;
			for (; offset < stmt.length() && CharTypes.isIdentifierChar(stmt.charAt(offset)); ++offset)
				;
			return stmt.substring(aliasIndex, offset);
		}
	}

	/**
	 * 
	 * 
	 * @param stmt
	 * @param offset
	 * @return
	 */
	public static int comment(String stmt, int offset) {
		int len = stmt.length();
		int n = offset;
		switch (stmt.charAt(n)) {
		case '/':
			if (len > ++n && stmt.charAt(n++) == '*' && len > n + 1) {
				// 对两种注解放过：/*!mycat: 和 /*#mycat:
				if (stmt.charAt(n) == '!') {
					break;
				} else if (stmt.charAt(n) == '#') {
					if (len > n + 5 && stmt.charAt(n + 1) == 'm' && stmt.charAt(n + 2) == 'y'
							&& stmt.charAt(n + 3) == 'c' && stmt.charAt(n + 4) == 'a' && stmt.charAt(n + 5) == 't') {
						break;

					}
				}
				for (int i = n; i < len; ++i) {
					if (stmt.charAt(i) == '*') {
						int m = i + 1;
						if (len > m && stmt.charAt(m) == '/')
							return m;
					}
				}
			}
			break;
		case '#':
			for (int i = n + 1; i < len; ++i) {
				if (stmt.charAt(i) == '\n')
					return i;
			}
			break;
		}
		return offset;
	}

	public static boolean currentCharIsSep(String stmt, int offset) {
		if (stmt.length() > offset) {
			switch (stmt.charAt(offset)) {
			case ' ':
			case '\t':
			case '\r':
			case '\n':
				return true;
			default:
				return false;
			}
		}
		return true;
	}

	
	public static boolean nextCharIsSep(String stmt, int offset) {
		return currentCharIsSep(stmt, ++offset);
	}


	public static int nextStringIsExpectedWithIgnoreSepChar(String stmt, int offset, String nextExpectedString,
			boolean checkSepChar) {
		if (nextExpectedString == null || nextExpectedString.length() < 1)
			return offset;
		int i = offset;
		int index = 0;
		char expectedChar;
		char actualChar;
		boolean isSep;
		for (; i < stmt.length() && index < nextExpectedString.length(); ++i) {
			if (index == 0) {
				isSep = currentCharIsSep(stmt, i);
				if (isSep) {
					continue;
				}
			}
			actualChar = stmt.charAt(i);
			expectedChar = nextExpectedString.charAt(index++);
			if (actualChar != expectedChar) {
				return offset;
			}
		}
		if (index == nextExpectedString.length()) {
			boolean ok = true;
			if (checkSepChar) {
				ok = nextCharIsSep(stmt, i);
			}
			if (ok)
				return i;
		}
		return offset;
	}

	private static final String JSON = "json";
	private static final String EQ = "=";

	// private static final String WHERE = "where";
	// private static final String SET = "set";


	public static int nextStringIsJsonEq(String stmt, int offset) {
		int i = offset;

		// / drds 之后的符号
		if (!currentCharIsSep(stmt, ++i)) {
			return offset;
		}

		// json 串
		int k = nextStringIsExpectedWithIgnoreSepChar(stmt, i, JSON, false);
		if (k <= i) {
			return offset;
		}
		i = k;

		// 等于符号
		k = nextStringIsExpectedWithIgnoreSepChar(stmt, i, EQ, false);
		if (k <= i) {
			return offset;
		}
		return i;
	}

	public static int move(String stmt, int offset, int length) {
		int i = offset;
		for (; i < stmt.length(); ++i) {
			switch (stmt.charAt(i)) {
			case ' ':
			case '\t':
			case '\r':
			case '\n':
				continue;
			case '/':
			case '#':
				i = comment(stmt, i);
				continue;
			default:
				return i + length;
			}
		}
		return i;
	}

	public static boolean compare(String s, int offset, char[] keyword) {
		if (s.length() >= offset + keyword.length) {
			for (int i = 0; i < keyword.length; ++i, ++offset) {
				if (Character.toUpperCase(s.charAt(offset)) != keyword[i]) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

}
