package org.miv.graphstream.tool.workbench.gui;

import org.miv.graphstream.graph.Element;
import java.util.HashSet;

public class WClipboard
{
	HashSet<Element> clipboard;
	
	public WClipboard()
	{
		clipboard = new HashSet<Element>();
	}
	
	public void addToClipboard( Element e )
	{
		clipboard.add(e);
	}
	
	public void removeFromClipboard( Element e )
	{
		clipboard.remove(e);
	}
}
