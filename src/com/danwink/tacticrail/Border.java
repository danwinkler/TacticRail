package com.danwink.tacticrail;

import java.awt.Color;
import java.util.ArrayList;

import javax.vecmath.Point2f;

import com.phyloa.dlib.renderer.Graphics2DRenderer;

public class Border 
{
	ArrayList<Point2f> points = new ArrayList<Point2f>();
	BorderType type = BorderType.NORMAL;
	
	public void render( Graphics2DRenderer g )
	{
		switch( type )
		{
		case NORMAL:
			g.color( Color.black );
			break;
		case RIVER:
			g.color( Color.BLUE );
			break;
		}
		
		for( int i = 0; i < points.size()-1; i++ )
		{
			Point2f a = points.get( i );
			Point2f b = points.get( i+1 );
			g.line( a.x, a.y, b.x, b.y );
		}
	}
	
	public enum BorderType 
	{
		NORMAL,
		RIVER
	}
}
