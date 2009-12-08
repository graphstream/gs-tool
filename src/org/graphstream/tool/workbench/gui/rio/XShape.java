package org.graphstream.tool.workbench.gui.rio;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;

import java.util.HashMap;
import java.util.LinkedList;

public class XShape
{
	static class CSS
	{
		Color 	color;
		Shape	shape;
		Stroke	stroke;
		
		boolean	fill;
		boolean	draw;
		
		boolean enable;
		
		public CSS( Color color, Shape shape, Stroke stroke, boolean fill, boolean draw )
		{
			this.color 	= color;
			this.shape	= shape;
			this.stroke	= stroke;
			
			this.fill	= fill;
			this.draw	= draw;
			
			this.enable	= true;
		}
	}
	
	HashMap<String,CSS>	map;
	LinkedList<CSS> 	csss;
	
	public XShape()
	{
		map		= new HashMap<String,CSS>();
		csss	= new LinkedList<CSS>();
	}
	
	public void paint( Graphics2D g )
	{
		Color 	c 	= g.getColor();
		Stroke 	s	= g.getStroke();
		
		for( CSS css : csss )
		{
			if( css.enable )
			{
				g.setColor(		css.color != null	? css.color		: c );
				g.setStroke(	css.stroke != null 	? css.stroke 	: s );

				if( css.shape != null )
				{
					if( css.fill )	g.fill(css.shape);
					if( css.draw )	g.draw(css.shape);
				}
			}
		}
	}
	
	public void draw( String id, Shape shape, Color color, Stroke stroke )
	{
		append(id,shape,color,stroke,true,false);
	}
	
	public void draw( String id, Shape shape, Color color )
	{
		append(id,shape,color,null,true,false);
	}
	
	public void draw( String id, Shape shape, Stroke stroke )
	{
		append(id,shape,null,stroke,true,false);
	}
	
	public void draw( String id, Shape shape )
	{
		append(id,shape,null,null,true,false);
	}
	
	public void fill( String id, Shape shape, Color color, Stroke stroke )
	{
		append(id,shape,color,stroke,false,true);
	}
	
	public void fill( String id, Shape shape, Color color )
	{
		append(id,shape,color,null,false,true);
	}
	
	public void fill( String id, Shape shape, Stroke stroke )
	{
		append(id,shape,null,stroke,false,true);
	}
	
	public void fill( String id, Shape shape )
	{
		append(id,shape,null,null,false,true);
	}
	
	public void fillAndDraw( String id, Shape shape, Color color, Stroke stroke )
	{
		append(id,shape,color,stroke,true,true);
	}
	
	public void fillAndDraw( String id, Shape shape, Color color )
	{
		append(id,shape,color,null,true,true);
	}
	
	public void fillAndDraw( String id, Shape shape, Stroke stroke )
	{
		append(id,shape,null,stroke,true,true);
	}
	
	public void fillAndDraw( String id, Shape shape )
	{
		append(id,shape,null,null,true,true);
	}
	
	public void append( String id, Shape shape, Color color, Stroke stroke, boolean draw, boolean fill )
	{
		CSS css = new CSS(color,shape,stroke,fill,draw);
		
		csss.add( css );
		map.put(id,css);
	}
	
	public void setShape( String id, Shape shape )
	{
		if( map.containsKey(id) )
			map.get(id).shape = shape;
	}
	
	public void setColor( String id, Color color )
	{
		if( map.containsKey(id) )
			map.get(id).color = color;
	}
	
	public void setStroke( String id, Stroke stroke )
	{
		if( map.containsKey(id) )
			map.get(id).stroke = stroke;
	}
	
	public void enable( String id )
	{
		if( map.containsKey(id) )
			map.get(id).enable = true;
	}
	
	public void disable( String id )
	{
		if( map.containsKey(id) )
			map.get(id).enable = false;
	}
}
