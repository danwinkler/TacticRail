package com.danwink.tacticrail;

import java.awt.Color;

public class Player
{
	int id;
	
	int money;
	
	//Server only
	boolean readyForContinue;
	boolean sentRails;

	public int color;
	
	public Player()
	{
		
	}
	
	public Player( int id )
	{
		this.id = id;
	}
}
