package com.danwink.tacticrail;

import com.phyloa.dlib.dui.AWTComponentEventMapper;
import com.phyloa.dlib.renderer.DScreenHandler;
import com.phyloa.dlib.renderer.Graphics2DRenderer;

public class RailClient extends Graphics2DRenderer
{
	DScreenHandler<RailClient, Graphics2DRenderer> dsh;
	
	RailServer server;
	
	public RailClient()
	{
		dsh = new DScreenHandler<RailClient, Graphics2DRenderer>();
		
		dsh.register( "home", new HomeScreen( this ) );
		dsh.register( "play", new GameScreen() );
	}
	
	public void initialize() 
	{
		size( 800, 600 );
		
		dsh.activate( "home", this );
	}

	public void update()
	{
		dsh.update( this, 10 );
		dsh.render( this, this );
	}
	
	public static void main( String[] args )
	{
		RailClient rc = new RailClient();
		rc.begin();
	}
}
