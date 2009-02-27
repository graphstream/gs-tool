/*
 * This file is part of GraphStream.
 * 
 * GraphStream is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * GraphStream is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with GraphStream.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright 2006 - 2009
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */
package org.miv.graphstream.tool.workbench.gui;

import org.miv.graphstream.tool.workbench.WAlgorithm;
import org.miv.graphstream.tool.workbench.cli.CLI;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

public class WAlgorithmGUI
	extends JDialog
	implements ActionListener
{
	private static final long serialVersionUID = 0x0001L;
	
	public static ImageIcon ALGORITHM_ICON = null;
	static
	{
		ALGORITHM_ICON = new ImageIcon(ClassLoader.getSystemResource(
		"org/miv/graphstream/tool/workbench/gears_32.png"));
	}
	
	private WAlgorithm algorithm;
	private CLI cli;
	
	public WAlgorithmGUI( CLI cli, WAlgorithm algorithm )
	{
		this.cli = cli;
		this.algorithm = algorithm;
		
		setLayout( new BorderLayout() );
		setTitle( "Algorithm : " + algorithm.getName() );
		
		JTextPane desc = new JTextPane();
		desc.setText(algorithm.getDescription());
		desc.setEditable(false);
		add( desc, BorderLayout.CENTER );
		
		JButton run = new JButton("run");
			run.setActionCommand( "algorithm.run" );
			run.addActionListener(this);
		JButton options = new JButton( "options" );
		
		JPanel buttons = new JPanel();
		buttons.add(run);
		buttons.add(options);
		add( buttons, BorderLayout.SOUTH );
		
		setIconImage(ALGORITHM_ICON.getImage());
		
		pack();
	}
	
	public WAlgorithm getAlgorithm()
	{
		return algorithm;
	}
	
	public void actionPerformed( ActionEvent ae )
	{
		if( ae.getActionCommand().equals("algorithm.run") )
			execute();
	}
	
	public void execute()
	{
		SwingUtilities.invokeLater( new Runnable()
		{
			public void run()
			{
				System.err.printf( "execute algo\n" );
				algorithm.execute(cli.getCore().getActiveContext().getGraph());
			}
		} );
	}
}
