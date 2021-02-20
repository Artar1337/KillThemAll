package ru.sibsutis.killthemall;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Matrix;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import static ru.sibsutis.killthemall.GameView.getRandomNumber;


public class Bug extends Thread {


    private GameView gameView;
    final Bitmap bmp;
    private int xSpeed = 5;
    private int ySpeed = 5;
    final private int destroyBorder = 200;

    float x;
    boolean canBeDestroyed = false;
    float y;
    int score;

    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth()
                , source.getHeight(), matrix, true);
    }

    private void randomizeSpeedAndSpawn(Bitmap bmp) {
        x = -190;
        y = -190;
        xSpeed = getRandomNumber(-7, 7);
        ySpeed = getRandomNumber(-7, 7);

        if (xSpeed < 0) {
            x = -x;
            x += gameView.getWidth();
            //xSpeed-=3;
        } else if (xSpeed == 0) {
            x = (gameView.getWidth() - bmp.getWidth()) / 2 + getRandomNumber(-200, 200);
        }
        /*else
        {
            xSpeed+=3;
        }*/

        if (ySpeed < 0) {
            y = -y;
            y += gameView.getHeight();
            //ySpeed-=3;
        } else if (ySpeed == 0) {
            y = (gameView.getHeight() - bmp.getWidth()) / 2 + getRandomNumber(-300, 300);
            ;
        }
        /*else
        {
            ySpeed+=3;
        }*/

        if (xSpeed == 0 && ySpeed == 0) {
            xSpeed = 3;
            ySpeed = 3;
            x = -190;
            y = -190;
        }
    }

    public double polarAngle2(double x, double y) {
        double degree = Math.atan2(y, x);
        if (degree < 0) degree += 2 * Math.PI;
        degree = Math.toDegrees(degree);
        return degree;
    }

    public Bug(GameView gameView, Bitmap bmp, int score) {
        this.gameView = gameView;
        randomizeSpeedAndSpawn(bmp);
        canBeDestroyed = false;
        float degree = (float) polarAngle2(xSpeed, ySpeed);
        this.bmp = rotateBitmap(bmp, degree);
        this.score = score;
        this.start();
    }

    @Override
    public void run() {
        while (!canBeDestroyed) {
            x = x + xSpeed;
            y = y + ySpeed;

            if (x > gameView.getWidth() + destroyBorder) {
                xSpeed = 0;
                ySpeed = 0;
                canBeDestroyed = true;
            }
            if (x + bmp.getWidth() < -destroyBorder) {
                xSpeed = 0;
                ySpeed = 0;
                canBeDestroyed = true;
            }
            if (y > gameView.getHeight() + destroyBorder) {
                xSpeed = 0;
                ySpeed = 0;
                canBeDestroyed = true;
            }
            if (y + bmp.getHeight() < -destroyBorder) {
                xSpeed = 0;
                ySpeed = 0;
                canBeDestroyed = true;
            }

            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                System.out.println("Interrupt");
            }
        }
    }

    public void onDraw(Canvas canvas) {
        //update();
        canvas.drawBitmap(bmp, x, y, null);
    }


}