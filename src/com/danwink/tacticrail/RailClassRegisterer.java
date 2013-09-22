package com.danwink.tacticrail;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;

import javax.vecmath.Point2f;
import javax.vecmath.Point2i;

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
		Train.TrainMove.class,
		Train.TrainType.class,
		City.class,
		Cargo.class,
		
		Player.class,
		
		ArrayList.class,
		HashMap.class,
		
		Polygon.class,
		ComplexPolygon.class,
		Rectangle.class,
		
		Point2f.class,
		Point2i.class,
		int[].class,
		
		GamePhase.class,
	};
}
