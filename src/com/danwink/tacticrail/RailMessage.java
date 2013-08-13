package com.danwink.tacticrail;

public class RailMessage 
{
	RailMessageType type;
	Object o;
	
	public RailMessage()
	{
		
	}
	
	public RailMessage( RailMessageType type, Object o )
	{
		this.type = type;
		this.o = o;
	}
	
	public enum RailMessageType
	{
		MAP
	}
}
