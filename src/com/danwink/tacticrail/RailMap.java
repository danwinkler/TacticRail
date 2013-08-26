package com.danwink.tacticrail;

import java.util.ArrayList;

import javax.vecmath.Point2i;

import com.danwink.tacticrail.Point.PointType;
import com.phyloa.dlib.renderer.Graphics2DRenderer;
import com.phyloa.dlib.util.DMath;

public class RailMap 
{
	ArrayList<Border> borders = new ArrayList<Border>();
	Point[][] pointMap;
	
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

	public void render( Graphics2DRenderer g )
	{
		//g.scale( 2 );
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
	
	public boolean isValid( Railway r )
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
		
		return true;
	}
}
