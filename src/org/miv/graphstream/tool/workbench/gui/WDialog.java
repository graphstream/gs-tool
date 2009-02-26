package org.miv.graphstream.tool.workbench.gui;

import javax.swing.JDialog;
import javax.swing.JComponent;

public class WDialog extends JDialog
{
	private static final long serialVersionUID = 0x0001L;
	
	private JComponent content;
	
	public WDialog( String name, JComponent content )
	{
		super();
		
		this.content = content;
		
		setTitle(name);
		setName(name);
		add(content);
		
		pack();
		
		WDialogManager.registerDialog(this);
	}
	
	public JComponent getContent()
	{
		return content;
	}
}
