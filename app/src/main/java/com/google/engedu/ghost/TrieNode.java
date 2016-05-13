package com.google.engedu.ghost;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import hugo.weaving.DebugLog;


public class TrieNode {
    private HashMap<String, TrieNode> children;
    private boolean isWord;


    public TrieNode() {
        children = new HashMap<>();
        isWord = false;
    }

    public void add(String word) {
        HashMap<String, TrieNode> children = this.children;

        for (int i = 0; i < word.length() ; i++) {
            String c = String.valueOf(word.charAt(i));

            TrieNode t;
//            Log.i("character i now : ", c);
            if(children.containsKey(c)){
                //get already existing trinode
                t = children.get(c);
            }else{
//                create a new trinode for the character
                t = new TrieNode();
                children.put(c, t);
            }
//            Log.i("character put is: ", c);
            children = t.children;

            //find end of a word
            if(i==word.length()-1) {
//                Log.i("Word : ", word);
//                Log.i("WordLength : ", String.valueOf(i));
                t.isWord = true;
            }
        }
    }

    public boolean isWord(String word) {
        ArrayList<String> results = new ArrayList<>();
//        Log.i("before ", "calling");
//        error here
        findWords(this, "", results);
//        Log.i("after ", "calling");
        if(results.isEmpty())
            Log.i("asdfghjkl", "Work on that");
        for (int i =0;i< results.size();i++) {
            Log.i("Words got: ", results.get(i));
        }
        TrieNode t = searchNode(word);
        if(t!=null && t.isWord)
            return true;
        return false;
    }

    public String getAnyWordStartingWith(String prefix) {
        String word="";
        ArrayList<String> results = new ArrayList<>();
        if(prefix.isEmpty()){
            Random r = new Random();
            char c = (char)(new Random().nextInt(26) + 'a');
            word+=c;
//            findWords(this, "", results);
        }
        else {
//            Log.i("searching for : ", prefix);
            TrieNode t = this.searchNode(prefix);
            if (t != null) {
                // Start at the root node (level 0)...
//                Log.i("before ", "calling");
                findWords(t, prefix, results);
//                Log.i("after ", "calling");
            }
        }
        if(!results.isEmpty()) {
            int n = new Random().nextInt(results.size());
            word = results.get(n);
        }
        Log.i("Word returned :", word );
        return word;
    }

    public String getGoodWordStartingWith(String s) {
        return null;
    }

    public TrieNode searchNode(String word){
        HashMap<String, TrieNode> children = this.children;
        TrieNode t = null;
        for (int i = 0; i < word.length() ; i++) {
            String c = String.valueOf(word.charAt(i));
//            Log.i("current char", c);
            if(children.containsKey(c)){
//                Log.i("contains char", c);
                //get node
                t = children.get(c);
               //and its children map
                children = t.children;
            }else{
//                Log.i("returned: ", "null");
                return null;
            }
        }
//        Log.i("returned : ", String.valueOf(t));
        return t;
    }
        public void findWords(TrieNode t, String wordSoFar, ArrayList<String> results){
            if(t == null || results.size()>300) return;
            if(t.isWord) results.add(wordSoFar);
            for (String ch : t.children.keySet()){
                wordSoFar+=ch;
                findWords(t.children.get(ch), wordSoFar, results);
                wordSoFar = wordSoFar.substring(0, wordSoFar.length()-1);
            }
        }
}