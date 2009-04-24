package org.miv.graphstream.tool.workbench.gui.rio;

import java.io.File;
import java.net.URI;

public class Processor
{
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
