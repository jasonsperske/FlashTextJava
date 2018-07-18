import java.io.IOException;

import com.sperske.jason.flashtext.KeywordProcessor;
import com.sperske.jason.flashtext.KeywordProcessorFactory;

public class FlashText {
	public static void main(String ... args) throws IOException {
		KeywordProcessor processor = KeywordProcessorFactory.fromFlashTextFile("test\\keywords_format_one.txt");
		String sentance = "With product management techniques I like Python, but I use java_2e";
		System.out.println("Processing the sentance '"+sentance+"'");
		for (String keyword : processor.extractKeywords(sentance)) {
			System.out.println("   " + keyword);
		}
		System.out.println(processor.replace(sentance));
	}
}
