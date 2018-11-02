package com.omelchenkoaleks.addressbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.omelchenkoaleks.addressbook.data.DatabaseDescription.Contact;

/**
 * Предоставляет графический интерфейс для добавления нового
 * или редактирования существующего фрагмента.
 * Класс управляет компонентами TextInputLayout и кнопкой FloatingActionButton для добавления
 * нового или редактирования существующего контакта.
 * Вложенный интерфейс AddEditFragment определяет метод обратного вызова,
 * реализуемый MainActivity, чтобы активность могла реагировать на сохранение
 * нового или обновленного контакта.
 */
public class AddEditFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    // Определяет метод обратного вызова, реализованный MainActivity.
    public interface AddEditFragmentListener {
        // Вызывается при сохранении контакта.
        void onAddEditCompleted(Uri contactUri);
    }

    // Константа для идентификации Loader.
    private static final int CONTACT_LOADER = 0;

    // MainActivity.
    private AddEditFragmentListener listener;

    // Uri выбранного контакта.
    private Uri contactUri;

    // Добавление (true) или изменение.
    private boolean addingNewContact = true;

    // Компоненты EditText для информации контакта.
    private TextInputLayout nameTextInputLayout;
    private TextInputLayout phoneTextInputLayout;
    private TextInputLayout emailTextInputLayout;
    private TextInputLayout streetTextInputLayout;
    private TextInputLayout cityTextInputLayout;
    private TextInputLayout stateTextInputLayout;
    private TextInputLayout zipTextInputLayout;
    private FloatingActionButton saveContactFAB;

    // Для SnackBar.
    private CoordinatorLayout coordinatorLayout;

    // Назначение AddEditFragmentListener при присоединении фрагмента.
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        listener = (AddEditFragmentListener) context;
    }

    // Удаление AddEditFragmentListener при отсоединении фрагмента.
    @Override
    public void onDetach() {
        super.onDetach();

        listener = null;
    }

    // Вызывается при создании представлений фрагмента.
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);

        // У фрагмента есть команды меню
        setHasOptionsMenu(true);

        // Заполнение GUI и получение ссылок на компоненты EditText.
        View view =
                inflater.inflate(R.layout.fragment_add_edit, container, false);
        nameTextInputLayout = view.findViewById(R.id.nameTextInputLayout);
        nameTextInputLayout.getEditText().addTextChangedListener(nameChangedListener);
        phoneTextInputLayout = view.findViewById(R.id.phoneTextInputLayout);
        emailTextInputLayout = view.findViewById(R.id.emailTextInputLayout);
        streetTextInputLayout = view.findViewById(R.id.streetTextInputLayout);
        cityTextInputLayout = view.findViewById(R.id.cityTextInputLayout);
        stateTextInputLayout = view.findViewById(R.id.stateTextInputLayout);
        zipTextInputLayout = view.findViewById(R.id.zipTextInputLayout);

        // Назначение слушателя событий FloatingActionButton.
        saveContactFAB = view.findViewById(R.id.saveFloatingActionButton);
        saveContactFAB.setOnClickListener(saveContactButtonClicked);

        updateSaveButtonFAB();

        // Используется для отображения SnackBar с короткими сообщениями.
        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);

        // Используется для отображения SnackBar с короткими сообщениями.
        Bundle arguments = getArguments();

        if (arguments != null) {
            addingNewContact = false;
            contactUri = arguments.getParcelable(MainActivity.CONTACT_URI);
        }

        // При изменении существующего контакта создать Loader.
        if (contactUri != null)
            getLoaderManager().initLoader(CONTACT_LOADER, null, this);

        return view;
    }

    // Обнаруживает изменения в тексте поля EditTex, связанного c
    // nameTextInputLayout, для отображения или скрытия saveButtonFAB.
    private final TextWatcher nameChangedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        // Вызывается при изменении текста в nameTextInputLayout.
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            updateSaveButtonFAB();
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    // Кнопка saveButtonFAB видна, если имя не пусто.
    private void updateSaveButtonFAB() {

        String input = nameTextInputLayout.getEditText().getText().toString();

        // Если для контакта указано имя, показать FloatingActionButton.
        if (input.trim().length() != 0)
            saveContactFAB.show();
        else
            saveContactFAB.hide();
    }

    // Реагирует на событие, генерируемое при сохранении контакта.
    private final View.OnClickListener saveContactButtonClicked =
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //
                    ((InputMethodManager) getActivity().getSystemService(
                            Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                            getView().getWindowToken(), 0);
                    saveContact(); // Сохранение контакта в базе данных.
                }
            };

    // Сохранение информации контакта в базе данных.
    private void saveContact() {

        // Создание объекта ContentValues с парами "ключ—значение".
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contact.COLUMN_NAME,
                nameTextInputLayout.getEditText().getText().toString());
        contentValues.put(Contact.COLUMN_PHONE,
                phoneTextInputLayout.getEditText().getText().toString());
        contentValues.put(Contact.COLUMN_EMAIL,
                emailTextInputLayout.getEditText().getText().toString());
        contentValues.put(Contact.COLUMN_STREET,
                streetTextInputLayout.getEditText().getText().toString());
        contentValues.put(Contact.COLUMN_CITY,
                cityTextInputLayout.getEditText().getText().toString());
        contentValues.put(Contact.COLUMN_STATE,
                stateTextInputLayout.getEditText().getText().toString());
        contentValues.put(Contact.COLUMN_ZIP,
                zipTextInputLayout.getEditText().getText().toString());

        if (addingNewContact) {
            // Использовать объект ContentResolver активности для вызова
            // insert для объекта AddressBookContentProvider.
            Uri newContactUri = getActivity().getContentResolver().insert(
                    Contact.CONTENT_URI, contentValues);

            if (newContactUri != null) {
                Snackbar.make(coordinatorLayout,
                        R.string.contact_added,
                        Snackbar.LENGTH_LONG).show();
                listener.onAddEditCompleted(newContactUri);
            } else {
                Snackbar.make(coordinatorLayout,
                        R.string.contact_not_added,
                        Snackbar.LENGTH_LONG).show();
            }
        } else {
            // Использовать объект ContentResolver активности для вызова
            // update для объекта AddressBookContentProvider.
            int updatedRows = getActivity().getContentResolver().update(
                    contactUri, contentValues, null, null);

            if (updatedRows > 0) {
                listener.onAddEditCompleted(contactUri);
                Snackbar.make(coordinatorLayout,
                        R.string.contact_updated, Snackbar.LENGTH_LONG).show();
            }
            else {
                Snackbar.make(coordinatorLayout,
                        R.string.contact_not_updated, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    // Вызывается LoaderManager для создания Loader

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle args) {
        // Создание CursorLoader на основании аргумента id.
        switch (id) {
            case CONTACT_LOADER:
                return new CursorLoader(getActivity(),
                        contactUri, // Uri отображаемого контакта.
                        null, // Все столбцы.
                        null, // Все столбцы.
                        null, // Без аргументов.
                        null); // Порядок сортировки.
                default:
                    return null;
        }
    }

    // Вызывается LoaderManager при завершении загрузки.
    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        // Если контакт существует в базе данных, вывести его информацию.
        if (data != null && data.moveToFirst()) {
            // Получение индекса столбца для каждого элемента данных.
            int nameIndex = data.getColumnIndex(Contact.COLUMN_NAME);
            int phoneIndex = data.getColumnIndex(Contact.COLUMN_PHONE);
            int emailIndex = data.getColumnIndex(Contact.COLUMN_EMAIL);
            int streetIndex = data.getColumnIndex(Contact.COLUMN_STREET);
            int cityIndex = data.getColumnIndex(Contact.COLUMN_CITY);
            int stateIndex = data.getColumnIndex(Contact.COLUMN_STATE);
            int zipIndex = data.getColumnIndex(Contact.COLUMN_ZIP);

            // Заполнение компонентов EditText полученными данным.
            nameTextInputLayout.getEditText().setText(
                    data.getString(nameIndex));
            phoneTextInputLayout.getEditText().setText(
                    data.getString(phoneIndex));
            emailTextInputLayout.getEditText().setText(
                    data.getString(emailIndex));
            streetTextInputLayout.getEditText().setText(
                    data.getString(streetIndex));
            cityTextInputLayout.getEditText().setText(
                    data.getString(cityIndex));
            stateTextInputLayout.getEditText().setText(
                    data.getString(stateIndex));
            zipTextInputLayout.getEditText().setText(
                    data.getString(zipIndex));

            updateSaveButtonFAB();
        }
    }

    // Вызывается LoaderManager при сбросе Loader
    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) { }
}
