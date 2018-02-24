package org.mdk.battle.mysqlagent.opencapacity;
import org.mdk.sqlparser.BufferSQLContext;
import com.alibaba.fastjson.JSON;
public class DefInPutObj {
	public String param;
    public DefMetaData MetaDataObj;
    public void Update(MetaDataManager MetaData){
    	MetaDataObj = new DefMetaData();
    	MetaDataObj.SessionId = MetaData.SessionData.getSessionId();
        
        if(MetaData.sqlContext.getSQLCount()>0){
        	for(int i=0;i<MetaData.sqlContext.getSQLCount();i++){
        		DefMetaData.MetaSqlInfo sqlinfo = MetaDataObj.new MetaSqlInfo();
        		sqlinfo.Sql = MetaData.sqlContext.getRealSQL(i);
        		sqlinfo.SqlType = MetaData.sqlContext.getSQLType(i);
        		for(int j=0;j<MetaData.sqlContext.getSQLTblCount(i);j++){
        			sqlinfo.Tblist.add(MetaData.sqlContext.getSQLTableName(i, j));
    			}
        		sqlinfo.tbcount = sqlinfo.Tblist.size();
        		MetaDataObj.SqlInfoList.add(sqlinfo);
        	}
        	MetaDataObj.SqlCount = MetaDataObj.SqlInfoList.size();
        }
        
        if(MetaData.sqlContext.getAnnotationType()==BufferSQLContext.ANNOTATION_CUSTOM){
        	MetaDataObj.Annotation = MetaData.sqlContext.getAnnotationStringValue(BufferSQLContext.ANNOTATION_CUSTOM);
        }
        param = JSON.toJSONString(MetaDataObj);
        param = param.replaceAll("\\s","\\u0000");
        param = param.replaceAll("\"","\\\\\"");
        System.out.println("DefInPutObj:" + param);
    }
}
