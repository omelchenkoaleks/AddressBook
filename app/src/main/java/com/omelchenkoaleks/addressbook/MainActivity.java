package com.omelchenkoaleks.addressbook;

import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

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

    // Метод заполняет графический интерфейс MainActivity.
    // В этом приложении при первой загрузке будет отображать ContactsFragment.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /**
         * Если макет содержит fragmentContainer, используется макет
         * для телефона. Отображаем ContactFragment.
         */
        if (savedInstanceState != null && findViewById(R.id.fragmentContainer) != null) {
            // Создаем объект ContactFragment
            contactsFragment = new ContactsFragment();

            // Объект FragmentTransaction используется для добавления ContactsFragment
            // в пользовательский интерфейс.
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragmentContainer, contactsFragment);
            // Выводим ContactFragment.
            transaction.commit();

        } else {
            contactsFragment = (ContactsFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.contactsFragment);
        }
    }

    // Отображение DetailFragment для выбранного контакта.
    @Override
    public void onContactSelected(Uri contactUri) {
        // Телефон.
        if (findViewById(R.id.fragmentContainer) != null) {
            displayContact(contactUri, R.id.fragmentContainer);
        } else {
            // Планшет.
            // Извлекаем с вершины стека возврата.
            getSupportFragmentManager().popBackStack();

            displayContact(contactUri, R.id.rightPaneContainer);
        }
    }

    // Отображение AddEditFragment для добавления нового контакта.
    @Override
    public void onAddContact() {
        if (findViewById(R.id.fragmentContainer) != null)
            // Телефон.
            displayAddEditFragment(R.id.fragmentContainer, null);
        else
            // Планшет.
            displayAddEditFragment(R.id.rightPaneContainer, null);
    }

    // Отображение информации о контакте.
    private void displayContact(Uri contactUri, int viewID) {

        DetailFragment detailFragment = new DetailFragment();

        // Передаем URI контакт в аргументе DetailFragment
        Bundle arguments = new Bundle();
        arguments.putParcelable(CONTACT_URI, contactUri);
        detailFragment.setArguments(arguments);

        // Используем FragmentTransaction для отображения
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(viewID, detailFragment);
        transaction.addToBackStack(null);
        // Отображение.
        transaction.commit();
    }

    // Отображение фрагмента для добавления или изменения контакта.
    private void displayAddEditFragment(int viewID, Uri contactUri) {

        AddEditFragment addEditFragment = new AddEditFragment();

        // При изменении передаем аргумент contactUri.
        if (contactUri != null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(CONTACT_URI, contactUri);
            addEditFragment.setArguments(arguments);
        }

        // Используем FragmentTransaction для отображения AddEditFragment.
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(viewID, addEditFragment);
        transaction.addToBackStack(null);
        // Отображаем.
        transaction.commit();
    }
}
