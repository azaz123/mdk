package org.mdk.battle.mysqlagent.cmd;

import java.io.IOException;

import org.mdk.battle.mysqlagent.Handle.CmdProxyHandle;
import org.mdk.battle.mysqlagent.attr.CmdAttr;
public class PassThroughCmd implements IMysqlCmd {
	public static final PassThroughCmd INSTANCE = new PassThroughCmd();
	@Override
	public void OnCmdResponse(CmdRunTime CR, boolean success) throws IOException{
		// TODO Auto-generated method stub
		if(success){
			setCmdStatus(CR,CmdStatus.OVER_NORMAL);
			System.out.println("success");
		}else{
			setCmdStatus(CR,CmdStatus.OVER_ABNORMAL);
			System.out.println("fail");
		}
		CR.Context.BSession.clearReadWriteOpts();
		CR.Context.FSession.setCurNIOHandler(CmdProxyHandle.INSTANCE);
		CR.Context.FSession.sessionBuffer.flip();
		CR.Context.FSession.writeToChannel();
	}

	@Override
	public boolean Excute(CmdRunTime CR) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void setCmdStatus(CmdRunTime CR,CmdStatus s){
		CR.CmdAttrMap.put(CmdAttr.CMD_ATTR_RUN_STATUS.getKey(), s);
	}

}
