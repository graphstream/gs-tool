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
