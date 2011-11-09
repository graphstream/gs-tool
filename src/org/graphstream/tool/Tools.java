/*
 * Copyright 2006 - 2011 
 *     Stefan Balev 	<stefan.balev@graphstream-project.org>
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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.LinkedList;

import org.graphstream.algorithm.generator.BarabasiAlbertGenerator;
import org.graphstream.algorithm.generator.ChvatalGenerator;
import org.graphstream.algorithm.generator.DorogovtsevMendesGenerator;
import org.graphstream.algorithm.generator.FlowerSnarkGenerator;
import org.graphstream.algorithm.generator.FullGenerator;
import org.graphstream.algorithm.generator.Generator;
import org.graphstream.algorithm.generator.GridGenerator;
import org.graphstream.algorithm.generator.IncompleteGridGenerator;
import org.graphstream.algorithm.generator.LCFGenerator;
import org.graphstream.algorithm.generator.LobsterGenerator;
import org.graphstream.algorithm.generator.PetersenGraphGenerator;
import org.graphstream.algorithm.generator.PointsOfInterestGenerator;
import org.graphstream.algorithm.generator.PreferentialAttachmentGenerator;
import org.graphstream.algorithm.generator.RandomEuclideanGenerator;
import org.graphstream.algorithm.generator.RandomFixedDegreeDynamicGraphGenerator;
import org.graphstream.algorithm.generator.RandomGenerator;
import org.graphstream.algorithm.generator.lcf.Balaban10CageGraphGenerator;
import org.graphstream.algorithm.generator.lcf.Balaban11CageGraphGenerator;
import org.graphstream.algorithm.generator.lcf.BidiakisCubeGenerator;
import org.graphstream.algorithm.generator.lcf.BiggsSmithGraphGenerator;
import org.graphstream.algorithm.generator.lcf.CubicalGraphGenerator;
import org.graphstream.algorithm.generator.lcf.DesarguesGraphGenerator;
import org.graphstream.algorithm.generator.lcf.DodecahedralGraphGenerator;
import org.graphstream.algorithm.generator.lcf.DyckGraphGenerator;
import org.graphstream.algorithm.generator.lcf.F26AGraphGenerator;
import org.graphstream.algorithm.generator.lcf.FosterGraphGenerator;
import org.graphstream.algorithm.generator.lcf.FranklinGraphGenerator;
import org.graphstream.algorithm.generator.lcf.FruchtGraphGenerator;
import org.graphstream.algorithm.generator.lcf.GrayGraphGenerator;
import org.graphstream.algorithm.generator.lcf.HarriesGraphGenerator;
import org.graphstream.algorithm.generator.lcf.HarriesWongGraphGenerator;
import org.graphstream.algorithm.generator.lcf.HeawoodGraphGenerator;
import org.graphstream.algorithm.generator.lcf.LjubljanaGraphGenerator;
import org.graphstream.algorithm.generator.lcf.McGeeGraphGenerator;
import org.graphstream.algorithm.generator.lcf.MobiusKantorGraphGenerator;
import org.graphstream.algorithm.generator.lcf.NauruGraphGenerator;
import org.graphstream.algorithm.generator.lcf.PappusGraphGenerator;
import org.graphstream.algorithm.generator.lcf.TetrahedralGraphGenerator;
import org.graphstream.algorithm.generator.lcf.TruncatedCubicalGraphGenerator;
import org.graphstream.algorithm.generator.lcf.TruncatedDodecahedralGraphGenerator;
import org.graphstream.algorithm.generator.lcf.TruncatedOctahedralGraphGenerator;
import org.graphstream.algorithm.generator.lcf.TruncatedTetrahedralGraphGenerator;
import org.graphstream.algorithm.generator.lcf.Tutte12CageGraphGenerator;
import org.graphstream.algorithm.generator.lcf.TutteCoxeterGraphGenerator;
import org.graphstream.algorithm.generator.lcf.UtilityGraphGenerator;
import org.graphstream.algorithm.generator.lcf.WagnerGraphGenerator;
import org.graphstream.stream.file.FileSink;
import org.graphstream.stream.file.FileSinkDGS;
import org.graphstream.stream.file.FileSinkDOT;
import org.graphstream.stream.file.FileSinkGML;
import org.graphstream.stream.file.FileSinkImages;
import org.graphstream.stream.file.FileSinkSVG;
import org.graphstream.stream.file.FileSinkTikZ;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceDGS;
import org.graphstream.stream.file.FileSourceDOT;
import org.graphstream.stream.file.FileSourceGML;

public class Tools implements ToolsCommon {
	public static final int MAJOR_VERSION_NUMBER = 0;
	public static final int MINOR_VERSION_NUMBER = 1;

	public static String getToolsVersion() {
		return String.format("%d.%d", MAJOR_VERSION_NUMBER,
				MINOR_VERSION_NUMBER);
	}

	/**
	 * Get a source of format with given options.
	 */
	public static FileSource sourceFor(SourceFormat format, String[][] options) {
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
		}

		return source;
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
	public static FileSink sinkFor(SinkFormat format, String[][] options)
			throws IllegalArgumentException {
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
			FileSinkImages.OutputPolicy policy = FileSinkImages.OutputPolicy.BY_STEP;
			FileSinkImages.Quality quality = FileSinkImages.Quality.MEDIUM;
			FileSinkImages.LayoutPolicy layout = FileSinkImages.LayoutPolicy.NO_LAYOUT;
			String stylesheet = null;
			int spf = -1;
			int sas = -1;

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
					} else if (options[i][0].equals("layoutStepPerFrame")) {
						if (options[i][1].matches("\\d+"))
							spf = Integer.parseInt(options[i][1]);
						else {
							System.err.printf("Bad stepPerFrame value : %s\n",
									options[i][1]);
							System.exit(1);
						}
					} else if (options[i][0]
							.equals("layoutStepAfterStabilization")) {
						if (options[i][1].matches("\\d+"))
							sas = Integer.parseInt(options[i][1]);
						else {
							System.err.printf(
									"Bad stepAfterStabilization value : %s\n",
									options[i][1]);
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

			if (spf >= 0)
				((FileSinkImages) sink).setLayoutStepPerFrame(spf);

			if (sas >= 0)
				((FileSinkImages) sink).setLayoutStepAfterStabilization(sas);
		}
			break;
		case SVG:
			sink = new FileSinkSVG();
			break;
		case TIKZ: {
			sink = new FileSinkTikZ();

			boolean layout = false;
			double width = Double.NaN, height = Double.NaN;
			String css = null;

			if (options != null) {
				for (int o = 0; o < options.length; o++) {
					if (options[o][0].equals("layout")) {
						layout = Boolean.parseBoolean(options[o][1]);
					} else if (options[o][0].equals("stylesheet")) {
						css = getCSS(options[o][1]);
					} else if (options[o][0].equals("width")) {
						width = Double.parseDouble(options[o][1]);
					} else if (options[o][0].equals("height")) {
						height = Double.parseDouble(options[o][1]);
					}
				}
			}

			((FileSinkTikZ) sink).setLayout(layout);

			if (!Double.isNaN(width))
				((FileSinkTikZ) sink).setWidth(width);
			if (!Double.isNaN(height))
				((FileSinkTikZ) sink).setHeight(height);

			((FileSinkTikZ) sink).setCSS(css);
		}
			break;
		}

		return sink;
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
	public static Generator generatorFor(GeneratorType type, String[][] options) {
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
		case BARABASI_ALBERT:
			gen = new BarabasiAlbertGenerator();
			break;
		case CHVATAL:
			gen = new ChvatalGenerator();
			break;
		case FLOWER_SNARK:
			gen = new FlowerSnarkGenerator();
			break;
		case LCF:
			gen = new LCFGenerator(null, 0, false);
			break;
		case LOBSTER:
			gen = new LobsterGenerator(2);
			break;
		case CATERPILLAR:
			gen = new LobsterGenerator(1);
			break;
		case PETERSEN:
			gen = new PetersenGraphGenerator();
			break;
		case BALABAN_10_CAGE:
			gen = new Balaban10CageGraphGenerator();
			break;
		case BALABAN_11_CAGE:
			gen = new Balaban11CageGraphGenerator();
			break;
		case BIDIAKIS_CUBE:
			gen = new BidiakisCubeGenerator();
			break;
		case BIGGS_SMITH:
			gen = new BiggsSmithGraphGenerator();
			break;
		case CUBICAL:
			gen = new CubicalGraphGenerator();
			break;
		case DESARGUES:
			gen = new DesarguesGraphGenerator();
			break;
		case DODECAHEDRAL:
			gen = new DodecahedralGraphGenerator();
			break;
		case DYCK:
			gen = new DyckGraphGenerator();
			break;
		case F26A:
			gen = new F26AGraphGenerator();
			break;
		case FOSTER:
			gen = new FosterGraphGenerator();
			break;
		case FRANKLIN:
			gen = new FranklinGraphGenerator();
			break;
		case FRUCHT:
			gen = new FruchtGraphGenerator();
			break;
		case GRAY:
			gen = new GrayGraphGenerator();
			break;
		case HARRIES:
			gen = new HarriesGraphGenerator();
			break;
		case HARRIES_WONG:
			gen = new HarriesWongGraphGenerator();
			break;
		case HEAWOOD:
			gen = new HeawoodGraphGenerator();
			break;
		case LJUBLJANA:
			gen = new LjubljanaGraphGenerator();
			break;
		case MCGEE:
			gen = new McGeeGraphGenerator();
			break;
		case MOBIUS_KANTOR:
			gen = new MobiusKantorGraphGenerator();
			break;
		case NAURU:
			gen = new NauruGraphGenerator();
			break;
		case PAPPUS:
			gen = new PappusGraphGenerator();
			break;
		case TETRAHEDRAL:
			gen = new TetrahedralGraphGenerator();
			break;
		case TRUNCATED_CUBICAL:
			gen = new TruncatedCubicalGraphGenerator();
			break;
		case TRUNCATED_DODECAHEDRAL:
			gen = new TruncatedDodecahedralGraphGenerator();
			break;
		case TRUNCATED_OCTAHEDRAL:
			gen = new TruncatedOctahedralGraphGenerator();
			break;
		case TRUNCATED_TETRAHEDRAL:
			gen = new TruncatedTetrahedralGraphGenerator();
			break;
		case TUTTE_12_CAGE:
			gen = new Tutte12CageGraphGenerator();
			break;
		case TUTTE_COXETER:
			gen = new TutteCoxeterGraphGenerator();
			break;
		case UTILITY:
			gen = new UtilityGraphGenerator();
			break;
		case WAGNER:
			gen = new WagnerGraphGenerator();
			break;
		}

		return gen;
	}

	public static String getCSS(String url) {
		try {
			return loadFileContent(url);
		} catch (IOException e) {
			return url;
		}
	}

	public static Reader getFileOrUrlAsStream(String url)
			throws FileNotFoundException {
		File f = new File(url);

		if (f.exists())
			return new FileReader(f);
		else {
			InputStream in = Tools.class.getClassLoader().getResourceAsStream(
					url);

			return new InputStreamReader(in);
		}
	}

	/**
	 * Load file/url content into one string.
	 * 
	 * @param url
	 *            url or path
	 * @return content
	 * @throws IOException
	 */
	public static String loadFileContent(String url) throws IOException {
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

	public static String[][] getKeyValue(String full)
			throws IllegalArgumentException {
		String[] options = full.split("\\s*;\\s*");
		String[][] finalOptions = null;

		if (options != null) {
			finalOptions = new String[options.length][2];
			for (int i = 0; i < options.length; i++) {
				if (options[i].indexOf('=') > 0) {
					finalOptions[i][0] = options[i].substring(0,
							options[i].indexOf('='));
					finalOptions[i][1] = options[i].substring(options[i]
							.indexOf('=') + 1);
				} else {
					throw new IllegalArgumentException(options[i]);
				}
			}
		} else {
			throw new NullPointerException();
		}

		return finalOptions;
	}

	public static <T extends Enum<T>> void printChoice(PrintStream out,
			Class<T> choices, String prefix) {
		if (prefix == null)
			prefix = "";

		out.printf("%s%s values:\n", prefix, choices.getSimpleName());
		for (T c : choices.getEnumConstants())
			out.printf("%s  - %s\n", prefix, c);
	}

	public static boolean check(Optionable opt, String[][] options) {
		if (options == null)
			return true;

		for (int i = 0; i < options.length; i++)
			if (!opt.isValidOption(options[i][0]))
				return false;

		return true;
	}

	public static void removeShortcuts(String[] args, String[][] shortcuts) {
		LinkedList<Integer> nonKey = null;

		for (int k = 0; k < args.length; k++) {
			for (int l = 0; l < shortcuts.length; l++) {
				if (shortcuts[l][0].equals(args[k])) {
					args[k] = shortcuts[l][1];
					break;
				}
			}

			if (args[k].charAt(0) != '-') {
				if (nonKey == null)
					nonKey = new LinkedList<Integer>();

				nonKey.add(k);
			}
		}

		if (nonKey != null) {
			for (int i = 0; i < nonKey.size(); i++) {
				for (int l = 0; l < shortcuts.length; l++) {
					if (shortcuts[l][0].matches(String.format("#%d", i + 1))) {
						args[nonKey.get(i)] = String.format(shortcuts[l][1],
								args[nonKey.get(i)]);
						break;
					}
				}
			}
		}
	}

	public static void parseArgs(String[] args, ToolOption.ParsedOptions options) {
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

				options.registerOption(key, value);
			} else {
				options.registerNotOption(args[k]);
			}
		}
	}

	public static InputStream getInput(String url) throws IOException {
		File f = new File(url);
		InputStream in = null;

		if (!f.exists()) {
			in = Convert.class.getResourceAsStream(url);

			if (in == null)
				throw new FileNotFoundException(String.format(
						"Input file \"%s\" does not exists.\n", url));
		} else
			in = new FileInputStream(f);

		return in;
	}
}
