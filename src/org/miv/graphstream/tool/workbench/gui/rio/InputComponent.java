package org.miv.graphstream.tool.workbench.gui.rio;

import org.miv.graphstream.io2.Input;

import java.awt.Color;
import java.awt.Insets;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.io.File;
import java.net.URI;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class InputComponent
	extends IOComponent
{
	private static final long serialVersionUID = 0x06004000L;
	
	Input in;
	
	public InputComponent( String id, GSLinker linker )
	{
		super(id,linker,true,false);
	}
	
	protected void click( int x, int y )
	{
		linker.splash( getX()+x, getY()+y, 250, 50, "input: \"" + id + "\"", new InputDialog() );
	}
	
	class InputDialog
		extends JPanel
	{
		private static final long serialVersionUID = 0x06004001L;
		
		JTextField 	field;
		
		public InputDialog()
		{
			super();
			
			GridBagLayout bag = new GridBagLayout();
			
			setPreferredSize( new java.awt.Dimension(250,50) );
			setOpaque(true);
			setLayout( bag );
			setBackground( new Color(0,0,0,0.5f) );
			
			GridBagConstraints c = new GridBagConstraints();
			
			c.fill = GridBagConstraints.BOTH;
			c.ipadx = 10;
			c.insets = new Insets(0,10,0,10);
			field = new JTextField();
			field.setBackground( new Color(0,0,0) );
			field.setForeground( new Color(1,1,1,0.7f) );
			field.setFont(field.getFont().deriveFont(8));
			field.setBorder( BorderFactory.createLineBorder(new Color(1,1,1,0.5f),1) );
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.weightx = 3;
			
			bag.setConstraints(field,c);
			add(field);
			field.addActionListener( new ActionListener()
			{
				public void actionPerformed( ActionEvent e )
				{
					InputDialog.this.processText();
				}
			});
		}
		
		public void processText()
		{
			String 	txt 	= field.getText();
			boolean	process = false;
			
			// Try local file
			if( ! process )
			{
				File f = new File(txt);

				if( f.exists() )
				{
					process = true;
					linker.setStatusInfo( "input detects a file" );
				}
			}
			
			// Try url
			if( ! process )
			{
				try
				{
					URI uri = URI.create(txt);
					uri.toURL().openConnection();
					linker.setStatusInfo( "input detects an URL" );
					process = true;
				}
				catch( Exception e )
				{
					process = false;
				}
			}
			
			// Try class
			if( ! process )
			{
				try
				{
					Class.forName(txt);
					linker.setStatusInfo( "input detects a class" );
					process = true;
				}
				catch( Exception e )
				{
					process = false;
				}
			}
			
			if( ! process )
			{
				linker.setStatusError( "input detects nothing" );
			}
			
			repaint();
		}
	}
}
