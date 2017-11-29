package org.mdk.protocol.mysql;


public enum AutoCommit {
    ON("SET autocommit = 1;"),
    OFF("SET autocommit = 0;");

    AutoCommit(String cmd) {
        this.cmd = cmd;
    }

    private String cmd;

    public String getCmd() {
        return cmd;
    }
}
