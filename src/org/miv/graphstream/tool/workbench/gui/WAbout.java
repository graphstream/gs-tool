package org.miv.graphstream.tool.workbench.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

public class WAbout
	extends JDialog
{
	private static final long serialVersionUID = 0x0001L;
	
	private static final WAbout about = new WAbout();
	
	public static void whatAbout()
	{
		about.setVisible(true);
	}
	
	private WAbout()
	{
		setTitle( "About" );
		setLayout( new BorderLayout() );
		setResizable(false);
		
		Icon splash = WorkbenchUtils.getImageIcon("splash");
		JLabel splashLabel = new JLabel(splash);
		splashLabel.setBounds(0,0,splash.getIconWidth(),splash.getIconHeight());
		
		JLayeredPane layers = new JLayeredPane();
		layers.setPreferredSize(splashLabel.getPreferredSize());
		layers.add( splashLabel, new Integer(0) );
		
		JLabel institute = new JLabel( "LITIS, University of Le Havre (FR)" );
		institute.setFont( WorkbenchUtils.getDefaultFont() );
		institute.setBounds( 15, 15, 
				(int) institute.getPreferredSize().getWidth(),
				(int) institute.getPreferredSize().getHeight() );
		
		layers.add( institute, new Integer(1) );
		
		JPanel autors = new JPanel();
		String [] autorsData = getAutors();
		autors.setLayout(new GridLayout(autorsData.length,1));
		autors.setOpaque(false);
		for( String a : autorsData )
		{
			JLabel al = new JLabel(a,JLabel.RIGHT);
			al.setFont( WorkbenchUtils.getDefaultFont() );
			autors.add(al);
		}
		autors.setBounds( splash.getIconWidth() - 15 - autors.getPreferredSize().width, 15,
				 autors.getPreferredSize().width,
				 autors.getPreferredSize().height );
		
		layers.add( autors, new Integer(1) );
		
		JLabel copyrights = new JLabel( "Copyright (c) 2006 - 2009" );
		copyrights.setFont(WorkbenchUtils.getDefaultFont());
		copyrights.setBounds( 15, splash.getIconHeight() - 15 - copyrights.getPreferredSize().height,
				copyrights.getPreferredSize().width, copyrights.getPreferredSize().height );
		layers.add( copyrights, new Integer(1) );
		
		add(layers,BorderLayout.CENTER);
		
		pack();
	}
	
	public String [] getAutors()
	{
		return 	new String [] {
				"Julien Baudry",
				"Antoine Dutot",
				"Yoann Pigné",
				"Guilhelm Savin" };
	}
}
