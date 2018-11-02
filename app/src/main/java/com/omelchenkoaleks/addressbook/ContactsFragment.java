package com.omelchenkoaleks.addressbook;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.omelchenkoaleks.addressbook.data.DatabaseDescription.Contact;

/**
 * Класс управляет списком RecyclerView и кнопкой FloatingActionButton для добавления контактов.
 * На телефоне это первый фрагмент, отображаемый MainActivity.
 * На планшете MainActivity всегда отображает этот фрагмент.
 * Вложенный интерфейс ContactsFragment определяет методы обратного вызова,
 * реализуемые MainActivity, чтобы активность могла реагировать на выбор или добавление контакта.
 */

// Реализуем интерфейс, чтобы этот фрагмент мог реагировать на вызовы методов LoaderManager,
// создать Loader и обработать результаты, возвращаемые AddressBookContentProvider.
public class ContactsFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    // Идентификатор Loader.
    private static final int СONTACTS_LOADER = 0;

    // Сообщает MainActivity о выборе контакта.
    private ContactsFragment listener;

    // Адаптер для recyclerView.
    private ContactsAdapter contactsAdapter;

    // Метод обратного вызова, реализуемый MainActivity.
    public interface ContactsFragmentListener {

        // Вызывается при выборе контакта.
        void onContactSelected(Uri contactUri);

        // Вызывается при нажатии кнопки добавления.
        void onAddContact();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);

        // У фрагмента есть команды меню.
        setHasOptionsMenu(true);

        // Заполнение GUI и получение ссылки на RecyclerView.
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        // recyclerView выводит элементы в вертикальном списке.
        recyclerView.setLayoutManager(
                new LinearLayoutManager(getActivity().getBaseContext()));

        // Создаем адаптер recyclerView и слушателя щелчков на элементах.
        contactsAdapter = new ContactsAdapter(new ContactsAdapter.ContactClickListener() {
            @Override
            public void onClick(Uri contactUri) {
                listener.onContactSelected(contactUri);
            }
        });


        // Назначаем адаптер.
        recyclerView.setAdapter(contactsAdapter);

        // Присоединяем ItemDecorator для вывода разделителей.
        recyclerView.addItemDecoration(new ItemDivider(getContext()));

        // Улучшает быстродействие, если размер макета RecyclerView не изменяется.
        recyclerView.setHasFixedSize(true);

        // Получение FloatingActionButton и настройка слушателя.
        FloatingActionButton addButton =
                (FloatingActionButton) view.findViewById(R.id.addButton);
        addButton.setOnClickListener(
                new View.OnClickListener() {
                    // Отображение AddEditFragment при касании FAB.
                    @Override
                    public void onClick(View view) {
                        listener.onAddContact();
                    }
                }
        );
    }

    // Присваивание ContactsFragment при присоединении фрагмента.
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (ContactsFragment) context;
    }

    // Удаление ContactsFragment при отсоединении фрагмента.
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    // Инициализация Loader при создании активности этого фрагмента.
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(CONTACTS_LOADER, null, this);
    }

    // Вызывается из MainActivity при обновлении базы данных другим фрагментом.
    public void updateContactList() {
        contactsAdapter.notifyDataSetChanged();
    }

    // Вызывается LoaderManager для создания Loader.
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        // Создание CursorLoader на основании аргумента id.
        // В этом фрагменте только один объект Loader, и команда switch не нужна.
        switch (id) {
            case CONTACTS_LOADER:
                return new CursorLoader(getActivity(),
                        Contact.CONTENT_URI, // Uri таблицы contacts
                        null, // все столбцы
                        null, // все записы
                        null, // без аргументов
                        Contact.COLUMN_NAME + " COLLATE NOCASE ASC"); // сортировка
            default:
                return null;
        }
    }

    // Вызывается LoaderManager при завершении загрузки.
    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        contactsAdapter.swapCursor(data);
    }

    // Вызывается LoaderManager при сбросе Loader.
    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        contactsAdapter.swapCursor(null);
    }
}
