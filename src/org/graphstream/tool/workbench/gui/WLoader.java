/*
 * Copyright 2006 - 2011 
 *     Julien Baudry	<julien.baudry@graphstream-project.org>
 *     Antoine Dutot	<antoine.dutot@graphstream-project.org>
 *     Yoann Pigné		<yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin	<guilhelm.savin@graphstream-project.org>
 * 
 * This file is part of GraphStream <http://graphstream-project.org>.
 * 
 * GraphStream is a library whose purpose is to handle static or dynamic
 * graph, create them from scratch, file or any source and display them.
 * 
 * This program is free software distributed under the terms of two licenses, the
 * CeCILL-C license that fits European law, and the GNU Lesser General Public
 * License. You can  use, modify and/ or redistribute the software under the terms
 * of the CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
 * URL <http://www.cecill.info> or under the terms of the GNU LGPL as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C and LGPL licenses and that you accept their terms.
 */
package org.graphstream.tool.workbench.gui;

import org.graphstream.tool.workbench.WCore;
import org.graphstream.tool.workbench.WNotificationServer;

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
		final float count = 11;
		float current = 0;

		splashInfo( "loading ressources : fonts...", current++/count );
		WFonts.load();

		splashInfo( "loading ressources : icons...", current++/count );
		WIcons.load();

		splashInfo( "loading ressources : gettext...", current++/count );
		WGetText.load();

		splashInfo( "loading ressources : css...", current++/count );
		WCss.load();

		splashInfo( "loading user settings...", current++/count );
		WUserSettings.loadUserSettings();

		splashInfo( "loading notifications server...", current++/count );
		WNotificationServer.init(WCore.getCore());

		splashInfo( "loading module : help...", current++/count );
		WHelp.init();
		
		splashInfo( "loading module : search...", current++/count );
		WSearch.init();
		
		splashInfo( "loading module : actions...", current++/count );
		WActions.load();
		WActionAccessory.load();

		splashInfo( "loading gui...", current++/count );
		WGui.init();
		
		splashInfo( "loading module : options...", current++/count );
		WGui.loadOptions();

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
