package ru.sibsutis.killthemall;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static ru.sibsutis.killthemall.GameView.getRandomNumber;

public class MainActivity extends Activity implements OnTouchListener {

    private Bitmap bmp;
    public static List<Bug> bugs = new ArrayList<Bug>(0);
    private GameView gameView;
    private int userScore = 0;
    private float lastTime = 30.0f;
    private SoundPool sounds;
    private int sHit, sMiss, sGameOver;
    boolean touched = false;

    String sDown;

    private void statsChange() {
        gameView.scoreView.setText("Time left: " + lastTime + " s.\nScore: " + userScore);
    }

    private boolean BugClicked(Bug bug, float xc, float yc) {
        float x = bug.x;
        float y = bug.y;
        int h = bug.bmp.getHeight();
        int w = bug.bmp.getWidth();

        if (xc >= x && xc <= x + w && yc >= y && yc <= y + h) {
            bug.canBeDestroyed = true;
            return true;
        }

        return false;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        sounds = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        sHit = sounds.load(this, R.raw.kill, 1);
        sMiss = sounds.load(this, R.raw.miss, 1);
        sGameOver = sounds.load(this, R.raw.gameover, 1);

        gameView = new GameView(this);
        gameView.setOnTouchListener(this);
        userScore = 0;
        setContentView(gameView);
        new CountDownTimer(30000, 300) {
            public void onTick(long msUntilFinished) {
                lastTime = msUntilFinished / 1000.0f;
                statsChange();
                int bugIndex = getRandomNumber(1, 10);
                int score;
                switch (bugIndex) {
                    case 1:
                        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.bug);
                        score = 300;
                        break;
                    case 2:
                    case 3:
                        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.cock);
                        score = 200;
                        break;
                    default:
                        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ant);
                        score = 100;
                        break;
                }
                Bug bug = new Bug(gameView, bmp, score);
                bugs.add(bug);
                /*if(bugs.add(bug)) {
                    Log.wtf("Bug add", Integer.toString(bugs.size()));
                }
                else
                {
                    Log.wtf("Bug???",Integer.toString(bugs.size()));
                }*/

            }

            @Override
            public void onFinish() {
                //игра окончена
                bugs.clear();
                lastTime = -1;
                gameView.scoreView.setText("Game Over! \nYour score: " + userScore);
                sounds.play(sGameOver, 1.0f, 1.0f, 0, 0, 1.5f);
                //Log.wtf("Bug clean",Integer.toString(bugs.size()));
            }
        }.start();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (lastTime <= 0)
            return false;
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // нажатие
                sDown = x + "," + y;
                boolean bugClicked = false;
                for (int i = 0; i < bugs.size(); i++) {
                    if (BugClicked(bugs.get(i), x, y)) {
                        bugs.get(i).x = -1000;
                        bugs.get(i).y = -1000;
                        bugClicked = true;
                        userScore += bugs.get(i).score;
                    }
                }
                if (!bugClicked) {
                    sounds.play(sMiss, 1.0f, 1.0f, 0, 0, 1.5f);
                    userScore -= 100;
                } else
                    sounds.play(sHit, 1.0f, 1.0f, 0, 0, 1.5f);
                statsChange();
                break;
            case MotionEvent.ACTION_MOVE: // движение
            case MotionEvent.ACTION_UP: // отпускание
            case MotionEvent.ACTION_CANCEL:
                break;
        }


        //Log.w("touched",sDown);
        return true;
    }
}