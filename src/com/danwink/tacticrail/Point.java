package com.danwink.tacticrail;

import java.awt.Color;
import java.awt.Polygon;

import com.phyloa.dlib.renderer.Graphics2DRenderer;

public class Point 
{
	static Polygon triangle;
	
	static {
		triangle = new Polygon();
		triangle.addPoint( -2, 1 );
		triangle.addPoint( 2, 1 );
		triangle.addPoint( 0, 2 );
	}
	
	PointType type = PointType.NORMAL;
	
	public void render( Graphics2DRenderer g )
	{
		switch( type )
		{
		case NORMAL:
			g.color( Color.black );
			g.fillOval( -3, -3, 5, 5 );
			break;
		case MOUNTAIN:
			g.color( Color.black );
			g.g.fill( triangle );
			break;
		case SMALLCITY:
			g.color( Color.red );
			g.fillOval( -4, -4, 8, 8 );
			g.color( Color.black );
			g.drawOval( -4, -4, 8, 8 );
		}
	}
	
	public Point()
	{
		
	}
	
	public enum PointType
	{
		NORMAL,
		MOUNTAIN,
		SMALLCITY,
		MEDIUMCITY,
		LARGECITY,
		NONE
	}
}
