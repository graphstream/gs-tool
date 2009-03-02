package org.miv.graphstream.tool.workbench.xml;

import org.miv.graphstream.tool.workbench.gui.WGetText;

public class WXTest
{
	public static void main( String [] args )
	{
		long m1 = System.currentTimeMillis();
		WGetText.load();
		long m2 = System.currentTimeMillis();
		
		System.err.printf( "gettext loaded in %dms\n", m2 - m1 );
		
		System.err.printf( "%s = %s\n", "menu:file", WGetText.getText("menu:file") );
	}
}
