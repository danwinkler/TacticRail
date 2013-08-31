package com.danwink.tacticrail;

import java.util.ArrayList;

import javax.vecmath.Point2i;

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
}
