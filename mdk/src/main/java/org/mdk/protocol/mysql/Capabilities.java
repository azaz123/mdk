
package org.mdk.protocol.mysql;

/**
 * processing capacity
 * 
 * @author hrz
 */
public interface Capabilities {

    /**
     * server capabilities
     * 
     * <pre>
     * server:        11110111 11111111
     * client_cmd: 11 10100110 10000101
     * client_jdbc:10 10100010 10001111
     *  
     * @see http://dev.mysql.com/doc/refman/5.1/en/mysql-real-connect.html
     * </pre>
     */
    // new more secure passwords
    public static final int CLIENT_LONG_PASSWORD = 1;

    // Found instead of affected rows
    // 返回找到（匹配）的行数，而不是改变了的行数。
    public static final int CLIENT_FOUND_ROWS = 2;

    // Get all column flags
    public static final int CLIENT_LONG_FLAG = 4;

    // One can specify db on connect
    public static final int CLIENT_CONNECT_WITH_DB = 8;

    // Don't allow database.table.column
    public static final int CLIENT_NO_SCHEMA = 16;

    // Can use compression protocol
    public static final int CLIENT_COMPRESS = 32;

    // Odbc client
    public static final int CLIENT_ODBC = 64;

    // Can use LOAD DATA LOCAL
    public static final int CLIENT_LOCAL_FILES = 128;

    // Ignore spaces before '('
    public static final int CLIENT_IGNORE_SPACE = 256;

    // New 4.1 protocol This is an interactive client
    public static final int CLIENT_PROTOCOL_41 = 512;

    // This is an interactive client
    public static final int CLIENT_INTERACTIVE = 1024;

    // Switch to SSL after handshake
    public static final int CLIENT_SSL = 2048;

    // IGNORE sigpipes
    public static final int CLIENT_IGNORE_SIGPIPE = 4096;

    // Client knows about transactions
    public static final int CLIENT_TRANSACTIONS = 8192;

    // Old flag for 4.1 protocol
    public static final int CLIENT_RESERVED = 16384;

    // New 4.1 authentication
    public static final int CLIENT_SECURE_CONNECTION = 32768;

    // Enable/disable multi-stmt support
    public static final int CLIENT_MULTI_STATEMENTS = 65536;

    // Enable/disable multi-results
    public static final int CLIENT_MULTI_RESULTS = 131072;

}