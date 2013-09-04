package com.danwink.tacticrail;

import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.util.ArrayList;

import javax.vecmath.Point2f;

import com.danwink.tacticrail.Border.BorderType;
import com.danwink.tacticrail.Point.PointType;
import com.phyloa.dlib.util.DMath;

public class RailMapGen 
{
	public static RailMap map1()
	{
		RailMap map = new RailMap( 50, 50 );
		
		ArrayList<Polygon> polys = new ArrayList<Polygon>();
		
		for( int i = 0; i < 10; i++ )
		{
			float xc =  map.getWidth()/2 + DMath.randomf( -map.getWidth()/3, map.getWidth()/3 );
			float yc =  map.getHeight()/2 + DMath.randomf( -map.getHeight()/3, map.getHeight()/3 );
			Polygon p = new Polygon();
			for( float a = 0; a < DMath.PI2F; a += DMath.PIF / 6 + .00001f )
			{
				float mag = DMath.randomf( .5f, .8f );
				float x = xc + DMath.cosf( a ) * mag * 600;
				float y = yc + DMath.sinf( a ) * mag * 600;
				p.addPoint( (int)(x*1000), (int)(y*1000) );
			}
			
			polys.add( p );
		}
		
		
		Area a = new Area( polys.get( 0 ) );
		for( int i = 1; i < polys.size(); i++ )
		{
			a.add( new Area( polys.get( i ) ) );
		}
		
		PathIterator pi = a.getPathIterator( null );
		
		Border b = new Border();
		b.type = BorderType.NORMAL;
		
		Polygon currentPoly;
		map.fill.add( currentPoly = new Polygon() );
		
		do
		{
			float[] f = new float[6];
			int type = pi.currentSegment( f );
			if( type != PathIterator.SEG_CLOSE )
			{
				if( type == PathIterator.SEG_MOVETO && b.points.size() > 0 )
				{
					map.borders.add( b );
					b.points.add( b.points.get( 0 ) );
					b = new Border();
					b.type = BorderType.NORMAL;
					map.fill.add( currentPoly = new Polygon() );
				}
				else
				{
					Point2f p = new Point2f( f[0]/1000.f, f[1]/1000.f );
					b.points.add( p );
					currentPoly.addPoint( (int)p.x, (int)p.y );
				}
			}
			
			pi.next();
		} while( !pi.isDone() );
		
		map.borders.add( b );
		b.points.add( b.points.get( 0 ) );
		
		for( int y = 0; y < map.pointMap[0].length; y++ )
		{
			for( int x = 0; x < map.pointMap.length; x++ )
			{
				if( !a.contains( map.getPX( x, y )*1000, map.getPY( x, y )*1000 ) )
				{
					map.pointMap[x][y].type = PointType.NONE;
				}
			}
		}
		
		int added = 0;
		while( added < 10 )
		{
			int x = DMath.randomi( 0, 49 );
			int y = DMath.randomi( 0, 49 );
			
			if( map.pointMap[x][y].type == PointType.NORMAL )
			{
				map.pointMap[x][y].type = PointType.SMALLCITY;
				map.cities.add( new City( x, y, City.SMALL ) );
				added++;
			}
		}
		
		return map;
	}
}
