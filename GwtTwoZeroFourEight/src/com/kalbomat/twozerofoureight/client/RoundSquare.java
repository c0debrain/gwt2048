package com.kalbomat.twozerofoureight.client;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

public class RoundSquare 
{
	private Position position;
	private double width;
	private double r;
	private CssColor colour;
	
//	public RoundSquare(Position position, double width, double r, CssColor colour)
//	{
//		this.position = position;
//		this.width = width;
//		this.r = r;
//		this.colour = colour;
//	}
	
	public RoundSquare(Position position, double width, CssColor colour)
	{
		this.position = position;
		this.width = width;
		this.r = 0.02 * width;
		this.colour = colour;
	}

	
	public void update(Position position, double width)
	{
		this.position = position;
		this.width = width;
		this.r = 0.02 * width;
	}
	
	public void update(CssColor colour)
	{
		this.colour = colour;
	}

	public Position getPosition()
	{
		return position;
	}
	
	public double getWidth()
	{
		return width;
	}
	
	public double getR()
	{
		return r;
	}
	
	public CssColor getColour()
	{
		return colour;
	}
	
	public void draw(Context2d context)
	{
		context.setFillStyle(colour);
		context.beginPath();
		context.moveTo(position.getX() + r, position.getY());
		context.lineTo(position.getX() + width - r, position.getY());
		context.quadraticCurveTo(position.getX() + width, position.getY(), position.getX() + width, position.getY() + r);
		context.lineTo(position.getX() + width, position.getY() + width - r);
		context.quadraticCurveTo(position.getX() + width, position.getY() + width, position.getX() + width - r, position.getY() + width);
		context.lineTo(position.getX() + r, position.getY() + width);
		context.quadraticCurveTo(position.getX(), position.getY() + width, position.getX(), position.getY() + width - r);
		context.lineTo(position.getX(), position.getY() + r);
		context.quadraticCurveTo(position.getX(), position.getY(), position.getX() + r, position.getY());
		context.fill(); 
	}
}