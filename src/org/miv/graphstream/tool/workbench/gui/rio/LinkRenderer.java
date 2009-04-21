package org.miv.graphstream.tool.workbench.gui.rio;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import javax.swing.JPanel;

public class LinkRenderer 
	extends JPanel
	implements PropertyChangeListener
{
	private static final long serialVersionUID = 0x06003000L;
	
	protected static final BasicStroke LINK_STROKE  = new BasicStroke( 3, BasicStroke.CAP_ROUND,  BasicStroke.JOIN_ROUND );

	protected static final Color BORDER 		= new Color(1,1,1,0.9f);
	protected static final Color VIRTUAL_BORDER = new Color(1,1,1,0.3f);
	//protected static final Color BORDER_ON_DRAG = new Color(1,1,1,0.6f);
	
	protected static final Color COLOR_SRC		= new Color(1,1,1,0.1f);
	protected static final Color COLOR_TRG		= new Color(1,1,1,0.8f);
	
	IOComponent src;
	IOComponent trg;
	
	int [] p1 = {0,0};
	int [] p2 = {0,0};
	
	boolean		virtual;
	
	public LinkRenderer( IOComponent src, IOComponent trg )
	{
		this(src,trg,false);
	}
	
	public LinkRenderer( IOComponent src, IOComponent trg, boolean virtual )
	{
		this.src = src;
		this.trg = trg;
		this.virtual = virtual;
		
		setOpaque(false);
		
		rebuild();
		
		src.addPropertyChangeListener("position",this);
		src.addPropertyChangeListener("dragged",this);
		trg.addPropertyChangeListener("position",this);
		trg.addPropertyChangeListener("dragged",this);
		
		rebuild();
	}
	
	public void paintComponent( Graphics g )
	{
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.setRenderingHint( RenderingHints.KEY_INTERPOLATION,       RenderingHints.VALUE_INTERPOLATION_BICUBIC );
		g2d.setRenderingHint( RenderingHints.KEY_RENDERING,           RenderingHints.VALUE_RENDER_QUALITY );
		g2d.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,   RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
	    g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING,        RenderingHints.VALUE_ANTIALIAS_ON );
	    g2d.setRenderingHint( RenderingHints.KEY_COLOR_RENDERING,     RenderingHints.VALUE_COLOR_RENDER_QUALITY );
	    g2d.setRenderingHint( RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY );
	    g2d.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL,      RenderingHints.VALUE_STROKE_PURE );
		
		//g2d.setColor( virtual ? VIRTUAL_BORDER : BORDER );
	    g2d.setPaint( new GradientPaint( 5 + p1[0], 5 + p1[1], COLOR_TRG, 5 + p2[0], 5 + p2[1], COLOR_SRC ) );
	    g2d.setStroke(LINK_STROKE);
	    g2d.drawLine( 5 + p1[0], 5 + p1[1], 5 + p2[0], 5 + p2[1] );
	    
	    //g2d.fillOval( 5 + p1[0] - 5, 5 + p1[1] - 5, 10, 10 );
	    //g2d.drawRect(0,0,getWidth(),getHeight());
	}

	public void propertyChange(PropertyChangeEvent evt)
	{
		rebuild();
	}
	
	protected void computePoints()
	{
		int x1,x2,y1,y2;
	    
	    if( src.getX() > trg.getX() )
	    {
	    	x1 = 0;
	    	x2 = Math.abs(src.getLocation().x - trg.getLocation().x);
	    }
	    else
	    {
	    	x1 = Math.abs(src.getLocation().x - trg.getLocation().x);
	    	x2 = 0;
	    }
	    
	    if( src.getY() > trg.getY() )
	    {
	    	y1 = 0;
	    	y2 = Math.abs(src.getLocation().y - trg.getLocation().y);
	    }
	    else
	    {
	    	y1 = Math.abs(src.getLocation().y - trg.getLocation().y);
	    	y2 = 0;
	    }
	    
	    double teta = 0;
	    
	    double x = x1 - x2;
	    double y = y1 - y2;
	    
	    int c = src.getWidth() / 2;
	    
	    teta = Math.atan2(y, x) + Math.PI;
	    
	    p1 [0] = x1 + (int) ( Math.cos(teta) * c );
	    p1 [1] = y1 + (int) ( Math.sin(teta) * c );
	    p2 [0] = x2 + (int) ( Math.cos(Math.atan2(-y, -x) + Math.PI) * c );
	    p2 [1] = y2 + (int) ( Math.sin(Math.atan2(-y, -x) + Math.PI) * c );
	}
	
	protected void rebuild()
	{
		Dimension dim = getPreferredSize();
		
		dim.setSize(
				Math.abs(src.getLocation().x - trg.getLocation().x) + 10,
				Math.abs(src.getLocation().y - trg.getLocation().y) + 10
		);
		
		setLocation(
				Math.min(src.getLocation().x,trg.getLocation().x) + src.getWidth() / 2 - 5,
				Math.min(src.getLocation().y,trg.getLocation().y) + src.getHeight() / 2 - 5
		);
		
		setPreferredSize(dim);
		setSize(dim);

		computePoints();
		revalidate();
		repaint();
	}
	
	public void setVirtual( boolean on )
	{
		virtual = on;
	}
	
	public boolean isVirtual()
	{
		return virtual;
	}
}
