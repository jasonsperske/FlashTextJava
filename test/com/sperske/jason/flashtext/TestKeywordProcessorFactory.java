package com.sperske.jason.flashtext;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.Set;

import org.junit.jupiter.api.Test;

class TestKeywordProcessorFactory {

	@Test
	void testFileFormatOne() throws IOException {
		KeywordProcessor processor = KeywordProcessorFactory.fromFlashTextFile("keywords_format_one.txt");
		Set<String> keywords = processor.extractKeywords("I know java_2e and product management techniques");
		
		assertTrue(keywords.size() == 2);
		assertTrue(keywords.contains("java"));
		assertTrue(keywords.contains("product management"));
	}

}
