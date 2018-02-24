package org.mdk.battle.mysqlagent.cmd;

import java.io.IOException;

import org.mdk.battle.mysqlagent.Handle.FrontEndDefaultHandle;
import org.mdk.battle.mysqlagent.attr.CmdAttr;


//this cmd don't feedback anything to frontend
public class DefaultSingleCmd implements IMysqlCmd {
	public static final DefaultSingleCmd INSTANCE = new DefaultSingleCmd();
	@Override
	public void OnCmdResponse(CmdRunTime CR,boolean success) throws IOException {
		// TODO Auto-generated method stub
		if(success){
			setCmdStatus(CR,CmdStatus.OVER_NORMAL);
			if(CR.selfAdmiral != null) {
				CR.selfAdmiral.SetOneActionOver(true);
			}
			System.out.println("success");
		}else{
			setCmdStatus(CR,CmdStatus.OVER_ABNORMAL);
			if(CR.selfAdmiral != null) {
				CR.selfAdmiral.SetOneActionOver(false);
			}
			System.out.println("fail");
		}
		

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
