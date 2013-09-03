package com.danwink.tacticrail;

import java.util.ArrayList;
import java.util.HashMap;

import javax.vecmath.Point2i;

import com.phyloa.dlib.util.DMath;

public class City
{
	public static final int SMALL = 1;
	public static final int MEDIUM = 2;
	public static final int LARGE = 3;
	
	int size = SMALL;
	Point2i pos;
	
	ArrayList<Cargo> produces = new ArrayList<Cargo>();
	int[] supplies;
	
	public City()
	{
		
	}

	public City( int x, int y, int size )
	{
		pos = new Point2i( x, y );
		this.size = size;
		
		for( int i = 0; i < Cargo.values().length; i++ )
		{
			if( Math.random() < .25 )
			{
				produces.add( Cargo.values()[i] );
			}
		}
		
		supplies = new int[Cargo.values().length];
		for( int i = 0; i < supplies.length; i++ )
		{
			supplies[i] = DMath.randomi( 1000, 9000 );
		}
	}

	public int sellPrice( int i )
	{
		return (20000-supplies[i])/50 + 5;
	}

	public int buyPrice( int i )
	{
		return (20000-supplies[i])/50 - 5;
	}
}
