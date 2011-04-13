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

import org.graphstream.stream.file.FileSinkTikZ;
import org.graphstream.stream.file.FileSourceDGS;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.layout.springbox.SpringBox;

/**
 * A tool to convert dgs to tikz.
 * 
 */
public class DGS2TikZ {

	public static void convert(String in, String out, boolean layout)
			throws IOException {
		FileInputStream fis = new FileInputStream(in);
		FileOutputStream fos = new FileOutputStream(out);
		convert(fis, fos, layout);
	}

	public static void convert(String in, OutputStream out, boolean layout)
			throws IOException {
		FileInputStream fis = new FileInputStream(in);
		convert(fis, out, layout);
	}

	public static void convert(InputStream in, String out, boolean layout)
			throws IOException {
		FileOutputStream fos = new FileOutputStream(out);
		convert(in, fos, layout);
	}

	public static void convert(InputStream in, OutputStream out, boolean layout)
			throws IOException {
		convert(in, out, layout, null, Double.NaN, Double.NaN);
	}

	public static void convert(InputStream in, OutputStream out,
			boolean layout, String css, double width, double height)
			throws IOException {
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

	public static void main(String... args) {
		InputStream in = System.in;
		OutputStream out = System.out;
		boolean layout = false;
		String css = null;
		double width = Double.NaN, height = Double.NaN;

		if (args != null) {
			LinkedList<String> noArg = new LinkedList<String>();

			for (int i = 0; i < args.length; i++) {
				if (args[i].matches("--.*|-\\w")) {
					if (args[i].startsWith("--stylesheet=")) {
						css = args[i].substring("--stylesheet=".length());

						if (css.matches("^\".*\"$"))
							css = css.substring(1, css.length() - 1);
					} else if (args[i].equals("--do-layout")) {
						layout = true;
					} else if (args[i].startsWith("--width=")) {
						String w = args[i].substring("--width=".length());

						if (w.matches("^\".*\"$"))
							w = w.substring(1, w.length() - 1);

						if (!w.matches("^\\d+([.]\\d+)?$")) {
							System.err.printf("Invalid width : %s%n", w);
							System.exit(1);
						}

						width = Double.parseDouble(w);
					} else if (args[i].startsWith("--height=")) {
						String h = args[i].substring("--height=".length());

						if (h.matches("^\".*\"$"))
							h = h.substring(1, h.length() - 1);

						if (!h.matches("^\\d+([.]\\d+)?$")) {
							System.err.printf("Invalid height : %s%n", h);
							System.exit(1);
						}

						height = Double.parseDouble(h);
					} else if (args[i].matches("(--help|-h)")) {
						usage();
						System.exit(0);
					} else {
						usage();
						System.exit(1);
					}
				} else {
					noArg.add(args[i]);
				}
			}

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
						System.err.printf(
								"Input file \"%s\" does not exists.\n",
								noArg.get(0));
						System.exit(1);
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
		}

		try {
			convert(in, out, layout, css, width, height);
		} catch (IOException ioe) {
			System.err.printf("Failed to convert. %s : %s%n", ioe.getClass()
					.getName(), ioe.getMessage());
			System.exit(1);
		}

		System.exit(0);
	}

	public static void usage() {
		System.out.printf("java %s [OPTIONS] IN OUT\n",
				DGS2TikZ.class.getName());
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
