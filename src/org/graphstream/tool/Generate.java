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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;

import org.graphstream.algorithm.generator.DorogovtsevMendesGenerator;
import org.graphstream.algorithm.generator.FullGenerator;
import org.graphstream.algorithm.generator.Generator;
import org.graphstream.algorithm.generator.GridGenerator;
import org.graphstream.algorithm.generator.IncompleteGridGenerator;
import org.graphstream.algorithm.generator.PointsOfInterestGenerator;
import org.graphstream.algorithm.generator.PreferentialAttachmentGenerator;
import org.graphstream.algorithm.generator.RandomEuclideanGenerator;
import org.graphstream.algorithm.generator.RandomFixedDegreeDynamicGraphGenerator;
import org.graphstream.algorithm.generator.RandomGenerator;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.ElementSink;
import org.graphstream.stream.file.FileSink;
import org.graphstream.stream.file.FileSinkDGS;
import org.graphstream.stream.file.FileSinkDOT;
import org.graphstream.stream.file.FileSinkGML;
import org.graphstream.stream.file.FileSinkImages;
import org.graphstream.stream.file.FileSinkSVG;
import org.graphstream.stream.file.FileSinkTikZ;

/**
 * Helper to generate graph in command line.
 * 
 * @author Guilhelm Savin
 * 
 */
public class Generate {

	/**
	 * Type of generator used.
	 * 
	 */
	public static enum Type {
		PREFERENTIAL_ATTACHMENT, DOROGOVTSEV_MENDES, GRID, INCOMPLETE_GRID, RANDOM, RANDOM_EUCLIDEAN, RANDOM_FIXED_DEGREE_DYNAMIC_GRAPH, FULL, POINTS_OF_INTEREST
	}

	/**
	 * Output format.
	 * 
	 */
	public static enum Format {
		DGS(true), TIKZ(false), DOT(true), GML(false), SVG(false), IMAGES(true,
				"prefix", "outputType", "outputPolicy", "resolution",
				"layoutPolicy", "quality", "stylesheet")

		;

		private boolean dynamic;
		private String[] options;

		private Format(boolean dynamic, String... options) {
			this.dynamic = dynamic;
			this.options = options;
		}

		public boolean hasDynamicSupport() {
			return dynamic;
		}

		public boolean isValidOption(String key) {
			if (options == null)
				return false;

			for (int i = 0; i < options.length; i++)
				if (options[i].equals(key))
					return true;

			return false;
		}

		public int getOptionCount() {
			if (options == null)
				return 0;
			return options.length;
		}

		public String getOption(int i) {
			if (options == null || i >= options.length || i < 0)
				return null;

			return options[i];
		}
	}

	/**
	 * Create a generator type with given options.
	 * 
	 * @param type
	 *            type of generator
	 * @param options
	 *            options passed to the generator.
	 * @return a generator
	 */
	public static Generator generatorFor(Type type, String[][] options) {
		Generator gen = null;

		switch (type) {
		case PREFERENTIAL_ATTACHMENT:
			gen = new PreferentialAttachmentGenerator();
			break;
		case DOROGOVTSEV_MENDES:
			gen = new DorogovtsevMendesGenerator();
			break;
		case GRID:
			gen = new GridGenerator();
			break;
		case INCOMPLETE_GRID:
			gen = new IncompleteGridGenerator();
			break;
		case RANDOM:
			gen = new RandomGenerator();
			break;
		case RANDOM_EUCLIDEAN:
			gen = new RandomEuclideanGenerator();
			break;
		case RANDOM_FIXED_DEGREE_DYNAMIC_GRAPH:
			gen = new RandomFixedDegreeDynamicGraphGenerator();
			break;
		case FULL:
			gen = new FullGenerator();
			break;
		case POINTS_OF_INTEREST:
			gen = new PointsOfInterestGenerator();
			break;
		}

		return gen;
	}

	/**
	 * Get a sink of format with given options.
	 * 
	 * @param format
	 *            format of the sink
	 * @param options
	 *            options passed to the sink
	 * @return a sink
	 */
	public static FileSink sinkFor(Format format, String[][] options) {
		FileSink sink = null;

		switch (format) {
		case DGS:
			sink = new FileSinkDGS();
			break;
		case DOT:
			sink = new FileSinkDOT();
			break;
		case GML:
			sink = new FileSinkGML();
			break;
		case IMAGES: {
			String prefix = "";
			FileSinkImages.OutputType outputType = FileSinkImages.OutputType.JPG;
			FileSinkImages.Resolution resolution = FileSinkImages.Resolutions.VGA;
			FileSinkImages.OutputPolicy policy = FileSinkImages.OutputPolicy.ByStepOutput;
			FileSinkImages.Quality quality = FileSinkImages.Quality.MEDIUM;
			FileSinkImages.LayoutPolicy layout = FileSinkImages.LayoutPolicy.NoLayout;
			String stylesheet = null;

			if (options != null) {
				for (int i = 0; i < options.length; i++) {
					if (options[i][0].equals("prefix")) {
						prefix = options[i][1];
					} else if (options[i][0].equals("outputType")) {
						try {
							outputType = FileSinkImages.OutputType
									.valueOf(options[i][1]);
						} catch (IllegalArgumentException e) {
							System.err
									.printf("Invalid outputType. Use one of:\n");
							for (FileSinkImages.OutputType t : FileSinkImages.OutputType
									.values())
								System.err.printf("- %s\n", t);
							System.exit(1);
						}
					} else if (options[i][0].equals("resolution")) {
						if (options[i][1].matches("\\d+x\\d+")) {
							int width = Integer.parseInt(options[i][1]
									.substring(0, options[i][1].indexOf('x')));
							int height = Integer.parseInt(options[i][1]
									.substring(options[i][1].indexOf('x') + 1));

							resolution = new FileSinkImages.CustomResolution(
									width, height);
						} else {
							try {
								resolution = FileSinkImages.Resolutions
										.valueOf(options[i][1]);
							} catch (IllegalArgumentException e) {
								System.err
										.printf("Invalid outputType. Use \"withxheight\" or one of:\n");
								for (FileSinkImages.Resolutions t : FileSinkImages.Resolutions
										.values())
									System.err.printf("- %s\n", t);
								System.exit(1);
							}
						}
					} else if (options[i][0].equals("outputPolicy")) {
						try {
							policy = FileSinkImages.OutputPolicy
									.valueOf(options[i][1]);
						} catch (IllegalArgumentException e) {
							System.err.printf("Invalid policy. Use one of:\n");
							for (FileSinkImages.OutputPolicy t : FileSinkImages.OutputPolicy
									.values())
								System.err.printf("- %s\n", t);
							System.exit(1);
						}
					} else if (options[i][0].equals("layoutPolicy")) {
						try {
							layout = FileSinkImages.LayoutPolicy
									.valueOf(options[i][1]);
						} catch (IllegalArgumentException e) {
							System.err
									.printf("Invalid layout policy. Use one of:\n");
							for (FileSinkImages.LayoutPolicy t : FileSinkImages.LayoutPolicy
									.values())
								System.err.printf("- %s\n", t);
							System.exit(1);
						}
					} else if (options[i][0].equals("stylesheet")) {
						try {
							stylesheet = loadFileContent(options[i][1]);
						} catch (IOException e) {
							System.err
									.printf("Can not load stylesheet content : \"%s\".\n",
											options[i][1]);
							System.err.printf("Error is %s : %s\n", e
									.getClass().getName(), e.getMessage());
							System.exit(1);
						}
					}
				}
			}

			sink = new FileSinkImages(prefix, outputType, resolution, policy);

			((FileSinkImages) sink).setQuality(quality);
			((FileSinkImages) sink).setLayoutPolicy(layout);

			if (stylesheet != null)
				((FileSinkImages) sink).setStyleSheet(stylesheet);
		}
			break;
		case SVG:
			sink = new FileSinkSVG();
			break;
		case TIKZ:
			sink = new FileSinkTikZ();
			break;
		}

		return sink;
	}

	/**
	 * Load file/url content into one string.
	 * 
	 * @param url
	 *            url or path
	 * @return content
	 * @throws IOException
	 */
	private static String loadFileContent(String url) throws IOException {
		Reader reader = null;
		File f = new File(url);

		if (f.exists())
			reader = new FileReader(f);
		else {
			InputStream in = Generate.class.getClassLoader()
					.getResourceAsStream(url);
			if (in != null)
				reader = new InputStreamReader(in);
		}

		if (reader == null)
			throw new FileNotFoundException(url);

		StringBuilder builder = new StringBuilder();
		char[] buffer = new char[128];
		int r;

		while ((r = reader.read(buffer, 0, 128)) > 0)
			builder.append(buffer, 0, r);

		return builder.toString();
	}

	public static void main(String... args) {
		Type type = Type.PREFERENTIAL_ATTACHMENT;
		Format format = Format.DGS;
		String[][] generatorOptions = null;
		String[][] formatOptions = null;

		int size = 0;
		int ite = 0;
		long delay = 0;
		boolean loop = true;
		int iteration = 0;
		boolean export = false;
		boolean force = false;

		OutputStream out = System.out;
		Generator gen = null;
		FileSink sink = null;
		ElementCounter counter = new ElementCounter();

		String path = null;

		if (args == null) {
			usage(System.err);
			System.exit(1);
		}

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

				if (idx < 0) {
					key = args[k].substring(2);
					value = null;
				} else {
					key = args[k].substring(2, idx);
					value = args[k].substring(idx + 1).trim();
				}

				if (value != null && value.matches("^\".*\"$"))
					value = value.substring(1, value.length() - 1);

				if (key.equals("type")) {
					try {
						type = Type.valueOf(value);
					} catch (IllegalArgumentException e) {
						System.err.printf("Invalid generator type : \"%s\".\n",
								value);
						System.exit(1);
					}
				} else if (key.equals("format")) {
					try {
						format = Format.valueOf(value);
					} catch (IllegalArgumentException e) {
						System.err.printf("Invalid output format : \"%s\".\n",
								value);
						System.exit(1);
					}
				} else if (key.equals("size")) {
					if (value.matches("^\\d+$")) {
						size = Integer.parseInt(value);
					} else {
						System.err.printf("Invalid size : %s.\n", value);
						System.exit(1);
					}
				} else if (key.equals("iteration")) {
					if (value.matches("^\\d+$")) {
						iteration = Integer.parseInt(value);
					} else {
						System.err.printf("Invalid iteration count : %s.\n",
								value);
						System.exit(1);
					}
				} else if (key.equals("delay")) {
					if (value.matches("^\\d+$")) {
						delay = Long.parseLong(value);
					} else {
						System.err.printf("Invalid delay : %s.\n", value);
						System.exit(1);
					}
				} else if (key.equals("generator-options")) {
					String[] options = value.split("\\s+;\\s+");

					if (options != null) {
						generatorOptions = new String[options.length][2];
						for (int i = 0; i < options.length; i++) {
							if (options[i].indexOf('=') > 0) {
								generatorOptions[i][0] = options[i].substring(
										0, options[i].indexOf('='));
								generatorOptions[i][1] = options[i]
										.substring(options[i].indexOf('=') + 1);
							} else {
								System.err.printf("Invalid options : %s.\n",
										options[i]);
								System.err.printf("Format is : key=value.\n");
								System.exit(1);
							}
						}
					}
				} else if (key.equals("output-options")) {
					String[] options = value.split("\\s*;\\s*");

					if (options != null) {
						formatOptions = new String[options.length][2];
						for (int i = 0; i < options.length; i++) {
							if (options[i].indexOf('=') > 0) {
								formatOptions[i][0] = options[i].substring(0,
										options[i].indexOf('='));
								formatOptions[i][1] = options[i]
										.substring(options[i].indexOf('=') + 1);
							} else {
								System.err.printf("Invalid options : %s.\n",
										options[i]);
								System.err.printf("Format is : key=value.\n");
								System.exit(1);
							}
						}
					} else {
						System.err
								.printf("--output-options is done but value is null.\n");
					}
				} else if (key.equals("export")) {
					export = true;
				} else if (key.equals("force")) {
					force = true;
				} else {
					System.err.printf("Unknown option : \"%s\".\n", key);
					usage(System.err);
					System.exit(1);
				}
			} else if (args[k].matches("--help|-h")) {
				usage(System.out);
				System.exit(0);
			} else if (args[k].startsWith("--")) {
				System.err.printf("Unknown option : \"%s\"\n", args[k]);
				usage(System.err);
				System.exit(1);
			} else if (path == null) {
				path = args[k];
			} else {
				System.err
						.printf("Just one path is allowed. Previous is \"%s\".\n",
								path);
				usage(System.err);
				System.exit(1);
			}
		}

		if (path != null) {
			try {
				out = new FileOutputStream(path);
			} catch (IOException e) {
				System.err.printf("Error with file \"%s\".\n", path);
				System.err.printf("Message is : %s.\n", e.getMessage());
				System.exit(1);
			}
		}

		if (!export && !format.hasDynamicSupport() && !force) {
			System.err.printf("The format \"%s\" is not dynamic. ",
					format.name());
			System.err
					.printf("Use --export to export the whole at the end of the generation ");
			System.err
					.printf("or use --force to force the dynamic generation.\n");
			System.exit(1);
		}

		if (formatOptions != null) {
			for (int i = 0; i < formatOptions.length; i++) {
				if (!format.isValidOption(formatOptions[i][0])) {
					System.err.printf(
							"Invalid option \"%s\" for output. Options are:\n",
							formatOptions[i][0]);
					for (int k = 0; k < format.getOptionCount(); k++)
						System.err.printf("- %s\n", format.getOption(k));
					System.exit(1);
				}
			}
		}

		Graph exportGraph = null;

		gen = generatorFor(type, generatorOptions);
		sink = sinkFor(format, formatOptions);
		gen.addElementSink(counter);

		if (export) {
			exportGraph = new DefaultGraph("export");
			gen.addSink(exportGraph);
		} else {
			gen.addSink(sink);

			try {
				sink.begin(out);
			} catch (IOException e1) {
				System.err.printf("Cannot begin the sink.\n");
				System.err.printf("Message is : %s.\n", e1.getMessage());
				System.exit(1);
			}
		}

		gen.begin();

		do {
			gen.nextEvents();

			ite++;

			loop = (iteration <= 0 || ite < iteration)
					&& (size <= 0 || counter.getNodeCount() < size);

			try {
				if (!export)
					sink.flush();
			} catch (IOException e1) {
				// Ignore
			}

			if (delay > 0) {
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					loop = false;
				}
			}
		} while (loop);

		gen.end();

		try {
			if (export) {
				sink.writeAll(exportGraph, out);
			} else {
				sink.end();
			}
		} catch (IOException e) {
			System.err.printf("Cannot end the sink.\n");
			System.err.printf("Message is : %s.\n", e.getMessage());
			System.exit(1);
		}
	}

	private static final String[][] shortcuts = {
			{ "-pa", "--type=PREFERENTIAL_ATTACHMENT" },
			{ "-dm", "--type=DOROGOVTSEV_MENDES" }, { "-f", "--type=FULL" },
			{ "-r", "--type=RANDOM" }, { "-g", "--type=GRID" },
			{ "-dgs", "--format=DGS" }, { "-dot", "--format=DOT" },
			{ "-gml", "--format=GML" }, { "-tikz", "--format=TIKZ" },
			{ "-i", "--format=IMAGES" }, { "-e", "--export" },
			{ "-H", "--size=100" }, { "-K", "--size=1000" },
			{ "-M", "--size=1000000" } };

	/**
	 * Usage of this class.
	 */
	public static void usage(PrintStream out) {
		out.printf("Usage: java %s [OPTIONS] [OUT]\n\n",
				Generate.class.getName());
		out.printf("with OUT is the output file path, or empty for stdout.\n");
		out.printf("with OPTIONS:\n");
		out.printf("\t--type=X                    : type of generator\n");
		for (Type t : Type.values())
			out.printf("\t\t%s%n", t.name());
		out.printf("\t--format=X                  : output format\n");
		for (Format f : Format.values())
			out.printf("\t\t%s%n", f.name());
		out.printf("\t--iteration=xxx             : iteration of the generator\n");
		out.printf("\t--size=xxx                  : size of graph\n");
		out.printf("\t--generator-options=\"...\" : options given to the generator\n");
		out.printf("\t--output-options=\"...\"    : options given to the output\n");
		out.printf("\t--delay=xxx                 : delay between iteration (ms)\n");
		out.printf("\t--export                    : export the graph after the generation.\n");
		out.printf("Shortcuts :\n");
		for (int i = 0; i < shortcuts.length; i++)
			out.printf("\t\"%s\"\t: \"%s\"\n", shortcuts[i][0], shortcuts[i][1]);
	}

	private static class ElementCounter implements ElementSink {

		int nodes = 0;
		int edges = 0;

		public int getNodeCount() {
			return nodes;
		}

		/*
		 * public int getEdgeCount() { return edges; }
		 */
		public void nodeAdded(String sourceId, long timeId, String nodeId) {
			nodes++;
		}

		public void nodeRemoved(String sourceId, long timeId, String nodeId) {
			nodes--;
		}

		public void edgeAdded(String sourceId, long timeId, String edgeId,
				String fromNodeId, String toNodeId, boolean directed) {
			edges++;
		}

		public void edgeRemoved(String sourceId, long timeId, String edgeId) {
			edges--;
		}

		public void graphCleared(String sourceId, long timeId) {
			nodes = 0;
			edges = 0;
		}

		public void stepBegins(String sourceId, long timeId, double step) {
		}
	}
}
