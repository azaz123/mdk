package org.mdk.battle.mysqlagent.cmd;

import java.util.HashMap;
import java.util.Map;
import org.mdk.battle.mysqlagent.protocol.*;
import org.mdk.battle.mysqlagent.task.*;
import org.mdk.battle.mysqlagent.armada.*;

public class CmdRunTime {
	public IMysqlCmd currentcmd;
	public AbstractTask headTask;
	public Admiral selfAdmiral;
    public CmdContext Context = new CmdContext();
	public final Map<String, Object> CmdAttrMap = new HashMap<String, Object>();
	public AckPacketPraserStatusTracker Tracker = new AckPacketPraserStatusTracker(); 
	
}
