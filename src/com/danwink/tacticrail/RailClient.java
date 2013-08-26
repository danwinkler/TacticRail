package com.danwink.tacticrail;

import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.vecmath.Point2i;

import com.danwink.dngf.DNGFClient;
import com.phyloa.dlib.dui.AWTComponentEventMapper;
import com.phyloa.dlib.renderer.DScreenHandler;
import com.phyloa.dlib.renderer.Graphics2DRenderer;

public class RailClient extends DNGFClient<RailMessageType>
{
	GamePhase phase = GamePhase.BUILD;
	
	RailMap map;
	
	ZoomTransform zt;
	
	Player player;
	
	//Build Phase
	Point2i lastPoint;
	ArrayList<Railway> attemptedBuild = new ArrayList<Railway>();
	
	boolean firstFrame = true;
	
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
			break;
		case SETPHASE:
			phase = (GamePhase)o;
			break;
		case SETPLAYER:
			player = (Player)o;
			break;
		}
	}

	public void update( float d )
	{
		if( firstFrame )
		{
			zt = new ZoomTransform( canvas );
			canvas.addMouseListener( zt );
			canvas.addMouseMotionListener( zt );
			canvas.addMouseWheelListener( zt );
			
			firstFrame = false;
		}
		
		//Update
		switch( phase )
		{
		case BUILD:
		{
			if( m.clicked )
			{
				Point2D.Float mw = zt.transformPoint( new java.awt.Point( m.x, m.y ) );
				
				Point2i p = map.getClosestPoint( mw.x, mw.y );
				Point2D.Float pw = new Point2D.Float( map.getPX( p.x, p.y ), map.getPY( p.x, p.y ) );
				
				if( lastPoint == null )
				{
					if( mw.distanceSq( pw ) < 15*15 )
					{
						lastPoint = p;
					}
				}
				else
				{
					if( (p.x != lastPoint.x || p.y != lastPoint.y) && mw.distanceSq( pw ) < 15*15 )
					{
						Railway r = new Railway( player.id, lastPoint, p );
						if( map.isValid( r ) )
						{
							attemptedBuild.add( r );
							lastPoint = p;
						}
					}
				}
			}
			else if( lastPoint != null )
			{
				lastPoint = null;
			}
			
			break;
		}
		case MANAGETRAINS:
			break;
		case SHOWBUILD:
			break;
		case SHOWPROFIT:
			break;
		default:
			break;
		}
		
		
		//Render
		color( Color.white );
		fillRect( 0, 0, getWidth(), getHeight() );
		
		if( map != null )
		{
			pushMatrix();
			g.transform( zt.getCoordTransform() );
			
			map.render( this );
			
			float translateAmount = zt.getZoomDistance( 20 );
			
			if( k.up || k.w )
			{
				zt.getCoordTransform().translate( 0, -translateAmount );
			}
			if( k.down || k.s )
			{
				zt.getCoordTransform().translate( 0, translateAmount );
			}
			if( k.left || k.a )
			{
				zt.getCoordTransform().translate( -translateAmount, 0 );
			}
			if( k.right || k.d )
			{
				zt.getCoordTransform().translate( translateAmount, 0 );
			}
			
			Point2D.Float wcoord = zt.transformPoint( new java.awt.Point( m.x, m.y ) );
			
			Point2i p = map.getClosestPoint( wcoord.x, wcoord.y );
			
			color( Color.green );
			drawRect( map.getPX( p.x, p.y ) - 10, map.getPY( p.x, p.y ) - 10, 20, 20 );
			
			for( int i = 0; i < attemptedBuild.size(); i++ )
			{
				Railway r = attemptedBuild.get( i );
				color( Color.BLACK );
				line( map.getPX( r.p1.x, r.p1.y ), map.getPY( r.p1.x, r.p1.y ), map.getPX( r.p2.x, r.p2.y ), map.getPY( r.p2.x, r.p2.y ) );
			}
			
			popMatrix();
			
			text( p.x + ", " + p.y, 50, 50 );
		}
		
		
	}
	
	public static void main( String[] args )
	{
		RailClient rc = new RailClient();
		rc.setServerClass( RailServer.class );
		rc.begin();
	}
}
