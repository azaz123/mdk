package org.mdk.battle.mysqlagent.armada;

import org.mdk.battle.mysqlagent.cmd.*;
import org.mdk.battle.mysqlagent.task.AbstractTask;
import org.mdk.battle.mysqlagent.task.MysqlTaskChainManager;

import org.mdk.protocol.mysql.packet.*;

import org.mdk.protocol.mysql.*;

import org.mdk.battle.mysqlagent.*;
import org.mdk.battle.mysqlagent.beans.*;
import java.io.IOException;

import java.util.*;
import org.mdk.battle.mysqlagent.opencapacity.*;

public class Admiral {
	private PlanType Type;
	private int TotalCmdNum = 0;
	private int OverCmdCounter = 0;
	private int SucessCmdCounter = 0;
	private List<CmdRunTime> tCmdList = new ArrayList<CmdRunTime>();
	private CmdRunTime ExitCmd;
	//ifmotify:sql:datanode;ifmotify:sql:datanode;
	public boolean DoAction(FrontEndSession Commander,PlanData CmdStr) throws IOException {
		CmdRunTime CmdTmp = null;
		MysqlMetaBeans beanstmp;
		ArrayList<BackEndSession> allbsession;
		BackEndSession bsession = null;
		String strType = CmdStr.Type;
		SetType(strType);
		for(int i =0;i<CmdStr.subPlanInfoList.size();i++){
			subPlanInfo subPlanInfo = CmdStr.subPlanInfoList.get(i);
    		if(subPlanInfo.Type.equals("0")){
    			//passthrough
    			beanstmp = AllConfigure.INSTANCE.MysqlBeans.get(subPlanInfo.NodeName);
    			for(int j=0;j<beanstmp.SessionMapObj.size();j++) {
    				bsession = BackEndSessionManager.INSTANCE.getSessionPool().get(beanstmp.SessionMapObj.get(j));
    				if(bsession != null) {
    					break;
    				}
    			}
    			if(Type == PlanType.NORMAL_PASSTHROUGH) {
    				CmdTmp = MysqlTaskChainManager.INSTANCE.CreateTaskChain(PassThroughCmd.INSTANCE, Commander,bsession , 2,null);
    			}else if(Type == PlanType.MUTICMD_PASSTHROUGH) {
    				CmdTmp = MysqlTaskChainManager.INSTANCE.CreateTaskChain(PassThroughCmd.INSTANCE, Commander,bsession , 1,null);
    			}
    			
    			CmdTmp.selfAdmiral = this;
    			ExitCmd = CmdTmp;
    		}else if(subPlanInfo.Type.equals("1")){
    			//non passthrough
    			beanstmp = AllConfigure.INSTANCE.MysqlBeans.get(subPlanInfo.NodeName);
    			for(int j=0;j<beanstmp.SessionMapObj.size();j++) {
    				bsession = BackEndSessionManager.INSTANCE.getSessionPool().get(beanstmp.SessionMapObj.get(j));
    				if(bsession != null) {
    					break;
    				}
    			}
    			CmdTmp = MysqlTaskChainManager.INSTANCE.CreateTaskChain(DefaultSingleCmd.INSTANCE, Commander,bsession , 3,subPlanInfo.Sql);
    			CmdTmp.selfAdmiral = this;
    			tCmdList.add(CmdTmp);
    			TotalCmdNum++;
    		}
    	}
		if(Type == PlanType.NORMAL_PASSTHROUGH) {
			ExitCmd.headTask.Excute();
		}else {
			for(CmdRunTime CmdElm : tCmdList){
				CmdElm.headTask.Excute();
			}
		}
		
		return true;
			
	}
	
	public void SetOneActionOver(boolean status) throws IOException {
		OverCmdCounter++;
		if(status) {
			SucessCmdCounter++;
		}
		if(OverCmdCounter == TotalCmdNum) {
			JudgeAllAction();
		}
		
	}
	
	private void JudgeAllAction() throws IOException{
		if(TotalCmdNum == SucessCmdCounter) {
			if (Type == PlanType.MUTICMD_PASSTHROUGH) {
				ExitCmd.Context.FSession.sessionBuffer.reset();
				OKPacket packet = new OKPacket();
		        packet.packetId = 1;
		        packet.affectedRows = 1;
		        packet.serverStatus = 2;
		        packet.write(ExitCmd.Context.FSession.sessionBuffer);
		        ExitCmd.Context.FSession.sessionBuffer.readIndex = ExitCmd.Context.FSession.sessionBuffer.writeIndex;
			}
			
		}else {
			if (Type == PlanType.MUTICMD_PASSTHROUGH) {
				ExitCmd.Context.FSession.sessionBuffer.reset();
				ErrorPacket errPkg = new ErrorPacket();
				errPkg.packetId = 1;
				errPkg.errno  = MysqlErrorCode.ER_UNKNOWN_ERROR;
				errPkg.message = "unknow error" ;
				errPkg.write(ExitCmd.Context.FSession.sessionBuffer);
		        ExitCmd.Context.FSession.sessionBuffer.readIndex = ExitCmd.Context.FSession.sessionBuffer.writeIndex;
			}
		}
		ExitCmd.headTask.Excute();
	}
	
	private void SetType(String Type) throws IOException{
		if(Type.equals("1")) {
			this.Type = PlanType.NORMAL_PASSTHROUGH;
		}else if(Type.equals("2")) {
			this.Type = PlanType.MUTICMD_PASSTHROUGH;
		}
	}
	
}
