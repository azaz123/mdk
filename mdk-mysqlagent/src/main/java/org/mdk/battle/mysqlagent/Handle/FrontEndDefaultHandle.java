package org.mdk.battle.mysqlagent.Handle;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.util.ArrayList;

import org.mdk.net.nio.NIOHandler;
import org.mdk.protocol.mysql.tools.bufferHelper;

import org.mdk.sqlparser.BufferSQLParser;

import org.mdk.battle.mysqlagent.BackEndSession;
import org.mdk.battle.mysqlagent.BackEndSessionManager;
import org.mdk.battle.mysqlagent.FrontEndSession;
import org.mdk.battle.mysqlagent.cmd.DefaultSingleCmd;
import org.mdk.battle.mysqlagent.cmd.PassThroughCmd;
import org.mdk.battle.mysqlagent.protocol.*;
import org.mdk.battle.mysqlagent.task.*;
import org.mdk.protocol.mysql.*;
import org.mdk.protocol.mysql.packet.*;
import org.mdk.protocol.mysql.tools.*;
import org.mdk.battle.mysqlagent.*;
import org.mdk.battle.mysqlagent.armada.*;


public class FrontEndDefaultHandle implements NIOHandler<FrontEndSession> {
	public static final FrontEndDefaultHandle INSTANCE = new FrontEndDefaultHandle();
	public void onSocketRead(final FrontEndSession session) throws IOException {
		System.out.println("FrontEndDefaultHandle onSocketRead ");
	    session.readFromChannel();
	    //shutdown frontend io event selector,because we need wait feedback packet
	    session.clearReadWriteOpts();
	    switch(MysqlPrase.resolveMySQLPackage(session.sessionBuffer, session.curMSQLPackgInf,false)){
		 case Full:
			break;
		 case LongHalfPacket:
		 case ShortHalfPacket:
			 session.sessionBuffer.readMark = session.sessionBuffer.readIndex;
			return;
		}
	    BufferSQLParser parser = new BufferSQLParser();
	    int rowDataIndex = session.curMSQLPackgInf.startPos + MySQLPacket.packetHeaderSize +1 ;
		int length = session.curMSQLPackgInf.pkgLength -  MySQLPacket.packetHeaderSize - 1 ;
		try {
			parser.parse(session.sessionBuffer.getBuffer(), rowDataIndex, length, session.sqlContext);
			session.OCMetaData.sqlContext = session.sqlContext;
			session.OCMetaData.SessionData = session;
			/*
			System.out.println("recv tablename:"+session.sqlContext.getTableName(0) + "/r/nSql:" + session.sqlContext.getRealSQL(0)
			                   + "/r/nsqltype:"+session.sqlContext.getCurSQLType()
			                   + "/r/nlimitcount:"+session.sqlContext.getLimitCount()
			                   + "/r/nlimitstart:" + session.sqlContext.getLimitStart()
			                   + "/r/nschemacount:"+ session.sqlContext.getSchemaCount()
			                   + "/r/nschemaname:" + session.sqlContext.getSchemaName(0)
			                   + "/r/nselectitem:" + session.sqlContext.getSelectItem(0)
			                   + "/r/nsqlcount:" + session.sqlContext.getSQLCount()
			                   + "/r/nsqltablename:"+ session.sqlContext.getSQLTableName(0, 0)
			                   + "/r/nsqltablecount:" + session.sqlContext.getSQLTblCount(0)
			                   + "/r/nanotype:" + session.sqlContext.getAnnotationType()
			                   + "/r/nanovalue:" + session.sqlContext.getAnnotationStringValue(session.sqlContext.getAnnotationType()));
			*/                   
			session.OCMetaData.DipObj.Update(session.OCMetaData);
			session.OCChain.doAction(session.OCMetaData);
		} catch (Exception e) {
			System.out.println("sql prase exception");
			e.printStackTrace();
            return;
		}
		Admiral oneAdmiral = new Admiral();
		oneAdmiral.DoAction(session, session.OCMetaData.DopObj.plandata);
	    return;
	}


	
	public void onSocketClosed(FrontEndSession session, boolean normal) {

	}

	@Override
	public void onSocketWrite(FrontEndSession session) throws IOException {
		session.writeToChannel();

	}

	@Override
	public void onConnect(SelectionKey curKey, FrontEndSession session, boolean success, String msg)
			throws IOException {
		throw new java.lang.RuntimeException("not implemented ");
	}

	@Override
	public void onWriteFinished(FrontEndSession session) throws IOException {
                 //nothing to do,because this handle don't do any write action
		}
}
