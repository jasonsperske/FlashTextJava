package com.sperske.jason.flashtext;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Set;
import org.junit.jupiter.api.Test;

class KeywordReplacerTests {
	@Test
	void shouldFindKeywordAtTheEndOfTheSentence() {
		KeywordProcessor processor = new KeywordProcessor();
		processor.addKeyword("Python", "python");

		Set<String> keywords = processor.extractKeywords("I like python");
		assertTrue(keywords.size() == 1);
		assertTrue(keywords.contains("python"));
	}
	@Test
	void shouldSkipIncompleteKeywordAtTheEndOfTheSentence() {
		KeywordProcessor processor = new KeywordProcessor();
		processor.addKeyword("Pythonizer", "pythonizer");

		Set<String> keywords = processor.extractKeywords("I like python");
		assertTrue(keywords.size() == 0);
	}
	@Test
	void shouldFindKeywordAtTheBeginningOfTheSentence() {
		KeywordProcessor processor = new KeywordProcessor();
		processor.addKeyword("Python", "python");

		Set<String> keywords = processor.extractKeywords("python I like");
		assertTrue(keywords.size() == 1);
		assertTrue(keywords.contains("python"));
	}
	@Test
	void shouldFindKeywordBeforeTheEndOfTheSentence() {
		KeywordProcessor processor = new KeywordProcessor();
		processor.addKeyword("Python", "python");

		Set<String> keywords = processor.extractKeywords("I like python also");
		assertTrue(keywords.size() == 1);
		assertTrue(keywords.contains("python"));
	}
	@Test
	void shouldFindMultipleKeywordsInTheEndOfTheSentence() {
		KeywordProcessor processor = new KeywordProcessor();

		processor.addKeyword("Python", "python");
		processor.addKeyword("Java", "java");

		Set<String> keywords = processor.extractKeywords("I like python java");
		assertTrue(keywords.size() == 2);
		assertTrue(keywords.contains("python"));
		assertTrue(keywords.contains("java"));
	}
}
