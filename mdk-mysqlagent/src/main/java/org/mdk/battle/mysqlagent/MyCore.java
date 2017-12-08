package org.mdk.battle.mysqlagent;

import org.mdk.net.nio.*;

import java.io.IOException;

import org.mdk.battle.mysqlagent.*;
import org.mdk.battle.mysqlagent.beans.*;
import org.mdk.battle.mysqlagent.cmd.PassThroughCmd;
import org.mdk.battle.mysqlagent.*;
import org.mdk.battle.mysqlagent.task.*;
import org.mdk.battle.mysqlagent.cmd.*;

public class MyCore {
	public static void main(String[] args) 
	{
		try{
			//business mysql config
			MysqlMetaBeans mysqlbeans = new MysqlMetaBeans();
			mysqlbeans.SetIp("123.206.135.216");
			mysqlbeans.SetPort(3307);
			mysqlbeans.SetUserName("root");
			mysqlbeans.SetPassword("123456");
			mysqlbeans.SetName("firstmysql");
			mysqlbeans.SetDatabase("hrz1");
			
			FrontEndUserBean userbean = new FrontEndUserBean();
			userbean.setName("hrz");
			userbean.setPassword("123456789");
			AllConfigure.INSTANCE.FrontEndUserBeans.put("hrz", userbean);
			AllConfigure.INSTANCE.MysqlBeans.put("firstmysql", mysqlbeans);
			
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
			Acceptor.startServerChannel("127.0.0.1", 8080);
			nioRuntime.INSTANCE.setAcceptor(Acceptor);
			nioRuntime.INSTANCE.setReactorInfo(RContexts, cpus);
			nioRuntime.INSTANCE.start();
			
			//connect backendmysql
			AbstractTask HeadTask = MysqlTaskChainManager.INSTANCE.CreateTaskChain(DefaultSingleCmd.INSTANCE, null,null , 1);
			HeadTask.Excute();
		}catch(Exception e){
			
		}
			
		
		
	}
}
