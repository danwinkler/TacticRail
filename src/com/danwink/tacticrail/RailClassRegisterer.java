package com.danwink.tacticrail;

import java.awt.Color;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Vector;

import javax.vecmath.Point2f;
import javax.vecmath.Point2i;

import com.phyloa.dlib.network.ClassRegister;

public class RailClassRegisterer 
{
	public static Class[] classes = 
	{
		RailMessageType.class,
		
		Point.class,
		Point.PointType.class,
		Point[].class,
		Point[][].class,
		
		RailMap.class,
		Border.class,
		Border.BorderType.class,
		Railway.class,
		Train.class,
		Train.TrainType.class,
		City.class,
		Cargo.class,
		
		Player.class,
		
		ArrayList.class,
		
		Polygon.class,
		ComplexPolygon.class,
		Rectangle.class,
		
		Point2f.class,
		Point2i.class,
		int[].class,
		
		GamePhase.class,
	};
}
