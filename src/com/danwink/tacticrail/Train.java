package com.danwink.tacticrail;

import java.awt.Color;
import java.util.ArrayList;

import javax.vecmath.Point2i;

import com.phyloa.dlib.renderer.Graphics2DRenderer;
import com.phyloa.dlib.util.DMath;

public class Train
{
	public static final float renderMoveSpeedFactor = .01f;
	
	public static int nextID = 0;
	
	int id;
	TrainType type;
	Point2i pos;
	int owner;
	int[] cargo = new int[Cargo.values().length];
	
	public Train()
	{
		id = nextID++;
	}
	
	public Train( TrainType type, Point2i pos, int owner )
	{
		this();
		this.type = type;
		this.pos = new Point2i( pos );
		this.owner = owner;
	}
	
	public void render( RailClient g, RailMap map, float time, ArrayList<Point2i> points, boolean selected )
	{
		float dx = 0, dy = 0;
		if( points == null )
		{
			dx = map.getPX( pos.x, pos.y ) + 5;
			dy = map.getPY( pos.x, pos.y ) - 5;
		}
		else
		{
			float pt = time * renderMoveSpeedFactor * type.speed;
			int point = (int)pt;
			if( point >= points.size()-1 )
			{
				Point2i p2i = points.get( points.size()-1 );
				dx = map.getPX( p2i.x, p2i.y );
				dy = map.getPY( p2i.x, p2i.y );
			}
			else
			{
				Point2i p2i1 = points.get( point );
				Point2i p2i2 = points.get( point+1 );
				
				float amt = pt - point;
				float dx1 = map.getPX( p2i1.x, p2i1.y );
				float dy1 = map.getPY( p2i1.x, p2i1.y );
				float dx2 = map.getPX( p2i2.x, p2i2.y );
				float dy2 = map.getPY( p2i2.x, p2i2.y );
				dx = DMath.lerp( amt, dx1, dx2 );
				dy = DMath.lerp( amt, dy1, dy2 );
			}
			
		}
		g.pushMatrix();
			g.translate( dx, dy );
			g.color( g.players.get( owner ).color );
			g.drawOval( -4, -4, 8, 8 );
			
			if( selected )
			{
				g.color( Color.RED );
				g.drawOval( -6, -6, 12, 12 );
			}
		g.popMatrix();
	}
	
	public void render( RailClient g, RailMap map, boolean selected )
	{
		this.render( g, map, 0, null, selected );
	}
	
	public enum TrainType
	{
		BASIC( "Simpleton 500", 500, 8, 8000000 ),
		UPGRADED( "Simpleton 800", 800, 12, 12000000 ),
		SPEEDER( "SpeedMaster 15", 800, 15, 8000000 ),
		HEAVY( "CargoMover 1100", 1100, 12, 8000000 );
		
		String name;
		int cargoCapacity;
		int speed;
		int price;
		
		TrainType( String name, int cargoCapacity, int speed, int price )
		{
			this.name = name;
			this.cargoCapacity = cargoCapacity;
			this.speed = speed;
			this.price = price;
		}
	}

	public void set( Train t )
	{
		this.pos.set( t.pos );
		this.type = t.type;
		this.cargo = t.cargo;
	}
	
	public static class TrainMove
	{
		int id;
		ArrayList<TrainAction> trainActions = new ArrayList<TrainAction>();
		
		public TrainMove()
		{
			
		}
		
		public TrainMove( int id )
		{
			this.id = id;
		}

		public void render( RailClient r )
		{
			Point2i lastPoint = null;
			for( int i = 0; i < trainActions.size(); i++ )
			{
				TrainAction ac = trainActions.get( i );
				if( ac.move != null )
				{
					if( lastPoint == null )
					{
						lastPoint = ac.move;
					}
					else
					{
						Point2i a = lastPoint;
						Point2i b = ac.move;
						r.color( Color.GREEN );
						r.line( r.map.getPX( a.x, a.y ), r.map.getPY( a.x, a.y )+1, r.map.getPX( b.x, b.y ), r.map.getPY( b.x, b.y )+1 );
						lastPoint = b;
					}
				}
			}
		}

		public TrainAction getFirstMoveAction()
		{
			for( int i = 0; i < trainActions.size(); i++ )
			{
				if( trainActions.get( i ).move != null ) return trainActions.get( i );
			}
			return null;
		}

		public TrainAction getLastMoveAction()
		{
			for( int i = trainActions.size()-1; i >= 0; i-- )
			{
				if( trainActions.get( i ).move != null ) return trainActions.get( i );
			}
			return null;
		}
	}
	
	public static class TrainAction
	{
		Cargo toBuy;
		Cargo toSell;
		int amount;
		Point2i move;
		Point2i city;
		
		public TrainAction()
		{
			
		}
		
		public TrainAction( Point2i move )
		{
			this.move = new Point2i( move );
		}
	}
}
