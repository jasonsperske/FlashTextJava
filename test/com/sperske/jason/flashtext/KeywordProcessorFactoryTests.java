package com.sperske.jason.flashtext;

import java.io.IOException;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class KeywordProcessorFactoryTests {
	@Test
	void shouldSupportFileFormatOne() throws IOException {
		KeywordProcessor processor = KeywordProcessorFactory.fromFlashTextFile("test/keywords_format_one.txt");
		Set<String> keywords = processor.extractKeywords("I know java_2e and product management techniques");

		assertTrue(keywords.size() == 2);
		assertTrue(keywords.contains("java"));
		assertTrue(keywords.contains("product management"));
	}

	@Test
	void shouldSupportFileFormatTwo() throws IOException {
		KeywordProcessor processor = KeywordProcessorFactory.fromFlashTextFile("test/keywords_format_two.txt");
		Set<String> keywords = processor.extractKeywords("I know java and product management");

		assertTrue(keywords.size() == 2);
		assertTrue(keywords.contains("java"));
		assertTrue(keywords.contains("product management"));
	}
}
