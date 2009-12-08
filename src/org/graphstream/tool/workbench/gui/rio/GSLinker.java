package org.graphstream.tool.workbench.gui.rio;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Shape;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

public class GSLinker 
	extends JLayeredPane
	implements PropertyChangeListener
{
	private static final long serialVersionUID = 0x06002000L;
	
	private static final Color BACKGROUND = Color.BLACK;
	
	HashMap<String,IOComponent> 	ioComponents = new HashMap<String,IOComponent>();
	HashMap<String,LinkRenderer>  	links 		 = new HashMap<String,LinkRenderer>();
	JPanel							ioContainer  = new JPanel();
	Splash							splash		 = new Splash();
	StatusBar						status;
	
	LinkedList<Shape>				pulserCover;					
	
	public GSLinker()
	{
		super();
		
		setPreferredSize( new java.awt.Dimension(600,400) );
		setBackground(BACKGROUND);
		setOpaque(true);
		
		pulserCover = new LinkedList<Shape>();
		status = new StatusBar(this);
		
		Trash trash = new Trash(this);
		trash.setLocation( 10, 10 );
		
		Creator creator = new Creator(this);
		creator.setLocation( trash.getX() + trash.getWidth(), trash.getY() + trash.getHeight() / 2 - creator.getHeight() / 2 );
		
		add( splash,			JLayeredPane.POPUP_LAYER );
		add( status,			JLayeredPane.POPUP_LAYER );
		add( creator,			JLayeredPane.POPUP_LAYER );
		add( trash,				JLayeredPane.DRAG_LAYER );
	}
	
	protected void addIOComponent( String name, IOComponent ioc )
	{
		ioc.setLocation( getWidth() / 2 - ioc.getWidth() / 2, getHeight() / 2 - ioc.getHeight() / 2 );
		ioc.activate( isPulserCovered(ioc) );
		add( ioc, JLayeredPane.MODAL_LAYER );
		
		for( IOComponent ioc2 : ioComponents.values() )
		{
			ioc2.componentAdded(ioc);
			ioc.componentAdded(ioc2);
		}
		
		ioComponents.put(name,ioc);
		ioc.addPropertyChangeListener("position",this);
	}
	
	public void addInput( String name )
	{
		IOComponent gsi = new IOComponent(name,this,true,false);
		addIOComponent(name,gsi);
		gsi.checkPosition();
	}
	
	public void addOutput( String name )
	{
		IOComponent gso = new IOComponent(name,this,false,true);
		addIOComponent(name,gso);
		gso.checkPosition();
	}
	
	public void addFilter( String name )
	{
		IOComponent gso = new IOComponent(name,this,true,true);
		addIOComponent(name,gso);
		gso.checkPosition();
	}
	
	public void removeIOComponent( IOComponent ioc )
	{
		if( ioc != null )
		{
			ioc.removePropertyChangeListener(this);
			ioComponents.remove(ioc.id);
			remove(ioc);
			validate();
			repaint();
			
			for( IOComponent ioc2 : ioComponents.values() )
			{
				ioc2.componentRemoved(ioc);
			}
			
			while( ioc.links.size() > 0 )
			{
				LinkRenderer link = ioc.links.poll();
				
				link.src.linkRemoved(link);
				link.trg.linkRemoved(link);
				
				links.remove(getLinkTag(link.src.id,link.trg.id));
				remove(link);
			}
		}
	}
	
	public void addPulser()
	{
		Pulser pulser = new Pulser( this );
		
		add( pulser, JLayeredPane.DEFAULT_LAYER );
		addPulserCover(pulser.cover);
	}
	
	void addPulserCover( Shape s )
	{
		pulserCover.add(s);
	}
	
	public void removePulser( Pulser pulser )
	{
		remove(pulser);
		pulserCover.remove(pulser.cover);
		checkPulserCover();
		revalidate();
		repaint();
	}
	
	public boolean isPulserCovered( IOComponent ioc )
	{
		for( Shape s : pulserCover )
			if( s.contains(ioc.getX()+ioc.getWidth()/2,ioc.getY()+ioc.getHeight()/2) ) return true;
		
		return false;
	}
	
	protected String getLinkTag( String src, String trg )
	{
		return src + "::" + trg;
	}
	
	public void link( String src, String trg )
	{
		String 			tag 	= getLinkTag(src,trg);
		LinkRenderer 	link 	= links.get(tag);
		
		if( link == null )
		{
			link = new LinkRenderer( ioComponents.get(src), ioComponents.get(trg) );
			add( link, 1 );
			links.put(tag,link);
			ioComponents.get(src).linkAdded(link);
			ioComponents.get(trg).linkAdded(link);
		}
		
		link.setVirtual(false);
	}
	
	public void virtualLink( String src, String trg )
	{
		String 			tag 	= getLinkTag(src,trg);
		LinkRenderer 	link 	= links.get(tag);
		
		if( link == null )
		{
			link = new LinkRenderer( ioComponents.get(src), ioComponents.get(trg), true );
			link.setVirtual(true);
			add( link, 1 );
			links.put(tag,link);
			ioComponents.get(src).linkAdded(link);
			ioComponents.get(trg).linkAdded(link);
		}
	}
	
	public void unlink( String src, String trg )
	{
		String 			tag 	= getLinkTag(src,trg);
		LinkRenderer 	link 	= links.get(tag);
		
		if( link != null )
		{
			remove(link);
			links.remove(tag);
		}
	}
	
	public void unlinkIfVirtual( String src, String trg )
	{
		String 			tag 	= getLinkTag(src,trg);
		LinkRenderer 	link 	= links.get(tag);
		
		if( link != null && link.isVirtual() )
		{
			links.remove(tag);
			JLayeredPane.getLayeredPaneAbove(link).remove(link);
		}
	}
	
	public IOComponent getDraggedComponent()
	{
		for( IOComponent ioc : ioComponents.values() )
			if( ioc.isBeingDragged() ) return ioc;
		
		return null;
	}
	
	public IOComponent getNearestIOComponent( int x, int y )
	{
		IOComponent ioc = null;
		float		distance = getWidth() * getHeight();
		
		for( IOComponent i : ioComponents.values() )
		{
			float d = (float) Math.sqrt( Math.pow( i.getX() - x, 2 ) + Math.pow( i.getY() - y, 2 ) );
			
			if( d < distance )
			{
				ioc = i;
				distance = d;
			}
		}
		
		return ioc;
	}
	
	public void display()
	{
		JFrame frame = new JFrame( "GSLinker" );
		frame.add(this);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setVisible(true);
	}
	
	protected int collisionExists( IOComponent ioc1, IOComponent ioc2 )
	{
		if( ioc1.getX() > ioc2.getX() && ioc1.getX() < ioc2.getX() + ioc2.getWidth() &&
				ioc1.getY() > ioc2.getY() && ioc1.getY() < ioc2.getY() + ioc2.getHeight() )
		{
			return Math.max( 1, (int) Math.sqrt( Math.pow(ioc1.getX()-ioc2.getX(),2)) );
		}

		if( ioc2.getX() > ioc1.getX() && ioc2.getX() < ioc1.getX() + ioc1.getWidth() &&
				ioc2.getY() > ioc1.getY() && ioc2.getY() < ioc1.getY() + ioc1.getHeight() )
		{
			return Math.max( 1, (int) Math.sqrt( Math.pow(ioc1.getX()-ioc2.getX(),2)) );
		}
		
		return 0;
	}
	
	public IOComponent getFirstCollision( IOComponent ioc )
	{
		int max = 0;
		IOComponent fc = null;
		
		for( IOComponent ioc2 : ioComponents.values() )
		{
			if( ioc != ioc2 )
			{
				int t = collisionExists(ioc,ioc2);
				
				if( t > max )
				{
					max = t;
					fc  = ioc2;
				}
			}
		}
		return fc;
	}

	public void propertyChange(PropertyChangeEvent evt)
	{
		if( evt.getPropertyName().equals("position") && 
				evt.getSource() instanceof IOComponent )
		{
			for( IOComponent ioc : ioComponents.values() )
			{
				if( ioc != evt.getSource() )
					ioc.componentMoved( (IOComponent) evt.getSource() );
			}
		}
	}
	
	public void splash( int x, int y, int width, int height, String title, JComponent c )
	{
		splash.init(x,y,width,height,title,c);
	}
	
	public void setStatusInfo( String txt )
	{
		status.setStatus(StatusBar.INFO,txt);
	}
	
	public void setStatusWarning( String txt )
	{
		status.setStatus(StatusBar.WARNING,txt);
	}
	
	public void setStatusError( String txt )
	{
		status.setStatus(StatusBar.ERROR,txt);
	}
	
	public void disableComponents()
	{
		for( IOComponent ioc : ioComponents.values() )
			ioc.setEnabled(false);
	}
	
	public void enableComponents()
	{
		for( IOComponent ioc : ioComponents.values() )
			ioc.setEnabled(true);
	}
	
	public void checkPulserCover()
	{
		for( IOComponent ioc : ioComponents.values() )
			ioc.activate(isPulserCovered(ioc));
	}
	
	class Splash
		extends JPanel
	{
		private static final long serialVersionUID = 0x06002001L;
		
		JLabel		title;
		JComponent 	component;
		JPanel		top;
		
		public Splash()
		{
			setVisible(false);
			setOpaque(false);
			setBorder( BorderFactory.createLineBorder(new Color(1,1,1,0.5f),1) );
			setLayout( new BorderLayout() );
			addComponentListener( new ComponentAdapter()
			{
				public void componentShown( ComponentEvent e )
				{
					GSLinker.this.disableComponents();
				}
				
				public void componentHidden( ComponentEvent e )
				{
					GSLinker.this.enableComponents();
				}
			});
			
			title = new JLabel(" ");
			
			top = new JPanel();
			top.setLayout( new BorderLayout() );
			top.add( title, BorderLayout.CENTER );
			
			JButton close = new JButton();
			close.setForeground( new Color(0,0,0,0.5f) );
			close.setBackground( new Color(1,1,1,0.7f) );
			close.setPreferredSize( new Dimension(title.getPreferredSize().height,title.getPreferredSize().height) );
			top.add(close,BorderLayout.EAST);
		
			add(top,BorderLayout.NORTH);
			
			top.addMouseMotionListener( new MouseMotionAdapter()
			{
				public void mouseDragged( MouseEvent e )
				{
					Splash.this.setLocation( 
							Splash.this.getX() + e.getX() - top.getWidth() / 2,
							Splash.this.getY() + e.getY() - top.getHeight() / 2
					);
				}
			});
			
			close.addActionListener( new ActionListener()
			{
				public void actionPerformed( ActionEvent e )
				{
					Splash.this.setVisible(false);
				}
			});
		}
		
		public void init( int x, int y, int width, int height, String title, JComponent c )
		{
			if( component != null )
			{
				remove(component);
				validate();
			}
			
			this.title.setText(title);
			
			setLocation(x,y);
			setSize(width,height+top.getHeight());
			
			component = c;
			
			add( component, BorderLayout.CENTER );
			validate();
			setVisible(true);
		}
	}
	
	public static void main( String [] args )
	{
		GSLinker gsl = new GSLinker();
		/*gsl.addInput("input:1");
		gsl.addInput("input:2");
		gsl.addInput("input:3");
		gsl.addOutput("output:1");
		gsl.addFilter("filter:1");
		gsl.link("input:1","filter:1");
		gsl.link("input:2","filter:1");
		gsl.link("input:3","output:1");
		gsl.link("filter:1","output:1");*/
		gsl.display();
	}
}
