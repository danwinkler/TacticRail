package com.danwink.tacticrail;

import java.awt.Color;
import java.awt.RenderingHints;

import com.danwink.dngf.DNGFClient;
import com.phyloa.dlib.dui.AWTComponentEventMapper;
import com.phyloa.dlib.renderer.DScreenHandler;
import com.phyloa.dlib.renderer.Graphics2DRenderer;

public class RailClient extends DNGFClient<RailMessageType>
{
	RailMap map;
	
	ZoomTransform zt;
	
	public RailClient()
	{
		addClasses( RailClassRegisterer.classes );
	}
	
	public void setup()
	{

	}

	public void handleMessage( RailMessageType type, Object o )
	{
		switch( type )
		{
		case MAP:
			map = (RailMap)o;
		}
	}

	public void update( float d )
	{
		if( zt == null )
		{
			zt = new ZoomTransform( canvas );
			canvas.addMouseListener( zt );
			canvas.addMouseMotionListener( zt );
			canvas.addMouseWheelListener( zt );
		}
		
		color( Color.white );
		fillRect( 0, 0, getWidth(), getHeight() );
		
		pushMatrix();
		g.transform( zt.getCoordTransform() );
		if( map != null )
		{
			pushMatrix();
			scale( .5f, .5f );
			map.render( this );
			popMatrix();
		}
		popMatrix();
	}
	
	public static void main( String[] args )
	{
		RailClient rc = new RailClient();
		rc.setServerClass( RailServer.class );
		rc.begin();
	}
}
