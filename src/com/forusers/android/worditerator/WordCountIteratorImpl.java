package com.forusers.android.worditerator;

public class WordCountIteratorImpl implements WordIterator {
    public WordCountIteratorImpl() {
        index = 0;
    }

    public boolean open(String fname) {
        index = 0;
        return true;
    }

    public void close() {
    }

    public void gotoIndex(int word) {
        index = word;
    }

    public int getWordIndex() {
        return index;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public String next() {
        index++;
        return String.format("Word%03d", index);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    private int index;
}
