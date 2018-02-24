package org.mdk.battle.mysqlagent.beans;

import java.util.ArrayList;
import java.util.List;

public class ComBean {
	public String Name;
	public String Type;
	public String Path;
	   
	public void setName(String Name) {
		this.Name = Name;   
	}
	   
	public String getName() {
	   	 return this.Name;
	}
	   
	public void setType(String Type) {
		this.Type = Type;   
	}
	   
	public String getType() {
	   	 return this.Type;
	}
	   
	public void setPath(String Path) {
		this.Path = Path;   
	}
	   
	public String getPath() {
	   	 return this.Path;
	}
}
