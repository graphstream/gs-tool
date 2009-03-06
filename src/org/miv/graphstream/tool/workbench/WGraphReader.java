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
package org.miv.graphstream.tool.workbench;

import javax.swing.SwingUtilities;

import org.miv.graphstream.io.GraphReader;
import org.miv.graphstream.io.GraphReaderFactory;
import org.miv.graphstream.io.GraphReaderListenerHelper;

/**
 * A reader using the Swing thread.
 * This prevent for concurrent modification of the graph.
 */
public class WGraphReader
	implements Runnable
{
	static enum Mode
	{
		Full,
		Begin,
		Step,
		SlowStep,
		End,
		Finish
	}
	
	String 		path;
	GraphReader reader;
	Mode 		mode;
	Context 	ctx;
	boolean		fullMode;
	boolean		slowMode;
	
	public WGraphReader()
	{
		fullMode = false;
		slowMode = false;
	}
	
	public void read( String file, Context ctx )
	{
		try
		{
			read( GraphReaderFactory.readerFor(file), file, ctx );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	public void read( GraphReader reader, String file, Context ctx )
	{
		this.reader = reader;
		this.path   = file;
		this.mode	= fullMode ? Mode.Full : Mode.Begin;
		this.ctx	= ctx;
		
		GraphReaderListenerHelper l = new GraphReaderListenerHelper( ctx.getGraph() );
		reader.addGraphReaderListener(l);
		
		SwingUtilities.invokeLater(this);
	}
	
	private void full()
	{
		try
		{
			reader.read(path);
			ctx.resetChanged();
			mode = Mode.Finish;
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	private void begin()
	{
		try
		{
			reader.begin(path);
			mode = slowMode ? Mode.SlowStep : Mode.Step;
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	private void step()
	{
		try
		{
			boolean b;
			
			if( mode == Mode.Step )
				b = reader.nextStep();
			else if( mode == Mode.SlowStep )
				b = reader.nextEvents();
			else
				return;
			
			ctx.resetChanged();
			
			mode = b ? mode : Mode.End;
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	private void end()
	{
		try
		{
			reader.end();
			mode = Mode.Finish;
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	private void next()
	{
		if( mode == Mode.Begin )
		{
			begin();
		}
		else if( mode == Mode.Step || mode == Mode.SlowStep )
		{
			step();
		}
		else if( mode == Mode.End )
		{
			end();
		}
		else if( mode == Mode.Full )
		{
			full();
		}
	}
	
	public void run()
	{
		next();
		
		if( mode != Mode.Finish )
			SwingUtilities.invokeLater(this);
	}
}
