package org.miv.graphstream.tool.workbench.gui;

import java.util.HashMap;
import javax.swing.text.html.StyleSheet;

public class WCss
{
	private static final HashMap<String,StyleSheet> sheets = new HashMap<String,StyleSheet>();
	
	public static void load()
	{
		String [][] toLoad = {
				{ "cliterm", "org/miv/graphstream/tool/workbench/ressources/css/cliterm.css" },
				{ "help", "org/miv/graphstream/tool/workbench/ressources/css/gswb-help.css" },
				{ "algorithm", "org/miv/graphstream/tool/workbench/ressources/css/gswb-algorithms.css" }
		};
		
		for( String [] entry : toLoad )
		{
			StyleSheet sheet = new StyleSheet();
			
			java.net.URL url = ClassLoader.getSystemResource(entry[1]);
			
			if( url != null )
				sheet.importStyleSheet(url);
			
			sheets.put(entry[0],sheet);
		}
	}
	
	public static StyleSheet getStyleSheet( String key )
	{
		return sheets.get(key);
	}
	
	public static boolean hasStyleSheet( String key )
	{
		return sheets.containsKey(key);
	}
}
