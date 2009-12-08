package org.graphstream.tool.workbench.gui.rio;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

public class Trash
	extends JPanel
{
	private static final long serialVersionUID = 0x06009000L;

	protected static final Color BASIC = new Color( 0.8f, 1, 0.2f, 0.5f );
	protected static final Color TRASH = new Color( 1, 0, 0, 0.6f );
	protected static final BasicStroke STROKE = new BasicStroke( 3, BasicStroke.CAP_ROUND,  BasicStroke.JOIN_ROUND );
	protected static final BasicStroke TRASH_STROKE = new BasicStroke( 10, BasicStroke.CAP_ROUND,  BasicStroke.JOIN_ROUND );
	
	GSLinker 	linker;
	IOComponent	ioc;
	
	public Trash( GSLinker linker )
	{
		this.linker = linker;
		
		setSize( 50, 50 );
		setOpaque(false);
		setBackground( BASIC );
		setForeground( Color.BLACK );
		
		addMouseListener( new MouseAdapter()
		{
			public void mouseEntered( MouseEvent e )
			{
				Trash.this.ioc  = Trash.this.linker.getDraggedComponent();
				
				Trash.this.setBackground( Trash.this.ioc != null ? TRASH : BASIC );
				Trash.this.getParent().repaint(
						Trash.this.getX(),
						Trash.this.getY(),
						Trash.this.getWidth(),
						Trash.this.getHeight());
				Trash.this.repaint();
			}
			
			public void mouseExited( MouseEvent e )
			{
				Trash.this.setBackground(BASIC);
				Trash.this.getParent().repaint(
						Trash.this.getX(),
						Trash.this.getY(),
						Trash.this.getWidth(),
						Trash.this.getHeight());
				Trash.this.repaint();
				
				Trash.this.ioc = null;
			}
			
			public void mouseReleased( MouseEvent e )
			{
				if( Trash.this.ioc != null )
					Trash.this.linker.removeIOComponent( Trash.this.ioc );
				
				Trash.this.ioc = null;
			}
		});
		
		addMouseMotionListener( new MouseMotionAdapter()
		{
			public void mouseDragged( MouseEvent e )
			{
				Trash.this.setLocation(
						Trash.this.getX() + e.getX() - Trash.this.getWidth() / 2,
						Trash.this.getY() + e.getY() - Trash.this.getHeight() / 2
				);
			}
		});
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
	    
	    g2d.setStroke(TRASH_STROKE);
		g2d.setColor( getBackground() );
		
		g2d.drawOval( getWidth() / 2 - 10, getHeight() / 2 - 10, 20, 20 );
		g2d.drawLine( 
				5, 
				getHeight() - 5, 
				getWidth() / 2 + (int) ( Math.cos( 3 * Math.PI / 4 ) * 10 ),
				getHeight() / 2 + (int) ( Math.sin( 3 * Math.PI / 4 ) * 10 )
		);
		g2d.drawLine( 
				5, 
				5, 
				getWidth() / 2 + (int) ( Math.cos( -3 * Math.PI / 4 ) * 10 ),
				getHeight() / 2 + (int) ( Math.sin( -3 * Math.PI / 4 ) * 10 )
		);
		g2d.drawLine( 
				getWidth() - 5, 
				getHeight() - 5, 
				getWidth() / 2 + (int) ( Math.cos( Math.PI / 4 ) * 10 ),
				getHeight() / 2 + (int) ( Math.sin( Math.PI / 4 ) * 10 )
		);
		g2d.drawLine( 
				getWidth() - 5, 
				5, 
				getWidth() / 2 + (int) ( Math.cos( -Math.PI / 4 ) * 10 ),
				getHeight() / 2 + (int) ( Math.sin( -Math.PI / 4 ) * 10 )
		);
		
	    g2d.setStroke(STROKE);
		g2d.setColor( getForeground() );
		
		g2d.drawOval( getWidth() / 2 - 10, getHeight() / 2 - 10, 20, 20 );
		g2d.drawLine( 
				5, 
				getHeight() - 5, 
				getWidth() / 2 + (int) ( Math.cos( 3 * Math.PI / 4 ) * 10 ),
				getHeight() / 2 + (int) ( Math.sin( 3 * Math.PI / 4 ) * 10 )
		);
		g2d.drawLine( 
				5, 
				5, 
				getWidth() / 2 + (int) ( Math.cos( -3 * Math.PI / 4 ) * 10 ),
				getHeight() / 2 + (int) ( Math.sin( -3 * Math.PI / 4 ) * 10 )
		);
		g2d.drawLine( 
				getWidth() - 5, 
				getHeight() - 5, 
				getWidth() / 2 + (int) ( Math.cos( Math.PI / 4 ) * 10 ),
				getHeight() / 2 + (int) ( Math.sin( Math.PI / 4 ) * 10 )
		);
		g2d.drawLine( 
				getWidth() - 5, 
				5, 
				getWidth() / 2 + (int) ( Math.cos( -Math.PI / 4 ) * 10 ),
				getHeight() / 2 + (int) ( Math.sin( -Math.PI / 4 ) * 10 )
		);
	}
}
