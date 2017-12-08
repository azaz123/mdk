package org.mdk.battle.mysqlagent.task;

import java.io.IOException;
import org.mdk.battle.mysqlagent.cmd.CmdContext;

public interface TaskCallBack {
	void finished(CmdContext Context, Object sender, boolean success, Object result) throws IOException;
}
