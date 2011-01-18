/*
 * Copyright 2006 - 2011 
 *     Julien Baudry	<julien.baudry@graphstream-project.org>
 *     Antoine Dutot	<antoine.dutot@graphstream-project.org>
 *     Yoann Pigné		<yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin	<guilhelm.savin@graphstream-project.org>
 * 
 * This file is part of GraphStream <http://graphstream-project.org>.
 * 
 * GraphStream is a library whose purpose is to handle static or dynamic
 * graph, create them from scratch, file or any source and display them.
 * 
 * This program is free software distributed under the terms of two licenses, the
 * CeCILL-C license that fits European law, and the GNU Lesser General Public
 * License. You can  use, modify and/ or redistribute the software under the terms
 * of the CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
 * URL <http://www.cecill.info> or under the terms of the GNU LGPL as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C and LGPL licenses and that you accept their terms.
 */
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
