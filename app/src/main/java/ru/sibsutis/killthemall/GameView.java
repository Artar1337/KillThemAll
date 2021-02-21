package ru.sibsutis.killthemall;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import static ru.sibsutis.killthemall.MainActivity.bugs;

import java.util.Random;

public class GameView extends SurfaceView {

    private Bitmap bmp;

    /*Поле рисования*/
    private SurfaceHolder holder;

    TextView scoreView;

    Paint scorePaint;

    private GameManager gameLoopThread;

    public static int getRandomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

    public GameView(Context context) {
        super(context);
        gameLoopThread = new GameManager(this);
        holder = getHolder();

        scoreView = new TextView(context);
        scoreView.setText("Time left: 30.0 s\nScore: 0");
        //scoreView.setText("Tap on screen to begin!");
        scoreView.setTextColor(Color.GREEN);

        scorePaint = new Paint();
        scorePaint.setColor(Color.GREEN);
        scorePaint.setTextSize(50);

        /*Рисуем все наши объекты и все все все*/
        holder.addCallback(new SurfaceHolder.Callback() {
            /* Уничтожение области рисования */
            public void surfaceDestroyed(SurfaceHolder holder) {
                boolean retry = true;
                gameLoopThread.setRunning(false);
                while (retry) {
                    try {
                        gameLoopThread.join();
                        retry = false;
                    } catch (InterruptedException e) {
                    }
                }
            }

            /* Создание области рисования */
            public void surfaceCreated(SurfaceHolder holder) {
                gameLoopThread.setRunning(true);
                gameLoopThread.start();
            }

            /* Изменение области рисования */
            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {
            }
        });

    }

    /*Функция рисующая все спрайты и фон*/
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);

        //цикл по изменению координат уже идет в потоках
        //оставлю для дебага
        /*for(int i=0;i<n;i++)
        {
            Bug sprite = bugs.get(i);
            if(sprite!=null)
                sprite.update();
        }*/

        for (int i = 0; i < bugs.size(); i++) {
            Bug sprite = bugs.get(i);
            if (sprite != null)
                sprite.onDraw(canvas);
        }
        //проверяем, пора ли удалить жучка
        //(не в потоке, для минимизации ошибок)
        for (int i = 0; i < bugs.size(); ) {
            Bug sprite = bugs.get(i);
            if (sprite != null) {
                if (sprite.canBeDestroyed)
                    bugs.remove(i);
                else
                    i++;
            } else
                i++;
        }
        String lines[] = scoreView.getText().toString().split("\\r?\\n");
        canvas.drawText(lines[0],
                100, 140, scorePaint);
        canvas.drawText(lines[1],
                100, 190, scorePaint);

    }
}
