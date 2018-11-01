package com.omelchenkoaleks.addressbook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * Принимает фрагменты приложения и обрабатывает связь между ними.
 * Класс управляет фрагментами приложения и реализует их методы интерфейса обратного вызова
 * для реактции на выбор контакта, добавление нового, обновление или удаление существующего
 * контакта.
 */
public class MainActivity extends AppCompatActivity implements
        ContactsFragment.ContactsFragmentListener,
        DetailFragment.DetailFragmentListener,
        AddEditFragment.AddEditFragmentListener{

    // Ключ для сохранения Uri контакта в переданном объекте Bundle.
    public static final String CONTACT_URI = "contact_uri";

    // В эту переменную будем выводить список контактов.
    private ContactsFragment contactsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
