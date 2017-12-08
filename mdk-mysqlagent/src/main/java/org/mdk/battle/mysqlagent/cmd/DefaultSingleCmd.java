package org.mdk.battle.mysqlagent.cmd;

import org.mdk.battle.mysqlagent.Handle.FrontEndDefaultHandle;


//this cmd don't feedback anything to frontend
public class DefaultSingleCmd implements IMysqlCmd {
	public static final DefaultSingleCmd INSTANCE = new DefaultSingleCmd();
	@Override
	public void OnCmdResponse(CmdContext Context,boolean success) {
		// TODO Auto-generated method stub
		if(success){
			System.out.println("success");
		}else{
			System.out.println("fail");
		}
		

	}

	@Override
	public boolean Excute(CmdContext Context) {
		// TODO Auto-generated method stub
		return false;
	}

}
