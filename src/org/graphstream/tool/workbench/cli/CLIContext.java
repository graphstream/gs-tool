package org.graphstream.tool.workbench.cli;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;

import org.graphstream.graph.Graph;
import org.graphstream.stream.Sink;
import org.graphstream.stream.Source;
import org.graphstream.ui.swingViewer.Viewer;

public class CLIContext
{
	public static enum ConnectionMode
	{
		ConnectFull,
		ConnectAttributes,
		ConnectElements,
		ConnectReverseFull,
		ConnectReverseAttributes,
		ConnectReverseElements,
		DisconnectFull,
		DisconnectAttributes,
		DisconnectElements,
		DisconnectReverseFull,
		DisconnectReverseAttributes,
		DisconnectReverseElements
	}
	
	ConcurrentHashMap<String,String> 	env;
	ConcurrentHashMap<String,Source> 	sources;
	ConcurrentHashMap<String,Sink>		sinks;
	ConcurrentHashMap<String,Graph>		graphs;
	ConcurrentHashMap<String,Viewer>	activeViewers;
	
	public CLIContext()
	{
		env 	= new ConcurrentHashMap<String,String>();
		sources	= new ConcurrentHashMap<String,Source>();
		sinks	= new ConcurrentHashMap<String,Sink>();
		graphs	= new ConcurrentHashMap<String,Graph>();
		activeViewers = new ConcurrentHashMap<String,Viewer>();
	}
	
	public void set( String key, String val )
	{
		env.put(key,val);
	}
	
	public String get( String key )
	{
		return env.get(key);
	}
	
	public boolean has( String key )
	{
		return env.containsKey(key);
	}
	
	public void unset( String key )
	{
		env.remove(key);
	}
	
	@SuppressWarnings("unchecked")
	public void newStream( String id, String classname )
	{
		if( sources.containsKey(id) || sinks.containsKey(id) ||
				graphs.containsKey(id) )
			return;
		
		Class<?> cls = null;
		
		try
		{
			cls = Class.forName(classname);
			
			if( Graph.class.isAssignableFrom(cls) )
			{
				Constructor<? extends Graph> c = 
					(Constructor<? extends Graph>) cls.getConstructor();

				Graph g = c.newInstance();
				graphs.put(id,g);
				
				System.out.printf(":: new graph \"%s\" (%s)\n", id, classname );
			}
			else if( Source.class.isAssignableFrom(cls) )
			{
				Constructor<? extends Source> c = 
					(Constructor<? extends Source>) cls.getConstructor();

				Source src = c.newInstance();
				sources.put(id,src);
				
				System.out.printf(":: new source \"%s\" (%s)\n", id, classname );
			}
			else if( Sink.class.isAssignableFrom(cls) )
			{
				Constructor<? extends Sink> c = 
					(Constructor<? extends Sink>) cls.getConstructor();

				Sink sink = c.newInstance();
				sinks.put(id,sink);
				
				System.out.printf(":: new sink \"%s\" (%s)\n", id, classname );
			}
			else
			{
				System.out.printf( ":: not a stream\n" );
			}
		}
		catch( ClassNotFoundException e )
		{
			e.printStackTrace();
		}
		catch( NoSuchMethodException e )
		{
			e.printStackTrace();
		}
		catch( InvocationTargetException e )
		{
			e.printStackTrace();	
		}
		catch( IllegalArgumentException e )
		{
			e.printStackTrace();
		}
		catch( InstantiationException e )
		{
			e.printStackTrace();
		}
		catch( IllegalAccessException e )
		{
			e.printStackTrace();
		}
	}
	
	public void connect( String src, String snk, ConnectionMode mode )
	{
		Source 	source;
		Sink	sink;
		
		if( sources.containsKey(src) )
			source = sources.get(src);
		else if( graphs.containsKey(src) )
			source = graphs.get(src);
		else source = null;
		
		if( sinks.containsKey(snk) )
			sink = sinks.get(snk);
		else if( graphs.containsKey(snk) )
			sink = graphs.get(snk);
		else sink = null;
		
		switch(mode)
		{
		case ConnectReverseFull:
		{
			if( ( source instanceof Sink ) && ( sink instanceof Source ) )
				((Source) sink).addSink((Sink) source);
		}
		case ConnectFull:		source.addSink(sink); 			break;
		case ConnectReverseAttributes:
		{
			if( ( source instanceof Sink ) && ( sink instanceof Source ) )
				((Source) sink).addAttributeSink((Sink) source);
		}
		case ConnectAttributes:	source.addAttributeSink(sink); 	break;
		case ConnectReverseElements:
		{
			if( ( source instanceof Sink ) && ( sink instanceof Source ) )
				((Source) sink).addElementSink((Sink) source);
		}
		case ConnectElements:	source.addElementSink(sink);	break;

		case DisconnectReverseFull:
		{
			if( ( source instanceof Sink ) && ( sink instanceof Source ) )
				((Source) sink).removeSink((Sink) source);
		}
		case DisconnectFull:		source.removeSink(sink); 			break;
		case DisconnectReverseAttributes:
		{
			if( ( source instanceof Sink ) && ( sink instanceof Source ) )
				((Source) sink).removeAttributeSink((Sink) source);
		}
		case DisconnectAttributes:	source.removeAttributeSink(sink); 	break;
		case DisconnectReverseElements:
		{
			if( ( source instanceof Sink ) && ( sink instanceof Source ) )
				((Source) sink).removeElementSink((Sink) source);
		}
		case DisconnectElements:	source.removeElementSink(sink);	break;
		}
	}
	
	public Source getSource( String id )
	{
		if( graphs.containsKey(id) )
			return graphs.get(id);
		
		return sources.get(id);
	}
	
	public Sink getSink( String id )
	{
		if( graphs.containsKey(id) )
			return graphs.get(id);
		
		return sinks.get(id);
	}
	
	public Graph getGraph( String id )
	{
		return graphs.get(id);
	}
	
	public boolean hasGraph( String id )
	{
		return graphs.containsKey(id);
	}
	
	public boolean hasSource( String id )
	{
		return sources.containsKey(id) || hasGraph(id);
	}
	
	public boolean hasSink( String id )
	{
		return sinks.containsKey(id) || hasGraph(id);
	}
	
	public boolean hasStream( String id )
	{
		return
			hasSource(id) 	||
			hasSink(id);
	}
	
	public void display( String graphId, boolean on )
	{
		if( hasGraph(graphId) )
		{
			Viewer v;
			
			if( activeViewers.containsKey(graphId) )
			{
				v = activeViewers.get(graphId);
				
				if( ! on )
				{
					v.close();
					activeViewers.remove(graphId);
				}
			}
			else if( on )
			{
				boolean autolayout = has("display.autolayout") &&
					Boolean.valueOf(get("display.autolayout"));
				
				v = graphs.get(graphId).display(autolayout);
				activeViewers.put(graphId,v);
			}
		}
	}
}
