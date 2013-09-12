package com.danwink.tacticrail;

import java.awt.Color;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Stack;

import javax.vecmath.Point2i;

import com.danwink.tacticrail.Point.PointType;
import com.phyloa.dlib.util.DHashList;
import com.phyloa.dlib.util.DMath;

public class RailMap 
{
	ComplexPolygon fill;
	ArrayList<Border> borders = new ArrayList<Border>();
	Point[][] pointMap;
	ArrayList<Railway> rails = new ArrayList<Railway>();
	ArrayList<City> cities = new ArrayList<City>();
	
	//Hex stuff 
	float s = 50;
	float h = DMath.sinf( DMath.PIF/3 ) * s;
	float r = DMath.cosf( DMath.PIF/3 ) * s;
	float bbh = s + 2 * h;
	float bbw = r * 2;
	
	public RailMap()
	{
		
	}
	
	public RailMap( int width, int height )
	{
		pointMap = new Point[width][height];
		for( int y = 0; y < height; y++ )
		{
			for( int x = 0; x < width; x++ )
			{
				pointMap[x][y] = new Point();
			}
		}
	}

	public void render( RailClient g )
	{
		g.color( Color.WHITE );
		fill.fill( Color.WHITE, Color.BLUE, g );
		for( Border b : borders )
		{
			b.render( g );
		}
		
		for( int y = 0; y < pointMap[0].length; y++ )
		{
			for( int x = 0; x < pointMap.length; x++ )
			{
				g.pushMatrix();
				g.translate( getPX(x,y), getPY(x,y) );
				pointMap[x][y].render( g );
				g.popMatrix();
			}
		}
		
		for( Railway r : rails )
		{
			r.render( g, this, new Color( g.players.get( r.owner ).color ) );
		}
	}

	public float getWidth()
	{
		return r + (pointMap.length-1)*bbw + r + r;
	}
	
	public float getHeight()
	{
		return r + (pointMap[0].length-1)*h + r;
	}
	
	public float getPX( int x, int y )
	{
		return r + x*bbw + (y % 2 == 0 ? 0 : r);
	}
	
	public float getPY( int x, int y )
	{
		return r + y * (h);
	}
	
	public Point2i getClosestPoint( float x, float y )
	{
		Point2i p = new Point2i();
		p.y = Math.round( (y-r)/h );
		p.x = Math.round( (x - r - (p.y % 2 == 0 ? 0 : r)) / bbw );
		return p;
	}
	
	public boolean isValid( Railway r, DHashList<Integer, Train> trains, Player player, ArrayList<Railway> railsToBuild )
	{
		Point p1 = pointMap[r.p1.x][r.p1.y];
		Point p2 = pointMap[r.p2.x][r.p2.y];
		
		int xdiff = r.p1.x - r.p2.x;
		int ydiff = r.p1.y - r.p2.y;
		
		//Points can't be nonetype
		if( p1.type == PointType.NONE ) return false;
		if( p2.type == PointType.NONE ) return false;
		
		//Points can't be the same point
		if( xdiff == 0 && ydiff == 0 ) return false;
		
		//If points are on same y, the points have to be exactly 1 away from each other
		if( ydiff == 0 && Math.abs( xdiff ) != 1 ) return false;
		
		if( Math.abs( ydiff ) > 1 ) return false;
		
		if( Math.abs( ydiff ) == 1 && !(xdiff == 0 || (r.p1.y % 2 == 0 ? xdiff == 1 : xdiff == -1)) ) return false;
		
		for( Railway toCheck : rails )
		{
			if( toCheck.equals( r ) ) return false;
		}
		
		for( Train t : trains )
		{
			if( t.owner == player.id )
			{
				if( t.pos.equals( r.p1 ) )
				{
					return true;
				}
				if( t.pos.equals( r.p2 ) )
				{
					return true;
				}
			}
		}
		
		for( Railway railToCheck : rails )
		{
			if( railToCheck.owner == r.owner && (railToCheck.p1.equals( r.p1 ) || railToCheck.p2.equals( r.p1 )) )
			{
				return true;
			}
			if( railToCheck.owner == r.owner && (railToCheck.p1.equals( r.p2 ) || railToCheck.p2.equals( r.p2 )) )
			{
				return true;
			}
		}
		
		for( Railway railToCheck : railsToBuild )
		{
			if( railToCheck.owner == r.owner && (railToCheck.p1.equals( r.p1 ) || railToCheck.p2.equals( r.p1 )) )
			{
				return true;
			}
			if( railToCheck.owner == r.owner && (railToCheck.p1.equals( r.p2 ) || railToCheck.p2.equals( r.p2 )) )
			{
				return true;
			}
		}
			
		return false;
	}

	public void addRails( ArrayList<Railway> buildAttempt )
	{
		for( Railway r : buildAttempt )
		{
			rails.add( r );
		}
	}
	
	public int getCost( Point2i a, Point2i b )
	{
		return pointMap[a.x][a.y].type.cost + pointMap[b.x][b.y].type.cost;
	}

	public City getCity( Point2i p )
	{
		for( int i = 0; i < cities.size(); i++ )
		{
			if( cities.get( i ).pos.equals( p ) ) return cities.get( i );
		}
		return null;
	}

	public ArrayList<Point2i> findRoute( Point2i start, Point2i end, int player, boolean allowOthers )
	{
		ArrayList<ArrayList<Point2i>> possibleRoutes = new ArrayList<ArrayList<Point2i>>();
		
		findRouteHelper( start, end, player, possibleRoutes, new Stack<Point2i>(), allowOthers );
		
		int length = -1;
		ArrayList<Point2i> shortest = null;
		for( int i = 0; i < possibleRoutes.size(); i++ )
		{
			ArrayList<Point2i> route = possibleRoutes.get( i );
			if( length == -1 || route.size() < length )
			{
				length = route.size(); shortest = route;
			}
		}
		return shortest;
	}
	
	private void findRouteHelper( Point2i start, Point2i end, int player, ArrayList<ArrayList<Point2i>> possibleRoutes, Stack<Point2i> visited, boolean allowOthers )
	{
		visited.push( start );
		ArrayList<Point2i> nextInLine = new ArrayList<Point2i>();
		for( int i = 0; i < rails.size(); i++ )
		{
			Railway r = rails.get( i );
			if( !allowOthers && player != r.owner ) continue;
			if( r.p1.equals( start ) )
				nextInLine.add( r.p2 );
			if( r.p2.equals( start ) )
				nextInLine.add( r.p1 );
		}
		for( int i = 0; i < nextInLine.size(); i++ )
		{
			Point2i p = nextInLine.get( i );
			if( p.equals( end ) )
			{
				visited.push( p );
				possibleRoutes.add( new ArrayList<Point2i>( visited ) );
				visited.pop();
			}
			else
			{
				if( !visited.contains( p ) )
				{
					findRouteHelper( p, end, player, possibleRoutes, visited, allowOthers );
				}
			}
		}
		visited.pop();
	}
}
