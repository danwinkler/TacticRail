package com.danwink.tacticrail;

import javax.vecmath.Point2i;

public class Train
{
	TrainType type;
	Point2i pos;
	
	public Train()
	{
		
	}
	
	public Train( TrainType type, Point2i pos )
	{
		this.type = type;
		this.pos = pos;
	}
	
	public enum TrainType
	{
		BASIC( "Simpleton 500", 500, 8, 8000000 ),
		UPGRADED( "Simpleton 800", 800, 12, 12000000 ),
		SPEEDER( "SpeedMaster 15", 800, 15, 8000000 ),
		HEAVY( "CargoMover 1100", 1100, 12, 8000000 );
		
		String name;
		int cargoCapacity;
		int speed;
		int price;
		
		TrainType( String name, int cargoCapacity, int speed, int price )
		{
			this.name = name;
			this.cargoCapacity = cargoCapacity;
			this.speed = speed;
			this.price = price;
		}
	}
}
