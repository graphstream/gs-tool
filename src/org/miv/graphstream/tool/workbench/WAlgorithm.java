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

/**
 * WAlgorithm is an object making a bridge between GraphStream Algorithm
 * and the workbench. It proposes features to easily manage algorithm's launch.
 *
 * It is composed of a name, a class, a category, a description and some parameters.
 * Category is used to release a classification (coloring,generator,measure,...).
 * Class is the class of the object to instantiate to create the algorithm.
 * Paremeters describe objects to give to the Algorithm constructor.
 * 
 * WAlgorithm provides a method to launch the algorithm. This execution depends of
 * if the algorithm is dynamic ( @link org.mv.graphstream.algorithm.DynamicAlgorithm )
 * or if it is a generator ( @link org.mv.graphstream.algorithm.generator.Generator )
 * or if it is simply an algorithm ( @link org.mv.graphstream.algorithm.Algorithm ).
 * 
 * @see org.miv.graphstream.tool.workbench.AlgorithmLoader
 */
public class WAlgorithm
{	
	/**
	 * Collection containing all loaded algorithms.
	 */
	static final LinkedList<WAlgorithm> ALGORITHMS = 
		new LinkedList<WAlgorithm>();
	/**
	 * Register a new algorithm.
	 * 
	 * @param wa the algorithm
	 */
	public static synchronized void register( WAlgorithm wa )
	{
		ALGORITHMS.add(wa);
	}
	/**
	 * Iterate on loaded algorithms.
	 * 
	 * @return an iterator
	 */
	public static Iterator<WAlgorithm> algorithms()
	{
		return ALGORITHMS.iterator();
	}
	/**
	 * Describe an algorithm's parameter.
	 * 
	 * A parameter is composed of some elements :
	 * <ul>
	 * <li>name : name of the parameter</li>
	 * <li>type : describe the type of the parameter</li>
	 * <li>clazz: describe the class of the parameter</li>
	 * <li>def  : default value</li>
	 * </ul>
	 * 
	 * Type can be the class name of clazz, or a special value.
	 * For example : int, boolean, (all primitive types), node, edge
	 * nodeid, edgeid.
	 */
	public static class Parameter
	{
		String name;
		String type;
		Class<?> clazz;
		String def;
		
		/**
		 * Build a new parameter from a name and a type.
		 * 
		 * @param name
		 * @param type
		 */
		public Parameter( String name, String type )
		{
			this( name, type, null );
		}
		/**
		 * Build a new parameter from a name, a type and a 
		 * default value.
		 * 
		 * @param name
		 * @param type
		 * @param def
		 */
		public Parameter( String name, String type, String def )
		{
			this.name 	= name;
			this.type 	= type;
			this.def	= def;
			
			try
			{
				if( type.equals("int") )
					clazz = Integer.TYPE;
				else if( type.equals("float") )
					clazz = Float.TYPE;
				else if( type.equals("double") )
					clazz = Double.TYPE;
				else if( type.equals("long") )
					clazz = Long.TYPE;
				else if( type.equals("boolean") )
					clazz = Boolean.TYPE;
				else if( type.equals("char") )
					clazz = Character.TYPE;
				else if( type.equals("node") )
					clazz = org.miv.graphstream.graph.Node.class;
				else if( type.equals("nodeid") )
					clazz = String.class;
				else if( type.equals("edge") )
					clazz = org.miv.graphstream.graph.Edge.class;
				else if( type.equals("edgeid") )
					clazz = String.class;
				else
					clazz = Class.forName(type);
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
			
			if( clazz.isEnum() )
				this.type = "enum";
		}
		/**
		 * Getter for the parameter's name.
		 */
		public String getName()
		{
			return name;
		}
		/**
		 * Getter for the parameter's type.
		 */
		public String getType()
		{
			return type;
		}
		/**
		 * Getter for the parameter's class.
		 */
		public Class<?> getTypeClass()
		{
			return clazz;
		}
		/**
		 * Getter for the parameter's default value.
		 */
		public String getDefaultValue()
		{
			return def;
		}
		/**
		 * Used to know is this parameter has a default value.
		 * 
		 * @return true is there is a default value.
		 */
		public boolean hasDefaultValue()
		{
			return def != null;
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
	
	public void addParameter( String name, String type )
	{
		addParameter( new Parameter( name, type ) );
	}
	
	public void addParameter( String name, String type, String def )
	{
		addParameter( new Parameter( name, type, def ) );
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
						paramsTypes [i+1] = parameters.get(i).getTypeClass();
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
		
		if( parameters == null || parameters.size() == 0 )
			gen = (Generator) c.newInstance();
		else
		{
			Class<?> [] paramsTypes = new Class<?>[parameters.size()];
		
			paramsTypes [0] = Graph.class;
			for( int i = 0; i < parameters.size(); i++ )
			{
				paramsTypes [i] = parameters.get(i).getTypeClass();
			}
			
			Constructor<? extends Generator> co = (Constructor<? extends Generator>)
				c.getConstructor(paramsTypes);
			gen = co.newInstance(values);
		}
	
		dynamize = true;
		gen.begin(graph);
		while( dynamize )
		{
			if( ! gen.nextElement() )
				break;
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
