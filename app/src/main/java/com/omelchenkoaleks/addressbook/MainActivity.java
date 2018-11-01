package com.omelchenkoaleks.addressbook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * Принимает фрагменты приложения и обрабатывает связь между ними.
 * Класс управляет фрагментами приложения и реализует их методы интерфейса обратного вызова
 * для реактции на выбор контакта, добавление нового, обновление или удаление существующего
 * контакта.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
