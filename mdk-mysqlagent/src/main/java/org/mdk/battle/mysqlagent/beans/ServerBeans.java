package org.mdk.battle.mysqlagent.beans;

public class ServerBeans {
    public String ip;
    public int nPort;
    
    public void setIP(String ip) {
    	this.ip = ip;   
    }
    
    public String getIP() {
    	return ip;
    }
    
    public void setPort(int nPort) {
    	this.nPort = nPort;    
    }
    
    public int getPort() {
    	return nPort;
    }
}
