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
package org.graphstream.tool.workbench.cli;

import org.graphstream.graph.Node;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.tool.workbench.cli.CLIContext.ConnectionMode;
import org.graphstream.stream.Source;
import org.graphstream.stream.Sink;
import org.graphstream.stream.file.FileSource;
import org.graphstream.algorithm.generator.Generator;

import java.io.FileInputStream;
import java.io.Reader;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.net.MalformedURLException;
import java.net.URL;
import java.lang.reflect.Constructor;

public class CLIParser implements CLIParserConstants {
        static class KeyVal
        {
                String key;
                Object val;

                public KeyVal( String key, Object val )
                {
                        this.key = key;
                        this.val = val;
                }
        }

        public static enum Element
        {
                graph,
                node,
                edge
        }

        CLIContext ctx;
        PrintStream out = System.out;
        String parserAsSourceId;
        long currentTimeId = 0;

        public CLIParser()// Context ctx )
        {
                this.ctx = new CLIContext();
                parserAsSourceId = String.format("CLI_%X_%X",Thread.currentThread().getId(),System.currentTimeMillis());
                initEnv();
        }

        protected void initEnv()
        {
                ctx.set("package.graph","org.graphstream.graph.implementations");
                ctx.set("package.generator","org.graphstream.algorithm.generator");
                ctx.set("display.autolayout","true");
                ctx.set("stream.event.betweenStep","100");
        }

        public void read( String inPath )
        {
                try
                {
                        ReInit( new FileInputStream(inPath) );
                        start();
                }
                catch( Exception e )
                {
                        e.printStackTrace();
                }
        }

        public void read( InputStream in )
        {
                ReInit(in);
                try
                {
                        start();
                }
                catch( Exception e )
                {
                        e.printStackTrace();
                }
        }

        public void read( Reader in )
        {
                ReInit(in);
                try
                {
                        start();
                }
                catch( Exception e )
                {
                        e.printStackTrace();
                }
        }

        public void begin( String inPath )
        {
                try
                {
                        ReInit( new FileInputStream(inPath) );
                        start();
                }
                catch( Exception e )
                {
                        e.printStackTrace();
                }
        }

        public void begin( InputStream in )
        {
                ReInit(in);
                try
                {
                        start();
                }
                catch( Exception e )
                {
                        e.printStackTrace();
                }
        }

        public void begin( Reader in )
        {
                ReInit(in);
                try
                {
                        start();
                }
                catch( Exception e )
                {
                        e.printStackTrace();
                }
        }

        public void init( InputStream in )
        {
                if( jj_input_stream == null )
                {
                        try
                        {
                                jj_input_stream = new SimpleCharStream(in, null, 1, 1);
                        }
                        catch(java.io.UnsupportedEncodingException e)
                        {
                                throw new RuntimeException(e);
                        }

                token_source = new CLIParserTokenManager(jj_input_stream);
                token = new Token();

                jj_ntk = -1;
                jj_gen = 0;

                for (int i = 0; i < jj_la1.length; i++) jj_la1[i] = -1;
        }
        else
        {
                ReInit(in);
        }
        }

        public void init( Reader in )
        {
                if( jj_input_stream == null )
                {
                        jj_input_stream = new SimpleCharStream(in, 1, 1);

                token_source = new CLIParserTokenManager(jj_input_stream);
                token = new Token();

                jj_ntk = -1;
                jj_gen = 0;

                for (int i = 0; i < 10; i++) jj_la1[i] = -1;
        }
        else
        {
                ReInit(in);
        }
        }

        protected String unquote( String str )
        {
                if( str.startsWith("\u005c"") && str.endsWith("\u005c"") )
                        return str.substring(1,str.length()-1);
                else if( str.startsWith("\u005c"") )
                        return str.substring(1);
                else if( str.endsWith("\u005c"") )
                        return str.substring(0,str.length()-1);

                return str;
        }

        protected void streamConnection( String id1, String id2, String op )
        {
                if( ! ctx.hasStream(id1) )
                {
                        error("unknown stream", id1);
                        return;
                }

                if( ! ctx.hasStream(id2) )
                {
                        error("unknown stream", id2);
                        return;
                }

                op = op.toLowerCase();

                if( op.equals("<--") )
                        ctx.connect(id2,id1,ConnectionMode.ConnectFull);
                else if( op.equals("-->") )
                        ctx.connect(id1,id2,ConnectionMode.ConnectFull);
                else if( op.equals("<->") )
                        ctx.connect(id1,id2,ConnectionMode.ConnectReverseFull);

                if( op.equals("!--") )
                        ctx.connect(id2,id1,ConnectionMode.DisconnectFull);
                else if( op.equals("--!") )
                        ctx.connect(id1,id2,ConnectionMode.DisconnectFull);
                else if( op.equals("!-!") )
                        ctx.connect(id1,id2,ConnectionMode.DisconnectReverseFull);
        }

        protected void error( String title, String message )
        {
                out.printf("%s: %s\u005cn", title, message );
        }

        protected void betweenStep()
        {
                if( ctx.has("stream.event.betweenStep") )
                {
                        try
                        {
                                Thread.sleep(Long.parseLong(ctx.get("stream.event.betweenStep")));
                        }
                        catch(Exception e) {}
                }
        }

        protected Object createObject( String classname, LinkedList<Object> args )
        {
                try
                {
                        Class<?> cls = Class.forName(classname);
                        Class<?> [] argsCls = (args != null ? new Class<?>[args.size()] : null );

                        if( args != null )
                        {
                                for( int i = 0; i < args.size(); i++ )
                                {
                                        argsCls [i] = args.get(i).getClass();

                                        if( args.get(i) instanceof Integer )
                                                argsCls [i] = Integer.TYPE;
                                        else if( args.get(i) instanceof Double )
                                                argsCls [i] = Double.TYPE;
                                }
                }

                        Constructor<?> c = cls.getConstructor(argsCls);
                        return c.newInstance( args == null ? null : args.toArray() );
                }
                catch( Exception e )
                {
                        e.printStackTrace();
                }

                return null;
        }

        public static void main( String [] args )
        {
                try
                {
                        CLIParser cp = new CLIParser();
                        cp.init(System.in);
                        cp.start();
                }
                catch( Exception e )
                {
                        e.printStackTrace();
                }
        }

  final public void start() throws ParseException {
        if( out == System.out ) out.printf( "> " );
    label_1:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case ATTRIBUTE:
      case CREATE:
      case ELEMENT:
      case FILE:
      case GENERATOR:
      case OPEN:
      case NEW:
      case SET:
      case EXIT:
      case UNSET:
      case GET:
      case DISPLAY:
      case STRING:
        ;
        break;
      default:
        jj_la1[0] = jj_gen;
        break label_1;
      }
      axioms();
      jj_consume_token(EOI);
                         if( out == System.out ) out.printf( "> " );
    }
    jj_consume_token(0);
  }

  final public void axioms() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case SET:
    case EXIT:
    case UNSET:
    case GET:
      axiomsSystem();
      break;
    case CREATE:
    case OPEN:
    case DISPLAY:
      axiomsGraph();
      break;
    case NEW:
    case STRING:
      axiomsStream();
      break;
    case GENERATOR:
      axiomsGenerator();
      break;
    case FILE:
      axiomsFile();
      break;
    case ATTRIBUTE:
      axiomsAttributes();
      break;
    case ELEMENT:
      axiomsElements();
      break;
    default:
      jj_la1[1] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public String readClassname() throws ParseException {
        Token cls = null;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case WORD:
      cls = jj_consume_token(WORD);
      break;
    case CLASS:
      cls = jj_consume_token(CLASS);
      break;
    default:
      jj_la1[2] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
                                           {if (true) return cls.image;}
    throw new Error("Missing return statement in function");
  }

  final public String readID() throws ParseException {
        Token id = null;
    id = jj_consume_token(STRING);
                        {if (true) return unquote(id.image);}
    throw new Error("Missing return statement in function");
  }

  final public int readInt() throws ParseException {
        Token i = null;
    i = jj_consume_token(INT);
                    {if (true) return Integer.parseInt(i.image);}
    throw new Error("Missing return statement in function");
  }

  final public String readString() throws ParseException {
        Token t = null;
    t = jj_consume_token(STRING);
                       {if (true) return unquote(t.image);}
    throw new Error("Missing return statement in function");
  }

  final public double readDouble() throws ParseException {
        Token t = null;
    t = jj_consume_token(REAL);
                     {if (true) return Double.parseDouble(t.image);}
    throw new Error("Missing return statement in function");
  }

  final public URL readURL() throws ParseException {
        Token t = null;
    t = jj_consume_token(STRING);
                try
                {
                        {if (true) return new URL(unquote(t.image));}
                }
                catch( MalformedURLException e )
                {
                        error("malformed url",e.getMessage());
                }

                {if (true) return null;}
    throw new Error("Missing return statement in function");
  }

  final public Object [] readArray() throws ParseException {
        LinkedList<Object> array = new LinkedList<Object>();
        Object  o = null;
    jj_consume_token(64);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case INT:
    case REAL:
    case NEW:
    case STRING:
    case 64:
      o = readObject();
                                 array.addLast(o);
      label_2:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case 65:
          ;
          break;
        default:
          jj_la1[3] = jj_gen;
          break label_2;
        }
        jj_consume_token(65);
        o = readObject();
                                                                              array.addLast(o);
      }
      break;
    default:
      jj_la1[4] = jj_gen;
      ;
    }
    jj_consume_token(66);
          {if (true) return array.toArray();}
    throw new Error("Missing return statement in function");
  }

  final public Object readObject() throws ParseException {
        Token t = null;
        String cls = null;
        LinkedList<Object> args = null;
        Object obj;
        double d;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case STRING:
      t = jj_consume_token(STRING);
                       {if (true) return unquote(t.image);}
      break;
    case INT:
      d = readInt();
                        {if (true) return (int) d;}
      break;
    case REAL:
      d = readDouble();
                           {if (true) return d;}
      break;
    case NEW:
      jj_consume_token(NEW);
      cls = readClassname();
      jj_consume_token(LPAREN);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case INT:
      case REAL:
      case NEW:
      case STRING:
      case 64:
        obj = readObject();
                if( args == null )
                        args = new LinkedList<Object>();
                args.addLast(obj);
        label_3:
        while (true) {
          switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
          case 65:
            ;
            break;
          default:
            jj_la1[5] = jj_gen;
            break label_3;
          }
          jj_consume_token(65);
          obj = readObject();
                if( args == null )
                        args = new LinkedList<Object>();
                args.addLast(obj);
        }
        break;
      default:
        jj_la1[6] = jj_gen;
        ;
      }
      jj_consume_token(RPAREN);
                obj = createObject(cls,args);
                {if (true) return obj;}
      break;
    case 64:
      obj = readArray();
                            {if (true) return obj;}
      break;
    default:
      jj_la1[7] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final public KeyVal readKeyVal() throws ParseException {
        String k = null;
        Object v = null;
    k = readClassname();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 67:
      jj_consume_token(67);
      v = readObject();
      break;
    default:
      jj_la1[8] = jj_gen;
      ;
    }
                                                        {if (true) return new KeyVal(k,v);}
    throw new Error("Missing return statement in function");
  }

  final public LinkedList<KeyVal> readKeyVals() throws ParseException {
        KeyVal kv = null;
        LinkedList<KeyVal> keys = new LinkedList<KeyVal>();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case WORD:
    case CLASS:
      kv = readKeyVal();
                              keys.addLast(kv);
      label_4:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case 65:
          ;
          break;
        default:
          jj_la1[9] = jj_gen;
          break label_4;
        }
        jj_consume_token(65);
        kv = readKeyVal();
                                                                            keys.addLast(kv);
      }
      break;
    default:
      jj_la1[10] = jj_gen;
      ;
    }
                                                                                                        {if (true) return keys;}
    throw new Error("Missing return statement in function");
  }

  final public LinkedList<KeyVal> readAttributes() throws ParseException {
        LinkedList<KeyVal> attributes = null;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case WITH:
      jj_consume_token(WITH);
      attributes = readKeyVals();
      break;
    default:
      jj_la1[11] = jj_gen;
      ;
    }
                                                 {if (true) return attributes == null ? new LinkedList<KeyVal>() : attributes;}
    throw new Error("Missing return statement in function");
  }

  final public void axiomsSystem() throws ParseException {
        String key = null;
        Token val = null;
        Object o = null;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case EXIT:
      jj_consume_token(EXIT);
                 System.exit(0);
      break;
    case SET:
      jj_consume_token(SET);
      key = readClassname();
      val = jj_consume_token(STRING);
                                                     ctx.set(key,unquote(val.image));
      break;
    case GET:
      jj_consume_token(GET);
      key = readClassname();
                                      out.printf("%s = \u005c"%s\u005c"\u005cn", key, ctx.get(key));
      break;
    case UNSET:
      jj_consume_token(UNSET);
      key = readClassname();
                                        ctx.unset(key);
      break;
    default:
      jj_la1[12] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

/*
 * Axioms definition for graph operations.
 */
  final public void axiomsGraph() throws ParseException {
        Token id = null;
        HashMap<String,Object> attr;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case CREATE:
      axiomGraphCreation();
      break;
    case OPEN:
      axiomGraphRead();
      break;
    case DISPLAY:
      axiomGraphDisplay();
      break;
    default:
      jj_la1[13] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public void axiomGraphCreation() throws ParseException {
        Token id = null;
        LinkedList<KeyVal> attr;
    jj_consume_token(CREATE);
    jj_consume_token(GRAPH);
    id = jj_consume_token(STRING);
    attr = readAttributes();
                                                                   System.out.printf(":: graph creation \u005c"%s\u005c", %s\u005cn",unquote(id.image), attr);
  }

  final public void axiomGraphRead() throws ParseException {
        Token id = null;
        String reader = null;
    jj_consume_token(OPEN);
    jj_consume_token(GRAPH);
    id = jj_consume_token(STRING);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case USING:
      jj_consume_token(USING);
      reader = readClassname();
      break;
    default:
      jj_la1[14] = jj_gen;
      ;
    }
                                                                             System.out.printf(":: open graph \u005c"%s\u005c"\u005cn",unquote(id.image));
  }

  final public void axiomGraphDisplay() throws ParseException {
        String id = null;
        boolean on = true;
    jj_consume_token(DISPLAY);
    id = readID();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case OFF:
      jj_consume_token(OFF);
                                          on = false;
      break;
    default:
      jj_la1[15] = jj_gen;
      ;
    }
                                                             ctx.display(id,on);
  }

/*
 * Axioms definitions for stream operations.
 */
  final public void axiomsStream() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case NEW:
      axiomStreamNew();
      break;
    case STRING:
      axiomStreamConnect();
      break;
    default:
      jj_la1[16] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public void axiomStreamNew() throws ParseException {
        Token id = null;
        String cls = null;
        String pkg = null;
    jj_consume_token(NEW);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case STREAM:
      jj_consume_token(STREAM);
      break;
    case GENERATOR:
      jj_consume_token(GENERATOR);
                                           pkg = ctx.get("package.generator");
      break;
    case GRAPH:
      jj_consume_token(GRAPH);
                                                                                             pkg = ctx.get("package.graph");
      break;
    default:
      jj_la1[17] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    id = jj_consume_token(STRING);
    jj_consume_token(68);
    cls = readClassname();
                if( pkg != null )
                        cls = pkg + "." + cls;

                ctx.newStream(unquote(id.image),cls);
  }

  final public void axiomStreamConnect() throws ParseException {
        Token idFrom = null, idTo = null;
        Token s;
    idFrom = jj_consume_token(STRING);
    s = jj_consume_token(STREAM_OP);
    idTo = jj_consume_token(STRING);
                                                            streamConnection(unquote(idFrom.image),unquote(idTo.image),s.image);
  }

/*
 * Axioms definitions for generators.
 */
  final public void axiomsGenerator() throws ParseException {
        String id = null;
        int step = 1;
    jj_consume_token(GENERATOR);
    id = readID();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case BEGIN:
      jj_consume_token(BEGIN);
                if( ctx.hasSource(id) )
                {
                        Source src = ctx.getSource(id);

                        if( src instanceof Generator )
                                ((Generator) src).begin();
                }
      break;
    case STEP:
      jj_consume_token(STEP);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case INT:
        step = readInt();
        break;
      default:
        jj_la1[18] = jj_gen;
        ;
      }
                if( ctx.hasSource(id) )
                {
                        Source src = ctx.getSource(id);

                        if( src instanceof Generator )
                        {
                                for( int i = 0; i < step; i++ )
                                {
                                        ((Generator) src).nextElement();
                                        betweenStep();
                                }
                        }
                }
      break;
    case END:
      jj_consume_token(END);
                if( ctx.hasSource(id) )
                {
                        Source src = ctx.getSource(id);

                        if( src instanceof Generator )
                                ((Generator) src).end();
                }
      break;
    default:
      jj_la1[19] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

/*
 * Axioms definitions for file source.
 */
  final public void axiomsFile() throws ParseException {
        String id = null;
        URL url = null;
        int count = 1;
    jj_consume_token(FILE);
    id = readID();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case READ:
      jj_consume_token(READ);
      url = readURL();
                        if( url != null && ctx.hasSource(id) )
                        {
                                Source src = ctx.getSource(id);

                                if( src instanceof FileSource )
                                {
                                        try
                                        {
                                                ((FileSource) src).readAll(url);
                                        }
                                        catch( IOException e )
                                        {
                                                error( String.format("error while reading \u005c"%s\u005c"",url.toString()), e.getMessage() );
                                        }
                                }
                                else
                                {
                                        error( "error", String.format("\u005c"%s\u005c" is not a FileSource",id) );
                                }
                        }
      break;
    case BEGIN:
      jj_consume_token(BEGIN);
      url = readURL();
                        if( url != null && ctx.hasSource(id) )
                        {
                                Source src = ctx.getSource(id);

                                if( src instanceof FileSource )
                                {
                                        try
                                        {
                                                ((FileSource) src).begin(url);
                                        }
                                        catch( IOException e )
                                        {
                                                error( String.format("error while beginning \u005c"%s\u005c"",url.toString()), e.getMessage() );
                                        }
                                }
                                else
                                {
                                        error( "error", String.format("\u005c"%s\u005c" is not a FileSource",id) );
                                }
                        }
      break;
    case NEXT:
      jj_consume_token(NEXT);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case EVENTS:
        jj_consume_token(EVENTS);
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case INT:
          count = readInt();
          break;
        default:
          jj_la1[20] = jj_gen;
          ;
        }
                                if( ctx.hasSource(id) )
                                {
                                        Source src = ctx.getSource(id);

                                        if( src instanceof FileSource )
                                        {
                                                try
                                                {
                                                        for( int i = 0; i < count; i++ )
                                                        {
                                                                ((FileSource) src).nextEvents();
                                                                betweenStep();
                                                        }
                                                }
                                                catch( IOException e )
                                                {
                                                        error( String.format("error in next events of \u005c"%s\u005c"",id), e.getMessage() );
                                                }
                                        }
                                        else
                                        {
                                                error( "error", String.format("\u005c"%s\u005c" is not a FileSource",id) );
                                        }
                                }
        break;
      case STEP:
        jj_consume_token(STEP);
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case INT:
          count = readInt();
          break;
        default:
          jj_la1[21] = jj_gen;
          ;
        }
                                if( ctx.hasSource(id) )
                                {
                                        Source src = ctx.getSource(id);

                                        if( src instanceof FileSource )
                                        {
                                                try
                                                {
                                                        for( int i = 0; i < count; i++ )
                                                        {
                                                                ((FileSource) src).nextStep();
                                                                betweenStep();
                                                        }
                                                }
                                                catch( IOException e )
                                                {
                                                        error( String.format("error in next step of \u005c"%s\u005c"",id), e.getMessage() );
                                                }
                                        }
                                        else
                                        {
                                                error( "error", String.format("\u005c"%s\u005c" is not a FileSource",id) );
                                        }
                                }
        break;
      default:
        jj_la1[22] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      break;
    case END:
      jj_consume_token(END);
                        if( ctx.hasSource(id) )
                        {
                                Source src = ctx.getSource(id);

                                if( src instanceof FileSource )
                                {
                                        try
                                        {
                                                ((FileSource) src).end();
                                        }
                                        catch( IOException e )
                                        {
                                                error( String.format("error while ending \u005c"%s\u005c"",id), e.getMessage() );
                                        }
                                }
                                else
                                {
                                        error( "error", String.format("\u005c"%s\u005c" is not a FileSource",id) );
                                }
                        }
      break;
    default:
      jj_la1[23] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

/*
 * Axioms definitions for attribute.
 */
  final public void axiomsAttributes() throws ParseException {
        String id = null;
        LinkedList<KeyVal> attributes = null;
        Element mode = Element.graph;
        String target = null;
        boolean isRegex = false;
    jj_consume_token(ATTRIBUTE);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case ADD:
      jj_consume_token(ADD);
      attributes = readKeyVals();
      jj_consume_token(ON);
      id = readID();
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TO:
        jj_consume_token(TO);
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case NODE:
          jj_consume_token(NODE);
                                         mode = Element.node;
          break;
        case EDGE:
          jj_consume_token(EDGE);
                                         mode = Element.edge;
          break;
        default:
          jj_la1[24] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case MATCHING:
          jj_consume_token(MATCHING);
                                         isRegex = true;
          break;
        default:
          jj_la1[25] = jj_gen;
          ;
        }
        target = readString();
        break;
      default:
        jj_la1[26] = jj_gen;
        ;
      }
                        if( ctx.hasSink(id) )
                        {
                                Sink sink = ctx.getSink(id);

                                if( isRegex && ! ( sink instanceof Graph ) )
                                {
                                        error( "invalid requirement", "matching need a graph as sink" );
                                }
                                else
                                {
                                        switch(mode)
                                        {
                                        case graph:
                                                for( int i = 0; i < attributes.size(); i++ )
                                                        sink.graphAttributeAdded(parserAsSourceId,currentTimeId++,attributes.get(i).key,attributes.get(i).val);
                                                break;
                                        case node:
                                                if( isRegex )
                                                {
                                                        for( Node n : (Graph) sink )
                                                        {
                                                                if( n.getId().matches(target) )
                                                                {
                                                                        for( int i = 0; i < attributes.size(); i++ )
                                                                                sink.nodeAttributeAdded(parserAsSourceId,currentTimeId++,n.getId(),attributes.get(i).key,attributes.get(i).val);
                                                                }
                                                        }
                                                }
                                                else
                                                {
                                                        for( int i = 0; i < attributes.size(); i++ )
                                                                sink.nodeAttributeAdded(parserAsSourceId,currentTimeId++,target,attributes.get(i).key,attributes.get(i).val);
                                                }
                                                break;
                                        case edge:
                                                if( isRegex )
                                                {
                                                        for( Edge e : ((Graph) sink).edgeSet() )
                                                        {
                                                                if( e.getId().matches(target) )
                                                                {
                                                                        for( int i = 0; i < attributes.size(); i++ )
                                                                                sink.edgeAttributeAdded(parserAsSourceId,currentTimeId++,e.getId(),attributes.get(i).key,attributes.get(i).val);
                                                                }
                                                        }
                                                }
                                                else
                                                {
                                                        for( int i = 0; i < attributes.size(); i++ )
                                                                sink.edgeAttributeAdded(parserAsSourceId,currentTimeId++,target,attributes.get(i).key,attributes.get(i).val);
                                                }
                                                break;
                                        }
                                }
                        }
      break;
    case CHANGE:
      jj_consume_token(CHANGE);
      attributes = readKeyVals();
      jj_consume_token(ON);
      id = readID();
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TO:
        jj_consume_token(TO);
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case NODE:
          jj_consume_token(NODE);
                                         mode = Element.node;
          break;
        case EDGE:
          jj_consume_token(EDGE);
                                         mode = Element.edge;
          break;
        default:
          jj_la1[27] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case MATCHING:
          jj_consume_token(MATCHING);
                                         isRegex = true;
          break;
        default:
          jj_la1[28] = jj_gen;
          ;
        }
        target = readString();
        break;
      default:
        jj_la1[29] = jj_gen;
        ;
      }
                        if( ctx.hasSink(id) )
                        {
                                Sink sink = ctx.getSink(id);

                                if( isRegex && ! ( sink instanceof Graph ) )
                                {
                                        error( "invalid requirement", "matching need a graph as sink" );
                                }
                                else
                                {
                                        switch(mode)
                                        {
                                        case graph:
                                                for( int i = 0; i < attributes.size(); i++ )
                                                        sink.graphAttributeChanged(parserAsSourceId,currentTimeId++,attributes.get(i).key,null,attributes.get(i).val);
                                                break;
                                        case node:
                                                if( isRegex )
                                                {
                                                        for( Node n : (Graph) sink )
                                                        {
                                                                if( n.getId().matches(target) )
                                                                {
                                                                        for( int i = 0; i < attributes.size(); i++ )
                                                                                sink.nodeAttributeChanged(parserAsSourceId,currentTimeId++,n.getId(),attributes.get(i).key,null,attributes.get(i).val);
                                                                }
                                                        }
                                                }
                                                else
                                                {
                                                        for( int i = 0; i < attributes.size(); i++ )
                                                                sink.nodeAttributeChanged(parserAsSourceId,currentTimeId++,target,attributes.get(i).key,null,attributes.get(i).val);
                                                }
                                                break;
                                        case edge:
                                                if( isRegex )
                                                {
                                                        for( Edge e : ((Graph) sink).edgeSet() )
                                                        {
                                                                if( e.getId().matches(target) )
                                                                {
                                                                        for( int i = 0; i < attributes.size(); i++ )
                                                                                sink.edgeAttributeChanged(parserAsSourceId,currentTimeId++,e.getId(),attributes.get(i).key,null,attributes.get(i).val);
                                                                }
                                                        }
                                                }
                                                else
                                                {
                                                        for( int i = 0; i < attributes.size(); i++ )
                                                                sink.edgeAttributeChanged(parserAsSourceId,currentTimeId++,target,attributes.get(i).key,null,attributes.get(i).val);
                                                }
                                                break;
                                        }
                                }
                        }
      break;
    case REMOVE:
      jj_consume_token(REMOVE);
      attributes = readKeyVals();
      jj_consume_token(ON);
      id = readID();
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TO:
        jj_consume_token(TO);
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case NODE:
          jj_consume_token(NODE);
                                         mode = Element.node;
          break;
        case EDGE:
          jj_consume_token(EDGE);
                                         mode = Element.edge;
          break;
        default:
          jj_la1[30] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case MATCHING:
          jj_consume_token(MATCHING);
                                         isRegex = true;
          break;
        default:
          jj_la1[31] = jj_gen;
          ;
        }
        target = readString();
        break;
      default:
        jj_la1[32] = jj_gen;
        ;
      }
                        if( ctx.hasSink(id) )
                        {
                                Sink sink = ctx.getSink(id);

                                if( isRegex && ! ( sink instanceof Graph ) )
                                {
                                        error( "invalid requirement", "matching need a graph as sink" );
                                }
                                else
                                {
                                        switch(mode)
                                        {
                                        case graph:
                                                for( int i = 0; i < attributes.size(); i++ )
                                                        sink.graphAttributeRemoved(parserAsSourceId,currentTimeId++,attributes.get(i).key);
                                                break;
                                        case node:
                                                if( isRegex )
                                                {
                                                        for( Node n : (Graph) sink )
                                                        {
                                                                if( n.getId().matches(target) )
                                                                {
                                                                        for( int i = 0; i < attributes.size(); i++ )
                                                                                sink.nodeAttributeRemoved(parserAsSourceId,currentTimeId++,n.getId(),attributes.get(i).key);
                                                                }
                                                        }
                                                }
                                                else
                                                {
                                                        for( int i = 0; i < attributes.size(); i++ )
                                                                sink.nodeAttributeRemoved(parserAsSourceId,currentTimeId++,target,attributes.get(i).key);
                                                }
                                                break;
                                        case edge:
                                                if( isRegex )
                                                {
                                                        for( Edge e : ((Graph) sink).edgeSet() )
                                                        {
                                                                if( e.getId().matches(target) )
                                                                {
                                                                        for( int i = 0; i < attributes.size(); i++ )
                                                                                sink.edgeAttributeRemoved(parserAsSourceId,currentTimeId++,e.getId(),attributes.get(i).key);
                                                                }
                                                        }
                                                }
                                                else
                                                {
                                                        for( int i = 0; i < attributes.size(); i++ )
                                                                sink.edgeAttributeRemoved(parserAsSourceId,currentTimeId++,target,attributes.get(i).key);
                                                }
                                                break;
                                        }
                                }
                        }
      break;
    default:
      jj_la1[33] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

/*
 * Axioms definitions for elements.
 */
  final public void axiomsElements() throws ParseException {
    jj_consume_token(ELEMENT);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case ADD:
      axiomElementAdd();
      break;
    case REMOVE:
      axiomElementRemove();
      break;
    default:
      jj_la1[34] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public void axiomElementAdd() throws ParseException {
    jj_consume_token(ADD);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case NODE:
      axiomElementAddNode();
      break;
    case EDGE:
      axiomElementAddEdge();
      break;
    default:
      jj_la1[35] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public void axiomElementAddNode() throws ParseException {
        String nodeId = null;
        String sinkId = null;
        LinkedList<KeyVal> attributes = null;
    jj_consume_token(NODE);
    nodeId = readID();
    jj_consume_token(IN);
    sinkId = readID();
    attributes = readAttributes();
                Sink sink;

                if( ctx.hasSink(sinkId) )
                {
                        sink = ctx.getSink(sinkId);

                        sink.nodeAdded(parserAsSourceId,currentTimeId++,nodeId);

                        if( attributes != null )
                        {
                                for( KeyVal kv : attributes )
                                        sink.nodeAttributeAdded(parserAsSourceId,currentTimeId++,nodeId,kv.key,kv.val);
                        }
                }
                else
                {
                        error( "unknown sink", sinkId );
                }
  }

  final public void axiomElementAddEdge() throws ParseException {
        String edgeId = null;
        String sinkId = null;
        String fromId = null;
        String targId = null;
        boolean directed = false;
        LinkedList<KeyVal> attributes = null;
    jj_consume_token(EDGE);
    edgeId = readID();
    fromId = readID();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 69:
      jj_consume_token(69);
                                                           directed = true;
      break;
    default:
      jj_la1[36] = jj_gen;
      ;
    }
    targId = readID();
    jj_consume_token(IN);
    sinkId = readID();
    attributes = readAttributes();
                Sink sink;

                if( ctx.hasSink(sinkId) )
                {
                        sink = ctx.getSink(sinkId);

                        sink.edgeAdded(parserAsSourceId,currentTimeId++,edgeId,fromId,targId,directed);

                        if( attributes != null )
                        {
                                for( KeyVal kv : attributes )
                                        sink.edgeAttributeAdded(parserAsSourceId,currentTimeId++,edgeId,kv.key,kv.val);
                        }
                }
                else
                {
                        error( "unknown sink", sinkId );
                }
  }

  final public void axiomElementRemove() throws ParseException {
        String elementId = null;
        String sinkId = null;
        Element mode = Element.node;
    jj_consume_token(REMOVE);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case NODE:
      jj_consume_token(NODE);
      break;
    case EDGE:
      jj_consume_token(EDGE);
                                     mode = Element.edge;
      break;
    default:
      jj_la1[37] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    elementId = readID();
    jj_consume_token(FROM);
    sinkId = readID();
                Sink sink;

                if( ctx.hasSink(sinkId) )
                {
                        sink = ctx.getSink(sinkId);

                        switch(mode)
                        {
                        case node: sink.nodeRemoved(parserAsSourceId,currentTimeId++,elementId); break;
                        case edge: sink.edgeRemoved(parserAsSourceId,currentTimeId++,elementId); break;
                        }
                }
                else
                {
                        error( "unknown sink", sinkId );
                }
  }

  /** Generated Token Manager. */
  public CLIParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private int jj_gen;
  final private int[] jj_la1 = new int[38];
  static private int[] jj_la1_0;
  static private int[] jj_la1_1;
  static private int[] jj_la1_2;
  static {
      jj_la1_init_0();
      jj_la1_init_1();
      jj_la1_init_2();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x22222000,0x22222000,0x0,0x0,0x60,0x0,0x60,0x60,0x0,0x0,0x0,0x0,0x0,0x20000,0x0,0x0,0x0,0x60000000,0x20,0x408000,0x20,0x20,0x800000,0x408000,0x100000,0x0,0x0,0x100000,0x0,0x0,0x100000,0x0,0x0,0x10800,0x800,0x100000,0x0,0x100000,};
   }
   private static void jj_la1_init_1() {
      jj_la1_1 = new int[] {0x17802140,0x17802140,0x60000000,0x0,0x10000100,0x0,0x10000100,0x10000100,0x0,0x0,0x60000000,0x400000,0x3802000,0x4000040,0x200000,0x10,0x10000100,0x20000,0x0,0x10000,0x0,0x0,0x10000,0x404,0x200,0x2,0x100000,0x200,0x2,0x100000,0x200,0x2,0x100000,0x800,0x800,0x200,0x0,0x200,};
   }
   private static void jj_la1_init_2() {
      jj_la1_2 = new int[] {0x0,0x0,0x0,0x2,0x1,0x2,0x1,0x1,0x8,0x2,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x20,0x0,};
   }

  /** Constructor with InputStream. */
  public CLIParser(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public CLIParser(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new CLIParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 38; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 38; i++) jj_la1[i] = -1;
  }

  /** Constructor. */
  public CLIParser(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new CLIParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 38; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 38; i++) jj_la1[i] = -1;
  }

  /** Constructor with generated Token Manager. */
  public CLIParser(CLIParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 38; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(CLIParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 38; i++) jj_la1[i] = -1;
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }


/** Get the next Token. */
  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  private int[] jj_expentry;
  private int jj_kind = -1;

  /** Generate ParseException. */
  public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[70];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 38; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
          if ((jj_la1_1[i] & (1<<j)) != 0) {
            la1tokens[32+j] = true;
          }
          if ((jj_la1_2[i] & (1<<j)) != 0) {
            la1tokens[64+j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 70; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

}
