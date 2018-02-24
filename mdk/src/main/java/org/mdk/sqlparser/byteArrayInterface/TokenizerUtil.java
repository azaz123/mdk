package org.mdk.sqlparser.byteArrayInterface;

import java.util.function.Supplier;



import org.mdk.sqlparser.BufferSQLContext;
import org.mdk.sqlparser.IntTokenHash;
import org.mdk.sqlparser.TokenHash;
import org.mdk.sqlparser.SQLParseUtils.HashArray;
import org.mdk.base.sharebuffer.ShareBuffer;
import org.mdk.protocol.mysql.tools.bufferHelper;

/**
 * Created by jamie on 2017/8/31.
 */
public class TokenizerUtil {
	public static bufferHelper bufhelper = new bufferHelper();
    public static int pickNumber(int pos, HashArray hashArray, ByteArrayInterface sql) {
        int value = 0;
        int start = hashArray.getPos(pos);
        int end = start + hashArray.getSize(pos);
        for (int i = start; i < end; i++) {
            int l = sql.get(i);
            int r = (l - '0');
            value = (value * 10) + (r);
        }
        return value;
    }
    
    /**
     * 获取数字
     * @param pos
     * @param hashArray
     * @param buffer
     * @return
     */
    public static int pickNumber(int pos, HashArray hashArray, ShareBuffer buffer) {
    	bufhelper.setBuffer(buffer);
        int value = 0;
        int start = hashArray.getPos(pos);
        int end = start + hashArray.getSize(pos);
        for (int i = start; i < end; i++) {
            int l = bufhelper.getByte(i);
            int r = (l - '0');
            value = (value * 10) + (r);
        }
        return value;
    }
    
    /**
     * 获取bytes 数组
     * @param pos
     * @param hashArray
     * @param buffer
     * @return
     */
    public static byte[] pickBytes(int pos,HashArray hashArray,ShareBuffer buffer){
    	bufhelper.setBuffer(buffer);
    	int start = hashArray.getPos(pos);
    	int length = hashArray.getSize(pos);
        return bufhelper.getBytes(start, length);
    }
    
    public static   boolean isAlias(int pos, int type, HashArray hashArray) { //需要优化成数组判断
        switch (type) {
            case IntTokenHash.WHERE:
                if (hashArray.getHash(pos) == TokenHash.WHERE)
                    return false;
                else
                    return true;
            case IntTokenHash.GROUP:
                if (hashArray.getHash(pos) == TokenHash.GROUP)
                    return false;
                else
                    return true;
            case IntTokenHash.ORDER:
                if (hashArray.getHash(pos) == TokenHash.ORDER)
                    return false;
                else
                    return true;
            case IntTokenHash.LIMIT:
                if (hashArray.getHash(pos) == TokenHash.LIMIT)
                    return false;
                else
                    return true;
            case IntTokenHash.JOIN:
                if (hashArray.getHash(pos) == TokenHash.JOIN)
                    return false;
                else
                    return true;
            case IntTokenHash.LEFT:
                if (hashArray.getHash(pos) == TokenHash.LEFT)
                    return false;
                else
                    return true;
            case IntTokenHash.RIGHT:
                if (hashArray.getHash(pos) == TokenHash.RIGHT)
                    return false;
                else
                    return true;
            case IntTokenHash.FOR:
                if (hashArray.getHash(pos) == TokenHash.FOR)
                    return false;
                else
                    return true;
            case IntTokenHash.LOCK:
                if (hashArray.getHash(pos) == TokenHash.LOCK)
                    return false;
                else
                    return true;
            case IntTokenHash.ON:
                if (hashArray.getHash(pos) == TokenHash.ON)
                    return false;
                else
                    return true;
            case IntTokenHash.OFF:
                if (hashArray.getHash(pos) == TokenHash.OFF)
                    return false;
                else
                    return true;
            case IntTokenHash.FROM:
                if (hashArray.getHash(pos) == TokenHash.FROM)
                    return false;
                else
                    return true;
            default:
                return true;
        }
    }
    private static void debug(Supplier<String> template, Supplier<String> msg) {

    }


    /**
     * Account name syntax is 'user_name'@'host_name'.
     */
    public static int pickSpecifyingAccountNames(int pos, final int arrayCount, BufferSQLContext context, HashArray hashArray, ByteArrayInterface sql) {
        TokenizerUtil.debug(pos,context);
        //todo 捕获 'user_name'
        ++pos;
        if (Tokenizer2.AT == hashArray.getType(pos)) {
            TokenizerUtil.debug(pos,context);
            ++pos;
            TokenizerUtil.debug(pos,context);
            //todo 捕获 'host_name'
            ++pos;
        } else {
            //语法错误
        }
        return pos;
    }

    public static int pickColumnList(int pos, final int arrayCount, BufferSQLContext context, HashArray hashArray, ByteArrayInterface sql) {
        //todo 捕获 'column'
        TokenizerUtil.debug(pos,context);
        ++pos;
        int type = hashArray.getType(pos);
        while (Tokenizer2.COMMA == type) {
            TokenizerUtil.debug(pos,context);
            ++pos;
            TokenizerUtil.debug(pos,context);
            //todo 捕获 'column'
            type = hashArray.getType(++pos);
        }
        return pos;
    }



    public static void debug(Supplier<String> msg) {

    }

    public static void debug(int pos,Tokenizer2 tokenizer,HashArray hashArray) {
    
    }
    public static void debug(int pos,BufferSQLContext context) {
     
    }
    public static void debugError(int pos,BufferSQLContext context) {
 
    }

}
