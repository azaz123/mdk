package org.mdk.battle.mysqlagent.beans;

import java.util.List;

public class FrontEndUserBean {
    public String name;
    public String password;

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



    @Override
    public String toString() {
        return "UserBean{" + "name='" + name + '\'' + ", password='" + password +  '}';
    }
}
