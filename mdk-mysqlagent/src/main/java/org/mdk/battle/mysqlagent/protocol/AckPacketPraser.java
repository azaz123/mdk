package org.mdk.battle.mysqlagent.protocol;
import org.mdk.battle.mysqlagent.attr.CmdAttr;
import org.mdk.battle.mysqlagent.cmd.*;
import org.mdk.protocol.mysql.tools.MysqlPrase;

import org.mdk.protocol.mysql.packet.*;
import org.mdk.protocol.mysql.tools.*;

import java.io.IOException;

public class AckPacketPraser {
    public static void Excute(CmdRunTime CmdInfo) throws IOException{
    	boolean ifContinueRead = false;
    	while(true){
    		CmdInfo.Context.BSession.readFromChannel();
    		switch (MysqlPrase.resolveMySQLPackage(CmdInfo.Context.BSession.sessionBuffer,
    				CmdInfo.Context.BSession.curMSQLPackgInf,
    				true)) {
			case Full:
				ifContinueRead = false;
				if(CmdInfo.Context.BSession.sessionBuffer.readIndex != CmdInfo.Context.BSession.sessionBuffer.writeIndex){
					ifContinueRead = true;
				}
				CurrentStatusParase(CmdInfo);
				break;
			case LongHalfPacket:
				
				ifContinueRead = false;
				break;
			case ShortHalfPacket:

				ifContinueRead = false;
				break;
    		}
    		
    		if(ifContinueRead){
    			continue;
    		}else{
    			break;
    		}
    	}
    		
    }
    /*
     * FirstRead, 
	   ResultsetRead, 
	   ColumnInfoRead,
	   ColumnDataRead,
	   ColumnDataEndFlagRead,
	   RawDataRead,
	   RawDataEndFlagRead
     */
    
    public static void CurrentStatusParase(CmdRunTime CmdInfo) throws IOException{
    	MySQLPackageInf PkInfo = CmdInfo.Context.BSession.curMSQLPackgInf;
    	switch(CmdInfo.Tracker.currentstatus){
    	case FirstRead:
    		if( (PkInfo.pkgType ==  MySQLPacket.OK_PACKET) ){
    			CmdInfo.CmdAttrMap.put(CmdAttr.CMD_ATTR_RUN_STATUS.getKey(), CmdStatus.OVER_NORMAL);
    			return;
    		}else if((PkInfo.pkgType ==  MySQLPacket.ERROR_PACKET)) {
    			CmdInfo.CmdAttrMap.put(CmdAttr.CMD_ATTR_RUN_STATUS.getKey(), CmdStatus.OVER_ABNORMAL);
    			return;
    		}else{
    			CmdInfo.Tracker.currentstatus = AckPacketPraserStatus.ResultsetRead;
    			CmdInfo.Tracker.substatus = AckPacketPraserStatus.ColumnInfoRead;
    		}
    	case ResultsetRead:
    		if(!CmdInfo.CmdAttrMap.containsKey(CmdAttr.CMD_ATTR_RESULTSET_ACCEPT_OVER.getKey())){
    			CmdInfo.CmdAttrMap.put(CmdAttr.CMD_ATTR_RESULTSET_ACCEPT_OVER.getKey(), true);
    		}
    		SubStatusParase(CmdInfo);
    		break;
    	}
    		
    }
    
    public static void SubStatusParase(CmdRunTime CmdInfo) throws IOException{
    	MySQLPackageInf PkInfo = CmdInfo.Context.BSession.curMSQLPackgInf;
    	switch(CmdInfo.Tracker.substatus){
    	case ColumnInfoRead:
    		CmdInfo.Tracker.substatus = AckPacketPraserStatus.ColumnDataRead;
    		break;
    	case ColumnDataRead:
    		if(PkInfo.pkgType !=  MySQLPacket.EOF_PACKET){
    			break;
    		}
    		CmdInfo.Tracker.substatus = AckPacketPraserStatus.ColumnDataEndFlagRead;
    	case ColumnDataEndFlagRead:
    		CmdInfo.Tracker.substatus = AckPacketPraserStatus.RawDataRead;
    		break;
    	case RawDataRead:
    		if(PkInfo.pkgType !=  MySQLPacket.EOF_PACKET){
    			break;
    		}
    		CmdInfo.Tracker.substatus = AckPacketPraserStatus.RawDataEndFlagRead;
    	case RawDataEndFlagRead:
    		CmdInfo.CmdAttrMap.remove(CmdAttr.CMD_ATTR_RESULTSET_ACCEPT_OVER.getKey());
    		CmdInfo.Tracker.currentstatus = AckPacketPraserStatus.FirstRead;
    		break;
    	}
    		
    }
}
