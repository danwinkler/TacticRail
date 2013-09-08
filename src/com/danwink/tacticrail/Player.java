package com.danwink.tacticrail;

import java.awt.Color;

public class Player
{
	int id;
	
	int money;
	
	public int color;
	
	//Server only
	boolean readyForContinue;
	boolean sentRails;
	
	public Player()
	{
		
	}
	
	public Player( int id )
	{
		this.id = id;
	}
}
