package org.mdk.battle.mysqlagent.task;

import org.mdk.battle.mysqlagent.cmd.IMysqlCmd;

import java.io.IOException;

import org.mdk.battle.mysqlagent.BackEndSession;
import org.mdk.battle.mysqlagent.attr.CmdAttr;
import org.mdk.battle.mysqlagent.cmd.*;
import org.mdk.battle.mysqlagent.task.TaskCallBack;
import org.mdk.net.nio.NIOHandler;
import org.mdk.net.nio.Session;
import org.mdk.net.nio.netBuffer;

public abstract class AbstractTask {
	public boolean isLastTask;
	public CmdRunTime CmdInfo;
	public AbstractTask nextTask;
	private TaskCallBack CallBack;
	public  netBuffer newBuffer;
	public  netBuffer oldBackEndBuffer;
	public  boolean   isUseShareBuffer;
	public abstract void Excute() throws IOException;
	protected abstract void finished(boolean success) throws IOException;
	public void setCallBack(TaskCallBack CallBack){
		this.CallBack = CallBack;
	}
	
	public void setCmdStatus(CmdStatus s){
		CmdInfo.CmdAttrMap.put(CmdAttr.CMD_ATTR_RUN_STATUS.getKey(), s);
	}
	
    public boolean ifUseShareBuffer(){
    	return isUseShareBuffer;
    }

    public void setShareBuffer(){
    	if(ifUseShareBuffer()){
    		oldBackEndBuffer = CmdInfo.Context.BSession.sessionBuffer;
    		CmdInfo.Context.BSession.sessionBuffer = CmdInfo.Context.FSession.sessionBuffer;
    	}
    }
    
    public void revertShareBuffer(){
    	if(ifUseShareBuffer()){
    		CmdInfo.Context.BSession.sessionBuffer = oldBackEndBuffer;
    	}
    }

}
