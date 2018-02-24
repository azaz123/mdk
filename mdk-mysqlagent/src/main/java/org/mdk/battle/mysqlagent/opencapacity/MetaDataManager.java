package org.mdk.battle.mysqlagent.opencapacity;



import org.mdk.sqlparser.BufferSQLContext;
import org.mdk.battle.mysqlagent.FrontEndSession;

public class MetaDataManager {
    public DefInPutObj DipObj = new DefInPutObj();
    public DefOutPutObj DopObj = new DefOutPutObj();
    public BufferSQLContext sqlContext;
    public FrontEndSession SessionData;
}
