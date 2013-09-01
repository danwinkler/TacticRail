package com.danwink.tacticrail;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import com.danwink.dngf.DNGFServer;
import com.phyloa.dlib.util.DHashList;

public class RailServer extends DNGFServer<RailMessageType>
{
	DHashList<Integer, Player> players = new DHashList<Integer, Player>();
	
	RailMap map;
	
	ServerLoop sl;
	
	GamePhase phase = GamePhase.BEGIN;
	int turn = 1;
	
	long phaseStart;
	
	Color[] colors = { Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.PINK, Color.MAGENTA, Color.YELLOW, Color.CYAN };

	public RailServer()
	{
		addClasses( RailClassRegisterer.classes );
		
		map = RailMapGen.map1();
	}
	
	public void handleMessage( int sender, RailMessageType type, Object message )
	{
		System.out.println( "SERVER: " + sender + "::" + type.toString() );
		switch( type )
		{
		case CONTINUE:
			players.get( sender ).readyForContinue = true;
			break;
		case MAP:
			break;
		case SETPLAYER:
			break;
		case BUILDREQUEST:
			ArrayList<Railway> buildAttempt = (ArrayList<Railway>)message;
			int price = Railway.getPrice( buildAttempt, map );
			Player player = players.get( sender );
			if( price <= player.money )
			{
				for( Railway r : buildAttempt )
				{
					r.owner = player.id;
				}
				map.addRails( buildAttempt );
				sendAll( RailMessageType.UPDATERAILS, buildAttempt );
			}
			break;
		}
	}

	public void onConnect( int id )
	{
		Player newPlayer = new Player( id );
		newPlayer.money = 20000000;
		players.put( id, newPlayer );
		sendOne( id, RailMessageType.MAP, map );
		sendOne( id, RailMessageType.SETPLAYER, newPlayer );
		sendOne( id, RailMessageType.SETPHASE, phase );
	}

	public void onDisconnect( int id )
	{
		players.remove( id );
	}

	public void update( float d )
	{
		switch( phase )
		{
		case BEGIN:
			if( readyForContinue() && players.size() > 0 )
			{
				phase = GamePhase.BUILD;
				for( int i = 0; i < players.size(); i++ ) { players.getIndex( i ).color = colors[i].getRGB(); }
				sendAll( RailMessageType.PLAYERLIST, players.getArrayList() );
				sendAll( RailMessageType.SETPHASE, phase );
				for( Player p : players ) { p.readyForContinue = false; }
				phaseStart = System.currentTimeMillis();
			}
			break;
		case BUILD:
			if( readyForContinue() || System.currentTimeMillis() - phaseStart > phase.getPhaseLength() )
			{
				phase = GamePhase.SHOWBUILD;
				phaseStart = System.currentTimeMillis();
				sendAll( RailMessageType.SETPHASE, phase );
				for( Player p : players ) { p.readyForContinue = false; }
			}
			break;
		case SHOWBUILD:
			if( System.currentTimeMillis() - phaseStart > phase.getPhaseLength() )
			{
				phase = GamePhase.MANAGETRAINS;
				phaseStart = System.currentTimeMillis();
				sendAll( RailMessageType.SETPHASE, phase );
			}
			break;
		case MANAGETRAINS:
			if( System.currentTimeMillis() - phaseStart > phase.getPhaseLength() )
			{
				phase = GamePhase.SHOWPROFIT;
				phaseStart = System.currentTimeMillis();
				sendAll( RailMessageType.SETPHASE, phase );
			}
			break;
		case SHOWPROFIT:
			if( System.currentTimeMillis() - phaseStart > phase.getPhaseLength() )
			{
				phase = GamePhase.BUILD;
				phaseStart = System.currentTimeMillis();
				sendAll( RailMessageType.SETPHASE, phase );
				sendAll( RailMessageType.SETTURN, turn );
				turn++;
			}
			break;
		default:
			break;
		}
	}
	
	public boolean readyForContinue()
	{
		boolean foundNone = true;
		for( Player p : players )
		{
			if( !p.readyForContinue ) 
			{
				foundNone = false;
				break;
			}
		}
		return foundNone;
	}
}
