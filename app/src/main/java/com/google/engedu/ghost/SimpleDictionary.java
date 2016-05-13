package com.google.engedu.ghost;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class SimpleDictionary implements GhostDictionary {
    private ArrayList<String> words;

    public SimpleDictionary(InputStream wordListStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(wordListStream));
        words = new ArrayList<>();
        String line = null;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            if (word.length() >= MIN_WORD_LENGTH) {
                words.add(line.trim());
                Log.i("word : ", word);
            }
        }
        Log.i("Word Size : ", String.valueOf(words.size()));
    }

    @Override
    public boolean isWord(String word) {
        return words.contains(word);
    }

    @Override
    public String getAnyWordStartingWith(String prefix) {

        String word;
        if(prefix.isEmpty()){
            int n = new Random().nextInt(words.size()-1);
            return words.get(n);
        }
        else
           word = binarySearch(prefix, 0, words.size()-1);
        return word;
    }

    @Override
    public String getGoodWordStartingWith(String prefix) {
        return null;
    }

    String binarySearch(String prefix, int beg, int end){
        while(beg<end){
            int mid = (beg+end)/2;
            if(words.get(mid).startsWith(prefix))
                return words.get(mid);
            else if(prefix.compareTo(words.get(mid))>0)
                beg = mid+1;
            else
                end = mid-1;
        }
        return "";
    }
}
