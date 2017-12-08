package org.mdk.battle.mysqlagent.beans;

import java.util.List;

public class FrontEndUserBean {
    private String name;
    private String password;
    private List<String> schemas;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getSchemas() {
        return schemas;
    }

    public void setSchemas(List<String> schemas) {
        this.schemas = schemas;
    }

    @Override
    public String toString() {
        return "UserBean{" + "name='" + name + '\'' + ", password='" + password + '\'' + ", schemas=" + schemas + '}';
    }
}
