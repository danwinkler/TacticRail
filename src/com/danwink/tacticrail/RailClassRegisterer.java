package com.danwink.tacticrail;

import java.util.ArrayList;

import javax.vecmath.Point2f;

import com.phyloa.dlib.network.ClassRegister;

public class RailClassRegisterer 
{
	public static void register( ClassRegister c )
	{
		c.register( RailMessage.class );
		c.register( RailMessage.RailMessageType.class );
		
		c.register( Point.class );
		c.register( Point.PointType.class );
		c.register( Point[].class );
		c.register( Point[][].class );
		
		c.register( RailMap.class );
		c.register( Border.class );
		c.register( Border.BorderType.class );
		
		c.register( Player.class );
		
		c.register( ArrayList.class );
		
		c.register( Point2f.class );
	}
}
