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

import java.io.FileInputStream;
import java.nio.channels.FileChannel;

import javax.swing.SwingUtilities;
import javax.swing.DefaultBoundedRangeModel;

import org.miv.graphstream.io.GraphReader;
import org.miv.graphstream.io.GraphReaderFactory;
import org.miv.graphstream.io.GraphReaderListenerHelper;
import org.miv.graphstream.tool.workbench.event.ContextListener.GraphOperation;

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
	FileChannel	channel;
	
	final DefaultBoundedRangeModel progressionModel = new DefaultBoundedRangeModel(0,0,0,100);
	
	public WGraphReader()
	{
		fullMode 	= false;
		slowMode 	= false;
		mode		= Mode.Finish;
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
	
	public boolean isReading()
	{
		return mode != Mode.Finish;
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
		ctx.getGraph().clear();
		
		try
		{
			FileInputStream in = new FileInputStream(path);
			channel = in.getChannel();
			reader.begin(in);
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
			
			if( ctx instanceof DefaultContext )
			{
				( (DefaultContext) ctx ).fireGraphOperation(GraphOperation.ReadEnd,path);
			}
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
		
		try
		{
			progressionModel.setValue( (int) ( 100 * channel.position() / channel.size() ) );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	public void run()
	{
		next();
		
		if( mode != Mode.Finish )
			SwingUtilities.invokeLater(this);
	}
}
