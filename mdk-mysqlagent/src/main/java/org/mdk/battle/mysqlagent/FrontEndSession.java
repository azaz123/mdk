package org.mdk.battle.mysqlagent;


import java.io.IOException;
import java.nio.channels.SocketChannel;

import org.mdk.net.nio.AbstractSession;
import org.mdk.net.nio.ReactorContext;
import org.mdk.battle.mysqlagent.beans.MysqlMetaBeans;
import org.mdk.protocol.mysql.tools.*;

import org.mdk.sqlparser.BufferSQLContext;

import org.mdk.battle.mysqlagent.cmd.*;


import org.mdk.protocol.mysql.packet.*;
import org.mdk.battle.mysqlagent.opencapacity.MetaDataManager;
import org.mdk.battle.mysqlagent.opencapacity.ComChain;
import java.nio.ByteBuffer;


public class FrontEndSession extends AbstractSession {
	public MySQLPackageInf curMSQLPackgInf = new MySQLPackageInf();
	public CmdRunTime currentCmdInfo;
	public BufferSQLContext sqlContext = new BufferSQLContext();
	public MetaDataManager  OCMetaData = new MetaDataManager();
	public ComChain OCChain = new ComChain();
	public FrontEndSession(ReactorContext Context,SocketChannel frontChannel) throws IOException {
        super(Context,frontChannel);
        System.out.println("FrontEndSession create end");
    }
	
	public void closeBackendAndResponseError(BackEndSession mysqlsession,boolean normal, ErrorPacket error)throws IOException{
		mysqlsession.close(normal, error.message);
		responseOKOrError(error);
	}

	public void responseOKOrError(MySQLPacket pkg) throws IOException {
		this.sessionBuffer.reset();
		pkg.write(this.sessionBuffer);
		this.sessionBuffer.flip();
		this.sessionBuffer.readIndex = this.sessionBuffer.writeIndex;
		this.writeToChannel();
	}
	

	public void responseOK() throws IOException {
		// proxyBuffer.changeOwner(true);
		buftool.setBuffer(this.sessionBuffer);
		this.sessionBuffer.reset();
		buftool.writeBytes(OKPacket.OK);
		this.sessionBuffer.flip();
		this.sessionBuffer.readIndex = this.sessionBuffer.writeIndex;
		this.writeToChannel();
	}
	
	public void answerFront(byte[] rawPkg) throws IOException {
		this.sessionBuffer.reset();
		buftool.setBuffer(this.sessionBuffer);
		buftool.writeBytes(rawPkg);
		this.sessionBuffer.flip();
		this.sessionBuffer.readIndex = this.sessionBuffer.writeIndex;
		writeToChannel();
	}
}
