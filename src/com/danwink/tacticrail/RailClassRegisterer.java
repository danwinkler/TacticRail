package com.danwink.tacticrail;

import java.util.ArrayList;

import javax.vecmath.Point2f;

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
		
		GamePhase.class,
	};
}
