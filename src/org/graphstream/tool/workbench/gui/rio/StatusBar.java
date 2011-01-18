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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.font.GlyphVector;
import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;

import javax.swing.JPanel;

public class StatusBar
	extends JPanel
{
	private static final long serialVersionUID = 0x06007000L;

	public static final int INFO	= 0;
	public static final int WARNING	= 1;
	public static final int ERROR 	= 2;
	public static final int NONE	= 3;
	
	protected static final BasicStroke STROKE  = new BasicStroke( 5, BasicStroke.CAP_BUTT,  BasicStroke.JOIN_ROUND );
	
	protected static final Color [][] COLORS =
	{
		{ new Color(0.8f,1,0.2f,0.6f), new Color(1,1,1,1) },
		{ new Color(1,0.6f,0,0.6f), new Color(1,1,1,1) },
		{ new Color(1,0.1f,0,0.6f), new Color(1,1,1,1) },
		{ new Color(0,0,0,0), new Color(0,0,0,0) }
	};
	
	Path2D.Float 	shape;
	String			text;
	
	public StatusBar( GSLinker linker )
	{
		super();
		
		shape = new Path2D.Float();
		shape.append( new Arc2D.Float( 3, 3, 20, 20, 90, 180, Arc2D.OPEN ), true );
		shape.lineTo( 200, 23 );
		shape.lineTo( 200, 3 );
		shape.closePath();
		
		text = "";
		
		//resize(linker);
		setSize( 210, 30 );
		setLocation( 200, 200 );
		
		linker.addComponentListener( new ComponentAdapter()
		{
			public void componentResized( ComponentEvent e )
			{
				StatusBar.this.setLocation( 
						e.getComponent().getWidth() - StatusBar.this.getWidth(),
						e.getComponent().getHeight() - StatusBar.this.getHeight()
				);
			}
		});
		
		setOpaque(false);
		setType(NONE);
	}
	
	public void resize( Component c )
	{
		Dimension dim = StatusBar.this.getSize();
		dim.height = 25;
		dim.width = c.getWidth();
		setSize(dim);
		
		setLocation( 0, c.getHeight() - getHeight() );
		
		validate();
		repaint();
	}
	
	public void setType( int type )
	{
		setBackground( COLORS [type][0] );
		setForeground( COLORS [type][1] );
	}
	
	public void setStatus( int type, String text )
	{
		this.text = text;
		
		setType(type);
		
		getParent().repaint( 
				0, getParent().getHeight() - getHeight(),
				getParent().getWidth(), getParent().getHeight()
		);
		
		repaint();
	}
	
	public void paintComponent( Graphics g )
	{
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.setRenderingHint( RenderingHints.KEY_INTERPOLATION,       RenderingHints.VALUE_INTERPOLATION_BICUBIC );
		g2d.setRenderingHint( RenderingHints.KEY_RENDERING,           RenderingHints.VALUE_RENDER_QUALITY );
		g2d.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,   RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
	    g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING,        RenderingHints.VALUE_ANTIALIAS_ON );
	    g2d.setRenderingHint( RenderingHints.KEY_COLOR_RENDERING,     RenderingHints.VALUE_COLOR_RENDER_QUALITY );
	    g2d.setRenderingHint( RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY );
	    g2d.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL,      RenderingHints.VALUE_STROKE_PURE );
	    
	    g2d.setColor( getBackground() );
	    g2d.setStroke(STROKE);
	    
	    g2d.draw(shape);
	    //g2d.setColor( Color.white );
	    
	    GlyphVector gv = getFont().createGlyphVector( g2d.getFontRenderContext(), text );
	    g2d.drawGlyphVector( gv, 10, getHeight() / 2 + 1 - (int) gv.getVisualBounds().getCenterY() / 2 );
	}
}
