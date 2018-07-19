package com.sperske.jason.flashtext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class KeywordProcessorFactory {
	/*
	 * FlashText.py defines a text file format with two types of lines,
	 * format_one appears as a keyword, followed by '=>' and then a 'clean name'
	 * format_two is just the keyword.  This class implements these formats
	 * allowing you to reuse a single keyword file.
	 */
	public static KeywordProcessor fromFlashTextFile(String file) throws IOException {
		return fromFlashTextFile(file, false);
	}

	public static KeywordProcessor fromFlashTextFile(String file, boolean case_sensitive) throws IOException {
		try (Stream<String> stream = Files.lines(Paths.get(file))) {
			return fromFlashTextFile(stream, case_sensitive);
		} catch (IOException e) {
			throw e;
		}
	}

	public static KeywordProcessor fromFlashTextFile(Stream<String> stream) {
		return fromFlashTextFile(stream, false);
	}

	public static KeywordProcessor fromFlashTextFile(Stream<String> stream, boolean case_sensitive) {
		KeywordProcessor processor = new KeywordProcessor();
		stream.forEach(line -> {
			String[] data = line.split("=>");
			if (data.length == 2) {
				processor.addKeyword(data[0], data[1]);
			} else {
				processor.addKeyword(data[0]);
			}
		});
		return processor;
	}
}
