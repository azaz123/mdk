
package org.mdk.protocol.mysql;

/**
 * 报警关键词定义
 * 
 * @author hrz
 */
public interface Alarms {
    /** 默认报警关键词 **/
    public static final String DEFAULT           = "#!MDK#";
    
    /** 集群无有效的节点可提供服务 **/
    public static final String CLUSTER_EMPTY     = "#!CLUSTER_EMPTY#";
    
    /** 数据节点的数据源发生切换 **/
    public static final String DATANODE_SWITCH   = "#!DN_SWITCH#";
    
    /** 隔离区非法用户访问 **/
    public static final String QUARANTINE_ATTACK = "#!QT_ATTACK#";
    
}
