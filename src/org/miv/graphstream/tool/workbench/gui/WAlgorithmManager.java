package org.miv.graphstream.tool.workbench.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class WAlgorithmManager
	implements ActionListener
{
	private static WMenuBar menuBar = null;
	private static ImageIcon icon = null;
	
	static final void init( WGui gui )
	{
		menuBar = gui.getWMenuBar();
	}
	
	static final void registerAlgorithm( WAlgorithmGUI wag )
	{
		new WAlgorithmManager(wag);
	}
	
	private WAlgorithmGUI managedDialog;
	private JMenuItem associatedMenuEntry;
	
	private WAlgorithmManager( WAlgorithmGUI wag )
	{
		this.managedDialog = wag;
		
		this.associatedMenuEntry = new JMenuItem(wag.getAlgorithm().getName(),WAlgorithmGUI.ALGORITHM_ICON);
		
		JComponent menu = null;

		if( wag.getAlgorithm().getCategory() == null ||
				wag.getAlgorithm().getCategory().equals("") ||
				wag.getAlgorithm().getCategory().equals("default") )
		{
			menu = menuBar.getRegisteredComponent("algorithm");
		}
		else
		{
			menu = menuBar.getRegisteredComponent("algorithm:" + wag.getAlgorithm().getCategory() );
			if( menu == null )
				menu = menuBar.getRegisteredComponent("algorithm");
		}
		
		associatedMenuEntry.addActionListener(this);
		((JMenu) menu).add(associatedMenuEntry);
	}
	
	public void actionPerformed( ActionEvent e )
	{
		managedDialog.setVisible(true);
	}
}
