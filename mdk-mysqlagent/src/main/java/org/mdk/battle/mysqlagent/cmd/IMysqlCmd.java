package org.mdk.battle.mysqlagent.cmd;

import java.io.IOException;
import org.mdk.battle.mysqlagent.BackEndSession;
import org.mdk.battle.mysqlagent.FrontEndSession;

public interface IMysqlCmd {
    public void OnCmdResponse(CmdRunTime Context,boolean success) throws IOException;
    public boolean Excute(CmdRunTime Context);
}
