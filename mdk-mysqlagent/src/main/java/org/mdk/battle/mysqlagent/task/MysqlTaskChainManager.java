package org.mdk.battle.mysqlagent.task;

import org.mdk.battle.mysqlagent.task.AbstractTask;



import org.mdk.battle.mysqlagent.cmd.IMysqlCmd;
import org.mdk.battle.mysqlagent.cmd.*;
import org.mdk.battle.mysqlagent.*;
import org.mdk.battle.mysqlagent.beans.*;

public class MysqlTaskChainManager {
	public static final MysqlTaskChainManager INSTANCE = new MysqlTaskChainManager();
    public static CmdRunTime CreateTaskChain(IMysqlCmd Cmd,FrontEndSession fSession,BackEndSession bSession,int param,Object AttachmentInfo){
    	AbstractTask rTask = null;
    	CmdRunTime CmdInfo = new CmdRunTime();
    	CmdInfo.currentcmd = Cmd;
    	CmdInfo.Context.FSession = fSession;
		CmdInfo.Context.BSession = bSession;
    	if(Cmd instanceof DefaultSingleCmd ){
    		if(param == 1){
    			BackEndLoginTask rLoginTask = new BackEndLoginTask();
    			rLoginTask.CmdInfo = CmdInfo;
    			rLoginTask.isLastTask = true;
    			rLoginTask.setCallBack(
    		    		(optSession, sender, exeSucces, retVal) -> {
    		    			//nothing to do
    		    		});
    			rLoginTask.setBeans((MysqlMetaBeans)AttachmentInfo);
    			rLoginTask.isUseShareBuffer = false;
    			rTask = rLoginTask;
    		}else if(param == 2){
    			FrontEndAuthTask rAuthTask = new FrontEndAuthTask();
    			rAuthTask.CmdInfo= CmdInfo;
    			rAuthTask.isLastTask = true;
    			rAuthTask.setCallBack(
    		    		(optSession, sender, exeSucces, retVal) -> {
    		    			//nothing to do
    		    		});
    			rAuthTask.isUseShareBuffer = false;
    			rTask = rAuthTask;
    		}else if(param == 3){
    			BackendSynStateTask Task1 = new BackendSynStateTask();
        		BackendSynchemaTask Task2 = new BackendSynchemaTask();
        		SpecSqlTask     Task3 = new SpecSqlTask();
        		
        		CmdInfo.Context.FSession.currentCmdInfo = CmdInfo;
        		Task1.CmdInfo = CmdInfo;
        		Task1.isLastTask = false;
        		Task1.setCallBack(
    		    		(optSession, sender, exeSucces, retVal) -> {
    		    			//nothing to do
    		    		});
        		Task1.isUseShareBuffer = false;
        		
        		Task2.SetDatabase(CmdInfo.Context.BSession.MysqlBeans.GetDatabase());
        		Task2.CmdInfo = CmdInfo;
        		Task2.isLastTask = false;
        		Task2.setCallBack(
    		    		(optSession, sender, exeSucces, retVal) -> {
    		    			//nothing to do
    		    		});
        		Task2.isUseShareBuffer = false;
        		
        		Task3.SetSql((String)AttachmentInfo);
        		Task3.CmdInfo = CmdInfo;
        		Task3.isLastTask = true;
        		Task3.setCallBack(
    		    		(optSession, sender, exeSucces, retVal) -> {
    		    			//nothing to do
    		    		});
        		Task3.isUseShareBuffer = false;
        		
        		Task1.nextTask = Task2;
        		Task2.nextTask = Task3;
        		
        		rTask= Task1;
    		}
    	}else if(Cmd instanceof PassThroughCmd){
    		if(param == 1) {
    			rTask = new NullTask();
    			rTask.CmdInfo = CmdInfo;
    			rTask.isLastTask = true;
    			rTask.setCallBack(
    		    		(optSession, sender, exeSucces, retVal) -> {
    		    			//nothing to do
    		    		});
    			rTask.isUseShareBuffer = true;
    		}else if (param == 2) {
    			BackendSynStateTask Task1 = new BackendSynStateTask();
        		BackendSynchemaTask Task2 = new BackendSynchemaTask();
        		PassThroughTask     Task3 = new PassThroughTask();
        		
        		CmdInfo.Context.FSession.currentCmdInfo = CmdInfo;
        		Task1.CmdInfo = CmdInfo;
        		Task1.isLastTask = false;
        		Task1.setCallBack(
    		    		(optSession, sender, exeSucces, retVal) -> {
    		    			//nothing to do
    		    		});
        		Task1.isUseShareBuffer = false;
        		
        		Task2.SetDatabase(CmdInfo.Context.BSession.MysqlBeans.GetDatabase());
        		Task2.CmdInfo = CmdInfo;
        		Task2.isLastTask = false;
        		Task2.setCallBack(
    		    		(optSession, sender, exeSucces, retVal) -> {
    		    			//nothing to do
    		    		});
        		Task2.isUseShareBuffer = false;
        		
        		Task3.CmdInfo = CmdInfo;
        		Task3.isLastTask = true;
        		Task3.setCallBack(
    		    		(optSession, sender, exeSucces, retVal) -> {
    		    			//nothing to do
    		    		});
        		Task3.isUseShareBuffer = true;
        		
        		Task1.nextTask = Task2;
        		Task2.nextTask = Task3;
        		
        		rTask= Task1;
    		}
    		
    		
    	}
    	CmdInfo.headTask = rTask;
    	return CmdInfo;
    }
}
