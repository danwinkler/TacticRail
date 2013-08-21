package com.danwink.tacticrail;

import com.phyloa.dlib.dui.AWTComponentEventMapper;
import com.phyloa.dlib.dui.DButton;
import com.phyloa.dlib.dui.DPanel;
import com.phyloa.dlib.dui.DUI;
import com.phyloa.dlib.dui.DUIEvent;
import com.phyloa.dlib.dui.DUIListener;
import com.phyloa.dlib.renderer.DScreen;
import com.phyloa.dlib.renderer.DScreenHandler;
import com.phyloa.dlib.renderer.Graphics2DRenderer;

public class HomeScreen extends DScreen<RailClient, Graphics2DRenderer> implements DUIListener
{
	RailClient rc;
	
	DUI dui;
	
	DPanel panel;
	
	DButton startLocalServer;
	DButton joinGame;
	
	public HomeScreen( RailClient rc )
	{
		this.rc = rc;
		AWTComponentEventMapper em = new AWTComponentEventMapper();
		em.register( rc.canvas );
		dui = new DUI( em );
		
		panel = new DPanel( 0, 0, 300, 300 );
		
		startLocalServer = new DButton( "Start Local Server", 0, 0, 300, 100 );
		joinGame = new DButton( "Join Game", 0, 100, 300, 100 );
		
		panel.add( startLocalServer );
		panel.add( joinGame );
		
		dui.add( panel );
		
		dui.addDUIListener( this );
	}
	
	public void update( RailClient gc, int delta )
	{
		dui.update();
	}

	public void render( RailClient gc, Graphics2DRenderer g )
	{
		dui.render( g );
		
		if( gc.server != null )
		{
			g.text( "running", 100, 100 );
		}
	}

	public void onActivate( RailClient gc, DScreenHandler<RailClient, Graphics2DRenderer> dsh )
	{
		panel.setLocation( gc.getWidth()/2 - 150, gc.getHeight()/2 - 200 );
		
		dui.setEnabled( true );
	}

	public void onExit() 
	{
		dui.setEnabled( false );
	}

	public void message( Object o )
	{
		
	}

	public void event( DUIEvent event ) 
	{
		if( event.getElement() == startLocalServer && event.getType() == DButton.MOUSE_UP )
		{
			System.out.println( rc.server );
			if( rc.server == null )
			{
				rc.server = new RailServer();
				rc.server.begin();
				startLocalServer.setText( "Stop Local Server" );
			}
			else
			{
				rc.server.sl.running = false;
				rc.server = null;
				startLocalServer.setText( "Start Local Server" );
			}
		}
	}
}
