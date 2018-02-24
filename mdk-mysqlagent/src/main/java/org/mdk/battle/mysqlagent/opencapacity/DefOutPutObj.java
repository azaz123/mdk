package org.mdk.battle.mysqlagent.opencapacity;



import com.alibaba.fastjson.JSON;
public class DefOutPutObj {
    public PlanData plandata;
    
    public void Update(String Data){
    	plandata = JSON.parseObject(Data,PlanData.class);
    	System.out.println("DefOutPutObj:" + Data);
    }
}
