package org.mdk.battle.mysqlagent.task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.security.NoSuchAlgorithmException;

import org.mdk.net.nio.NIOHandler;
import org.mdk.net.nio.netBuffer;
import org.mdk.protocol.mysql.CurrPacketType;
import org.mdk.protocol.mysql.MysqlErrorCode;
import org.mdk.protocol.mysql.packet.AuthPacket;
import org.mdk.protocol.mysql.packet.ErrorPacket;
import org.mdk.protocol.mysql.packet.HandshakePacket;
import org.mdk.protocol.mysql.packet.MySQLPacket;
import org.mdk.protocol.mysql.tools.MysqlPrase;
import org.mdk.protocol.mysql.tools.ParseUtil;

import org.mdk.protocol.mysql.*;

import org.mdk.battle.mysqlagent.*;
import org.mdk.battle.mysqlagent.Handle.FrontEndDefaultHandle;
import org.mdk.battle.mysqlagent.beans.FrontEndUserBean;
import org.mdk.battle.mysqlagent.util.*;

public class FrontEndAuthTask extends AbstractTask implements NIOHandler<FrontEndSession> {
	private static final byte[] AUTH_OK = new byte[] { 7, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0 };
	@Override
	public void Excute() throws IOException {
		// TODO Auto-generated method stub
		setShareBuffer();
		Context.FSession.setCurNIOHandler(this);
		
	
		byte[] rand1 = RandomUtil.randomBytes(8);
		byte[] rand2 = RandomUtil.randomBytes(12);

	
		byte[] seed = new byte[rand1.length + rand2.length];
		System.arraycopy(rand1, 0, seed, 0, rand1.length);
		System.arraycopy(rand2, 0, seed, rand1.length, rand2.length);
		Context.FSession.seed = seed;


		HandshakePacket hs = new HandshakePacket();
		hs.packetId = 0;
		hs.protocolVersion = 10;
		hs.serverVersion = "our-mysql-agent".getBytes();
		hs.threadId = Context.FSession.getSessionId();
		hs.seed = rand1;
		hs.serverCapabilities = getServerCapabilities();

		hs.serverStatus = 2;
		hs.restOfScrambleBuff = rand2;
		hs.write(Context.FSession.sessionBuffer);

		Context.FSession.sessionBuffer.flip();
		Context.FSession.sessionBuffer.readIndex = Context.FSession.sessionBuffer.writeIndex;
		Context.FSession.writeToChannel();
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
	
	@Override
	public void onSocketRead(FrontEndSession session) throws IOException {
		netBuffer frontBuffer = session.sessionBuffer;
		if (session.readFromChannel() == false
				|| CurrPacketType.Full != MysqlPrase.resolveMySQLPackage(frontBuffer, session.curMSQLPackgInf, false)) {
			return;
		}

		// process user auth packet
		try {
			boolean bAuthSuccess = false;
			AuthPacket auth = new AuthPacket();
			auth.read(frontBuffer);
            
			/*
			for(Object o:AllConfigure.INSTANCE.FrontEndUserBeans.keySet()){       
	            FrontEndUserBean value = (FrontEndUserBean) AllConfigure.INSTANCE.FrontEndUserBeans.get(o); 
	            if( (value.getName().equals(auth.user))
	                 && (value.getPassword().equals(new String(auth.password))) ){
	            	bAuthSuccess = true;
	            	break;
	            }
	        } 
			*/
			bAuthSuccess = true;
			
			if(bAuthSuccess){
				session.sessionBuffer.reset();
				session.answerFront(AUTH_OK);
				session.setCurNIOHandler(FrontEndDefaultHandle.INSTANCE);
			}else{
				failure(session,MysqlErrorCode.ER_BAD_DB_ERROR,"databas auth failed");
			}
            

		} catch (Throwable e) {
			
		}
	}
	
	protected int getServerCapabilities() {
		int flag = 0;
		flag |= Capabilities.CLIENT_LONG_PASSWORD;
		flag |= Capabilities.CLIENT_FOUND_ROWS;
		flag |= Capabilities.CLIENT_LONG_FLAG;
		flag |= Capabilities.CLIENT_CONNECT_WITH_DB;
		// flag |= Capabilities.CLIENT_NO_SCHEMA;
		// boolean usingCompress = MycatServer.getInstance().getConfig()
		// .getSystem().getUseCompression() == 1;
		// if (usingCompress) {
		// flag |= Capabilities.CLIENT_COMPRESS;
		// }
		flag |= Capabilities.CLIENT_ODBC;
		flag |= Capabilities.CLIENT_LOCAL_FILES;
		flag |= Capabilities.CLIENT_IGNORE_SPACE;
		flag |= Capabilities.CLIENT_PROTOCOL_41;
		flag |= Capabilities.CLIENT_INTERACTIVE;
		// flag |= Capabilities.CLIENT_SSL;
		flag |= Capabilities.CLIENT_IGNORE_SIGPIPE;
		flag |= Capabilities.CLIENT_TRANSACTIONS;
		// flag |= ServerDefs.CLIENT_RESERVED;
		flag |= Capabilities.CLIENT_SECURE_CONNECTION;
		return flag;
	}
	
	@Override
	public void onSocketClosed(FrontEndSession userSession, boolean normal) {
	}

	@Override
	public void onSocketWrite(FrontEndSession session) throws IOException {
		session.writeToChannel();

	}

	@Override
	public void onWriteFinished(FrontEndSession s) throws IOException {
		if(ifUseShareBuffer()){
			s.sessionBuffer.flip();
		}else{
			s.sessionBuffer.reset();
		}
		s.change2ReadOpts();

	}
	
	public void onConnect(SelectionKey theKey, FrontEndSession userSession, boolean success, String msg)
			throws IOException {

	}
	
	private void failure(FrontEndSession session, int errno, String info) throws IOException {
		ErrorPacket errorPacket = new ErrorPacket();
		errorPacket.packetId = 2;
		errorPacket.errno = errno;
		errorPacket.message = info;
		session.responseOKOrError(errorPacket);
	}

}
