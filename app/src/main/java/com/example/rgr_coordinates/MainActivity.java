package com.example.rgr_coordinates;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // Описание тэга для логов debug
    private static final String TAG = "myLogs";

    ImageView imageView;
    ConstraintLayout constraintLayout;

    TextView radian;
    TextView degree;
    TextView distance;
    TextView coordX;
    TextView coordY;

    // Отображаемые и физические координаты рисунка
    int displayedX;
    int displayedY;
    int realX;
    int realY;

    // Необходима для проверки - первый ли запуска программы
    boolean isFirstRun = true;

    // Ширина и высота layout, по которому можно передвигать рисунок
    int layoutWidth;
    int layoutHeight;

    // Ширина и высота рисунка
    float pictureWidth;
    float pictureHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        radian = (TextView) findViewById(R.id.radianInfo);
        degree = (TextView) findViewById(R.id.degreeInfo);
        distance = (TextView) findViewById(R.id.distanceInfo);
        coordX = (TextView) findViewById(R.id.coordXInfo);
        coordY = (TextView) findViewById(R.id.coordYInfo);

        imageView = (ImageView) findViewById(R.id.move);
        constraintLayout = (ConstraintLayout) findViewById(R.id.downLayout);

        // Ранее размеры получались при каждом нажатии на экран, но это было не оптимально

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

                // Если это первый запуск, то помещаем рисунок в середину экрана
                if (isFirstRun) {
                    resetMovement(null);
                }

                Log.d(TAG, "Слушатель в onCreate");

                // Отсоединяемся от OnGlobalLayoutListener
                imageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        Log.d(TAG, "onCreate");

    }

    public void resetMovement(View view) {

        // Устанавливаем рисунок на середину экрана
        imageView.setX(layoutWidth / 2 - pictureWidth / 2);
        imageView.setY(layoutHeight / 2 - pictureHeight / 2);

        // Присваиваем отображаемым координатам значение 0
        displayedX = 0;
        displayedY = 0;

        // Обнуляем значения всех элементов textView
        radian.setText(String.valueOf(0));
        degree.setText(String.valueOf(0));
        distance.setText(String.valueOf(0));
        coordX.setText(String.valueOf(displayedX));
        coordY.setText(String.valueOf(displayedY));
    }

    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        // Отправляем отображаемые значения рисунка
        outState.putInt("displayedX", displayedX);
        outState.putInt("displayedY", displayedY);

        Log.d(TAG, "Отправляемое displayed -> " + String.valueOf(displayedX) + " " + String.valueOf(displayedY));

        Log.d(TAG, "onSaveInstanceState");
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);

        // Если состояния восстанавливается, то это уже не первый запуск программы
        isFirstRun = false;

        // Получаем отображаемые значения рисунка
        displayedX = savedInstanceState.getInt("displayedX");
        displayedY = savedInstanceState.getInt("displayedY");

        Log.d(TAG, "Получаемое displayed -> " + String.valueOf(displayedX) + " " + String.valueOf(displayedY));

        // Устанавливаем нулевые значения
        radian.setText(String.valueOf(0));
        degree.setText(String.valueOf(0));
        distance.setText(String.valueOf(0));
        //

        // Вызывается после того, как вызовется аналогичный слушатель в onCreate
        imageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                // Преобразуем отображаемые координаты рисунка в физические
                realX = (int)(displayedX + layoutWidth/2 - pictureWidth/2);
                realY = (int)(-displayedY + layoutHeight/2 - pictureHeight/2);

                // Поскольку высота и ширина layout разная при портретной и альбомной орентации,
                // то необходимо проверять координаты - входят ли они в layout при другой ориентации

                // Если координата X находится меньше нуля, то присваиваем ей нуль
                if (realX < 0) {

                    Log.d(TAG, "Изменение Х -> " + String.valueOf(realX) + " = " + String.valueOf(0));
                    realX = 0;

                    Log.d(TAG, "Изменение displayedX -> " + String.valueOf(displayedX) + " = " + String.valueOf(realX - layoutWidth/2 + pictureWidth/2));
                    displayedX = (int)(realX - layoutWidth/2 + pictureWidth/2);
                }

                // Если координата X больше максимального, то присваиваем максимальное значение
                if (realX >= layoutWidth - pictureWidth) {

                    Log.d(TAG, "Изменение Х -> " + String.valueOf(realX) + " = " + String.valueOf(layoutWidth - pictureWidth));
                    realX = (int)(layoutWidth - pictureWidth);

                    Log.d(TAG, "Изменение displayedX -> " + String.valueOf(displayedX) + " = " + String.valueOf(realX - layoutWidth/2 + pictureWidth/2));
                    displayedX = (int)(realX - layoutWidth/2 + pictureWidth/2);
                }

                // Если координата Y находится меньше нуля, то присваиваем ей нуль
                if (realY < 0) {

                    Log.d(TAG, "Изменение Y -> " + String.valueOf(realY) + " = " + String.valueOf(0));
                    realY = 0;

                    Log.d(TAG, "Изменение displayedY -> " + String.valueOf(displayedY) + " = " + String.valueOf(-(realY - layoutHeight/2 + pictureHeight/2)));
                    displayedY = (int)-(realY - layoutHeight/2 + pictureHeight/2);
                }

                // Если координата Y больше максимального, то присваиваем максимальное значение
                if (realY >= layoutHeight - pictureHeight) {

                    Log.d(TAG, "Изменение Y -> " + String.valueOf(realY) + " = " + String.valueOf(layoutHeight - pictureHeight));
                    realY = (int)(layoutHeight - pictureHeight);

                    Log.d(TAG, "Изменение displayedY -> " + String.valueOf(displayedY) + " = " + String.valueOf(-(realY - layoutHeight/2 + pictureHeight/2)));
                    displayedY = (int)-(realY - layoutHeight/2 + pictureHeight/2);
                }

                // Изменяем отображаемые координаты рисунка
                coordX.setText(String.valueOf(displayedX));
                coordY.setText(String.valueOf(displayedY));

                // Изменяем физические координаты рисунка
                imageView.setX(realX);
                imageView.setY(realY);

                Log.d(TAG, "Слушатель в onRestoreInstanceState");

                // Отсоединяемся от OnGlobalLayoutListener
                imageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        Log.d(TAG, "onRestoreInstanceState");
    }

    float x, y;
    float dx, dy;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // Получаем физические координаты рисунка
        realX = (int)imageView.getX();
        realY = (int)imageView.getY();

        // При нажатии на экран
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            x = event.getX();
            y = event.getY();

            // Отправляем в LogCat текущие координаты рисунка
            Log.d(TAG, "Рисунок Координата Y - " + String.valueOf(realY));
            Log.d(TAG, "Рисунок Координата X - " + String.valueOf(realX));
        }

        // При движению по экрану
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            dx = event.getX() - x;
            dy = event.getY() - y;

            // Если координата X находится в layout, то перемещаем рисунок
            if (realX + dx >= 0 && realX + dx <= layoutWidth - pictureWidth) {

                imageView.setX(realX + dx);

                displayedX = (int)(realX - layoutWidth/2 + pictureWidth/2);
                coordX.setText(String.valueOf(displayedX));
            }

            // Если координата Y находится в layout, то перемещаем рисунок
            if (realY + dy >= 0 && realY + dy <= layoutHeight - pictureHeight) {

                imageView.setY(realY + dy);

                displayedY = (int)(-(realY - layoutHeight/2 + pictureHeight/2));
                coordY.setText(String.valueOf(displayedY));
            }

            x = event.getX();
            y = event.getY();
        }
        return super.onTouchEvent(event);
    }

}