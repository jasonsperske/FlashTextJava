package com.sperske.jason.flashtext;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class KeywordTrieNode {
	private String keyword;
	private String clean_name;

	public Map<Character, KeywordTrieNode> children;
	
	public KeywordTrieNode() {
		// This is a transit node and will need a map to store values
		this.children = new HashMap<>();
	}
	public KeywordTrieNode(String keyword, String clean_name) {
		// This is a value node and will need to store keyword and clean_name
		this.keyword = keyword;
		this.clean_name = clean_name;
	}
	
	public boolean contains(Character c) {
		if (this.children != null) {
			return this.children.containsKey(c);			
		}
		return false;
	}

	public boolean contains(String word) {
		if (this.keyword != null) {
			return this.keyword.compareTo(word) == 0;			
		}
		return false;
	}
	
	public boolean isEmpty() {
		return this.children == null || this.children.isEmpty();
	}
	
	public KeywordTrieNode get(Character c) {
		if (this.children != null) {
			return this.children.get(c);
		} else {
			return null;
		}
	}
	
	public String get() {
		if (this.clean_name != null) {
			return this.clean_name;
		} else {
			return this.keyword;
		}
	}
	
	public KeywordTrieNode add(LinkedList<Character> characters, String word, String clean_name) {
		Character c = characters.poll();
		if (c == null) {
			this.keyword = word;
			this.clean_name = clean_name;
		} else {
			KeywordTrieNode node = get(c);
			if (node == null) {
				node = new KeywordTrieNode();
			}
			this.children.put(c, node.add(characters, word, clean_name));
		}
		return this;
	}
	
	@Override
	public String toString() {
		return toIndentedString("");
	}

	private String toIndentedString(String pad) {
		StringBuilder out = new StringBuilder();
		String name = get();
		if (name != null) {
			out.append(name);
		}
		out.append('\n');
		if (this.children != null) {
			for(Character c : this.children.keySet()) {
				out.append(pad).append(c).append(':').append(this.children.get(c).toIndentedString(pad + " "));
			}
		}
		
		return out.toString();
	}
}
