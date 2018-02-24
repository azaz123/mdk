package org.mdk.battle.mysqlagent.attr;

public enum CmdAttr {
	
	CMD_ATTR_RESULTSET_ACCEPT_OVER("CMD_ATTR_RESULTSET_ACCEPT_OVER"),
	
	CMD_ATTR_RESULTSET_READ_HANDLE("CMD_ATTR_RESULTSET_READ_HANDLE"),
	
	CMD_ATTR_RUN_STATUS("CMD_ATTR_RUN_STATUS"),
	
	;
	private String key;

	private CmdAttr(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
