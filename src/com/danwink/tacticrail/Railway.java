package com.danwink.tacticrail;

import java.awt.Color;
import java.util.ArrayList;

import javax.vecmath.Point2i;

import com.phyloa.dlib.renderer.Graphics2DRenderer;

public class Railway
{
	int owner;
	
	Point2i p1;
	Point2i p2;
	
	public Railway()
	{
		
	}
	
	public Railway( int owner, Point2i p1, Point2i p2 )
	{
		this.owner = owner;
		this.p1 = p1;
		this.p2 = p2;
	}

	public static int getPrice( ArrayList<Railway> rails, RailMap map )
	{
		int totalPrice = 0;
		for( Railway r : rails )
		{
			totalPrice += map.getCost( r.p1, r.p2 );
		}
		return totalPrice;
	}
	
	public boolean equals( Railway r )
	{
		return (r.p1.equals( p1 ) && r.p2.equals( p2 )) || (r.p1.equals( p2 ) && r.p2.equals( p1 ));
	}

	public void render( Graphics2DRenderer r, RailMap map, Color c )
	{
		r.color( c );
		r.line( map.getPX( p1.x, p1.y ), map.getPY( p1.x, p1.y ), map.getPX( p2.x, p2.y ), map.getPY( p2.x, p2.y ) );
	}
}
