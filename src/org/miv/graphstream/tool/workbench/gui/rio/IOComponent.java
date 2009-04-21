package org.miv.graphstream.tool.workbench.gui.rio;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Paint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
//import java.awt.GradientPaint;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseAdapter;

import java.util.LinkedList;
import java.util.HashMap;

import javax.swing.JPanel;

public class IOComponent
	extends JPanel
{
	private static final long serialVersionUID = 0x06001000L;
	
	protected static final Color INPUT 	= new Color(1,0,0,0.4f);
	protected static final Color OUTPUT = new Color(0,0,1,0.4f);
	protected static final Color FILTER = new Color(0,1,0,0.4f);
	protected static final Color DISABLE= new Color(1,1,1,0.2f);
	/*
	protected static final GradientPaint INPUT_G = new GradientPaint(
			10,10,INPUT,
			34,34,INPUT.brighter().brighter());
	protected static final GradientPaint OUTPUT_G = new GradientPaint(
			10,10,OUTPUT,
			34,34,OUTPUT.brighter().brighter());
	protected static final GradientPaint FILTER_G = new GradientPaint(
			10,10,FILTER,
			34,34,FILTER.brighter().brighter());
	
	protected static final GradientPaint DISABLE = new GradientPaint(
			10,10,new Color(0.4f,0.4f,0.4f,0.6f),
			34,34,new Color(0.2f,0.2f,0.2f,0.7f));
	*/
	protected static final Color BORDER 				= new Color(1,1,1,0.9f);
	protected static final Color BORDER_ON_COLLISION	= new Color(1,0,0,0.7f);
	protected static final Color BORDER_ON_DRAG 		= new Color(1,1,1,0.6f);
	protected static final Color		OVER	= new Color( 0.8f, 1, 0.2f, 0.5f );
	
	protected static final BasicStroke BASIC_STROKE  = new BasicStroke( 1, BasicStroke.CAP_ROUND,  BasicStroke.JOIN_ROUND );
	protected static final BasicStroke BORDER_STROKE  = new BasicStroke( 2, BasicStroke.CAP_ROUND,  BasicStroke.JOIN_ROUND );
	protected static final BasicStroke CHANNEL_STROKE = new BasicStroke( 4, BasicStroke.CAP_ROUND,  BasicStroke.JOIN_ROUND );
	
	protected class ProximityPeer
	{
		int		 	distance;
		IOComponent	ioc;
		
		public ProximityPeer( IOComponent ioc )
		{
			this.ioc = ioc;
		}
		
		public void updateDistance()
		{
			distance = (int) Math.sqrt( 
					Math.pow( IOComponent.this.getX() - ioc.getX(), 2 ) + 
					Math.pow( IOComponent.this.getY() - ioc.getY(), 2 )
			);
		}
	}
	
	GSLinker	linker;
	
	Paint 		fill;
	
	boolean		input;
	boolean		output;
	
	LinkedList<ProximityPeer> 			proximity;
	HashMap<IOComponent,ProximityPeer>	proximityMap;
	
	LinkedList<LinkRenderer>			links;
	
	boolean		onDrag = false;
	boolean		mouseOver = false;
	boolean		onCollision = false;
	
	String		id;
	
	public IOComponent( String id, GSLinker linker, boolean input, boolean output )
	{
		this.id		= id;
		this.linker = linker;
		this.input	= input;
		this.output = output;
		
		setBounds(0,0,44,44);
		setPreferredSize( new Dimension(44,44) );
		
		if( input && output )
			fill = FILTER;
		else if( input )
			fill = INPUT;
		else
			fill = OUTPUT;
		
		proximity 		= new LinkedList<ProximityPeer>();
		proximityMap 	= new HashMap<IOComponent,ProximityPeer>();
		
		links			= new LinkedList<LinkRenderer>();
		
		input = true;
		
		addMouseMotionListener( new MouseMotionAdapter()
		{
			public void mouseDragged( MouseEvent e )
			{
				if( IOComponent.this.isEnabled() )
					IOComponent.this.tracked(e.getX(),e.getY());
			}
		});
		
		addMouseListener( new MouseAdapter()
		{
			public void mousePressed( MouseEvent e )
			{
				if( IOComponent.this.isEnabled() )
					IOComponent.this.setOnDrag( true );
				repaint();
			}
			
			public void mouseReleased( MouseEvent e )
			{
				if( IOComponent.this.isEnabled() )
					IOComponent.this.setOnDrag( false );
				repaint();
			}

			public void mouseEntered( MouseEvent e )
			{
				IOComponent.this.mouseOver = true;
				repaint();
			}
			
			public void mouseExited( MouseEvent e )
			{
				IOComponent.this.mouseOver = false;
				repaint();
			}
			
			public void mouseClicked( MouseEvent e )
			{
				if( e.getClickCount() > 1 )
					IOComponent.this.click(e.getX(),e.getY());
			}
		});
		
		setOpaque(false);
	}
	
	public void repaint()
	{
		if( getParent() != null )
			getParent().repaint( getX(), getY(), getWidth(), getHeight() );
		
		super.repaint();
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
		
	    g2d.setStroke(BORDER_STROKE);
	    
	    if( isEnabled() )
	    	g2d.setPaint(fill);
	    else
	    	g2d.setPaint(DISABLE);
	    
		g2d.fillRoundRect(10,10,24,24,5,5);
		
		if( onCollision )
			g2d.setColor(BORDER_ON_COLLISION);
		else
			g2d.setColor( onDrag ? BORDER_ON_DRAG : BORDER );
		g2d.drawRoundRect(10,10,24,24,7,7);
		
		g2d.setStroke(CHANNEL_STROKE);
		if( mouseOver )
			g2d.setPaint(OVER);
		g2d.drawOval(2,2,40,40);
		/*
		if( mouseOver )
		{
			g2d.setStroke(BASIC_STROKE);
			g2d.drawRect(0,0,getWidth(),getHeight());
		}
		*/
	}
	
	public void tracked( int x, int y )
	{
		moveTo(
				getLocation().x + x - getSize().width / 2,
				getLocation().y + y - getSize().height / 2
		);
		
		int i = 0;
		
		while( ! checkPosition() && i++ < proximity.size() );
	}
	
	public void moveTo( int x, int y )
	{
		Object o = getLocation();
		setLocation(x,y);
		firePropertyChange("position",o,getLocation());
		
		for( ProximityPeer pp : proximityMap.values() )
		{
			proximity.remove(pp);
			pp.updateDistance();

			int i = 0;

			while( i < proximity.size() && proximity.get(i).distance < pp.distance ) i++;
			proximity.add(i,pp);

			if( pp.distance < 100 && pp.ioc.output && input )
			{
				linker.virtualLink(id,pp.ioc.id);
			}
			else if( pp.distance < 100 && output && pp.ioc.input )
			{
				linker.virtualLink(pp.ioc.id,id);
			}
			else if( pp.distance > 150 )
			{
				linker.unlinkIfVirtual(id,pp.ioc.id);
				linker.unlinkIfVirtual(pp.ioc.id,id);
			}

		}
		
		checkCollision();
	}
	
	public void setOnDrag(boolean v)
	{
		onDrag = v;
		firePropertyChange("dragged",onDrag,v);
	}
	
	protected void setOnCollision( boolean v )
	{
		onCollision = v;
		repaint();
	}
	
	public boolean isBeingDragged()
	{
		return onDrag;
	}
	
	public void componentAdded( IOComponent ioc )
	{
		componentMoved(ioc);
	}
	
	public void componentMoved( IOComponent ioc )
	{
		ProximityPeer pp = proximityMap.get(ioc);
		
		if( pp == null )
		{
			pp = new ProximityPeer(ioc);
			proximityMap.put(ioc,pp);
		}
		
		proximity.remove(pp);
		pp.updateDistance();
		
		int i = 0;
		
		while( i < proximity.size() && proximity.get(i).distance < pp.distance ) i++;
		proximity.add(i,pp);
		
		checkCollision();
	}
	
	public void linkAdded( LinkRenderer link )
	{
		links.add(link);
	}
	
	public void linkRemoved( LinkRenderer link )
	{
		links.remove(link);
	}
	
	public void checkCollision()
	{
		if( proximity.size() > 0 && proximity.getFirst().distance < getWidth() )
		{
			setOnCollision(true);
		}
		else
		{
			setOnCollision(false);
		}
	}
	
	protected boolean checkPosition()
	{
		if( proximity.size() == 0 || proximity.getFirst().distance >= getWidth())
			return true;
		
		int [] bar = {0,0,0,0};
		int i = 0;
		int s = 0;
		
		while( i < proximity.size() && proximity.get(i).distance < getWidth() )
		{
			s 		+= getWidth() - proximity.get(i).distance;
			bar [0] += proximity.get(i).ioc.getX()		* ( getWidth() - proximity.get(i).distance );
			bar [1] += proximity.get(i).ioc.getY()		* ( getWidth() - proximity.get(i).distance );
			bar [2] += proximity.get(i).ioc.getWidth()	* ( getWidth() - proximity.get(i).distance );
			bar [3] += proximity.get(i).ioc.getHeight()	* ( getWidth() - proximity.get(i).distance );
			
			i++;
		}
		
		bar [0] /= s;
		bar [1] /= s;
		bar [2] /= s;
		bar [3] /= s;
		
		//if( Math.sqrt( Math.pow( bar [0] - getX(), 2 ) + Math.pow( bar [1] - getY(), 2 ) ) < getWidth() )
		{
			double teta = Math.PI + Math.atan2(
					bar [1] - getY(),
					bar [0] - getX()
			);
			
			double x = bar [0] + Math.cos(teta) * bar [2];
			double y = bar [1] + Math.sin(teta) * bar [3];
			
			moveTo( (int) x, (int) y );
			
			return false;
		}
		
		//return true;
	}
	
	protected void click( int x, int y )
	{
		
	}
}
