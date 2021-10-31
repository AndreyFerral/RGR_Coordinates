package com.example.rgr_coordinates;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    // Описание тэга для логов debug
    private static final String TAG = "myLogs";

    ImageView imageView;
    ConstraintLayout constraintLayout;

    int layoutWidth;
    int layoutHeight;

    float pictureWidth;
    float pictureHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.move);
        constraintLayout = (ConstraintLayout) findViewById(R.id.downLayout);

        // Ранее размеры получались при каждом нажатии на экран, но это было не оптимально
        // К тому же при добавлении кнопки "Сбросить" появился баг
        // Если пользователь после запуска приложения сразу нажимал на кнопку, то она работала некорректно

        // Получаем размер рисунка и layout, когда их значения определены (не равны 0)
        imageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                // Получаем размеры рисунка
                pictureWidth = imageView.getWidth();
                pictureHeight = imageView.getHeight();

                // Отправляем в LogCat размеры рисунка
                Log.d(TAG, "Рисунок Высота - " + String.valueOf(pictureHeight));
                Log.d(TAG, "Рисунок Ширина - " + String.valueOf(pictureWidth));

                // Получаем размеры layout. В методе onCreate значения по 0
                layoutWidth = constraintLayout.getWidth();
                layoutHeight = constraintLayout.getHeight();

                // Отправляем в LogCat размеры layout
                Log.d(TAG, "Layout Высота - " + String.valueOf(layoutHeight));
                Log.d(TAG, "Layout Ширина - " + String.valueOf(layoutWidth));

                // Отсоединяемся от OnGlobalLayoutListener
                imageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

    }

    public void resetMovement(View view) {
        imageView.setX(layoutWidth/2 - pictureWidth/2);
        imageView.setY(layoutHeight/2 - pictureHeight/2);
    }

    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        //outState.putInt("count", cnt);
        Log.d(TAG, "onSaveInstanceState");
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //cnt = savedInstanceState.getInt("count");
        Log.d(TAG, "onRestoreInstanceState");
    }

    float x, y;
    float dx, dy;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // При нажатии на экран
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            x = event.getX();
            y = event.getY();

            // Отправляем в LogCat текущие координаты рисунка
            Log.d(TAG, "Рисунок Координата Y - " + String.valueOf(imageView.getY()));
            Log.d(TAG, "Рисунок Координата X - " + String.valueOf(imageView.getX()));
        }

        // При движению по экрану
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            dx = event.getX() - x;
            dy = event.getY() - y;

            // Если координата X находится в layout, то перемещаем рисунок
            if (imageView.getX() + dx >= 0 &&
                    imageView.getX() + dx <= layoutWidth - pictureWidth) {
                imageView.setX(imageView.getX() + dx);
            }

            // Если координата Y находится в layout, то перемещаем рисунок
            if (imageView.getY() + dy >= 0 &&
                    imageView.getY() + dy <= layoutHeight - pictureHeight) {
                imageView.setY(imageView.getY() + dy);
            }

            x = event.getX();
            y = event.getY();
        }
        return super.onTouchEvent(event);
    }

}