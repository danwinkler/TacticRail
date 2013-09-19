package com.danwink.tacticrail;

import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

import javax.vecmath.Point2i;
import javax.vecmath.Vector2f;

import com.danwink.dngf.DNGFClient;
import com.danwink.tacticrail.Train.TrainAction;
import com.danwink.tacticrail.Train.TrainMove;
import com.phyloa.dlib.dui.AWTComponentEventMapper;
import com.phyloa.dlib.dui.DButton;
import com.phyloa.dlib.dui.DDialog;
import com.phyloa.dlib.dui.DPanel;
import com.phyloa.dlib.dui.DText;
import com.phyloa.dlib.dui.DTextBox;
import com.phyloa.dlib.dui.DUI;
import com.phyloa.dlib.dui.DUIElement;
import com.phyloa.dlib.dui.DUIEvent;
import com.phyloa.dlib.dui.DUIListener;
import com.phyloa.dlib.renderer.DScreenHandler;
import com.phyloa.dlib.renderer.Graphics2DRenderer;
import com.phyloa.dlib.util.DHashList;
import com.phyloa.dlib.util.DMath;

public class RailClient extends DNGFClient<RailMessageType> implements DUIListener
{
	GamePhase phase = GamePhase.BEGIN;
	
	RailMap map;
	
	ZoomTransform zt;
	
	Player player;
	
	DHashList<Integer, Player> players = new DHashList<Integer, Player>();
	
	DHashList<Integer, Train> trains = new DHashList<Integer, Train>();
	
	DUI dui;
	DButton finishedButton;
	
	int turn;
	
	//Timer
	long phaseStart;
	
	//Build Phase
	Point2i lastPoint;
	ArrayList<Railway> attemptedBuild = new ArrayList<Railway>();
	int moneySpent = 0;
	
	//Manage Phase
	Train selectedTrain;
	City selectedCity;
	HashMap<Integer, TrainMove> moves = new HashMap<Integer, TrainMove>();
	DPanel manageButtons;
	DButton buy;
	DButton sell;
	
	public RailClient()
	{
		addClasses( RailClassRegisterer.classes );
	}
	
	public void gameSetup()
	{
	
	}
	
	//Called at beginning of game
	public void clientStart()
	{
		zt = new ZoomTransform( canvas );
		canvas.addMouseListener( zt );
		canvas.addMouseMotionListener( zt );
		canvas.addMouseWheelListener( zt );
		
		AWTComponentEventMapper dem = new AWTComponentEventMapper();
		dem.register( canvas );
		dui = new DUI( dem );
		dui.addDUIListener( this );
		finishedButton = new DButton( "Begin!", getWidth() - 100, getHeight() - 50, 100, 50 );
		dui.add( finishedButton );
		
		manageButtons = new DPanel( 300, 30, 200, 30 );
		buy = new DButton( "Buy from City", 0, 0, 100, 30 );
		manageButtons.add( buy );
		sell = new DButton( "Sell at City", 100, 0, 100, 30 );
		manageButtons.add( sell );
		
		phaseStart = System.currentTimeMillis();
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
			if( newPhase != phase ) 
			{ 
				phaseStart = System.currentTimeMillis();
				switch( newPhase )
				{
				case BEGIN:
					break;
				case BUILD:
					if( phase == GamePhase.BEGIN )
					{
						finishedButton.setText( "Finished!" );
					}
					break;
				case SHOWBUILD:
					send( RailMessageType.BUILDREQUEST, attemptedBuild );
					attemptedBuild.clear();
					moneySpent = 0;
					break;
				case MANAGETRAINS:
					break;
				case SHOWPROFIT:
					break;
				}
				phase = newPhase;
			}
			break;
		case SETPLAYER:
			player = (Player)o;
			break;
		case SETTURN:
			turn = (Integer)o;
			break;
		case RAILUPDATE:
			map.addRails( (ArrayList<Railway>)o );
			break;
		case PLAYERLIST:
			ArrayList<Player> par = (ArrayList<Player>)o;
			for( Player p : par )
			{
				players.put( p.id, p );
			}
			break;
		case TRAINUPDATE:
			Train t = (Train)o;
			Train thisTrain = trains.get( t.id );
			if( thisTrain == null )
			{
				thisTrain = t;
				trains.put( t.id, t );
			}
			else
			{
				thisTrain.set( t );
			}
			break;
		default:
			break;
		}
	}

	public void update( float d )
	{
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
					if( (p.x != lastPoint.x || p.y != lastPoint.y) && mw.distanceSq( pw ) < 15*15 )
					{
						Railway r = new Railway( player.id, lastPoint, p );
						if( map.isValid( r, trains, player, attemptedBuild ) )
						{
							//Check to see if we are already have a track to be built here
							boolean found = false;
							for( Railway toCheck : attemptedBuild )
							{
								if( toCheck.equals( r ) )
								{
									found = true; 
									break;
								}
							}
							
							if( !found )
							{
								int price = map.getCost( lastPoint, p );
								if( moneySpent <= RailOptions.MAX_SPENDING_PER_TURN-price && moneySpent <= player.money-price )
								{
									attemptedBuild.add( r );
									moneySpent += price;
									lastPoint = p;
								}
							}
						}
					}
				}
			}
			else if( lastPoint != null )
			{
				lastPoint = null;
			}
			
			if( k.ctrl && k.z && attemptedBuild.size() > 0 )
			{
				Railway r = attemptedBuild.remove( attemptedBuild.size()-1 );
				moneySpent -= map.getCost( r.p1, r.p2 );
				k.z = false;
			}
			
			break;
		}
		case MANAGETRAINS:
			if( m.clicked && m.y < getHeight() - 50 && m.y > 60 )
			{
				Point2D.Float mw = zt.transformPoint( new java.awt.Point( m.x, m.y ) );
				
				Point2i p = map.getClosestPoint( mw.x, mw.y );
				
				selectedTrain = null;
				for( Train t : trains )
				{
					if( t.owner == player.id )
					{
						if( t.pos.equals( p ) )
						{
							selectedTrain = t;
							return;
						}
					}
				}
				m.clicked = false;
			}
			if( m.rightClicked && m.y < getHeight() - 50 && m.y > 60 )
			{
				if( selectedTrain != null )
				{
					Point2D.Float mw = zt.transformPoint( new java.awt.Point( m.x, m.y ) );
					
					Point2i p = map.getClosestPoint( mw.x, mw.y );
					
					selectedCity = map.getCity( p );
					
					if( selectedCity == null )
					{
						dui.remove( manageButtons );
					}
					else
					{
						dui.add( manageButtons );
					}
					
					if( selectedTrain != null )
					{
						if( !selectedTrain.pos.equals( p ) )
						{
							TrainMove move = moves.get( selectedTrain.id );
							if( move == null )
							{
								moves.put( selectedTrain.id, move = new TrainMove( selectedTrain.id ) );
							}
							ArrayList<Point2i> path = map.findRoute( move.getLastMoveAction() == null ? selectedTrain.pos : move.getLastMoveAction().move, p, player.id, false );
							if( path != null )
							{
								for( int i = 1; i < path.size(); i++ )
								{
									move.trainActions.add( new TrainAction( path.get( i ) ) );
								}
							}
						}
					}
					
				}
				m.rightClicked = false;
			}
			break;
		case SHOWBUILD:
			break;
		case SHOWPROFIT:
			break;
		default:
			break;
		}
		
		
		//Render
		color( Color.blue );
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
				r.render( this, map, Color.black );
			}
			
			for( int i = 0; i < trains.size(); i++ )
			{
				trains.get( i ).render( this, map, selectedTrain == trains.get( i ) );
			}
			
			if( !m.clicked && phase != GamePhase.BEGIN )
			{
				City c = null;
				Point2D.Float mw = zt.transformPoint( new java.awt.Point( m.x, m.y ) );
				
				Point2i cPoint = map.getClosestPoint( mw.x, mw.y );
				for( int i = 0; i < map.cities.size(); i++ )
				{
					City test = map.cities.get( i );
					
					if( test.pos.equals( cPoint ) )
					{
						c = test;
						break;
					}
				}
				
				if( c != null )
				{
					pushMatrix();
						translate( map.getPX( c.pos.x, c.pos.y ), map.getPY( c.pos.x, c.pos.y ) );
						scale( -translateAmount/20 );
						color( 255, 255, 255 );
						fillRect( 0, 0, 300, c.supplies.length * 20 );
						color( 0, 0, 0 );
						drawRect( 0, 0, 300, c.supplies.length * 20 );
						for( int i = 0; i < c.supplies.length; i++ )
						{
							text( Cargo.values()[i].toString() + ": " + c.supplies[i] + " Buying: " + c.buyPrice( i ) + " Selling: " + c.sellPrice( i ), 10, 20 * i + 13 );
						}
					popMatrix();
				}
			}
			
			switch( phase )
			{
			case BEGIN:
				break;
			case BUILD:
				break;
			case MANAGETRAINS:
				if( selectedTrain != null )
				{
					TrainMove move = moves.get( selectedTrain.id );
					if( move != null )
					{
						move.render( this );
					}
				}
				break;
			case SHOWBUILD:
				break;
			case SHOWPROFIT:
				break;
			default:
				break;
				
			}
			
			popMatrix();
		}
		
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
		
		pushMatrix();
			translate( getWidth()-60, 0 );
			color( 230, 230, 230 );
			fillRect( 0, 0, 60, 60 );
			color( 100, 100, 100 );
			drawRect( 0, 0, 60, 60 );
			
			float timerNormal = (float)(System.currentTimeMillis()-phaseStart) / (phase.getPhaseLength());
			
			color( 0, 255, 0 );
			fillOval( 10, 10, 40, 40 );
			color( 255, 0, 0 );
			g.fillArc( 10, 10, 40, 40, 90, -(int)(timerNormal*360) );
			
			if( phase != GamePhase.BEGIN )
			{
				color( 0 );
				String timerText = Integer.toString( Math.round( (phase.getPhaseLength() - (System.currentTimeMillis()-phaseStart))/1000 ) );
				Vector2f timerTextSize = this.getStringSize( timerText );
				text( timerText, 30 - timerTextSize.x*.5f, 30 - timerTextSize.y*.5f );
			}
		popMatrix();
		
		dui.render( this );
	}
	
	public void showBuySellDialog()
	{
		if( phase == GamePhase.MANAGETRAINS && selectedCity != null )
		{
			DDialog d = new DDialog( 300, 400 );
			for( int i = 0; i < Cargo.values().length; i++ )
			{
				int y = 10 + i * 30;
				d.add( new DText( Cargo.values()[i].toString(), 10, y ) );
				DTextBox tb = new DTextBox( 100, y, 190, 20 );
				tb.setText( Integer.toString( selectedCity.supplies[i] ) );
				tb.setName( Cargo.values()[i].name() );
				d.add( tb );
			}
			dui.showDialog( d, getWidth()/2 - 150, getHeight()/2 - 200 );
		}
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
		if( e instanceof DButton && event.getType() == DButton.MOUSE_UP )
		{
			if( e == finishedButton )
			{
				send( RailMessageType.CONTINUE, null );
			}
			if( phase == GamePhase.MANAGETRAINS )
			{
				if( e == buy )
				{
					showBuySellDialog();
				}
				if( e == sell )
				{
					showBuySellDialog();
				}
			}
		}
	}

	@Override
	public void reset()
	{
		// TODO Auto-generated method stub
		
	}
}
