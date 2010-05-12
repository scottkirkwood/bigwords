package com.forusers.android.worditerator;

public class Word {
    private String word;
    private int delay;
    private static final int COMMA_PAUSE_MS = 200;
    private static final int PAUSE_PER_EXTRA_CH_MS = 10;
    private static final int BIG_WORD_SIZE = 8;
    private static final String COMMAS = ",.;:?!";

    public Word() {
    }

    public Word setWord(String curWord) {
        word = curWord;
        delay = 0;
        if (word == null || word.length() == 0) {
            return this;
        }
        char lastChar = word.charAt(curWord.length() - 1);
        if (COMMAS.indexOf(lastChar) != -1) {
            delay += COMMA_PAUSE_MS;
        }
        if (word.length() > BIG_WORD_SIZE) {
            delay += (word.length() - BIG_WORD_SIZE) * PAUSE_PER_EXTRA_CH_MS;
        }
        return this;
    }

    public String getWord() {
        return word;
    }

    /**
     * How much extra time to give to this word?
     *
     * @return Returns the addtional time in milliseconds.
     */
    public int extraMsDelay() {
        return delay;
    }
}
