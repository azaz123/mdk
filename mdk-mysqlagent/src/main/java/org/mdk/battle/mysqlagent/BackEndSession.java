package org.mdk.battle.mysqlagent;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import org.mdk.battle.mysqlagent.beans.MysqlMetaBeans;
import org.mdk.net.nio.AbstractSession;
import org.mdk.net.nio.ReactorContext;
import org.mdk.protocol.mysql.packet.MySQLPacket;
import org.mdk.protocol.mysql.packet.OKPacket;
import org.mdk.protocol.mysql.tools.*;

public class BackEndSession extends AbstractSession {
	public MySQLPackageInf curMSQLPackgInf = new MySQLPackageInf();
	public MysqlMetaBeans MysqlBeans;
	public boolean isLogin;
	public BackEndSession(ReactorContext Context,SocketChannel frontChannel) throws IOException {
        super(Context,frontChannel,SelectionKey.OP_CONNECT);
        isLogin = true;
    }
	
	public void SetBeans(MysqlMetaBeans Beans){
		this.MysqlBeans = Beans;
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
}
