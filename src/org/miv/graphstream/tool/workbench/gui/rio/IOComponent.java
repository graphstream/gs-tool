package org.miv.graphstream.tool.workbench.gui.rio;

import org.miv.graphstream.graph.GraphAttributesListener;
import org.miv.graphstream.graph.GraphElementsListener;
import org.miv.graphstream.graph.GraphListener;
import org.miv.graphstream.io2.Filter;
import org.miv.graphstream.io2.Input;
import org.miv.graphstream.io2.Output;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Paint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseAdapter;

import java.awt.geom.Arc2D;

import java.util.LinkedList;
import java.util.HashMap;

import javax.swing.JPanel;

public class IOComponent
	extends JPanel
	implements Filter
{
	private static final long serialVersionUID = 0x06001000L;
	
	protected static final Color 		channelColor 			= new Color(1,1,1,0.5f);
	protected static final BasicStroke 	channelStroke			= new BasicStroke( 5, BasicStroke.CAP_ROUND,  BasicStroke.JOIN_ROUND );
	protected static final BasicStroke 	channelLockedStroke		= new BasicStroke( 5, BasicStroke.CAP_ROUND,  BasicStroke.JOIN_ROUND, 0, new float [] { 5, 5 }, 0 );
	protected static final Color		channelActivatedColor	= new Color( 0.8f, 1, 0.2f, 0.5f );
	
	protected static final Color		connectorInColor		= new Color(1,0,0,0.4f);
	protected static final Color		connectorOutColor		= new Color(1,0.6f,0,0.4f);
	protected static final BasicStroke	connectorStroke			= new BasicStroke( 10, BasicStroke.CAP_ROUND,  BasicStroke.JOIN_ROUND );
	
	protected static final int			division			= 8;
	
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
	
	XShape								shape;
	
	GSLinker							linker;
	
	Paint 								fill;
	
	boolean								input;
	boolean								output;
	
	Input 								in;
	Output								out;
	
	LinkedList<ProximityPeer> 			proximity;
	HashMap<IOComponent,ProximityPeer>	proximityMap;
	
	LinkedList<LinkRenderer>			links;
	
	boolean								onDrag 			= false;
	boolean								mouseOver 		= false;
	boolean								onCollision 	= false;
	boolean								lock			= false;
	boolean								activated		= false;
	
	String								id;
	
	float								speed			= 0;
	float								acceleration	= 0;
	long								date			= System.currentTimeMillis();
	
	public IOComponent( String id, GSLinker linker, boolean input, boolean output )
	{
		this.id		= id;
		this.linker = linker;
		this.input	= input;
		this.output = output;
		
		//setBounds(0,0,64,64);
		setSize( 64, 64 );
		setPreferredSize( new Dimension(64,64) );
		
		buildShapes();
		if( ! input )
			shape.disable("input");
		if( ! output )
			shape.disable("output");
		
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
				
				IOComponent.this.activate( IOComponent.this.linker.isPulserCovered(IOComponent.this) );
				
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
				if( Math.sqrt( Math.pow( e.getX() - getWidth() / 2, 2 ) + Math.pow( e.getY() - getHeight() / 2, 2 ) ) < division )
				{
					IOComponent.this.lock();
				}
				else if( e.getClickCount() > 1 )
					IOComponent.this.click(e.getX(),e.getY());
			}
		});
		
		setOpaque(false);
	}
	
	protected void buildShapes()
	{
		int	div = division;
		
		XShape shape = new XShape();
		
		shape.draw( "channel", 	new Arc2D.Float(div,div,6*div,6*div,0,360,Arc2D.OPEN), 			channelColor, 		channelStroke );
		shape.draw( "input",	new Arc2D.Float(2*div,2*div,4*div,4*div,100,160,Arc2D.OPEN),	connectorInColor,	connectorStroke );
		shape.draw( "output",	new Arc2D.Float(2*div,2*div,4*div,4*div,80,-160,Arc2D.OPEN),	connectorOutColor,	connectorStroke );
		
		this.shape = shape;
	}
	
	protected void lock()
	{
		lock = ! lock;
		
		if( ! lock )
		{
			shape.setStroke( "channel", channelStroke );
		}
		else
		{
			shape.setStroke( "channel", channelLockedStroke );
		}
		
		repaint();
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
		/*
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
		*/
	    shape.paint(g2d);
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
		long date = Math.min( System.currentTimeMillis() - this.date, 1000 );
		
		float speed = (float) Math.min(
				Math.sqrt( Math.pow( x - getX(), 2 ) + Math.pow( y - getY(), 2 ) ),
				10 * date
		);
		
		speed /= (float) date;
		speed /= 10;
		
		acceleration = ( this.speed - speed );
		
		this.speed 	= speed;
		this.date	= System.currentTimeMillis();
		
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

			if( ! isLock() && ! pp.ioc.isLock() )
			{
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
		}
		
		checkCollision();
	}
	
	public void setOnDrag(boolean v)
	{
		onDrag = v;
		firePropertyChange("dragged",onDrag,v);
	}
	
	void setOnCollision( boolean v )
	{
		onCollision = v;
		repaint();
	}
	
	void setInput( boolean on )
	{
		this.input = on;
		
		if( on )	shape.enable("input");
		else		shape.disable("input");
		
		repaint();
	}
	
	void setOutput( boolean on )
	{
		this.output = on;
		
		if( on )	shape.enable("output");
		else		shape.disable("output");
		
		repaint();
	}
	
	public boolean isBeingDragged()
	{
		return onDrag;
	}
	
	public boolean isLock()
	{
		return lock;
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
	
	public void componentRemoved( IOComponent ioc )
	{
		ProximityPeer pp = proximityMap.get(ioc);
		
		if( pp != null )
		{
			proximityMap.remove(ioc);
			proximity.remove(pp);
		}
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
	
	public void activate( boolean on )
	{
		if( ! ( activated && on ) )
		{
			activated = on;
			shape.setColor("channel",on ? channelActivatedColor:channelColor);
			repaint();
		}
	}
	
// Filter
	
	/**
	 * @see org.miv.graphstream.io2.Input
	 */
	public void addGraphListener( GraphListener listener )
	{
		if( input && in != null )
			in.addGraphAttributesListener(listener);
	}
	
	/**
	 * @see org.miv.graphstream.io2.Input
	 */
	public void removeGraphListener( GraphListener listener )
	{
		if( input && in != null )
			in.removeGraphAttributesListener(listener);
	}
	
	/**
	 * @see org.miv.graphstream.io2.Input
	 */
	public void addGraphAttributesListener( GraphAttributesListener listener )
	{
		if( input && in != null )
			in.addGraphAttributesListener(listener);
	}
	
	/**
	 * @see org.miv.graphstream.io2.Input
	 */
	public void removeGraphAttributesListener( GraphAttributesListener listener )
	{
		if( input && in != null )
			in.removeGraphAttributesListener(listener);
	}
	
	/**
	 * @see org.miv.graphstream.io2.Input
	 */
	public void addGraphElementsListener( GraphElementsListener listener )
	{
		if( input && in != null )
			in.addGraphElementsListener(listener);
	}
	
	/**
	 * @see org.miv.graphstream.io2.Input
	 */
	public void removeGraphElementsListener( GraphElementsListener listener )
	{
		if( input && in != null )
			in.removeGraphElementsListener(listener);
	}
	
	/**
	 * @see org.miv.graphstream.io2.Output
	 */
	public void graphAttributeAdded( String graphId, String attribute, Object value )
	{
		if( output && out != null )
			out.graphAttributeAdded(graphId, attribute, value);
	}
	
	/**
	 * @see org.miv.graphstream.io2.Output
	 */
	public void graphAttributeChanged( String graphId, String attribute, Object oldValue, Object newValue )
	{
		if( output && out != null )
			out.graphAttributeChanged(graphId, attribute, oldValue, newValue);
	}
	
	/**
	 * @see org.miv.graphstream.io2.Output
	 */
	public void graphAttributeRemoved( String graphId, String attribute )
	{
		if( output && out != null )
			out.graphAttributeRemoved(graphId, attribute);
	}
	
	/**
	 * @see org.miv.graphstream.io2.Output
	 */
	public void nodeAttributeAdded( String graphId, String nodeId, String attribute, Object value )
	{
		if( output && out != null )
			out.nodeAttributeAdded(graphId, nodeId, attribute, value);
	}
	
	/**
	 * @see org.miv.graphstream.io2.Output
	 */
	public void nodeAttributeChanged( String graphId, String nodeId, String attribute, Object oldValue, Object newValue )
	{
		if( output && out != null )
			out.nodeAttributeChanged(graphId, nodeId, attribute, oldValue, newValue);
	}
	
	/**
	 * @see org.miv.graphstream.io2.Output
	 */
	public void nodeAttributeRemoved( String graphId, String nodeId, String attribute )
	{
		if( output && out != null )
			out.nodeAttributeRemoved(graphId, nodeId, attribute);
	}

	/**
	 * @see org.miv.graphstream.io2.Output
	 */
	public void edgeAttributeAdded( String graphId, String edgeId, String attribute, Object value )
	{
		if( output && out != null )
			out.edgeAttributeAdded(graphId, edgeId, attribute, value);
	}

	/**
	 * @see org.miv.graphstream.io2.Output
	 */
	public void edgeAttributeChanged( String graphId, String edgeId, String attribute, Object oldValue, Object newValue )
	{
		if( output && out != null )
			out.edgeAttributeChanged(graphId, edgeId, attribute, oldValue, newValue);
	}
	
	/**
	 * @see org.miv.graphstream.io2.Output
	 */
	public void edgeAttributeRemoved( String graphId, String edgeId, String attribute )
	{
		if( output && out != null )
			out.edgeAttributeRemoved(graphId, edgeId, attribute);
	}
	
	/**
	 * @see org.miv.graphstream.io2.Output
	 */
	public void nodeAdded( String graphId, String nodeId )
	{
		if( output && out != null )
			out.nodeAdded(graphId, nodeId);
	}

	/**
	 * @see org.miv.graphstream.io2.Output
	 */
	public void nodeRemoved( String graphId, String nodeId )
	{
		if( output && out != null )
			out.nodeRemoved(graphId, nodeId);
	}

	/**
	 * @see org.miv.graphstream.io2.Output
	 */
	public void edgeAdded( String graphId, String edgeId, String fromNodeId, String toNodeId, boolean directed )
	{
		if( output && out != null )
			out.edgeAdded(graphId, edgeId, fromNodeId, toNodeId, directed);
	}

	/**
	 * @see org.miv.graphstream.io2.Output
	 */
	public void edgeRemoved( String graphId, String edgeId )
	{
		if( output && out != null )
			out.edgeRemoved(graphId, edgeId);
	}
	
	/**
	 * @see org.miv.graphstream.io2.Output
	 */
	public void graphCleared( String graphId )
	{
		if( output && out != null )
			out.graphCleared(graphId);
	}
	
	/**
	 * @see org.miv.graphstream.io2.Output
	 */
	public void stepBegins( String graphId, double time )
	{
		if( output && out != null )
			out.stepBegins(graphId, time);
	}
}
