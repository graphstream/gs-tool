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
package org.graphstream.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceDGS;
import org.graphstream.stream.file.FileSourceDOT;
import org.graphstream.stream.file.FileSourceEdge;
import org.graphstream.stream.file.FileSourceGML;

public class Player {

	public static enum Format {
		DGS, DOT, GML, EDGE
	}

	public static enum Next {
		STEP, EVENTS
	}

	public static class Config {
		boolean quality = false;
		boolean antialias = false;
		boolean autolayout = false;
		boolean scala = false;
		long stepDelay = 10;
		String stylesheet = null;
		Format format = Format.DGS;
		Next next = Next.STEP;

		public void addStyle(String css) {
			if (stylesheet == null)
				stylesheet = css;
			else
				stylesheet += css;
		}

		public void option(Option option, String value) {
			switch (option) {
			case QUALITY:
				quality = value == null || Boolean.parseBoolean(value);
				break;
			case ANTIALIASING:
				antialias = value == null || Boolean.parseBoolean(value);
				break;
			case AUTOLAYOUT:
				autolayout = value == null || Boolean.parseBoolean(value);
				break;
			case STYLESHEET:
				if (value == null) {
					System.err.printf("Invalid path value \"%s\".\n", value);
					System.exit(1);
				}

				addStyle(readCssContent(value));
				break;
			case CSS:
				if (value == null) {
					System.err.printf("Invalid css value \"%s\".\n", value);
					System.exit(1);
				}

				addStyle(value);
				break;
			case STEP_DELAY:
				if (value == null || !value.matches("\\d+")) {
					System.err.printf("Invalid delay value \"%s\".\n", value);
					System.exit(1);
				}

				stepDelay = Long.parseLong(value);
				break;
			case SCALA_RENDERER:
				scala = value == null || Boolean.parseBoolean(value);
				break;
			case FORMAT:
				if (value == null) {
					usage(System.err);
					System.exit(1);
				}

				if (value.equals("?")) {
					printFormat(System.out);
					System.exit(0);
				}

				try {
					format = Format.valueOf(value);
				} catch (IllegalArgumentException e) {
					System.err.printf("Bad format \"%s\".\n", value);
					printFormat(System.err);
					System.exit(1);
				}
				break;
			case NEXT:
				if (value == null) {
					usage(System.err);
					System.exit(1);
				}

				if (value.equals("?")) {
					printNext(System.out);
					System.exit(0);
				}

				try {
					next = Next.valueOf(value);
				} catch (IllegalArgumentException e) {
					System.err.printf("Bad next action \"%s\".\n", value);
					printNext(System.err);
					System.exit(1);
				}
				break;
			case HELP:
				usage(System.out);
				System.exit(0);
			}
		}
	}

	public static void printFormat(PrintStream out) {
		out.printf("Available input format:\n");
		for (Format f : Format.values())
			out.printf("\t- %s\n", f);
	}

	public static void printNext(PrintStream out) {
		out.printf("Available next action:\n");
		for (Next n : Next.values())
			out.printf("\t- %s\n", n);
	}

	public static enum Option {
		QUALITY, ANTIALIASING, AUTOLAYOUT, STYLESHEET, CSS, STEP_DELAY, SCALA_RENDERER, FORMAT, NEXT, HELP
	}

	protected static String readCssContent(String path) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(path));
			StringBuilder css = new StringBuilder();

			while (in.ready())
				css.append(in.readLine());

			return css.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	public static void usage(PrintStream out) {
		out.printf("java %s [OPTIONS] IN\n\n", Player.class.getName());
		out.printf("with OPTIONS is:\n");
		out.printf("\t--quality[=bool]      : set quality rendering.\n");
		out.printf("\t--antialiasing[=bool] : set antialiasing rendering.\n");
		out.printf("\t--autolayout[=bool]   : set layout.\n");
		out.printf("\t--stylesheet=path     : load css stylesheet from an url/path.\n");
		out.printf("\t--css=\"...\"         : add css style/\n");
		out.printf("\t--step-delay=..       : set delay between step in milliseconds.\n");
		out.printf("\t--scala-renderer      : set scala renderer.\n");
		out.printf("\t--format=..           : set input format.\n");
		out.printf("\t--next=..             : set next action.\n");
		printFormat(out);
		printNext(out);
		out.printf("Shortcuts :\n");
		for (int i = 0; i < shortcuts.length; i++)
			out.printf("\t\"%s\"\t: \"%s\"\n", shortcuts[i][0], shortcuts[i][1]);
	}

	private static final String[][] shortcuts = { { "-q", "--quality" },
			{ "-a", "--antialiasing" }, { "-dgs", "--format=DGS" },
			{ "-dot", "--format=DOT" }, { "-gml", "--format=GML" },
			{ "-edge", "--format=EDGE" }, { "-l", "--autolayout" },
			{ "-h", "--help" }, { "-slow", "--step-delay=1000" },
			{ "-fast", "--step-delay=50" }, {"-scala", "--scala-renderer"} };

	public static FileSource sourceFor(Format format) {
		FileSource source = null;

		switch (format) {
		case DGS:
			source = new FileSourceDGS();
			break;
		case DOT:
			source = new FileSourceDOT();
			break;
		case GML:
			source = new FileSourceGML();
			break;
		case EDGE:
			source = new FileSourceEdge();
			break;
		}

		return source;
	}

	/**
	 * @param args
	 */
	public static void main(String... args) {
		Config cfg = new Config();
		String path = null;

		//
		// Remove shortcut
		//
		for (int k = 0; k < args.length; k++) {
			for (int l = 0; l < shortcuts.length; l++) {
				if (shortcuts[l][0].equals(args[k])) {
					args[k] = shortcuts[l][1];
					break;
				}
			}
		}

		for (int k = 0; k < args.length; k++) {
			if (args[k].matches("^--\\w+(-\\w+)*(=.*)?$")) {
				int idx = args[k].indexOf('=');
				String key;
				String value;

				Option option = null;

				if (idx < 0) {
					key = args[k].substring(2);
					value = null;
				} else {
					key = args[k].substring(2, idx);
					value = args[k].substring(idx + 1).trim();
				}

				if (value != null && value.matches("^\".*\"$"))
					value = value.substring(1, value.length() - 1);

				try {
					option = Option
							.valueOf(key.toUpperCase().replace('-', '_'));
				} catch (IllegalArgumentException e) {
					System.err.printf("Unknown option \"%s\".\n", key);
					System.exit(1);
				}

				cfg.option(option, value);
			} else if (path == null) {
				path = args[k];
			} else {
				System.err.printf("Path is already set to \"%s\".\n", path);
				System.exit(1);
			}
		}

		if (cfg.scala) {
			try {
				Class.forName("org.graphstream.ui.j2dviewer.J2DGraphRenderer");
			} catch (ClassNotFoundException e) {
				System.err
						.printf("Scala renderer is enable but not in classpath.\n");
				System.exit(1);
			}

			System.setProperty("gs.ui.renderer",
					"org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		}

		FileSource source = sourceFor(cfg.format);
		InputStream in = null;
		DefaultGraph g = new DefaultGraph(path == null ? "gs-play" : path);

		if (path == null)
			in = System.in;
		else {
			File f = new File(path);

			if (f.exists()) {
				try {
					in = new FileInputStream(f);
				} catch (FileNotFoundException e) {
					System.err.printf("File not found \"%s\".\n", path);
					System.exit(1);
				}
			} else {
				in = Player.class.getResourceAsStream(path);

				if (in == null) {
					System.err.printf("Resource not found \"%s\".\n", path);
					System.exit(1);
				}
			}
		}

		source.addSink(g);

		if (cfg.quality)
			g.addAttribute("ui.quality", true);

		if (cfg.antialias)
			g.addAttribute("ui.antialias", true);

		if (cfg.stylesheet != null)
			g.addAttribute("ui.stylesheet", cfg.stylesheet);

		g.display(cfg.autolayout);

		try {
			source.begin(in);
		} catch (IOException e) {
			System.err.printf("Can not start the source.\nError is %s : %s.\n",
					e.getClass().getName(), e.getMessage());
			System.exit(1);
		}

		try {
			while (cfg.next == Next.STEP ? source.nextStep() : source
					.nextEvents()) {
				try {
					Thread.sleep(cfg.stepDelay);
				} catch (InterruptedException e1) {
					// Ignore
				}
			}
		} catch (IOException e) {
			System.err.printf("Can not step the source.\nError is %s : %s.\n",
					e.getClass().getName(), e.getMessage());
			System.exit(1);
		}

		try {
			source.end();
		} catch (IOException e) {
			System.err.printf("Can not end the source.\nError is %s : %s.\n", e
					.getClass().getName(), e.getMessage());
			System.exit(1);
		}
	}

}
