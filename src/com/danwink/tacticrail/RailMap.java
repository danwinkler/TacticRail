package com.danwink.tacticrail;

import java.util.ArrayList;

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
	
}
