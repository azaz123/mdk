package org.mdk.battle.mysqlagent.cmd;

import java.io.IOException;
import org.mdk.battle.mysqlagent.BackEndSession;
import org.mdk.battle.mysqlagent.FrontEndSession;

public interface IMysqlCmd {
    public void OnCmdResponse(CmdContext Context,boolean success) throws IOException;
    public boolean Excute(CmdContext Context);
}
