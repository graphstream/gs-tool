package org.graphstream.tool.workbench.gui.rio;

import java.awt.Color;

import java.io.File;
import java.net.URI;

import java.util.LinkedList;

import javax.swing.JFileChooser;

public class Processor
{
	static LinkedList<String>	inputs	= new LinkedList<String>();
	static LinkedList<String>	outputs	= new LinkedList<String>();
	static LinkedList<String>	filters	= new LinkedList<String>();
	
	static
	{
		inputs.add("file");
		inputs.add("url");
		outputs.add("dgs");
		outputs.add("gml");
		outputs.add("svg");
		outputs.add("tlp");
		outputs.add("png");
		filters.add("attributes filters");
		filters.add("elements filters");
		filters.add("proxy");
	}
	
	public static int getInputsCount()
	{
		return inputs.size();
	}
	
	public static int getOutputsCount()
	{
		return outputs.size();
	}
	
	public static int getFiltersCount()
	{
		return filters.size();
	}
	
	public static String getInputLabel( int i )
	{
		return inputs.get(i);
	}
	
	public static String getOutputLabel( int i )
	{
		return outputs.get(i);
	}
	
	public static String getFilterLabel( int i )
	{
		return filters.get(i);
	}
	
	public static void selectFile( GSLinker linker, IOComponent ioc )
	{
		JFileChooser jfc = new JFileChooser(".");
		jfc.setOpaque(true);
		jfc.setBackground( new Color(0,0,0,0.5f) );
		jfc.setVisible(true);
		
		System.err.printf( "splash JFC\n" );
		
		linker.splash( ioc.getX(), ioc.getY(), 300, 200, "Choose a file", jfc );
	}
	
	public void processText( String txt, IOComponent ioc )
	{
		boolean	process = false;
		
		// Try local file
		if( ! process )
		{
			File f = new File(txt);

			if( f.exists() )
			{
				process = true;
				//linker.setStatusInfo( "input detects a file" );
				
				/*if( in != null )
				{
					in.removeGraphListener( InputComponent.this );
					in = null;
				}
				*/
			}
		}
		
		// Try url
		if( ! process )
		{
			try
			{
				URI uri = URI.create(txt);
				uri.toURL().openConnection();
				//linker.setStatusInfo( "input detects an URL" );
				process = true;
			}
			catch( Exception e )
			{
				process = false;
			}
		}
		
		// Try class
		if( ! process )
		{
			try
			{
				Class.forName(txt);
				//linker.setStatusInfo( "input detects a class" );
				process = true;
			}
			catch( Exception e )
			{
				process = false;
			}
		}
		
		if( ! process )
		{
			//linker.setStatusError( "input detects nothing" );
		}
	}
}
