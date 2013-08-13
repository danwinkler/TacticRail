package com.danwink.tacticrail;

import java.io.IOException;

import com.phyloa.dlib.network.DClient;
import com.phyloa.dlib.network.DMessage;
import com.phyloa.dlib.renderer.DScreen;
import com.phyloa.dlib.renderer.DScreenHandler;
import com.phyloa.dlib.renderer.Graphics2DRenderer;

public class GameScreen extends DScreen<RailClient, Graphics2DRenderer>
{
	RailMap m;
	
	DClient<RailMessage> c;
	
	public void update( RailClient gc, int delta )
	{
		if( c.hasClientMessages() )
		{
			DMessage<RailMessage> dm = c.getNextClientMessage();
			switch( dm.messageType )
			{
			case DATA:
				handleMessage( (RailMessage)dm.message );
				break;
			}
		}
	}
	
	public void handleMessage( RailMessage rm )
	{
		switch( rm.type )
		{
		case MAP:
			m = (RailMap)rm.o;
		}
	}

	public void render( RailClient gc, Graphics2DRenderer g )
	{
		if( m != null )
		{
			m.render( g );
		}
	}

	public void onActivate( RailClient gc, DScreenHandler<RailClient, Graphics2DRenderer> dsh )
	{
		gc.server = new RailServer();
		gc.server.begin();
		
		c = new DClient<RailMessage>( 1000, 5000 );
		RailClassRegisterer.register( c );
		try 
		{
			c.start( "localhost", 1000, 31456, 31457 );
		} catch( IOException e )
		{
			
		}
	}

	public void onExit() {
		
	}

	public void message( Object o )
	{
		
	}

}
