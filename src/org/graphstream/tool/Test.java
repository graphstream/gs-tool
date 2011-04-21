package org.graphstream.tool;

import java.util.Locale;

public class Test {
	public static void main( String ... args) {
		for(Locale l: Locale.getAvailableLocales()) {
			System.out.printf("- %s : \"%s;%s;%s\"\n", l, l.getLanguage(), l.getCountry(), l.getVariant());
		}
	}
}
