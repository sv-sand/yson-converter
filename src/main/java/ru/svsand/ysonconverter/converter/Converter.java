package ru.svsand.ysonconverter.converter;

import java.io.IOException;

/**
 * @author sand <sve.snd@gmail.com>
 * @since 07.06.2026
 */

public interface Converter {
	/**
	 * Converts the YSON file at the given input path to JSON and writes it to the output path.
	 *
	 * @throws IOException if reading the input or writing the output fails
	 */
	void convert() throws IOException;
}
