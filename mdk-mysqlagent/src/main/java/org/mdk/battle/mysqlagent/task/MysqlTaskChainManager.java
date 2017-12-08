package org.mdk.battle.mysqlagent.task;

import org.mdk.battle.mysqlagent.task.AbstractTask;



import org.mdk.battle.mysqlagent.cmd.IMysqlCmd;
import org.mdk.battle.mysqlagent.cmd.*;
import org.mdk.battle.mysqlagent.*;

public class MysqlTaskChainManager {
	public static final MysqlTaskChainManager INSTANCE = new MysqlTaskChainManager();
    public static AbstractTask CreateTaskChain(IMysqlCmd Cmd,FrontEndSession fSession,BackEndSession bSession,int param){
    	AbstractTask rTask = null;
    	if(Cmd instanceof DefaultSingleCmd ){
    		if(param == 1){
    			BackEndLoginTask rLoginTask = new BackEndLoginTask();
    			rLoginTask.currentcmd = Cmd;
    			rLoginTask.isLastTask = true;
    			rLoginTask.Context.FSession = fSession;
    			rLoginTask.Context.BSession = bSession;
    			rLoginTask.setCallBack(
    		    		(optSession, sender, exeSucces, retVal) -> {
    		    			//nothing to do
    		    		});
    			rLoginTask.setBeans(AllConfigure.INSTANCE.MysqlBeans.get("firstmysql"));
    			rLoginTask.isUseShareBuffer = false;
    			rTask = rLoginTask;
    		}else if(param == 2){
    			FrontEndAuthTask rAuthTask = new FrontEndAuthTask();
    			rAuthTask.currentcmd = Cmd;
    			rAuthTask.isLastTask = true;
    			rAuthTask.Context.FSession = fSession;
    			rAuthTask.Context.BSession = bSession;
    			rAuthTask.setCallBack(
    		    		(optSession, sender, exeSucces, retVal) -> {
    		    			//nothing to do
    		    		});
    			rAuthTask.isUseShareBuffer = false;
    			rTask = rAuthTask;
    		}
    	}else if(Cmd instanceof PassThroughCmd){
    		BackendSynStateTask Task1 = new BackendSynStateTask();
    		BackendSynchemaTask Task2 = new BackendSynchemaTask();
    		PassThroughTask     Task3 = new PassThroughTask();
    		
    		Task1.currentcmd = Cmd;
    		Task1.isLastTask = false;
    		Task1.Context.FSession = fSession;
    		Task1.Context.BSession = bSession;
    		Task1.setCallBack(
		    		(optSession, sender, exeSucces, retVal) -> {
		    			//nothing to do
		    		});
    		Task1.isUseShareBuffer = false;
    		
    		Task2.currentcmd = Cmd;
    		Task2.isLastTask = false;
    		Task2.Context.FSession = fSession;
    		Task2.Context.BSession = bSession;
    		Task2.setCallBack(
		    		(optSession, sender, exeSucces, retVal) -> {
		    			//nothing to do
		    		});
    		Task2.isUseShareBuffer = false;
    		
    		Task3.currentcmd = Cmd;
    		Task3.isLastTask = true;
    		Task3.Context.FSession = fSession;
    		Task3.Context.BSession = bSession;
    		Task3.setCallBack(
		    		(optSession, sender, exeSucces, retVal) -> {
		    			//nothing to do
		    		});
    		Task3.isUseShareBuffer = true;
    		
    		Task1.nextTask = Task2;
    		Task2.nextTask = Task3;
    		
    		rTask= Task1;
    		
    	}
    	return rTask;
    }
}
