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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;

import org.graphstream.algorithm.generator.Generator;
import org.graphstream.stream.file.FileSink;
import org.graphstream.stream.file.FileSource;
import org.graphstream.tool.ToolOption.ToolEnumOption;
import org.graphstream.tool.ToolOption.Type;

public abstract class Tool implements ToolsCommon {
	protected static final String[][] commonShortcuts = { { "-h", "--help" } };

	protected String name;
	protected String description;

	protected ToolOption.ParsedOptions options;
	protected HashMap<String, ToolOption> allowedOptions;
	protected String[][] shortcuts;
	protected boolean haveInput;
	protected boolean haveOutput;

	protected int nonOptions;

	protected PrintStream err;
	protected boolean exitOnFailed;

	public Tool(String name, String description, boolean input, boolean output) {
		this.name = name;
		this.description = description;
		this.haveInput = input;
		this.haveOutput = output;
		this.err = System.err;
		this.nonOptions = 0;
		this.allowedOptions = new HashMap<String, ToolOption>();
		this.exitOnFailed = true;

		if (input)
			addSourceOption();

		if (output)
			addSinkOption();
	}

	protected void setErr(PrintStream err) {
		this.err = err;
	}

	protected void setShortcuts(String[][] shortcuts) {
		this.shortcuts = shortcuts;
	}

	protected void addSourceOption() {
		addOption(SOURCE_KEY, SOURCE_DESCRIPTION, true, Type.STRING);
		addOption(SOURCE_FORMAT_KEY, SOURCE_FORMAT_DESCRIPTION, true,
				SourceFormat.class);
		addOption(SOURCE_OPTIONS_KEY, SOURCE_OPTIONS_DESCRIPTION, true,
				Type.STRING);
	}

	protected void addSinkOption() {
		addOption(SINK_KEY, SINK_DESCRIPTION, true, Type.STRING);
		addOption(SINK_FORMAT_KEY, SINK_FORMAT_DESCRIPTION, true,
				SinkFormat.class);
		addOption(SINK_OPTIONS_KEY, SINK_OPTIONS_DESCRIPTION, true, Type.STRING);
	}

	protected void addGeneratorOption(boolean optional) {
		addOption(GENERATOR_TYPE_KEY, GENERATOR_TYPE_DESCRIPTION, optional,
				GeneratorType.class);
		addOption(GENERATOR_OPTIONS_KEY, "", optional, Type.STRING);
	}

	protected void addStyleOption(boolean optional) {
		addOption(STYLESHEET_KEY, STYLESHEET_DESCRIPTION, optional, Type.STRING);
	}

	protected void addHelpOption() {
		addOption(HELP_KEY, HELP_DESCRIPTION, true, Type.FLAG);
	}

	protected void addOption(String key, String description, boolean optional,
			Type type) {
		ToolOption to = new ToolOption(key, description, optional, type);
		allowedOptions.put(key, to);
	}

	protected <T extends Enum<T>> void addOption(String key,
			String description, boolean optional, Class<T> choices) {
		ToolEnumOption<T> to = new ToolEnumOption<T>(key, description,
				optional, choices);
		allowedOptions.put(key, to);
	}

	public void init(String... args) {
		options = new ToolOption.ParsedOptions();

		if (shortcuts != null)
			Tools.removeShortcuts(args, shortcuts);
		
		Tools.removeShortcuts(args, commonShortcuts);
		Tools.parseArgs(args, options);

		if (options.isHelpNeeded()) {
			usage(System.out);
			System.exit(0);
		}

		check();
	}

	public FileSource getSource(SourceFormat def) {
		SourceFormat format = getSourceFormat(def);
		String[][] sourceOptions = null;

		if (options.contains(SOURCE_OPTIONS_KEY))
			sourceOptions = Tools.getKeyValue(options.get(SOURCE_OPTIONS_KEY));

		return Tools.sourceFor(format, sourceOptions);
	}

	public SourceFormat getSourceFormat(SourceFormat def) {
		if (options.contains(SOURCE_FORMAT_KEY))
			return options.getEnum(SOURCE_FORMAT_KEY, SourceFormat.class);

		return def;
	}

	public FileSink getSink(SinkFormat def) {
		SinkFormat format = getSinkFormat(def);
		String[][] sinkOptions = null;

		if (options.contains(SINK_OPTIONS_KEY))
			sinkOptions = Tools.getKeyValue(options.get(SINK_OPTIONS_KEY));

		return Tools.sinkFor(format, sinkOptions);
	}

	public SinkFormat getSinkFormat(SinkFormat def) {
		if (options.contains(SINK_FORMAT_KEY))
			return options.getEnum(SINK_FORMAT_KEY, SinkFormat.class);

		return def;
	}

	public Generator getGenerator(GeneratorType def) {
		GeneratorType format = def;
		String[][] generatorOptions = null;

		if (options.contains(GENERATOR_TYPE_KEY))
			format = options.getEnum(GENERATOR_TYPE_KEY, GeneratorType.class);

		if (options.contains(GENERATOR_OPTIONS_KEY))
			generatorOptions = Tools.getKeyValue(options
					.get(GENERATOR_OPTIONS_KEY));

		return Tools.generatorFor(format, generatorOptions);
	}

	public InputStream getInput() {
		if (options.contains(SOURCE_KEY)) {
			String url = options.get(SOURCE_KEY);

			try {
				InputStream in = Tools.getFileOrUrlAsStream(url);
				return in;
			} catch (FileNotFoundException e) {
				err.printf("File not found \"%s\"\n", url);
				System.exit(1);
			}
		}

		return System.in;
	}

	public OutputStream getOutput() {
		if (options.contains(SINK_KEY)) {
			String path = options.get(SINK_KEY);

			try {
				return new FileOutputStream(path);
			} catch (FileNotFoundException e) {
				err.printf("Can no write to \"%s\"\n", path);
				System.exit(1);
			}
		}

		return System.out;
	}

	public String getStyleSheet() {
		if (options.contains(STYLESHEET_KEY)) {
			String css = options.get(STYLESHEET_KEY);

			try {
				String content = Tools.loadFileContent(css);
				return content;
			} catch (FileNotFoundException e) {
				// Ignore
			} catch (IOException e) {
				err.printf("Error while getting style sheet %s : %s\n", e
						.getClass().getSimpleName(), e.getMessage());
				System.exit(1);
			}

			return css;
		}

		return "";
	}

	public boolean getFlagOption(String key) {
		return options.contains(key);
	}

	public boolean getBoolOption(String key, boolean def) {
		if (options.contains(key))
			return options.getBoolean(key);

		return def;
	}

	public int getIntOption(String key, int def) {
		if (options.contains(key))
			return options.getInt(key);

		return def;
	}

	public double getRealOption(String key, double def) {
		if (options.contains(key))
			return options.getDouble(key);

		return def;
	}

	public <T extends Enum<T>> T getEnumOption(String key, Class<T> choices,
			T def) {
		if (options.contains(key))
			return options.getEnum(key, choices);

		return def;
	}

	public String[][] getOptionsOption(String key) {
		if (options.contains(key))
			return options.getOptions(key);

		return null;
	}

	public boolean check() {
		for (String key : options) {
			if (!allowedOptions.containsKey(key)) {
				err.printf("unknown options \"%s\".\n", key);

				if (exitOnFailed)
					System.exit(1);

				return false;
			}
		}

		for (ToolOption opt : allowedOptions.values()) {
			switch (options.check(opt)) {
			case INVALID:
				err.printf("Invalid option \"%s\".\n", opt.key);

				if (exitOnFailed)
					System.exit(1);

				return false;
			case MISSING:
				err.printf("Missing option \"%s\".\n", opt.key);

				if (exitOnFailed)
					System.exit(1);

				return false;
			case HELP:
				if (opt instanceof ToolEnumOption)
					Tools.printChoice(System.out,
							((ToolEnumOption<?>) opt).choices, "");
				System.exit(0);
			}
		}

		if (!options.checkNotOptions(nonOptions)) {
			err.printf("Invalid args count.\n");
			usage(err);

			if (exitOnFailed)
				System.exit(1);

			return false;
		}

		return true;
	}

	public abstract void run();

	public void usage(PrintStream out) {
		out.printf("Usage : java %s [OPTION]\n", getClass().getName());
		out.printf("\n%s\n", description);
		out.printf("with OPTIONS :\n");

		int s = 0;

		for (ToolOption opt : allowedOptions.values())
			s = Math.max(s, opt.key.length());

		String format = String.format("\t--%%-%ds : %%s\n", s);

		for (ToolOption opt : allowedOptions.values())
			out.printf(format, opt.key, opt.description);

		if (shortcuts != null) {
			out.printf("\nand shortcuts (#n indicate the nth non-option arg):\n");

			s = 0;

			for (int i = 0; i < shortcuts.length; i++)
				s = Math.max(s, shortcuts[i][0].length());

			format = String.format("\t%%-%ds : %%s\n", s);

			for (int i = 0; i < shortcuts.length; i++)
				out.printf(format, shortcuts[i][0], shortcuts[i][1]);
		}
	}
}
