package com.danwink.tacticrail;

public class Player
{
	int id;
	
	int money;
	
	//Server only
	boolean readyForContinue;
	
	public Player()
	{
		
	}
	
	public Player( int id )
	{
		this.id = id;
	}
}
