package org.miv.graphstream.tool.workbench.gui.rio;

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
