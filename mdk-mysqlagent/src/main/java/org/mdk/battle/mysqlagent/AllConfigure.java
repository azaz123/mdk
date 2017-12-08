package org.mdk.battle.mysqlagent;

import java.util.HashMap;
import java.util.Map;


import org.mdk.battle.mysqlagent.beans.*;
import org.mdk.battle.mysqlagent.task.MysqlTaskChainManager;



public class AllConfigure {
	public static final AllConfigure INSTANCE = new AllConfigure();
	public Map<String, MysqlMetaBeans> MysqlBeans = new HashMap<String, MysqlMetaBeans>();
	public Map<String, FrontEndUserBean> FrontEndUserBeans = new HashMap<String, FrontEndUserBean>();
}
