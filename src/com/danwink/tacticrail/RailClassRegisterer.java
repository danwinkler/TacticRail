package com.danwink.tacticrail;

import java.awt.Color;
import java.util.ArrayList;

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
		
		Player.class,
		
		ArrayList.class,
		
		
		
		Point2f.class,
		Point2i.class,
		
		GamePhase.class,
	};
}
