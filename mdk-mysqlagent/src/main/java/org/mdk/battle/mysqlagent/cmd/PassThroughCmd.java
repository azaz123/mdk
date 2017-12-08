package org.mdk.battle.mysqlagent.cmd;

import java.io.IOException;

import org.mdk.battle.mysqlagent.Handle.CmdProxyHandle;
public class PassThroughCmd implements IMysqlCmd {
	public static final PassThroughCmd INSTANCE = new PassThroughCmd();
	@Override
	public void OnCmdResponse(CmdContext Context, boolean success) throws IOException{
		// TODO Auto-generated method stub
		if(success){
			System.out.println("success");
		}else{
			System.out.println("fail");
		}
		Context.FSession.setCurNIOHandler(CmdProxyHandle.INSTANCE);
		Context.FSession.sessionBuffer.readIndex = Context.FSession.sessionBuffer.writeIndex;
		Context.FSession.sessionBuffer.flip();
		Context.FSession.writeToChannel();
	}

	@Override
	public boolean Excute(CmdContext Context) {
		// TODO Auto-generated method stub
		return false;
	}

}
