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

    TextView radianInfo;
    TextView degreeInfo;
    TextView distanceInfo;
    TextView coordXInfo;
    TextView coordYInfo;

    // Отображаемые и физические координаты рисунка
    int displayedX;
    int displayedY;
    int realX;
    int realY;

    // Отображаемые величины на экране
    int distance;
    int degree;
    float radian;

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

        // Найдем компоненты в XML разметке
        radianInfo = (TextView) findViewById(R.id.radianInfo);
        degreeInfo = (TextView) findViewById(R.id.degreeInfo);
        distanceInfo = (TextView) findViewById(R.id.distanceInfo);
        coordXInfo = (TextView) findViewById(R.id.coordXInfo);
        coordYInfo = (TextView) findViewById(R.id.coordYInfo);

        imageView = (ImageView) findViewById(R.id.move);
        constraintLayout = (ConstraintLayout) findViewById(R.id.downLayout);

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

        // Присваиваем всем величинам значение 0
        realX = 0;
        realY = 0;
        displayedX = 0;
        displayedY = 0;
        radian = 0;
        degree = 0;
        distance = 0;

        // Обновляем значения всех элементов textView
        radianInfo.setText(String.format("%.2f", +radian));
        degreeInfo.setText(String.valueOf(degree));
        distanceInfo.setText(String.valueOf(distance));
        coordXInfo.setText(String.valueOf(displayedX));
        coordYInfo.setText(String.valueOf(displayedY));
    }

    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        // Отправляем отображаемые координаты рисунка
        outState.putInt("displayedX", displayedX);
        outState.putInt("displayedY", displayedY);

        Log.d(TAG, "onSaveInstanceState");
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);

        // Если состояния восстанавливается, то это уже не первый запуск программы
        isFirstRun = false;

        // Получаем отображаемые значения рисунка
        displayedX = savedInstanceState.getInt("displayedX");
        displayedY = savedInstanceState.getInt("displayedY");

        Log.d(TAG, "displayed -> " + String.valueOf(displayedX) + " " + String.valueOf(displayedY));

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

                // Изменяем физические координаты рисунка
                imageView.setX(realX);
                imageView.setY(realY);

                // Обновляем на эране отображаемые координаты рисунка
                coordXInfo.setText(String.valueOf(displayedX));
                coordYInfo.setText(String.valueOf(displayedY));

                // Рассчитываем дистанцию с учетом того, что координаты могли измениться
                distance = (int)(Math.sqrt(Math.pow(displayedX-0,2) + Math.pow(displayedY-0,2)));
                // Отображаем на экране дистанцию
                distanceInfo.setText(String.valueOf(distance));

                // Рассчитываем градус учетом того, что координаты могли измениться
                degree = (int)(Math.atan2(displayedY-0, displayedX-0) / Math.PI * 180);
                // Устанавливаем диапазон от 0 до 360 градусов
                degree = (degree < 0) ? degree + 360 : degree;
                // Отображаем на экране угол между точками относительно оси Х
                degreeInfo.setText(String.valueOf(degree));

                // Переводим из градусов в радианы с учетом того, что градусы могли измениться
                radian = (float)(degree * Math.PI/180);
                // Уменьшаем количество цифр после запятой, а также выводим на экран
                radianInfo.setText(String.format("%.2f", +radian));

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
                coordXInfo.setText(String.valueOf(displayedX));
            }

            // Если координата Y находится в layout, то перемещаем рисунок
            if (realY + dy >= 0 && realY + dy <= layoutHeight - pictureHeight) {

                imageView.setY(realY + dy);

                displayedY = (int)(-(realY - layoutHeight/2 + pictureHeight/2));
                coordYInfo.setText(String.valueOf(displayedY));
            }

            x = event.getX();
            y = event.getY();

            // Рассчитываем дистанцию по формуле
            // В качестве координат берем начальную точку (0,0) и отображаемую координату рисунка
            distance = (int)(Math.sqrt(Math.pow(displayedX-0,2) + Math.pow(displayedY-0,2)));
            // Отображаем на экране дистанцию
            distanceInfo.setText(String.valueOf(distance));

            // Рассчитываем градус учетом того, что координаты могли измениться
            degree = (int)(Math.atan2(displayedY-0, displayedX-0) / Math.PI * 180);
            // Устанавливаем диапазон от 0 до 360 градусов
            degree = (degree < 0) ? degree + 360 : degree;
            // Отображаем на экране угол между точками относительно оси Х
            degreeInfo.setText(String.valueOf(degree));

            // Переводим из градусов в радианы
            radian = (float)(degree * Math.PI/180);
            // Уменьшаем количество цифр после запятой, а также выводим на экран
            radianInfo.setText(String.format("%.2f", +radian));
        }
        return super.onTouchEvent(event);
    }

}