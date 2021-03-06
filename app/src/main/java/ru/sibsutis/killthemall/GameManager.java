package ru.sibsutis.killthemall;

import android.graphics.Canvas;

public class GameManager extends Thread {
    static final long FPS = 60;

    private GameView view;

    //состояние потока
    private boolean running = false;

    public GameManager(GameView view) {
        this.view = view;
    }

    public void setRunning(boolean run) {
        running = run;
    }


    @Override
    public void run() {
        long ticksPS = 1000 / FPS;
        long startTime;
        long sleepTime;
        while (running) {
            Canvas c = null;
            startTime = System.currentTimeMillis();
            try {
                c = view.getHolder().lockCanvas();
                synchronized (view.getHolder()) {
                    view.onDraw(c);
                }
            } finally {
                if (c != null) {
                    view.getHolder().unlockCanvasAndPost(c);
                }
            }
            sleepTime = ticksPS - (System.currentTimeMillis() - startTime);
            try {
                if (sleepTime > 0)
                    sleep(sleepTime);
                else
                    sleep(10);
            } catch (Exception e) {
            }
        }
    }
}
