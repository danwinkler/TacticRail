package com.danwink.tacticrail;

import java.util.HashMap;

import com.danwink.tacticrail.RailMessage.RailMessageType;
import com.phyloa.dlib.network.DMessage;
import com.phyloa.dlib.network.DServer;

public class RailServer 
{
	DServer<RailMessage> server;
	
	HashMap<Integer, Player> players = new HashMap<Integer, Player>();
	
	RailMap map;
	
	public RailServer()
	{
		
	}
	
	public void begin()
	{
		map = RailMapGen.map1();
		
		server = new DServer<RailMessage>( 1000, 5000 );
		RailClassRegisterer.register( server );
		server.start( 31456, 31457 );
		
		Thread t = new Thread( new ServerLoop() );
		t.start();
	}
	
	public void update( float f )
	{
		while( server.hasServerMessages() )
		{
			DMessage<RailMessage> dm = server.getNextServerMessage();
			
			switch( dm.messageType )
			{
			case CONNECTED:
				Player newPlayer = new Player( dm.sender );
				players.put( dm.sender, newPlayer );
				server.sendToClient( dm.sender, new RailMessage( RailMessageType.MAP, map ) );
				break;
			case DISCONNECTED:
				players.remove( dm.sender );
				break;
			case DATA:
				handleMessage( dm.sender, (RailMessage)dm.message );
				break;
			}
		}
	}
	
	public void handleMessage( int sender, RailMessage m )
	{
		switch( m.type )
		{
		
		}
	}
	
	public class ServerLoop implements Runnable 
	{
		long lastTime;
		long frameTime = (1000 / 30);
		long timeDiff;
		public boolean running = true;
		public ServerLoop()
		{
			
		}

		public void run() 
		{
			lastTime = System.currentTimeMillis();
			long lastWholeFrame = lastTime;
			while( running )
			{
				try{
				long timeDiff = System.currentTimeMillis() - lastWholeFrame;
				lastWholeFrame = System.currentTimeMillis();
				update( 1000.f / timeDiff );
				} catch( Exception ex )
				{
					ex.printStackTrace();
				}
				long time = System.currentTimeMillis();
				timeDiff = (lastTime + frameTime) - time;
				if( timeDiff > 0 )
				{
					try {
						Thread.sleep( timeDiff );
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				lastTime = System.currentTimeMillis();
			}
			server.stop();
		}	
	}
}
