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
import org.miv.graphstream.graph.Graph;

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
	}
	
	String name;
	String clazz;
	String desc;
	String category;
	LinkedList<Parameter> parameters;
	
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
		//parameters.add(param);
	}
	
	@SuppressWarnings("unchecked")
	public void execute( Graph graph, Object ... values )
	{
		Algorithm algo = null;
		
		try
		{
			Class<? extends Algorithm> c = (Class<? extends Algorithm>) Class.forName(clazz);
			
			if( parameters == null )
				algo = c.newInstance();
			else
			{
				Class<?> [] paramsTypes = new Class<?>[parameters.size()];
				
				for( int i = 0; i < parameters.size(); i++ )
					paramsTypes [i] = parameters.get(i).type;
				
				Constructor<? extends Algorithm> co = c.getConstructor(paramsTypes);
				algo = co.newInstance(values);
			}
			
			algo.setGraph(graph);
			algo.compute();
		}
		catch( Exception e )
		{
			System.err.printf( "while running \"%s\" algorithm:\n%s\n", name,
					e.getMessage() == null ? e.getClass() : e.getMessage() );
		}
	}
}
