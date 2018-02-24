package org.mdk.battle.mysqlagent.protocol;

public enum AckPacketPraserStatus {
	FirstRead, 
	ResultsetRead, 
	ColumnInfoRead,
	ColumnDataRead,
	ColumnDataEndFlagRead,
	RawDataRead,
	RawDataEndFlagRead
}
