package com.forusers.android.worditerator;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import android.content.Context;
import android.util.Log;

public class ESTWordIteratorImpl implements WordIterator {
    private static final String TAG = "BigWords";
    private int index;
    private StringTokenizer tokenizer;
    private Word word = new Word();
    static String text;
    static final String DELIMITERS = ",.;:?!)\n%&/\n\t\r ";

    public ESTWordIteratorImpl() {
    }

    @Override
    public boolean open(Context context, int id) {
        InputStream inputStream = context.getResources().openRawResource(id);
        InputStreamReader reader = new InputStreamReader(inputStream);
        int avail;
        try {
            avail = inputStream.available();
            char buf[] = new char[avail];
            int read = reader.read(buf, 0, avail);
            if (read == -1) {
                text = new String(buf);
            } else {
                text = new String(buf, 0, read);
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        tokenizer = new StringTokenizer(text, DELIMITERS, true);
        index = 0;
        return true;
    }

    @Override
    public void close() {
    }

    @Override
    public void gotoIndex(int word) {
        index = word;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public boolean hasNext() {
        return tokenizer.hasMoreTokens();
    }

    @Override
    public Word next() {
        if (!hasNext()) {
            return null;
        }
        index++;
        
        StringBuffer buffer = new StringBuffer();
        try {
            while (true) {
                String word = tokenizer.nextToken();
                if (isDelim(word)) {
                    buffer.append(stripBlanks(word));
                    if (buffer.length() > 0) {
                        break;
                    }
                } else {
                    buffer.append(word);
                }
            }
        } catch (NoSuchElementException e) {
            // done!
        }
        Log.i(TAG, "word: '" + buffer.toString() + "'");
        return word.setWord(buffer.toString());
    }

    private boolean isDelim(String word) {
        if (word.length() == 0) {
            return false;
        }
        for (int i = 0; i < word.length(); ++i) {
            if (DELIMITERS.contains(word.substring(i, i + 1))) {
                return true;
            }
        }
        return false;
    }

    private String stripBlanks(String word) {
        return word.replaceAll("[\n\r\t ]", "");
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
