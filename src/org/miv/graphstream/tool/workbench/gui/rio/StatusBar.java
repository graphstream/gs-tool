package org.miv.graphstream.tool.workbench.gui.rio;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JLabel;

public class StatusBar
	extends JLabel
{
	private static final long serialVersionUID = 0x06007000L;

	public static final int INFO	= 0;
	public static final int WARNING	= 1;
	public static final int ERROR 	= 2;
	public static final int NONE	= 3;
	
	protected static final Color [][] COLORS =
	{
		{ new Color(0.8f,1,0.2f,0.6f), new Color(0,0,0) },
		{ new Color(1,0.6f,0,0.6f), new Color(0,0,0) },
		{ new Color(1,0.6f,0,0.6f), new Color(0,0,0) },
		{ new Color(0,0,0,0), new Color(0,0,0,0) }
	};
	
	public StatusBar( GSLinker linker )
	{
		super( " " );
		
		resize(linker);
		
		linker.addComponentListener( new ComponentAdapter()
		{
			public void componentResized( ComponentEvent e )
			{
				StatusBar.this.resize(e.getComponent());
			}
		});
		
		setOpaque(true);
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
		setText(text);
		setType(type);
		
		getParent().repaint( 
				0, getParent().getHeight() - getHeight(),
				getParent().getWidth(), getParent().getHeight()
		);
		
		repaint();
	}
}
