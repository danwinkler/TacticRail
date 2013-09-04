package com.danwink.tacticrail;

public enum GamePhase
{
	BEGIN( "Game Start" ),
	BUILD( "Build Phase" ),
	SHOWBUILD( "Build Phase End" ),
	MANAGETRAINS( "Train Management Phase" ),
	SHOWPROFIT( "Turn Completion" );
	
	String phaseName;
	
	GamePhase( String phaseName )
	{
		this.phaseName = phaseName;
	}
	
	public String getPhaseName()
	{
		return phaseName;
	}
	
	public int getPhaseLength()
	{
		int val = 0;
		switch( this )
		{
		case BEGIN: val = 0; break;
		case BUILD: val = RailOptions.BUILD_PHASE_LENGTH; break;
		case MANAGETRAINS: val = RailOptions.MANAGETRAINS_PHASE_LENGTH; break;
		case SHOWBUILD: val = 10; break;
		case SHOWPROFIT: val = 10; break;
		}
		return val * 1000;
	}
}
