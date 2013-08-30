package com.danwink.tacticrail;

public enum GamePhase
{
	BEGIN( "Game Start", 0 ),
	BUILD( "Build Phase", 30 ),
	SHOWBUILD( "Build Phase End", 10 ),
	MANAGETRAINS( "Train Management Phase", 30 ),
	SHOWPROFIT( "Turn Completion", 10 );
	
	String phaseName;
	int phaseLength;
	
	GamePhase( String phaseName, int phaseLength )
	{
		this.phaseName = phaseName;
		this.phaseLength = phaseLength;
	}
	
	public String getPhaseName()
	{
		return phaseName;
	}
	
	public int getPhaseLength()
	{
		return phaseLength*1000;
	}
}
