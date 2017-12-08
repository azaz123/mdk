
package org.mdk.protocol.mysql;

/**
 * alarm keywords
 * 
 * @author hrz
 */
public interface Alarms {
    /** default **/
    public static final String DEFAULT           = "#!MDK#";
    
    /** cluster has non-node do work **/
    public static final String CLUSTER_EMPTY     = "#!CLUSTER_EMPTY#";
    
    /** switch happen **/
    public static final String DATANODE_SWITCH   = "#!DN_SWITCH#";
    
    /** level **/
    public static final String QUARANTINE_ATTACK = "#!QT_ATTACK#";
    
}
