package org.mdk.battle.mysqlagent;

import org.mdk.net.nio.*;


import java.io.IOException;

import org.mdk.battle.mysqlagent.util.*;
import org.mdk.battle.mysqlagent.*;
import org.mdk.battle.mysqlagent.beans.*;
import org.mdk.battle.mysqlagent.cmd.PassThroughCmd;
import org.mdk.battle.mysqlagent.*;
import org.mdk.battle.mysqlagent.task.*;
import org.mdk.battle.mysqlagent.cmd.*;
import org.mdk.battle.mysqlagent.opencapacity.*;
import java.util.*;

public class MyCore {
	
	public static void loadConfig(ConfigEnum configEnum) throws IOException {
        String fileName = configEnum.getFileName();
        
        if(configEnum.getClazz() == MysqlMetaBeansList.class) {
        	MysqlMetaBeansList  beanslist = (MysqlMetaBeansList)YamlUtil.load(fileName, configEnum.getClazz());

			for(int i =0 ; i<beanslist.getBeansList().size();i++) {
				List<MysqlMetaBeans> tmp = beanslist.getBeansList();
				AllConfigure.INSTANCE.MysqlBeans.put(tmp.get(i).GetName(),tmp.get(i));
			}
        }else if(configEnum.getClazz() == ServerBeans.class) {
        	AllConfigure.INSTANCE.SerBeans = (ServerBeans)YamlUtil.load(fileName, configEnum.getClazz());
        }else if(configEnum.getClazz() == FrontEndUserBean.class) {
        	FrontEndUserBean userbean = (FrontEndUserBean)YamlUtil.load(fileName, configEnum.getClazz());
        	AllConfigure.INSTANCE.FrontEndUserBeans.put(userbean.getName(),userbean);
        }else if(configEnum.getClazz() == ComBeansList.class) {
        	ComBeansList  combeanslist = (ComBeansList)YamlUtil.load(fileName, configEnum.getClazz());
			for(int i =0 ; i<combeanslist.getComBeansList().size();i++) {
				List<ComBean> tmp = combeanslist.getComBeansList();
				if(tmp.get(i).getType().equals("python")) {
					PyComImpl Com = new PyComImpl();
					Com.Py = tmp.get(i).getPath();
					HashMap<String,OneCom> elm = new HashMap<String,OneCom>();
					elm.put(tmp.get(i).getName(), Com);
					AllConfigure.INSTANCE.OCBeans.put("pre", elm);
				}
				
			}
        }
    }
	
	public static void loadAll() throws IOException {
        loadConfig(ConfigEnum.MYSQLBEANSLIST);
	    loadConfig(ConfigEnum.SERVER);
	    loadConfig(ConfigEnum.USER);
	    loadConfig(ConfigEnum.COMBEANS);
	}
	
	public static void main(String[] args) 
	{
		try{
			//business mysql config

			loadAll();
			
			
			/**
			 * nio = reactor + acceptor
			 * one server has one acceptor and multi-reactor
			 * reactor num = cpu num  ---  this is just one suggestion
			 * our nio framework
			 * 1： create some reactorcontext which is bind to one reactor thread
			 *    reactorcontext has 2 params: bufpoolsize and sessionmanager
			 * 2: create one reactorcontext which is bind to acceptor thread
			 *    acceptor is Derived-Class of reactor
			 * 3: acceptor need invoke startServerChannel api to config server info
			 * 4: nioRuntime is the container of some nio component
			 *    nioRuntime manange these component which is include acceptor and reactor
			 * 5: nioRuntime setAcceptor,setReactor Info and start these nio component
			 *
			 * @author hrz
			 *
			 */
			int cpus = Runtime.getRuntime().availableProcessors();
			ReactorContext[] RContexts = new ReactorContext[cpus];
			ReactorContext AcceptorContext = new ReactorContext<FrontEndSession>(1024,FrontEndSessionMaganer.INSTANCE);
			NIOAcceptor Acceptor = new NIOAcceptor(AcceptorContext,cpus);
			for(int i =0;i<cpus;i++){
				RContexts[i] = new ReactorContext<FrontEndSession>(1024,FrontEndSessionMaganer.INSTANCE);
			}
			Acceptor.startServerChannel(AllConfigure.INSTANCE.SerBeans.getIP(), AllConfigure.INSTANCE.SerBeans.getPort());
			nioRuntime.INSTANCE.setAcceptor(Acceptor);
			nioRuntime.INSTANCE.setReactorInfo(RContexts, cpus);
			nioRuntime.INSTANCE.start();
			
			//connect backendmysql
			
			for(Map.Entry<String, MysqlMetaBeans> entry : AllConfigure.INSTANCE.MysqlBeans.entrySet())      
			{   
			    MysqlMetaBeans beanstmp = entry.getValue();
			    System.out.println(beanstmp.GetDefNum());
				for(int i=0;i<beanstmp.GetDefNum();i++) {
				    CmdRunTime HeadCmd = MysqlTaskChainManager.INSTANCE.CreateTaskChain(DefaultSingleCmd.INSTANCE, null,null , 1,beanstmp);
				    HeadCmd.headTask.Excute();
				    System.out.println("count");
			    }
			}
            
			
			System.out.println("count2");
			
		}catch(Exception e){
			e.printStackTrace();
		}
			
		
		
	}
}
