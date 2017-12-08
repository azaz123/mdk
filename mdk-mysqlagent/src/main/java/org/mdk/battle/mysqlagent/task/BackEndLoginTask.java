package org.mdk.battle.mysqlagent.task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;

import org.mdk.net.nio.NIOHandler;
import org.mdk.net.nio.Session;
import org.mdk.battle.mysqlagent.BackEndSession;
import org.mdk.battle.mysqlagent.beans.MysqlMetaBeans;
import org.mdk.protocol.mysql.packet.*;


import org.mdk.protocol.mysql.*;
import org.mdk.protocol.mysql.packet.*;
import org.mdk.protocol.mysql.tools.*;
import org.mdk.battle.mysqlagent.AppErrorCode;
import org.mdk.util.*;

import org.mdk.battle.mysqlagent.util.*;
import org.mdk.net.nio.*;
import org.mdk.battle.mysqlagent.BackEndSessionManager;






public class BackEndLoginTask extends AbstractTask implements NIOHandler<BackEndSession>{
	private bufferHelper buffertool = new bufferHelper();
    private MysqlMetaBeans beans;
    protected ErrorPacket errPkg;
    private HandshakePacket handshake;
	private boolean welcomePkgReceived = false;
    
    public void setBeans(MysqlMetaBeans beans){
    	this.beans = beans;
    }
	@Override
	public void Excute() throws IOException{
		// TODO Auto-generated method stub
		String serverIP = beans.GetIp();
		int serverPort = beans.GetPort();
		InetSocketAddress serverAddress = new InetSocketAddress(serverIP, serverPort);
		SocketChannel backendChannel = SocketChannel.open();
		backendChannel.configureBlocking(false);
		backendChannel.connect(serverAddress);
		BackEndSession session = BackEndSessionManager.INSTANCE.createSession(nioRuntime.INSTANCE.Rc[0], backendChannel, false);
		super.Context.BSession = session;
		session.setCurNIOHandler(this);
	}
	
	@Override
	public void onSocketRead(BackEndSession session) throws IOException {
		session.sessionBuffer.reset();
		buffertool.setBuffer(session.sessionBuffer);
		if (!session.readFromChannel() || CurrPacketType.Full != MysqlPrase.resolveMySQLPackage(session.sessionBuffer,
				session.curMSQLPackgInf, false)) {
			return;
		}
		
		if(MySQLPacket.ERROR_PACKET == session.curMSQLPackgInf.pkgType){
			errPkg = new ErrorPacket();
			errPkg.packetId = buffertool.getByte(session.curMSQLPackgInf.startPos 
															+ ParseUtil.mysql_packetHeader_length);
			errPkg.read(session.sessionBuffer);
			this.finished(false);
			return;
		}

		if (!welcomePkgReceived) {
			handshake = new HandshakePacket();
			handshake.read(session.sessionBuffer);

			// set charset
			int charsetIndex = (handshake.serverCharsetIndex & 0xff);
			// send authpacket to frontend
			AuthPacket packet = new AuthPacket();
			packet.packetId = 1;
			packet.clientFlags = initClientFlags();
			packet.maxPacketSize = 1024 * 1000;
			packet.charsetIndex = charsetIndex;
			packet.user = beans.GetUserName();
			try {
				packet.password = passwd(beans.GetPassword(), handshake);
			} catch (NoSuchAlgorithmException e) {
				throw new RuntimeException(e.getMessage());
			}

			session.sessionBuffer.reset();
			packet.write(session.sessionBuffer);
			session.sessionBuffer.flip();

			session.sessionBuffer.readIndex = session.sessionBuffer.writeIndex;
			session.writeToChannel();
			welcomePkgReceived = true;
		} else {

			if (session.curMSQLPackgInf.pkgType == MySQLPacket.OK_PACKET) {
				this.finished(true);
			}else{
				this.finished(false);
			}
		}
	}

	@Override
	protected void finished(boolean success) throws IOException {
		// TODO Auto-generated method stub
		revertShareBuffer();
        if(super.isLastTask){
        	super.currentcmd.OnCmdResponse(super.Context,success);
        }else{
        	if(success){
        		super.nextTask.Excute();
        	}else{
        		super.currentcmd.OnCmdResponse(super.Context,success);
        	}
        	
        }
	}
	
	public void onConnect(SelectionKey theKey, BackEndSession userSession, boolean success, String msg)
			throws IOException {
		String logInfo = success ? " backend connect success " : "backend connect failed " + msg;
		if (success) {
			InetSocketAddress serverRemoteAddr = (InetSocketAddress) userSession.channel.getRemoteAddress();
			InetSocketAddress serverLocalAddr = (InetSocketAddress) userSession.channel.getLocalAddress();
			userSession.addr = "local port:" + serverLocalAddr.getPort() + ",remote " + serverRemoteAddr.getHostString()
					+ ":" + serverRemoteAddr.getPort();
			userSession.channelKey.interestOps(SelectionKey.OP_READ);

		} else {
			errPkg = new ErrorPacket();
			errPkg.packetId = 1;
			errPkg.errno = AppErrorCode.ERR_CONNECT_SOCKET;
			errPkg.message = logInfo;
			finished(false);
		}
	}
	
	private static byte[] passwd(String pass, HandshakePacket hs) throws NoSuchAlgorithmException {
		if (pass == null || pass.length() == 0) {
			return null;
		}
		byte[] passwd = pass.getBytes();
		int sl1 = hs.seed.length;
		int sl2 = hs.restOfScrambleBuff.length;
		byte[] seed = new byte[sl1 + sl2];
		System.arraycopy(hs.seed, 0, seed, 0, sl1);
		System.arraycopy(hs.restOfScrambleBuff, 0, seed, sl1, sl2);
		return SecurityUtil.scramble411(passwd, seed);
	}

	private static long initClientFlags() {
		int flag = 0;
		flag |= Capabilities.CLIENT_LONG_PASSWORD;
		flag |= Capabilities.CLIENT_FOUND_ROWS;
		flag |= Capabilities.CLIENT_LONG_FLAG;
		flag |= Capabilities.CLIENT_CONNECT_WITH_DB;
		// flag |= Capabilities.CLIENT_NO_SCHEMA;
		boolean usingCompress = false;
		if (usingCompress) {
			flag |= Capabilities.CLIENT_COMPRESS;
		}
		flag |= Capabilities.CLIENT_ODBC;
		flag |= Capabilities.CLIENT_LOCAL_FILES;
		flag |= Capabilities.CLIENT_IGNORE_SPACE;
		flag |= Capabilities.CLIENT_PROTOCOL_41;
		flag |= Capabilities.CLIENT_INTERACTIVE;
		// flag |= Capabilities.CLIENT_SSL;
		flag |= Capabilities.CLIENT_IGNORE_SIGPIPE;
		flag |= Capabilities.CLIENT_TRANSACTIONS;
		// flag |= Capabilities.CLIENT_RESERVED;
		flag |= Capabilities.CLIENT_SECURE_CONNECTION;
		// client extension
		flag |= Capabilities.CLIENT_MULTI_STATEMENTS;
		flag |= Capabilities.CLIENT_MULTI_RESULTS;
		return flag;
	}
	
	@Override
	public void onSocketClosed(BackEndSession userSession, boolean normal) {
	}

	@Override
	public void onSocketWrite(BackEndSession session) throws IOException {
		session.writeToChannel();

	}

	@Override
	public void onWriteFinished(BackEndSession s) throws IOException {
		if(ifUseShareBuffer()){
			s.sessionBuffer.flip();
		}else{
			s.sessionBuffer.reset();
		}
		s.change2ReadOpts();

	}


}
