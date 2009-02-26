package org.miv.graphstream.tool.workbench.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.KeyStroke;

public final class WDialogManager
	implements ActionListener
{
	private static JMenu dialogMenu = null;
	private static int currentKey = 0;
	
	static final void init( WGui gui )
	{
		dialogMenu = (JMenu) gui.getWMenuBar().getRegisteredComponent("dialog");
	}
	
	static final void registerDialog( WDialog wd )
	{
		new WDialogManager(wd);
	}
	
	private WDialog managedDialog;
	private JCheckBoxMenuItem associatedMenuEntry;
	
	private WDialogManager( WDialog wd )
	{
		this.managedDialog = wd;
		this.associatedMenuEntry = new JCheckBoxMenuItem(wd.getName());
		
		if( currentKey < 10 )
		{
			try
			{
				associatedMenuEntry.setAccelerator( KeyStroke.getKeyStroke(
							Character.forDigit(currentKey++,10) ));
			}
			catch( Exception e )
			{
			}
		}
		
		associatedMenuEntry.addActionListener(this);
		dialogMenu.add(associatedMenuEntry);
		
		actionPerformed(null);
	}
	
	public void actionPerformed( ActionEvent e )
	{
		managedDialog.setVisible(associatedMenuEntry.isSelected());
	}
}
