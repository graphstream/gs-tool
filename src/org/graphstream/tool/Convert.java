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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.LinkedList;

import org.graphstream.stream.file.FileSink;
import org.graphstream.stream.file.FileSource;

/**
 * A tool to convert from various formats to various formats...
 * 
 * @author Guilhelm Savin
 */
public class Convert implements ToolsCommon {

	public static void convert(InputStream in, FileSource source,
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

	protected static final String[][] shortcuts = { { "-h", "--help" } };

	public static void main(String... args) {
		InputStream in = System.in;
		OutputStream out = System.out;

		SourceFormat sourceFormat = null;
		SinkFormat sinkFormat = null;
		String[][] sourceOptions = null;
		String[][] sinkOptions = null;

		if (args == null) {
			usage(System.err);
			System.exit(1);
		}

		Tools.removeShortcuts(args, shortcuts);

		LinkedList<String> noArg = new LinkedList<String>();

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

				if (key.equals("help")) {
					usage(System.out);
					System.exit(0);
				} else if (key.equals("source")) {
					try {
						sourceFormat = SourceFormat.valueOf(value);
					} catch (IllegalArgumentException e) {
						System.err.printf("Bad source format \"%s\".\n", value);
						Tools.printChoice(System.err, SourceFormat.class, "");
						System.exit(1);
					}
				} else if (key.equals("sink")) {
					try {
						sinkFormat = SinkFormat.valueOf(value);
					} catch (IllegalArgumentException e) {
						System.err.printf("Bad sink format \"%s\".\n", value);
						Tools.printChoice(System.err, SinkFormat.class, "");
						System.exit(1);
					}
				} else if (key.equals("source-options")) {
					try {
						sourceOptions = Tools.getKeyValue(value);
					} catch (IllegalArgumentException e) {
						System.err
								.printf("Invalid options : %s.\nFormat is : key=value.\n",
										e.getMessage());
						System.exit(1);
					} catch (NullPointerException e) {
						System.err
								.printf("--source-options is done but value is null.\n");
					}
				} else if (key.equals("sink-options")) {
					try {
						sinkOptions = Tools.getKeyValue(value);
					} catch (IllegalArgumentException e) {
						System.err
								.printf("Invalid options : %s.\nFormat is : key=value.\n",
										e.getMessage());
						System.exit(1);
					} catch (NullPointerException e) {
						System.err
								.printf("--sink-options is done but value is null.\n");
					}
				} else {
					System.err.printf("Invalid option : %s.\n", key);
					usage(System.err);
					System.exit(1);
				}
			} else {
				noArg.add(args[i]);
			}
		}

		/*
		 * if (conv == null) {
		 * System.err.printf("No conversion type specified.\n"); System.exit(1);
		 * }
		 */
		if (sourceFormat == null) {
			System.err.printf("Source format not specified.\n");
			System.exit(1);
		}

		if (sinkFormat == null) {
			System.err.printf("Sink format not specified.\n");
			System.exit(1);
		}

		if (!Tools.check(sourceFormat, sourceOptions)) {
			System.err.printf("Invalid source options.\n");
			System.exit(1);
		}

		if (!Tools.check(sinkFormat, sinkOptions)) {
			System.err.printf("Invalid sink options.\n");
			System.exit(1);
		}
		
		FileSource source = Tools.sourceFor(sourceFormat, sourceOptions);
		FileSink sink = Tools.sinkFor(sinkFormat, sinkOptions);

		// options = optionsTMP.toArray(options);

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
			usage(System.err);
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
			convert(in, source, out, sink);
		} catch (IOException ioe) {
			System.err.printf("Failed to convert. %s : %s%n", ioe.getClass()
					.getName(), ioe.getMessage());
			System.exit(1);
		}

		System.exit(0);
	}

	public static void usage(PrintStream out) {
		out.printf("Usage: java %s --source=.. --sink=.. [OPTIONS] IN OUT\n\n",
				Convert.class.getName());

		out.printf("IN OUT can be:%n");
		out.printf("\t\"\" or \"- -\" : use system input/output%n");
		out.printf("\tifile       : read ifile and write to system output%n");
		out.printf("\t- ofile     : read system input and write to ofile%n");
		out.printf("\tifile ofile : read ifile and write to ofile%n");
		out.printf("OPTIONS :\n");
		out.printf("\t--source=..             : format of the source\n");
		Tools.printChoice(out, SourceFormat.class, "\t| ");
		out.printf("\t--sink=..               : format of the sink\n");
		Tools.printChoice(out, SinkFormat.class, "\t| ");
		out.printf("\t--source-options=\"..\" : options passed to the source\n");
		out.printf("\t--sink-options=\"..\"   : options passed to the sink\n");
	}
}
