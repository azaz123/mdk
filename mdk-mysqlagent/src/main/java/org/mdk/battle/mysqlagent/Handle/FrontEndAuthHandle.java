package org.mdk.battle.mysqlagent.Handle;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.regex.Pattern;

import org.mdk.net.nio.NIOHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.mdk.net.nio.*;
import org.mdk.protocol.mysql.tools.*;
import org.mdk.protocol.mysql.*;
import org.mdk.protocol.mysql.packet.*;
import org.mdk.battle.mysqlagent.*;
import org.mdk.battle.mysqlagent.protocol.*;
import org.mdk.battle.mysqlagent.beans.*;



public class FrontEndAuthHandle implements NIOHandler<FrontEndSession> {
	private static final byte[] AUTH_OK = new byte[] { 7, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0 };

	public static final FrontEndAuthHandle INSTANCE = new FrontEndAuthHandle();

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

			for(Object o:AllConfigure.INSTANCE.FrontEndUserBeans.keySet()){       
	            FrontEndUserBean value = (FrontEndUserBean) AllConfigure.INSTANCE.FrontEndUserBeans.get(o);   
	            if( (value.getName().equals(auth.user))
	                 && (value.getPassword().equals(auth.password)) ){
	            	bAuthSuccess = true;
	            	break;
	            }
	        } 
			
			if(bAuthSuccess){
				session.responseOK();
				session.setCurNIOHandler(FrontEndDefaultHandle.INSTANCE);
			}else{
				failure(session,MysqlErrorCode.ER_BAD_DB_ERROR,"databas auth failed");
			}
            

		} catch (Throwable e) {
			
		}
	}



	@Override
	public void onSocketWrite(FrontEndSession session) throws IOException {
		session.writeToChannel();
	}

	@Override
	public void onSocketClosed(FrontEndSession userSession, boolean normal) {
		
	}

	@Override
	public void onConnect(SelectionKey curKey, FrontEndSession session, boolean success, String msg) throws IOException {
		// TODO Auto-generated method stub
	}

	@Override
	public void onWriteFinished(FrontEndSession session) throws IOException {
		
		session.sessionBuffer.flip();
		session.change2ReadOpts();
	}
	
	private void failure(FrontEndSession session, int errno, String info) throws IOException {
		ErrorPacket errorPacket = new ErrorPacket();
		errorPacket.packetId = 2;
		errorPacket.errno = errno;
		errorPacket.message = info;
		session.responseOKOrError(errorPacket);
	}
}
