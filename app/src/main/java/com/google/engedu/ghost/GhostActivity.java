package com.google.engedu.ghost;

import android.app.Service;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


public class GhostActivity extends ActionBarActivity {
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String COMPUTER_WINS = "Computer challenges and wins";
    private static final String COMPUTER_WINS_ON_CHALLENGE = "You challenged and lost";
    private static final String USER_TURN = "Your turn";
    private static final String USER_WINS = "You Challenged and Won";
    private static int userScore  = 0;
    private static int compScore = 0;
    private GhostDictionary dictionary;
    private boolean userTurn = false;
    private Random random = new Random();
    Button challenge;
    boolean gameEnd;
    boolean rearFlag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);
        try {
            InputStream in = getAssets().open("words.txt");
            dictionary = new FastDictionary(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Button restartGame = (Button) findViewById(R.id.restart);
        restartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onStart(null);
            }
        });
        challenge = (Button) findViewById(R.id.challenge);
        challenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                challengeGame();
            }
        });

        Switch mySwitch = (Switch) findViewById(R.id.switch1);
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                    rearFlag = false;
                else
                    rearFlag = true;
            }
        });

        onStart(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void computerTurn() {
        TextView label = (TextView) findViewById(R.id.gameStatus);
        TextView text = (TextView) findViewById(R.id.ghostText);
        if(text.getText().length() > 3 && dictionary.isWord(text.getText().toString())) {
            Log.i("comp wins isword :" , text.getText().toString() );
            compScore++;
            scoreUpdate();
            label.setText(COMPUTER_WINS);
            return;
        }
        String prefix  =text.getText().toString();
        String word = dictionary.getAnyWordStartingWith(prefix);
        if(word.length()>0) {
            text.setText(text.getText() + "" + word.charAt(prefix.length()));
            userTurn = true;
            label.setText(USER_TURN);
        }else {
            Log.i("comp wins no prefix :" , prefix);
            compScore++;
            scoreUpdate();
            label.setText(COMPUTER_WINS);
        }
        // Do computer turn stuff then make it the user's turn again

    }

    /**
     * Handler for the "Reset" button.
     * Randomly determines whether the game starts with a user turn or a computer turn.
     * @param view
     * @return true
     */
    public boolean onStart(View view) {
        challenge.setEnabled(true);
        gameEnd = false;
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
        userTurn = random.nextBoolean();
//        userTurn = true;
        TextView text = (TextView) findViewById(R.id.ghostText);
        text.setText("");
        TextView label = (TextView) findViewById(R.id.gameStatus);
        if (userTurn) {
            label.setText(USER_TURN);
        } else {
            label.setText(COMPUTER_TURN);
            computerTurn();
        }

        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        char ch = (char) event.getUnicodeChar();
        if(Character.isLetter(ch) && !gameEnd ) {
            wordUpdate(ch);
            computerTurn();
        }
        return super.onKeyUp(keyCode, event);
    }
    public void wordUpdate(char ch){
        TextView tv = (TextView) findViewById(R.id.ghostText);
        if(rearFlag)
            tv.setText(tv.getText().toString()+ch);
        else
            tv.setText(ch + tv.getText().toString());
    }

    public void scoreUpdate(){
        challenge.setEnabled(false);
//        View view = this.getCurrentFocus();
//        if (view != null) {
//            InputMethodManager imm = (InputMethodManager)this.getSystemService(Service.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//        }
        gameEnd = true;
        TextView compScoreTextView = (TextView) findViewById(R.id.compScore);
        TextView userScoreTextView = (TextView) findViewById(R.id.userScore);
        compScoreTextView.setText("Computer Score: "+ compScore);
        userScoreTextView.setText("User Score: "+ userScore);
    }
    public void challengeGame(){
        TextView label = (TextView) findViewById(R.id.gameStatus);
        TextView text = (TextView) findViewById(R.id.ghostText);
        if(text.getText().length() > 3 && dictionary.isWord(text.getText().toString())) {
            userScore++;
            label.setText(USER_WINS);
            scoreUpdate();
            return;
        }
        String prefix = text.getText().toString();
        String word = dictionary.getAnyWordStartingWith(prefix);
        if(word.length()>0) {
            text.setText(word);
            compScore++;
            scoreUpdate();
            label.setText(COMPUTER_WINS_ON_CHALLENGE);
        }else {
            userScore++;
            scoreUpdate();
            label.setText(USER_WINS);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        TextView label = (TextView) findViewById(R.id.gameStatus);
        TextView text = (TextView) findViewById(R.id.ghostText);

        outState.putString("word", text.getText().toString());
        outState.putString("status", label.getText().toString());
        outState.putInt("cScore", compScore);
        outState.putInt("uScore", userScore);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        TextView label = (TextView) findViewById(R.id.gameStatus);
        TextView text = (TextView) findViewById(R.id.ghostText);

        text.setText(savedInstanceState.getString("word"));
        label.setText(savedInstanceState.getString("status"));
        savedInstanceState.putInt("cScore", compScore);
        savedInstanceState.putInt("uScore", userScore);
        scoreUpdate();
    }
}
