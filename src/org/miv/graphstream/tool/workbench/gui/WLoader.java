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

import org.miv.graphstream.tool.workbench.WCore;
import org.miv.graphstream.tool.workbench.WNotificationServer;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.SplashScreen;

public class WLoader
{
	public static String licence()
	{
		return 
		"GraphStream is free software: you can redistribute it and/or modify\n" +
		"it under the terms of the GNU General Public License as published by\n" +
		"the Free Software Foundation, either version 3 of the License, or\n" +
		"(at your option) any later version.\n\n" +
		"This program is distributed in the hope that it will be useful,\n" +
		"but WITHOUT ANY WARRANTY; without even the implied warranty of\n" +
		"MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n" +
		"GNU General Public License for more details.\n\n" +
		"You should have received a copy of the GNU General Public License\n" +
		"along with this program.  If not, see <http://www.gnu.org/licenses/>\n";
	}

	private static void splashInfo( String info, float percent )
	{
		SplashScreen ss = SplashScreen.getSplashScreen();

		if( ss == null )
		{
			System.err.printf( " * %s\n", info );
		}
		else
		{
			Graphics2D g = ss.createGraphics();
			Dimension d = ss.getSize();

			g.setColor( java.awt.Color.black );
			g.fillRect( 0, d.height - 20, d.width, 20 );
			g.setColor( java.awt.Color.white );
			g.drawString( info, 5, d.height - 5 );
			g.fillRect( d.width / 2, d.height - 10, d.width / 2 - 15, 5 );
			g.setColor( java.awt.Color.red );
			g.fillRect( d.width / 2, d.height - 10, (int) ( percent * ( d.width / 2 - 15) ), 5 );

			ss.update();
		}
	}

	public static final void launchWorkbench()
	{
		final float count = 8;
		float current = 0;

		splashInfo( "loading ressources : fonts...", current++/count );
		WFonts.load();

		splashInfo( "loading ressources : icons...", current++/count );
		WIcons.load();

		splashInfo( "loading ressources : gettext...", current++/count );
		WGetText.load();

		splashInfo( "loading user settings...", current++/count );
		WUserSettings.loadUserSettings();

		splashInfo( "loading notifications server...", current++/count );
		WNotificationServer.init(WCore.getCore());

		splashInfo( "loading module : help...", current++/count );
		WHelp.init();
		
		splashInfo( "loading module : search...", current++/count );
		WSearch.init();

		splashInfo( "loading gui...", current++/count );
		WGui.init();

		System.gc();

		splashInfo( "load complete", current++/count );

		WGui.display();
	}

	public static void main( String [] args )
	{
		System.err.printf( "%s\n", licence() );

		javax.swing.SwingUtilities.invokeLater( new Runnable()
		{
			public void run()
			{
				launchWorkbench();
			}
		} );
	}
}
