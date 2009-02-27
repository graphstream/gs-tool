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

import org.miv.graphstream.algorithm.Algorithm;
import org.miv.graphstream.algorithm.DynamicAlgorithm;
import org.miv.graphstream.algorithm.generator.Generator;
import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.tool.workbench.event.AlgorithmListener;

import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.LinkedList;

public class WAlgorithm
{	
	public static final LinkedList<WAlgorithm> ALGORITHMS = 
		new LinkedList<WAlgorithm>();
	
	public static synchronized void register( WAlgorithm wa )
	{
		ALGORITHMS.add(wa);
	}
	
	public static Iterator<WAlgorithm> algorithms()
	{
		return ALGORITHMS.iterator();
	}
	
	public static class Parameter
	{
		String name;
		Class<?> type;
		
		public Parameter( String name, Class<?> type )
		{
			this.name = name;
			this.type = type;
		}
		
		public String getName()
		{
			return name;
		}
		
		public String getClassName()
		{
			return type.getName();
		}
	}
	
	String name;
	String clazz;
	String desc;
	String category;
	LinkedList<Parameter> parameters;
	LinkedList<AlgorithmListener> listeners;
	boolean dynamize = false;
	
	public WAlgorithm( String clazz )
	{
		this( clazz, "unknown" );
	}
	
	public WAlgorithm( String clazz, String name)
	{
		this( clazz, name, "default" );
	}
	
	public WAlgorithm( String clazz, String name, String category )
	{
		this( clazz, name, category, "" );
	}
	
	public WAlgorithm( String clazz, String name, String category, String desc )
	{
		this( clazz, name, category, desc, (Parameter []) null );
	}
	
	public WAlgorithm( String clazz, String name, String category,
			String desc, Parameter ... parameters )
	{
		this.clazz 		= clazz;
		this.name 		= name;
		this.desc 		= desc;
		this.category  	= category;
		this.parameters = new LinkedList<Parameter>();
		this.listeners	= new LinkedList<AlgorithmListener>();
		
		if( parameters != null )
			for( Parameter param : parameters ) this.parameters.add(param);
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getClazz()
	{
		return clazz;
	}
	
	public String getCategory()
	{
		return category;
	}
	
	public String getDescription()
	{
		return desc;
	}
	
	public void setDescription( String desc )
	{
		this.desc = desc;
	}
	
	public int getParametersCount()
	{
		return parameters.size();
	}
	
	public Parameter getParameter( int i )
	{
		return parameters.get(i);
	}
	
	public void addParameter( String name, Class<?> type )
	{
		addParameter( new Parameter( name, type ) );
	}
	
	public void addParameter( Parameter param )
	{
		parameters.add(param);
	}
	
	public void addAlgorithmListener( AlgorithmListener al )
	{
		listeners.add(al);
	}
	
	public void removeAlgorithmListener( AlgorithmListener al )
	{
		listeners.remove(al);
	}
	
	protected void fireAlgorithmStart()
	{
		for( AlgorithmListener al : listeners )
			al.algorithmStart(this);
	}
	
	protected void fireAlgorithmError( String error )
	{
		for( AlgorithmListener al : listeners )
			al.algorithmError(this,error);
	}
	
	protected void fireAlgorithmEnd()
	{
		for( AlgorithmListener al : listeners )
			al.algorithmEnd(this);
	}
	
	public boolean isDynamic()
	{
		try
		{
			Class<?> c = Class.forName(clazz);
			return DynamicAlgorithm.class.isAssignableFrom(c) ||
				Generator.class.isAssignableFrom(c);
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		
		return false;
	}
	
	public void stop()
	{
		dynamize = false;
	}
	
	@SuppressWarnings("unchecked")
	public void execute( Graph graph, Object ... values )
	{
		fireAlgorithmStart();
		
		try
		{
			Class<?> c = (Class<?>) Class.forName(clazz);
			
			if( Generator.class.isAssignableFrom(c) )
			{
				generate( c, graph, values );
			}
			else
			{
				Algorithm algo = null;
			
				if( parameters == null )
					algo = (Algorithm) c.newInstance();
				else
				{
					Class<?> [] paramsTypes = new Class<?>[parameters.size()+1];
				
					paramsTypes [0] = Graph.class;
					for( int i = 0; i < parameters.size(); i++ )
					{
						paramsTypes [i+1] = parameters.get(i).type;
					}
				
					Object [] fullValues = new Object [paramsTypes.length];
					fullValues [0] = graph;
					if( values != null )
						for( int i = 0; i < values.length; i++ )
							fullValues [i+1] = values [i];
				
					Constructor<? extends Algorithm> co = (Constructor<? extends Algorithm>) 
						c.getConstructor(paramsTypes);
					algo = co.newInstance(fullValues);
				}
			
				algo.setGraph(graph);
			
				if( algo instanceof DynamicAlgorithm )
				{
					DynamicAlgorithm dalgo = (DynamicAlgorithm) algo;
					
					dynamize = true;
					dalgo.begin();
					while( dynamize )
					{
						dalgo.compute();
						try
						{
							Thread.sleep(200);
						}
						catch( Exception e )
						{
						}
					}
					dalgo.end();
				}
				else
					algo.compute();
			}
		}
		catch( Exception e )
		{
			String error;
			Throwable t = e;
			
			if( e instanceof java.lang.reflect.InvocationTargetException )
				t = ((java.lang.reflect.InvocationTargetException)e).getTargetException();
			
			error = t.getMessage() == null ? t.getClass().getName() : t.getMessage();
			
			t.printStackTrace();
			
			fireAlgorithmError( error );
		}
		
		fireAlgorithmEnd();
	}
	
	@SuppressWarnings("unchecked")
	protected void generate( Class<?> c, Graph graph, Object [] values )
		throws Exception
	{
		Generator gen = null;
		
		if( parameters == null )
			gen = (Generator) c.newInstance();
		else
		{
			Class<?> [] paramsTypes = new Class<?>[parameters.size()];
		
			paramsTypes [0] = Graph.class;
			for( int i = 0; i < parameters.size(); i++ )
			{
				paramsTypes [i] = parameters.get(i).type;
			}
			
			Constructor<? extends Generator> co = (Constructor<? extends Generator>)
				c.getConstructor(paramsTypes);
			gen = co.newInstance(values);
		}
	
		dynamize = true;
		gen.begin(graph);
		while( dynamize )
		{
			gen.nextElement();
			try
			{
				Thread.sleep(200);
			}
			catch( Exception e )
			{
			}
		}
		gen.end();
	}
}
