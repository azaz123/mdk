package org.mdk.battle.mysqlagent.task;

import org.mdk.battle.mysqlagent.cmd.IMysqlCmd;

import java.io.IOException;

import org.mdk.battle.mysqlagent.BackEndSession;
import org.mdk.battle.mysqlagent.cmd.CmdContext;
import org.mdk.battle.mysqlagent.task.TaskCallBack;
import org.mdk.net.nio.NIOHandler;
import org.mdk.net.nio.Session;
import org.mdk.net.nio.netBuffer;

public abstract class AbstractTask {
	public boolean isLastTask;
	public IMysqlCmd currentcmd;
	public AbstractTask nextTask;
	public CmdContext Context = new CmdContext();
	private TaskCallBack CallBack;
	public  netBuffer newBuffer;
	public  netBuffer oldBackEndBuffer;
	public  boolean   isUseShareBuffer;
	public abstract void Excute() throws IOException;
	protected abstract void finished(boolean success) throws IOException;
	public void setCallBack(TaskCallBack CallBack){
		this.CallBack = CallBack;
	}
	
    public boolean ifUseShareBuffer(){
    	return isUseShareBuffer;
    }

    public void setShareBuffer(){
    	if(ifUseShareBuffer()){
    		oldBackEndBuffer = Context.BSession.sessionBuffer;
    		Context.BSession.sessionBuffer = Context.FSession.sessionBuffer;
    	}
    }
    
    public void revertShareBuffer(){
    	if(ifUseShareBuffer()){
    		Context.BSession.sessionBuffer = oldBackEndBuffer;
    	}
    }

}
