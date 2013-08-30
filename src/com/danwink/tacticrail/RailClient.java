package com.danwink.tacticrail;

import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.vecmath.Point2i;

import com.danwink.dngf.DNGFClient;
import com.phyloa.dlib.dui.AWTComponentEventMapper;
import com.phyloa.dlib.dui.DButton;
import com.phyloa.dlib.dui.DUI;
import com.phyloa.dlib.dui.DUIElement;
import com.phyloa.dlib.dui.DUIEvent;
import com.phyloa.dlib.dui.DUIListener;
import com.phyloa.dlib.renderer.DScreenHandler;
import com.phyloa.dlib.renderer.Graphics2DRenderer;
import com.phyloa.dlib.util.DMath;

public class RailClient extends DNGFClient<RailMessageType> implements DUIListener
{
	GamePhase phase = GamePhase.BEGIN;
	
	RailMap map;
	
	ZoomTransform zt;
	
	Player player;
	
	boolean firstFrame = true;
	
	DUI dui;
	DButton finishedButton;
	
	int turn;
	
	//Timer
	long phaseStart;
	
	//Build Phase
	Point2i lastPoint;
	ArrayList<Railway> attemptedBuild = new ArrayList<Railway>();
	int moneySpent = 0;	
	
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
			GamePhase newPhase = (GamePhase)o;
			if( newPhase != phase ) phaseStart = System.currentTimeMillis();
			phase = newPhase;
			break;
		case SETPLAYER:
			player = (Player)o;
			break;
		case SETTURN:
				turn = (Integer)o;
			break;
		default:
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
			
			AWTComponentEventMapper dem = new AWTComponentEventMapper();
			dem.register( canvas );
			dui = new DUI( dem );
			dui.addDUIListener( this );
			finishedButton = new DButton( "Finished!", getWidth() - 100, getHeight() - 50, 100, 50 );
			dui.add( finishedButton );
			
			phaseStart = System.currentTimeMillis();
			
			firstFrame = false;
		}
		
		//Update
		dui.update();
		
		switch( phase )
		{
		case BUILD:
		{
			if( m.clicked && m.y < getHeight() - 50 && m.y > 60 )
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
					if( (p.x != lastPoint.x || p.y != lastPoint.y) && mw.distanceSq( pw ) < 15*15 && moneySpent <= 20000000-1000000 && moneySpent <= player.money-1000000 )
					{
						Railway r = new Railway( player.id, lastPoint, p );
						if( map.isValid( r ) )
						{
							attemptedBuild.add( r );
							moneySpent += 1000000;
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
		
		dui.render( this );
		
		color( 255, 240, 230 );
		fillRect( 0, 0, getWidth()-60, 30 );
		color( 230, 140, 100 );
		drawRect( 0, 0, getWidth()-60, 30 );
		
		color( 115, 70, 50 );
		text( "Money: " + DMath.humanReadableNumber( player.money ), 10, 20 );
		text( "Turn: " + turn, 160, 20 );
		
		color( 230, 240, 255 );
		fillRect( 0, 30, getWidth()-60, 30 );
		color( 100, 140, 230 );
		drawRect( 0, 30, getWidth()-60, 30 );
		
		color( 50, 70, 115 );
		text( "Phase: " + phase.getPhaseName(), 10, 50 );
		
		switch( phase )
		{
		case BEGIN:
			text( "Click the ready button when you are ready to begin the game", 160, 50 );
			break;
		case BUILD:
			text( "Build Costs: " + DMath.humanReadableNumber( moneySpent ), 160, 50 );
			break;
		case MANAGETRAINS:
			break;
		case SHOWBUILD:
			break;
		case SHOWPROFIT:
			break;
		default:
			break;
			
		}
		
		color( 230, 230, 230 );
		fillRect( getWidth()-60, 0, 60, 60 );
		color( 100, 100, 100 );
		drawRect( getWidth()-60, 0, 60, 60 );
		
		float timerNormal = (float)(System.currentTimeMillis()-phaseStart) / (phase.getPhaseLength());
		
		color( 0, 255, 0 );
		fillOval( getWidth()-50, 10, 40, 40 );
		color( 255, 0, 0 );
		g.fillArc( getWidth()-50, 10, 40, 40, 90, -(int)(timerNormal*360) );
		
	}
	
	public static void main( String[] args )
	{
		RailClient rc = new RailClient();
		rc.setServerClass( RailServer.class );
		rc.begin();
	}

	@Override
	public void event( DUIEvent event )
	{
		DUIElement e = event.getElement();
		if( e == finishedButton )
		{
			send( RailMessageType.CONTINUE, null );
		}
	}
}
