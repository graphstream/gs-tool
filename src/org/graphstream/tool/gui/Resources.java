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
package org.graphstream.tool.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.graphstream.tool.ToolGUI;

public class Resources {
	public static final String GS_LOGO = "org/graphstream/tool/resources/logo.png";
	public static final String GS_ICON = "org/graphstream/tool/resources/gs-icon.png";

	public static final String LINES = "org/graphstream/tool/resources/lines.png";

	public static final String ABOUT_OFF = "org/graphstream/tool/resources/about.png";
	public static final String ABOUT_ON = "org/graphstream/tool/resources/about_on.png";
	public static final String ABOUT_CLICK = "org/graphstream/tool/resources/about_click.png";

	public static final String CONFIG_OFF = "org/graphstream/tool/resources/config.png";
	public static final String CONFIG_ON = "org/graphstream/tool/resources/config_on.png";
	public static final String CONFIG_CLICK = "org/graphstream/tool/resources/config_click.png";

	public static final String FIND_OFF = "org/graphstream/tool/resources/find.png";
	public static final String FIND_ON = "org/graphstream/tool/resources/find_on.png";
	public static final String FIND_CLICK = "org/graphstream/tool/resources/find_click.png";

	public static final String SOURCE_OFF = "org/graphstream/tool/resources/source.png";
	public static final String SOURCE_ON = "org/graphstream/tool/resources/source_on.png";
	public static final String SOURCE_CLICK = "org/graphstream/tool/resources/source_click.png";

	public static final String SINK_OFF = "org/graphstream/tool/resources/sink.png";
	public static final String SINK_ON = "org/graphstream/tool/resources/sink_on.png";
	public static final String SINK_CLICK = "org/graphstream/tool/resources/sink_click.png";

	public static final String RUN_OFF = "org/graphstream/tool/resources/run.png";
	public static final String RUN_ON = "org/graphstream/tool/resources/run_on.png";
	public static final String RUN_CLICK = "org/graphstream/tool/resources/run_click.png";

	public static final String CLOSE_OFF = "org/graphstream/tool/resources/close.png";
	public static final String CLOSE_ON = "org/graphstream/tool/resources/close_on.png";
	public static final String CLOSE_CLICK = "org/graphstream/tool/resources/close_click.png";

	public static final String FONT_BOLD = "org/graphstream/tool/resources/Ubuntu-B.ttf";
	public static final String FONT_REGULAR = "org/graphstream/tool/resources/Ubuntu-R.ttf";

	protected static Font fontBold = null;
	protected static Font fontRegular = null;

	protected static Paint backgroundPaint;

	static {
		try {
			InputStream boldInput = ToolGUI.class.getClassLoader()
					.getResourceAsStream(FONT_BOLD);
			InputStream regularInput = ToolGUI.class.getClassLoader()
					.getResourceAsStream(FONT_REGULAR);

			if (boldInput == null) {
				System.err.printf("Can not find font resource.\n");
				System.exit(1);
			}

			if (regularInput == null) {
				System.err.printf("Can not find font resource.\n");
				System.exit(1);
			}

			fontBold = Font.createFont(Font.TRUETYPE_FONT, boldInput);
			fontRegular = Font.createFont(Font.TRUETYPE_FONT, regularInput);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Color[] colors = { new Color(224, 230, 237), new Color(238, 238, 238) };
		float[] fractions = { 0.25f, 0.75f };
		backgroundPaint = new LinearGradientPaint(0, 0, 600, 500, fractions,
				colors);
	}

	public static Image getImage(String url, int width, int height,
			boolean keepAspect) {
		InputStream in = ToolGUI.class.getClassLoader()
				.getResourceAsStream(url);
		BufferedImage image;

		try {
			image = ImageIO.read(in);
		} catch (IOException e) {
			image = new BufferedImage(width, height,
					BufferedImage.TYPE_INT_ARGB);
		}

		boolean validWidth = (width <= 0 || width == image.getWidth());
		boolean validHeight = (height <= 0 || height == image.getHeight());

		if ((validWidth && !validHeight && !keepAspect)
				|| (validHeight && !validWidth && !keepAspect)
				|| (!validWidth && !validHeight)) {
			if (keepAspect) {
				double ratio = image.getWidth() / (double) image.getHeight();

				width = validWidth ? width : (int) (ratio * height);
				height = validWidth ? (int) (width / ratio) : height;
			}

			return image.getScaledInstance(width, height,
					BufferedImage.SCALE_SMOOTH);
		}

		return image;
	}

	public static Font getRegularFont(float size) {
		return fontRegular.deriveFont(size);
	}

	public static Font getBoldFont(float size) {
		return fontBold.deriveFont(size);
	}

	public static Paint getBackgroundPaint() {
		return backgroundPaint;
	}
}
