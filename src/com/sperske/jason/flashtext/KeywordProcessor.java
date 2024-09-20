package com.sperske.jason.flashtext;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
 * FlashTextJava - An idiomatic Java port of the Python library FlashText by Vikash Singh
 * Original Python source can be found at https://github.com/vi3k6i5/flashtext
 * Based on the Aho-Corasick algorithm (https://en.wikipedia.org/wiki/Aho%E2%80%93Corasick_algorithm)
 * Java Version written by Jason Sperske
 */
public class KeywordProcessor {
	// immutable properties once KeywordProcessor is instantiated
	private final boolean CASE_SENSITIVE;

	// dynamic properties while KeywordProcessor is being built up
	private int _terms_in_trie = 0;
	private KeywordTrieNode rootNode;

	public KeywordProcessor() {
		this(false);
	}

	public KeywordProcessor(boolean case_sensitive) {
		this.CASE_SENSITIVE = case_sensitive;
		this.rootNode = new KeywordTrieNode();
	}

	public int length() {
		return this._terms_in_trie;
	}

	public boolean contains(String word) {
		KeywordTrieNode current_keyword_trie_node = this.rootNode;
		int chars_traveled = 0;

		if (!this.CASE_SENSITIVE) {
			word = word.toLowerCase();
		}
		for (Character c : word.toCharArray()) {
			if (current_keyword_trie_node.contains(c)) {
				current_keyword_trie_node = current_keyword_trie_node.children.get(c);
				chars_traveled += 1;
			} else {
				return false;
			}
		}

		return chars_traveled == word.length() && current_keyword_trie_node.contains(word);
	}

	public String get(String word) {
		KeywordTrieNode current_keyword_trie_node = this.rootNode;
		int chars_traveled = 0;

		if (!this.CASE_SENSITIVE) {
			word = word.toLowerCase();
		}
		for(Character c : word.toCharArray()) {
			if (current_keyword_trie_node.contains(c)) {
				current_keyword_trie_node = current_keyword_trie_node.children.get(c);
				chars_traveled += 1;
			} else {
				return null;
			}
		}

		if (chars_traveled == word.length()) {
			return current_keyword_trie_node.get();
		} else {
			return null;
		}
	}

	public void addKeyword(String word) {
		// Clean Name is set to word when not defined
		addKeyword(word, word);
	}

	public void addKeyword(String word, String clean_name) {
		if (!this.CASE_SENSITIVE) {
			word = word.toLowerCase();
		}
		LinkedList<Character> characters = word.chars().mapToObj(c -> (char)c).collect(Collectors.toCollection(LinkedList::new));

		this.rootNode.add(characters, word, clean_name);
		this._terms_in_trie += 1;
	}

	public Set<String> extractKeywords(String sentance) {
		return extractKeywords(sentance.chars().mapToObj(c -> (char) c));
	}
	public Set<String> extractKeywords(Stream<Character> chars) {
		return chars.collect(new Extractor(this.rootNode, this.CASE_SENSITIVE));
	}

	class Extractor implements Collector<Character, Set<String>, Set<String>> {
		private KeywordTrieNode currentNode;
		private final KeywordTrieNode rootNode;
		private final boolean CASE_SENSITIVE;
		private Set<String> keywords;

		public Extractor(KeywordTrieNode rootNode, boolean caseSensitive) {
			this.rootNode = rootNode;
			this.currentNode = rootNode;
			this.CASE_SENSITIVE = caseSensitive;
			this.keywords = new HashSet<>();
		}

		@Override
		public BiConsumer<Set<String>, Character> accumulator() {
			return (keywords, c) -> {
				if (!this.CASE_SENSITIVE) {
					c = Character.toLowerCase(c);
				}
				KeywordTrieNode node = currentNode.get(c);
				if (node == null) {
					currentNode = this.rootNode;
				} else {
					currentNode = node;
					String keyword = currentNode.get();
					if (keyword != null) {
						keywords.add(keyword);
					}
				}
			};
		}

		@Override
		public Set<Characteristics> characteristics() {
			return Collections.emptySet();
		}

		@Override
		public BinaryOperator<Set<String>> combiner() {
			return (a, b) -> a;
		}

		@Override
		public Function<Set<String>, Set<String>> finisher() {
			return (keywords) -> keywords;
		}

		@Override
		public Supplier<Set<String>> supplier() {
			return () -> this.keywords;
		}

	}

	public String replace(String sentance) {
		return replace(sentance.chars().mapToObj(c -> (char) c));
	}
	private String replace(Stream<Character> chars) {
		return chars.collect(new Replacer(this.rootNode, this.CASE_SENSITIVE));
	}

	// Design adapted from https://codereview.stackexchange.com/a/199677/9162
	class Replacer implements Collector<Character, StringBuffer, String> {
		private StringBuffer out;
		private StringBuffer buffer;
		private KeywordTrieNode currentNode;
		private final KeywordTrieNode rootNode;
		private final boolean CASE_SENSITIVE;

		public Replacer(KeywordTrieNode rootNode, boolean caseSensitive) {
			this.rootNode = rootNode;
			this.currentNode = rootNode;
			this.out = new StringBuffer();
			this.buffer = new StringBuffer();
			this.CASE_SENSITIVE = caseSensitive;
		}

		@Override
		public BiConsumer<StringBuffer, Character> accumulator() {
			return (out, c) -> {
				char match_c = c;
				if (!this.CASE_SENSITIVE) {
					match_c = Character.toLowerCase(c);
				}
				KeywordTrieNode node = currentNode.get(match_c);
				if (node != null) {
					buffer.append(c);
					currentNode = node;
					return;
				}

				String keyword = currentNode.get();
				out.append(keyword != null ? keyword : buffer);
				buffer = new StringBuffer();
				currentNode = this.rootNode;

				// re-match root node
				node = this.rootNode.get(match_c);
				if (node != null) {
					buffer.append(c);
					currentNode = node;
				} else {
					out.append(c);
				}
			};
		}

		@Override
		public Set<Characteristics> characteristics() {
			return Collections.emptySet();
		}

		@Override
		public BinaryOperator<StringBuffer> combiner() {
			return (a, b) -> a;
		}

		@Override
		public Function<StringBuffer, String> finisher() {
			return (out) -> {
				String keyword = currentNode.get();
				if (keyword == null) {
					out.append(buffer);
				} else {
					out.append(keyword);
				}
				return out.toString();
			};
		}

		@Override
		public Supplier<StringBuffer> supplier() {
			return () -> this.out;
		}
	}

	public String toString() {
		return this.rootNode.toString();
	}
}
