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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;

import org.graphstream.stream.file.FileSink;
import org.graphstream.stream.file.FileSinkDGS;
import org.graphstream.stream.file.FileSinkDOT;
import org.graphstream.stream.file.FileSinkGML;
import org.graphstream.stream.file.FileSinkTikZ;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceDGS;
import org.graphstream.stream.file.FileSourceDOT;
import org.graphstream.stream.file.FileSourceGML;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.layout.springbox.SpringBox;

/**
 * A tool to convert from various formats to various formats...
 * 
 * @author Guilhelm Savin
 */
public class Convert {

	/**
	 * Conversion type.
	 *
	 */
	public static enum Type {
		DGS2TIKZ("width", "height", "stylesheet"), DGS2DOT, DGS2GML, DOT2DGS, DOT2GML, GML2DGS

		;

		String[] options;

		private Type(String... options) {
			this.options = options;
		}

		public int getOptionCount() {
			return options == null ? 0 : options.length;
		}

		public String getOption(int i) {
			if (options == null || i < 0 || i >= options.length)
				return null;

			return options[i];
		}

		public boolean isValidOption(String option) {
			if (options == null)
				return false;

			for (int i = 0; i < options.length; i++)
				if (options[i].equals(option))
					return true;

			return false;
		}
	}

	protected static void convert(InputStream in, FileSource source,
			OutputStream out, FileSink sink) throws IOException {
		source.addSink(sink);

		sink.begin(out);
		source.begin(in);
		while (source.nextStep())
			;
		source.end();
		sink.end();

		source.removeSink(sink);
	}

	public static void convert_dgs2dot(InputStream in, OutputStream out,
			String[][] options) throws IOException {
		FileSourceDGS source = new FileSourceDGS();
		FileSinkDOT sink = new FileSinkDOT();

		convert(in, source, out, sink);
	}

	public static void convert_dgs2gml(InputStream in, OutputStream out,
			String[][] options) throws IOException {
		FileSourceDGS source = new FileSourceDGS();
		FileSinkGML sink = new FileSinkGML();

		convert(in, source, out, sink);
	}

	public static void convert_dgs2tikz(InputStream in, OutputStream out,
			String[][] options) throws IOException {

		boolean layout = false;
		String css = null;
		double width = 10;
		double height = 10;

		if (options != null) {
			for (int o = 0; o < options.length; o++) {
				String key = options[o][0];
				String value = options[o][1];

				if (key.equals("stylesheet")) {
					css = value;
				} else if (key.equals("layout")) {
					layout = true;
				} else if (key.equals("width")) {
					if (value == null || !value.matches("^\\d+([.]\\d+)?$")) {
						System.err.printf("Invalid width : %s%n", value);
						System.exit(1);
					}

					width = Double.parseDouble(value);
				} else if (key.equals("height")) {
					if (value == null || !value.matches("^\\d+([.]\\d+)?$")) {
						System.err.printf("Invalid height : %s%n", value);
						System.exit(1);
					}

					height = Double.parseDouble(value);
				}
			}
		}

		FileSourceDGS dgs = new FileSourceDGS();
		GraphicGraph g = new GraphicGraph("dgs2tikz");
		FileSinkTikZ tikz = new FileSinkTikZ();
		SpringBox sbox = null;

		if (layout) {
			sbox = new SpringBox();

			g.addSink(sbox);
			sbox.addAttributeSink(g);
		}

		dgs.addSink(g);
		dgs.readAll(in);
		dgs.removeSink(g);
		dgs = null;

		if (layout) {
			do
				sbox.compute();
			while (sbox.getStabilization() < 0.9);

			g.removeSink(sbox);
			sbox.removeAttributeSink(g);

			sbox = null;
		}

		if (css != null) {
			File f = new File(css);

			if (f.exists()) {
				FileReader reader = new FileReader(f);
				StringBuilder stylesheet = new StringBuilder();
				char[] buffer = new char[128];
				int r;

				while (reader.ready()) {
					r = reader.read(buffer, 0, buffer.length);
					stylesheet.append(buffer, 0, r);
				}

				reader.close();
				css = stylesheet.toString();
			}

			g.addAttribute("ui.stylesheet", css);
		}

		if (!Double.isNaN(width))
			g.addAttribute(FileSinkTikZ.WIDTH_ATTR, width);

		if (!Double.isNaN(height))
			g.addAttribute(FileSinkTikZ.HEIGHT_ATTR, height);

		tikz.writeAll(g, out);
	}

	public static void convert_dot2dgs(InputStream in, OutputStream out,
			String[][] options) throws IOException {
		FileSourceDOT source = new FileSourceDOT();
		FileSinkDGS sink = new FileSinkDGS();

		convert(in, source, out, sink);
	}

	public static void convert_dot2gml(InputStream in, OutputStream out,
			String[][] options) throws IOException {
		FileSourceDOT source = new FileSourceDOT();
		FileSinkGML sink = new FileSinkGML();

		convert(in, source, out, sink);
	}

	public static void convert_gml2dgs(InputStream in, OutputStream out,
			String[][] options) throws IOException {
		FileSourceGML source = new FileSourceGML();
		FileSinkDGS sink = new FileSinkDGS();

		convert(in, source, out, sink);
	}

	public static void main(String... args) {
		InputStream in = System.in;
		OutputStream out = System.out;
		String[][] options = new String[0][2];
		Type conv = null;

		if (args == null) {
			usage();
			System.exit(1);
		}

		LinkedList<String> noArg = new LinkedList<String>();
		LinkedList<String[]> optionsTMP = new LinkedList<String[]>();

		for (int i = 0; i < args.length; i++) {
			if (args[i].matches("^--\\w+(-\\w+)*(=.*)?$")) {
				int idx = args[i].indexOf('=');
				String key;
				String value;

				if (idx < 0) {
					key = args[i].substring(2);
					value = null;
				} else {
					key = args[i].substring(2, idx);
					value = args[i].substring(idx + 1).trim();
				}

				if (value != null && value.matches("^\".*\"$"))
					value = value.substring(1, value.length() - 1);

				if (key.equals("type")) {
					try {
						conv = Type.valueOf(value);
					} catch (IllegalArgumentException e) {
						System.err.printf("Invalid conversion type \"%s\".\n",
								value);
						System.exit(1);
					}
				} else if (key.equals("help")) {
					usage();
					System.exit(0);
				} else {
					optionsTMP.add(new String[] {key, value});
				}
			} else {
				noArg.add(args[i]);
			}
		}

		if (conv == null) {
			System.err.printf("No conversion type specified.\n");
			System.exit(1);
		}

		options = optionsTMP.toArray(options);

		boolean inputIsFile = false;
		boolean outputIsFile = false;

		switch (noArg.size()) {
		case 0:
			break;
		case 2:
			if (!noArg.get(1).equals("-"))
				outputIsFile = true;
		case 1:
			if (!noArg.get(0).equals("-"))
				inputIsFile = true;
			break;
		default:
			usage();
			System.exit(1);
		}

		if (inputIsFile) {
			try {
				File f = new File(noArg.get(0));

				if (!f.exists()) {
					in = Convert.class.getResourceAsStream(noArg.get(0));

					if (in == null) {
						System.err.printf(
								"Input file \"%s\" does not exists.\n",
								noArg.get(0));
						System.exit(1);
					}
				}

				in = new FileInputStream(f);
			} catch (IOException ioe) {
				System.err
						.printf("Failed to open input file \"%s\".\nCause is %s : %s%n",
								noArg.get(0), ioe.getClass().getName(),
								ioe.getMessage());
				System.exit(1);
			}
		}

		if (outputIsFile) {
			try {
				out = new FileOutputStream(noArg.get(1));
			} catch (IOException ioe) {
				System.err
						.printf("Failed to open output file \"%s\".\nCause is %s : %s%n",
								noArg.get(1), ioe.getClass().getName(),
								ioe.getMessage());
				System.exit(1);
			}
		}

		try {
			switch (conv) {
			case DGS2DOT:
				convert_dgs2dot(in, out, options);
				break;
			case DGS2GML:
				convert_dgs2gml(in, out, options);
				break;
			case DGS2TIKZ:
				convert_dgs2tikz(in, out, options);
				break;
			case DOT2DGS:
				convert_dot2dgs(in, out, options);
				break;
			case DOT2GML:
				convert_dot2dgs(in, out, options);
				break;
			case GML2DGS:
				convert_gml2dgs(in, out, options);
				break;
			}
		} catch (IOException ioe) {
			System.err.printf("Failed to convert. %s : %s%n", ioe.getClass()
					.getName(), ioe.getMessage());
			System.exit(1);
		}

		System.exit(0);
	}

	public static void usage() {
		System.out.printf("Usage: java %s [OPTIONS] IN OUT\n\n",
				Convert.class.getName());
		System.out.printf("with OPTIONS:%n");
		System.out
				.printf("\t--stylesheet=\"css code or css file path\"%n\t--do-layout%n");
		System.out.printf("\t--width=xx.xx%n\t--height=xx.xx%n");
		System.out.printf("IN OUT can be:%n");
		System.out.printf("\t\"\" or \"- -\" : use system input/output%n");
		System.out
				.printf("\tifile       : read ifile and write to system output%n");
		System.out
				.printf("\t- ofile     : read system input and write to ofile%n");
		System.out.printf("\tifile ofile : read ifile and write to ofile%n");
	}
}
