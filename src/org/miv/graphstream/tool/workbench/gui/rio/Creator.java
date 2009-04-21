package org.miv.graphstream.tool.workbench.gui.rio;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

public class Creator
	extends JPanel
{
	private static final long serialVersionUID = 0x06008000L;

	protected static final BasicStroke STROKE  		= new BasicStroke( 3, BasicStroke.CAP_ROUND,  BasicStroke.JOIN_ROUND );
	protected static final BasicStroke BORDER  		= new BasicStroke( 1, BasicStroke.CAP_ROUND,  BasicStroke.JOIN_ROUND );
	protected static final BasicStroke OVER_STROKE  = new BasicStroke( 10, BasicStroke.CAP_ROUND,  BasicStroke.JOIN_ROUND );
	
	protected static final Color		OVER	= new Color( 0, 0.5f, 0.95f, 0.5f );
	protected static final Color		SELECT	= new Color( 0.8f, 1, 0.2f, 0.5f );
	
	GSLinker 	linker;
	
	int 		mouse = -1;
	
	boolean 	input = false;
	boolean 	output = false;
	
	static int	count = 1;
	
	public Creator( GSLinker linker )
	{
		this.linker = linker;
		
		setSize( 90, 90 );
		setLocation(10,10);
		setOpaque(false);
		
		addMouseMotionListener( new MouseMotionAdapter()
		{
			public void mouseMoved( MouseEvent e )
			{
				Creator.this.mouse = (int) ( ( e.getX() / (float) Creator.this.getWidth() ) * 3 );
				Creator.this.repaint();
			}
			
			public void mouseDragged( MouseEvent e )
			{
				Creator.this.setLocation(
						Creator.this.getX() + e.getX() - Creator.this.getWidth() / 2,
						Creator.this.getY() + e.getY() - Creator.this.getHeight() / 2
				);
			}
		});
		
		addMouseListener( new MouseAdapter()
		{
			public void mouseExited( MouseEvent e )
			{
				Creator.this.mouse = -1;
				Creator.this.repaint();
			}
			
			public void mouseClicked( MouseEvent e )
			{
				switch(Creator.this.mouse)
				{
				case 0:
					Creator.this.input = ! Creator.this.input;
					break;
				case 1:
					if( input && output )
						Creator.this.linker.addFilter( String.format( "creator:filter#%04d", count++ ) );
					else if( input )
						Creator.this.linker.addInput( String.format( "creator:input#%04d", count++ ) );
					else if( output )
						Creator.this.linker.addOutput( String.format( "creator:output#%04d", count++ ) );
					/*
					input  = false;
					output = false;
					*/
					break;
				case 2:
					Creator.this.output = ! Creator.this.output;
					break;
				}
				
				Creator.this.repaint();
			}
		});
	}
	
	public void repaint()
	{
		if( getParent() != null )
			getParent().repaint( getLocation().x, getLocation().y, getWidth(), getHeight() );
		super.repaint();
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

	    if( mouse == 0 || input )
	    {
	    	g2d.setColor( input ? SELECT : OVER );
	    	g2d.setStroke(OVER_STROKE);
	    	
	    	g2d.fillOval( getWidth() / 18 - 4, 4 * getHeight() / 9 - 4, getWidth() / 9 + 8, getHeight() / 9 + 8 );
    		g2d.drawLine( getWidth() / 9, getHeight() / 2, getWidth() / 3 + 4, getHeight() / 2 );
	    }
	    
	    if( mouse == 1 )
	    {
	    	g2d.setColor( OVER );
	    	g2d.setStroke(OVER_STROKE);
	    	
	    	g2d.drawOval( getWidth() / 3 + 4, getHeight() / 3 + 4, getWidth() / 3 - 8, getHeight() / 3 - 8 );
	    }
	    
	    if( mouse == 2 || output )
	    {
	    	g2d.setColor( output ? SELECT : OVER );
	    	g2d.setStroke(OVER_STROKE);

    		g2d.fillOval( 15 * getWidth() / 18 - 4, 4 * getHeight() / 9 - 4, getWidth() / 9 + 8, getHeight() / 9 + 8 );
    		g2d.drawLine( 15 * getWidth() / 18, getHeight() / 2, 2 * getWidth() / 3 - 4, getHeight() / 2 );
	    }
	    
		g2d.setColor( Color.WHITE );
		g2d.setStroke(STROKE);
		
		// Center circle
		g2d.drawOval( getWidth() / 3 + 4, getHeight() / 3 + 4, getWidth() / 3 - 8, getHeight() / 3 - 8 );
		
		// Left disk
		g2d.fillOval( getWidth() / 18, 4 * getHeight() / 9, getWidth() / 9, getHeight() / 9 );
		g2d.drawLine( getWidth() / 9, getHeight() / 2, getWidth() / 3 + 4, getHeight() / 2 );
		
		// Right disk
		g2d.fillOval( 15 * getWidth() / 18, 4 * getHeight() / 9, getWidth() / 9, getHeight() / 9 );
		g2d.drawLine( 15 * getWidth() / 18, getHeight() / 2, 2 * getWidth() / 3 - 4, getHeight() / 2 );
	}
}
