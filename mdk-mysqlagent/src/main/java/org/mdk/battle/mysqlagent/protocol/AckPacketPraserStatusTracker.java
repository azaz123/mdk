package org.mdk.battle.mysqlagent.protocol;



public class AckPacketPraserStatusTracker {
   public AckPacketPraserStatus currentstatus = AckPacketPraserStatus.FirstRead;
   public AckPacketPraserStatus substatus;
}
