package com.danwink.tacticrail;

import java.awt.Color;
import java.awt.Polygon;
import java.util.ArrayList;

import com.phyloa.dlib.renderer.Graphics2DRenderer;

public class ComplexPolygon
{
	ArrayList<ComplexPolygon> children = new ArrayList<ComplexPolygon>();
	ArrayList<Polygon> polys = new ArrayList<Polygon>();
	
	public void fill( Color a, Color b, Graphics2DRenderer g )
	{
		g.color( a );
		for( Polygon p : polys )
		{
			g.g.fill( p );
		}
		for( ComplexPolygon c : children )
		{
			c.fill( b, a, g );
		}
	}
	
	public static ComplexPolygon createFromPolys( ArrayList<Polygon> polys )
	{
		ComplexPolygon cp = new ComplexPolygon();
		for( int i = 0; i < polys.size(); i++ )
		{
			boolean isContained = false;
			Polygon a = polys.get( i );
			for( int j = 0; j < polys.size(); j++ )
			{
				if( i != j )
				{
					Polygon b = polys.get( j );
					if( isInside( a, b ) )
					{
						isContained = true;
						break;
					}
				}
			}
			if( !isContained ) cp.polys.add( a );
		}
		
		for( int i = 0; i < cp.polys.size(); i++ )
		{
			Polygon a = cp.polys.get( i );
			ArrayList<Polygon> cps = new ArrayList<Polygon>();
			for( int j = 0; j < polys.size(); j++ )
			{
				Polygon b = polys.get( j );
				if( isInside( b, a ) )
				{
					cps.add( b );
				}
			}
			cp.children.add( createFromPolys( cps ) );
		}
		return cp;
	}
	
	public static boolean isInside( Polygon a, Polygon b )
	{
		for( int i = 0; i < a.npoints; i++ )
		{
			if( !b.contains( a.xpoints[i], a.ypoints[i] ) ) return false;
		}
		return true;
	}
}
