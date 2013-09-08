package com.danwink.tacticrail;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import com.danwink.dngf.DNGFServer;
import com.danwink.tacticrail.Train.TrainType;
import com.phyloa.dlib.util.DHashList;
import com.phyloa.dlib.util.DMath;

public class RailServer extends DNGFServer<RailMessageType>
{
	DHashList<Integer, Player> players = new DHashList<Integer, Player>();
	
	RailMap map;
	DHashList<Integer, Train> trains = new DHashList<Integer, Train>();
	
	ServerLoop sl;
	
	GamePhase phase = GamePhase.BEGIN;
	int turn = 1;
	
	long phaseStart;
	
	Color[] colors = { Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.PINK, Color.MAGENTA, Color.YELLOW, Color.CYAN };
	
	ArrayList<Railway> railsToAdd = new ArrayList<Railway>();

	public RailServer()
	{
		addClasses( RailClassRegisterer.classes );
		
		map = RailMapGen.map1();
	}
	
	public void handleMessage( int sender, RailMessageType type, Object message )
	{
		System.out.println( "SERVER RECIEVED: " + sender + ": " + type.toString() );
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
				player.money -= price;
				for( Railway r : buildAttempt )
				{
					r.owner = player.id;
				}
				railsToAdd.addAll( buildAttempt );
				sendOne( sender, RailMessageType.SETMONEY, player.money );
				player.sentRails = true;
			}
			break;
		}
	}

	public void onConnect( int id )
	{
		Player newPlayer = new Player( id );
		newPlayer.money = RailOptions.START_MONEY;
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
				for( int i = 0; i < players.size(); i++ ) 
				{ 
					players.getIndex( i ).color = colors[i].getRGB(); 
					Train t = new Train( TrainType.BASIC, map.cities.get( DMath.randomi( 0, map.cities.size()-1 ) ).pos, players.getIndex( i ).id ); 
					trains.put( t.id, t ); 
					sendAll( RailMessageType.TRAINUPDATE, t );
				}
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
			boolean allSent = true;
			for( Player p : players )
			{
				if( !p.sentRails )
				{
					allSent = false;
					break;
				}
			}
			if( allSent )
			{
				//If there are two rails on same spot, choose a random one
				//TODO: have some sort of auction in this circumstance
				for( int i = 0; i < railsToAdd.size(); i++ )
				{
					for( int j = 0; j < railsToAdd.size(); j++ )
					{
						if( i == j ) continue;
						if( railsToAdd.get( i ).equals( railsToAdd.get( j ) ) )
						{
							if( Math.random() > .5f )
							{
								railsToAdd.remove( i );
								i--;
								break;
							}
							else
							{
								railsToAdd.remove( j );
								j--;
								continue;
							}
						}
					}
				}
				
				map.addRails( railsToAdd );
				sendAll( RailMessageType.RAILUPDATE, railsToAdd );
				railsToAdd.clear();
				for( Player p : players )
				{
					p.sentRails = false;
				}
			}
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

	public void serverStart()
	{
		
	}

	public void reset()
	{
		
	}
}
