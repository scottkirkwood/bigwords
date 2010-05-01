package com.forusers.android.worditerator;

public interface WordIterator {
	boolean Open(String fname);
	void Close();
	String nextWord();
	void GotoIndex(int word);
	int GetWordIndex();
}
