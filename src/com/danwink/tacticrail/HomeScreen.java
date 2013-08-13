package com.danwink.tacticrail;

import com.phyloa.dlib.dui.AWTComponentEventMapper;
import com.phyloa.dlib.dui.DButton;
import com.phyloa.dlib.dui.DPanel;
import com.phyloa.dlib.dui.DUI;
import com.phyloa.dlib.renderer.DScreen;
import com.phyloa.dlib.renderer.DScreenHandler;
import com.phyloa.dlib.renderer.Graphics2DRenderer;

public class HomeScreen extends DScreen<RailClient, Graphics2DRenderer>
{
	DUI dui;
	
	DPanel panel;
	
	DButton startLocalServer;
	
	
	AWTComponentEventMapper em = new AWTComponentEventMapper();
	
	public HomeScreen()
	{
		dui = new DUI( em );
		
		panel = new DPanel( 0, 0, 300, 300 );
		
		startLocalServer = new DButton( "Start Local Server", 0, 0, 300, 100 );
		panel.add( startLocalServer );
		
		dui.add( panel );
	}
	
	public void update( RailClient gc, int delta )
	{
		dui.update();
	}

	public void render( RailClient gc, Graphics2DRenderer g )
	{
		dui.render( g );
	}

	public void onActivate( RailClient gc, DScreenHandler<RailClient, Graphics2DRenderer> dsh )
	{
		panel.setLocation( gc.getWidth()/2 - 150, gc.getHeight()/2 - 200 );
		
		em.register( gc.canvas );
		dui.setEnabled( true );
	}

	public void onExit() 
	{
		dui.setEnabled( false );
	}

	public void message( Object o )
	{
		
	}
}
