package org.mdk.battle.mysqlagent;

import java.util.stream.Stream;

import org.mdk.battle.mysqlagent.beans.*;
import java.util.stream.Stream;

public enum ConfigEnum {
	MYSQLBEANSLIST("datasourcelist.yml", MysqlMetaBeansList.class),
	SERVER("server.yml", ServerBeans.class),
    USER("user.yml", FrontEndUserBean.class),
	COMBEANS("combeanslist.yml", ComBeansList.class);


    private String fileName;
    private Class clazz;

    ConfigEnum(String fileName, Class clazz) {
        this.fileName = fileName;
        this.clazz = clazz;
    }

    public String getFileName() {
        return this.fileName;
    }

    public Class getClazz() {
        return clazz;
    }

    public static ConfigEnum getConfigEnum() {
        return Stream.of(ConfigEnum.values()).findFirst().orElse(null);
    }
}