package org.mdk.battle.mysqlagent;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;


import org.mdk.battle.mysqlagent.beans.*;
import org.mdk.battle.mysqlagent.task.MysqlTaskChainManager;
import org.mdk.battle.mysqlagent.opencapacity.*;



public class AllConfigure {
	public static final AllConfigure INSTANCE = new AllConfigure();
	public ServerBeans SerBeans;
	public Map<String, MysqlMetaBeans> MysqlBeans = new HashMap<String, MysqlMetaBeans>();
	public Map<String, FrontEndUserBean> FrontEndUserBeans = new HashMap<String, FrontEndUserBean>();
	public Map<String, HashMap<String,OneCom>> OCBeans = new HashMap<String, HashMap<String,OneCom>>();
}
