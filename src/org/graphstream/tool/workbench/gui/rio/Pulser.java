package org.graphstream.tool.workbench.gui.rio;

import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import java.awt.geom.Arc2D;

import javax.swing.JPanel;

public class Pulser
	extends JPanel
{
	private static final long serialVersionUID = 0x0600A000L;

	protected static final BasicStroke 	outterStroke 	= new BasicStroke( 1, BasicStroke.CAP_ROUND,  BasicStroke.JOIN_ROUND, 0, new float [] { 4, 5 }, 0 );
	protected static final Color		outterColor		= new Color( 0, 0.5f, 0.95f, 0.3f );
	protected static final BasicStroke 	areaStroke 		= new BasicStroke( 10, BasicStroke.CAP_ROUND,  BasicStroke.JOIN_ROUND );
	protected static final Color		areaColor		= new Color( 0, 0.5f, 0.95f, 0.1f );
	protected static final BasicStroke 	innerStroke 	= new BasicStroke( 5, BasicStroke.CAP_ROUND,  BasicStroke.JOIN_ROUND, 0, new float [] { 4, 5 }, 0 );
	protected static final Color		innerColor		= new Color( 0, 0, 0, 0.7f );
	
	XShape			shape;
	Arc2D.Float		inner;
	Arc2D.Float		area;
	Arc2D.Float		outter;
	Arc2D.Float		cover;
	
	GSLinker		linker;
	
	public Pulser( GSLinker linker )
	{
		setSize( 100, 100 );
		setOpaque(false);
		build();
		
		this.linker = linker;
		
		addMouseMotionListener( new MouseMotionAdapter()
		{
			public void mouseDragged( MouseEvent e )
			{
				int d = (int) Math.sqrt( 
						Math.pow(  - e.getX() + Pulser.this.getWidth() / 2, 2 ) +
						Math.pow(  - e.getY() + Pulser.this.getHeight() / 2, 2 )
				);
				
				
				
				if( d > getWidth() / 2 - 20 )
				{
					int [] p = { 
							Pulser.this.getX() + Pulser.this.getWidth() / 2,
							Pulser.this.getY() + Pulser.this.getHeight() / 2
					};
					int s = Math.max( 100, 2 * d );
					Pulser.this.setSize( s, s );
					Pulser.this.setLocation( p [0] - s / 2, p [1] - s / 2 );
				}
				else
				{
					Pulser.this.setLocation(
						Pulser.this.getX() + e.getX() - Pulser.this.getWidth() / 2,
						Pulser.this.getY() + e.getY() - Pulser.this.getHeight() / 2
					);
				}

				Pulser.this.rebuild();
				Pulser.this.repaint();
			}
		});
		
		addMouseListener( new MouseAdapter()
		{
			public void mouseReleased( MouseEvent e )
			{
				Pulser.this.linker.checkPulserCover();
			}
			
			public void mouseClicked( MouseEvent e )
			{
				if( e.getClickCount() > 1 )
					Pulser.this.linker.removePulser(Pulser.this);
			}
		} );
	}
	
	private void build()
	{
		shape = new XShape();

		outter 	= new Arc2D.Float(10,10,getWidth()-20,getHeight()-20,0,360,Arc2D.OPEN);
		cover 	= new Arc2D.Float(getX()+10,getY()+10,getWidth()-20,getHeight()-20,0,360,Arc2D.OPEN);
		inner	= new Arc2D.Float(getWidth()/2-10,getHeight()/2-10,20,20,0,360,Arc2D.OPEN);
		area 	= new Arc2D.Float(5,5,getWidth()-10,getHeight()-10,0,360,Arc2D.OPEN);
		
		shape.fill(	"area", 	outter,		areaColor );
		
		shape.draw(	"outter", 	outter,		outterColor,	outterStroke );
		shape.draw(	"area2", 	inner,		outterColor,		areaStroke );
		
		//shape.draw(	"outter",	outter,		outterColor,	outterStroke );
		
		shape.draw(	"inner",	inner,		innerColor,		innerStroke );
	}
	
	public void rebuild()
	{
		cover.setFrame( getX() + 10, getY() + 10, getWidth() - 20, getHeight() - 20 );
		inner.setFrame(getWidth()/2-10,getHeight()/2-10,20,20);
		outter.setFrame(5,5,getWidth()-10,getHeight()-10);
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
		
		shape.paint(g2d);
	}
}
