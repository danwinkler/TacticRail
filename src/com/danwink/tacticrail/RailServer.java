package com.danwink.tacticrail;

import java.util.HashMap;

import com.danwink.dngf.DNGFServer;

public class RailServer extends DNGFServer<RailMessageType>
{
	HashMap<Integer, Player> players = new HashMap<Integer, Player>();
	
	RailMap map;
	
	ServerLoop sl;
	
	public RailServer()
	{
		addClasses( RailClassRegisterer.classes );
		
		map = RailMapGen.map1();
	}
	
	public void handleMessage( int sender, RailMessageType type, Object message )
	{
		
	}

	public void onConnect( int id )
	{
		Player newPlayer = new Player( id );
		players.put( id, newPlayer );
		sendOne( id, RailMessageType.MAP, map );
	}

	public void onDisconnect( int id )
	{
		players.remove( id );
	}

	public void update( float d )
	{
		
	}
}
