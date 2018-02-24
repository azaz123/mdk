package org.mdk.battle.mysqlagent.beans;

import java.util.List;


public class MysqlMetaBeansList {
	public List<MysqlMetaBeans> beanslist;

    public List<MysqlMetaBeans> getBeansList() {
        return beanslist;
    }

    public void setBeansList(List<MysqlMetaBeans> beansList) {
        this.beanslist = beansList;
    }
}
