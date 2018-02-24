package org.mdk.battle.mysqlagent.opencapacity;
import java.util.HashMap;
import java.util.Map;

import org.mdk.battle.mysqlagent.AllConfigure;
import org.mdk.battle.mysqlagent.beans.MysqlMetaBeans;
import org.mdk.battle.mysqlagent.cmd.CmdRunTime;
import org.mdk.battle.mysqlagent.cmd.DefaultSingleCmd;
import org.mdk.battle.mysqlagent.task.MysqlTaskChainManager;

public class ComChain implements OneCom {

	@Override
	public void doAction(MetaDataManager data) {
		// TODO Auto-generated method stub
		HashMap<String,OneCom> tmpComList = AllConfigure.INSTANCE.OCBeans.get("pre");
		for(Map.Entry<String,OneCom> entry : tmpComList.entrySet())      
		{   
			OneCom tmpCom = entry.getValue();
			tmpCom.doAction(data);
		}
	}

}
