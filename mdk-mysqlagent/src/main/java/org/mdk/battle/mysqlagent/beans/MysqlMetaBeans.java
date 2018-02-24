package org.mdk.battle.mysqlagent.beans;

import java.util.*;

public class MysqlMetaBeans  {
    public String ip;
    public int nPort;
    public String sUserName;
    public String sPassword;
    public String sName;
    public String sDatabase;
    public int    nDefaultNodeNum;
    public int    nMaxNodeNum;
    public List<RunTimeMysqlMetaBeans> SessionMapObj = new ArrayList<RunTimeMysqlMetaBeans>();
    
    public String GetName(){
    	return sName;
    }
    
    public void SetName(String sName){
    	this.sName = sName;
    }
    
    public String GetDatabase(){
    	return sDatabase;
    }
    
    public void SetDatabase(String sDatabase){
    	this.sDatabase = sDatabase;
    }
    
    public String GetIp(){
    	return ip;
    }
    
    public void SetIp(String ip){
    	this.ip = ip;
    }
    
    public int GetPort(){
    	return nPort;
    }
    
    public void SetPort(int nPort){
    	this.nPort = nPort;
    }
    
    public int GetDefNum(){
    	return nDefaultNodeNum;
    }
    
    public void SetDefNum(int nDefaultNodeNum){
    	this.nDefaultNodeNum = nDefaultNodeNum;
    }
    
    public int GetMaxNum(){
    	return nMaxNodeNum;
    }
    
    public void SetMaxNum(int nMaxNodeNum){
    	this.nMaxNodeNum = nMaxNodeNum;
    }
    
    public String GetUserName(){
    	return sUserName;
    }
    
    public void SetUserName(String sUserName){
    	this.sUserName = sUserName;
    }
    
    public String GetPassword(){
    	return sPassword;
    }
    
    public void SetPassword(String sPassword){
    	this.sPassword = sPassword;
    }
    

}
