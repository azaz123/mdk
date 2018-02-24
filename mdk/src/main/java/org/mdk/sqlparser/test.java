package org.mdk.sqlparser;

import java.io.IOException;
import java.nio.ByteBuffer;





public class test {
	public static void main(String[] args){
		BufferSQLParser parser = new BufferSQLParser();
		BufferSQLContext sqlContext = new BufferSQLContext();
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		buffer.put("select * from hrz".getBytes());
		int rowDataIndex = 0;
		int length = "select * from hrz".getBytes().length;
		try {
			parser.parse(buffer, rowDataIndex, length, sqlContext);
		} catch (Exception e) {
			
		}
		byte sqltype = sqlContext.getSQLType();
		int ntablecount = sqlContext.getTableCount();
		String tablename = sqlContext.getTableName(0);
		String tablename2 = sqlContext.getTableName(2);
		return;
	}
}
