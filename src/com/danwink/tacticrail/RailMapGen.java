package com.danwink.tacticrail;

import java.awt.Polygon;

import javax.vecmath.Point2f;

import com.danwink.tacticrail.Border.BorderType;
import com.danwink.tacticrail.Point.PointType;
import com.phyloa.dlib.util.DMath;

public class RailMapGen 
{
	public static RailMap map1()
	{
		RailMap map = new RailMap( 20, 20 );
		Polygon p = new Polygon();
		Border b = new Border();
		b.type = BorderType.NORMAL;
		
		for( float a = 0; a < DMath.PI2F; a += DMath.PIF / 6 + .00001f )
		{
			float mag = DMath.randomf( .5f, .8f );
			float x = DMath.cosf( a ) * mag * (map.getWidth()/2) + map.getWidth()/2;
			float y = DMath.sinf( a ) * mag * (map.getHeight()/2) + map.getHeight()/2;
			b.points.add( new Point2f( x, y ) );
			p.addPoint( (int)(x*1000), (int)(y*1000) );
		}
		
		b.points.add( b.points.get( 0 ) );
		
		for( int y = 0; y < map.pointMap[0].length; y++ )
		{
			for( int x = 0; x < map.pointMap.length; x++ )
			{
				if( !p.contains( map.getPX( x, y )*1000, map.getPY( x, y )*1000 ) )
				{
					map.pointMap[x][y].type = PointType.NONE;
				}
			}
		}
		
		map.borders.add( b );
		
		return map;
	}
}
