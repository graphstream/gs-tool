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
package org.graphstream.tool.workbench;

import java.io.FileInputStream;
import java.nio.channels.FileChannel;

import javax.swing.SwingUtilities;
import javax.swing.DefaultBoundedRangeModel;

import org.graphstream.io.old.GraphReader;
import org.graphstream.io.old.GraphReaderFactory;
import org.graphstream.io.old.GraphReaderListenerHelper;
import org.graphstream.tool.workbench.event.ContextListener.GraphOperation;

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
			if( channel.isOpen() )
				progressionModel.setValue( (int) ( 100 * channel.position() / channel.size() ) );
			else
				progressionModel.setValue(100);
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
