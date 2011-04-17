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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

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
		Options options = new Options();

		if (args == null) {
			usage(System.err);
			System.exit(1);
		}

		Tools.removeShortcuts(args, shortcuts);
		Tools.parseArgs(args, options);

		if (!options.checkAllowedOptions(SOURCE_FORMAT_KEY, SOURCE_OPTIONS_KEY,
				SINK_FORMAT_KEY, SINK_OPTIONS_KEY, HELP_KEY)) {
			System.err.printf("Invalid options.\n");
			usage(System.err);
			System.exit(1);
		}

		options.checkSourceOptions(System.err, true);
		options.checkSinkOptions(System.err, true);
		options.checkHelp(System.err, true);

		options.checkNotOptions(0, 2, System.err, true);

		if (options.isHelpNeeded()) {
			usage(System.out);
			System.exit(0);
		}

		if (!options.contains(SOURCE_FORMAT_KEY)) {
			System.err.printf("Source format missing.\n");
			System.exit(1);
		}

		if (!options.contains(SINK_FORMAT_KEY)) {
			System.err.printf("Sink format missing.\n");
			System.exit(1);
		}
		
		sourceFormat = options.getEnum(SOURCE_FORMAT_KEY, SourceFormat.class);

		if (options.contains(SOURCE_OPTIONS_KEY))
			sourceOptions = Tools.getKeyValue(options.get(SOURCE_OPTIONS_KEY));

		sinkFormat = options.getEnum(SINK_FORMAT_KEY, SinkFormat.class);

		if (options.contains(SINK_OPTIONS_KEY))
			sinkOptions = Tools.getKeyValue(options.get(SINK_OPTIONS_KEY));

		FileSource source = Tools.sourceFor(sourceFormat, sourceOptions);
		FileSink sink = Tools.sinkFor(sinkFormat, sinkOptions);

		boolean inputIsFile = false;
		boolean outputIsFile = false;

		switch (options.getNotOptionsCount()) {
		case 0:
			break;
		case 2:
			if (!options.getNotOption(1).equals("-"))
				outputIsFile = true;
		case 1:
			if (!options.getNotOption(0).equals("-"))
				inputIsFile = true;
			break;
		default:
			usage(System.err);
			System.exit(1);
		}

		if (inputIsFile) {
			try {
				Tools.getInput(options.getNotOption(0));
			} catch (IOException ioe) {
				System.err
						.printf("Failed to open input file \"%s\".\nCause is %s : %s%n",
								options.getNotOption(0), ioe.getClass()
										.getName(), ioe.getMessage());
				System.exit(1);
			}
		}

		if (outputIsFile) {
			try {
				out = new FileOutputStream(options.getNotOption(1));
			} catch (IOException ioe) {
				System.err
						.printf("Failed to open output file \"%s\".\nCause is %s : %s%n",
								options.getNotOption(1), ioe.getClass()
										.getName(), ioe.getMessage());
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
		out.printf("\t--source-format=X     : format of the source, use X=? to list choices\n");
		out.printf("\t--source-options=..   : options passed to the source\n");
		out.printf("\t--sink-format=X       : format of the sink, use X=? to list choices\n");
		out.printf("\t--sink-options=..     : options passed to the sink\n");
	}
}
