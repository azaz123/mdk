package org.mdk.battle.mysqlagent.opencapacity;

import java.util.*;


public class DefMetaData {
    public int SessionId;
    public int SqlCount;
    public String Annotation;
    public List<MetaSqlInfo> SqlInfoList = new ArrayList<MetaSqlInfo>();
    
    public class MetaSqlInfo  {
        public String Sql;
        public int SqlType;
        public int tbcount;
        public List<String> Tblist = new ArrayList<String>();
    }
}
