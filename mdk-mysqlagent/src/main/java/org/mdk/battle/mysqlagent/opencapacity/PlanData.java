package org.mdk.battle.mysqlagent.opencapacity;

import java.util.ArrayList;
import java.util.List;

import org.mdk.battle.mysqlagent.opencapacity.DefMetaData.MetaSqlInfo;

public class PlanData {
	public String Status;
	public String Type;
	public List<subPlanInfo> subPlanInfoList = new ArrayList<subPlanInfo>();
}
