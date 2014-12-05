package com.kalbomat.twozerofoureight.client;

public class Position 
{
	private double x, y;

	public Position()
	{
		this(0, 0);
	}

	public Position(double x, double y)
	{
		this.x = x;
		this.y = y;
	}

	public Position(Position p)
	{
		this(p.x, p.y);
	}

	public double getX()
	{
		return x;
	}

	public double getY()
	{
		return y;
	}
	
	public void sub(Position p)
	{
		this.x -= p.getX();
	    this.y -= p.getY();
	}
	
	public void add(Position p)
	{
		this.x += p.getX();
	    this.y += p.getY();
	}
	
	public void mult(double c)
	{
		this.x *= c;
	    this.y *= c;
	}

	@Override
	public String toString() 
	{		
		return "(" + x + ", " + y + ")";
	}
	
	public static Position sub(Position p1, Position p2)
	{
		return new Position(p1.getX() - p2.getX(), p1.getY() - p2.getY());
	}
}