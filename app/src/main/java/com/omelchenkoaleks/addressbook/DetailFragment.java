package com.omelchenkoaleks.addressbook;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.omelchenkoaleks.addressbook.data.DatabaseDescription.Contact;

/**
 * Отображает информацию одного контакта и предоставляет
 * команды меню для редактирования и удаления этого контакта.
 * Класс управляет компонентами TextView, в которых отображается информация
 * о выбранном контакте, и командами на панели приложения,
 * которые позволяют пользователю отредактировать или удалить текущий контакт.
 * Вложенный интерфейс DetailFragment определяет методы обратного вызова,
 * реализуемые MainActivity, чтобы активность могла реагировать на удаление
 * контакта или прикосновение к команде на панели приложения для редактирования контакта.
 */
public class DetailFragment extends Fragment
    implements LoaderManager.LoaderCallbacks<Cursor> {

    // Методы обратного вызова, реализованные MainActivity.
    public interface DetailFragmentListener {

        // Вызывается при удалении контакта.
        void onContactDeleted();

        // Передает URI редактируемого контакта DetailFragmentListener.
        void onEditContact(Uri contactUri);
    }

    // Идентифицирует Loader.
    private static final int CONTACT_LOADER = 0;

    // Uri выбранного контакта.
    private Uri contactUri;

    // MainActivity.
    private DetailFragmentListener listener;

    // Имя контакта.
    private TextView nameTextView;
    // Телефон.
    private TextView phoneTextView;
    // Электронная почта.
    private TextView emailTextView;
    // Улица.
    private TextView streetTextView;
    // Город.
    private TextView cityTextView;
    // Штат.
    private TextView stateTextView;
    // Почтовый индекс.
    private TextView zipTextView;

    // Назначение DetailFragmentListener при присоединении фрагмента.
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (DetailFragmentListener) context;
    }

    // Удаление DetailFragmentListener при отсоединении фрагмента.
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

        // У фрагмента есть команды меню.
        setHasOptionsMenu(true);

        // Получение объекта Bundle с аргументами и извлечение URI.
        Bundle arguments = getArguments();

        if (arguments != null)
            contactUri = arguments.getParcelable(MainActivity.CONTACT_URI);

        // // Заполнение макета DetailFragment.
        View view = inflater.inflate(R.layout.fragment_details, container, false);

        // Получение компонентов EditTexts
        nameTextView = view.findViewById(R.id.nameTextView);
        phoneTextView = view.findViewById(R.id.phoneTextView);
        emailTextView = view.findViewById(R.id.emailTextView);
        streetTextView = view.findViewById(R.id.streetTextView);
        cityTextView = view.findViewById(R.id.cityTextView);
        stateTextView = view.findViewById(R.id.stateTextView);
        zipTextView = view.findViewById(R.id.zipTextView);

        // Загрузка контакта.
        getLoaderManager().initLoader(CONTACT_LOADER, null, this);

        return view;
    }

    // Отображение команд меню фрагмента.
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_details_menu, menu);
    }

    // Обработка выбора команд меню.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_edit:
                // Передача Uri слушател.
                listener.onEditContact(contactUri);
                return true;
            case R.id.action_delete:
                deleteContact();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Удаление контакта.
    private void deleteContact() {
        // FragmentManager используется для отображения confirmDelete.
        confirmDelete.show(getFragmentManager(), "confirm delete");
    }

    // DialogFragment для подтверждения удаления контакта.
    private final DialogFragment confirmDelete = new DialogFragment() {
        // Создание объекта AlertDialog и его возвращение.
        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle bundle) {
            // Создание объекта AlertDialog и его возвращение.
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setTitle(R.string.confirm_title);
            builder.setMessage(R.string.confirm_message);

            // Кнопка OK просто закрывает диалоговое окно.
            builder.setPositiveButton(R.string.button_delete,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int button) {
                            // Объект ContentResolver используется для вызова
                            // delete в AddressBookContentProvider.
                            getActivity().getContentResolver()
                                    .delete( contactUri, null, null);
                            // Оповещение слушателя.
                            listener.onContactDeleted();
                        }
                    });
            builder.setNegativeButton(R.string.button_cancel, null);

            // Вернуть AlertDialog.
            return builder.create();
        }
    };

    // Вызывается LoaderManager для создания Loader.
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        // Создание CursorLoader на основании аргумента id.
        CursorLoader cursorLoader;

        switch (id) {
            case CONTACT_LOADER:
                cursorLoader = new CursorLoader(getActivity(),
                        contactUri, // Uri отображаемого контакта.
                        null, // Все столбцы.
                        null, // Все записи.
                        null, // Без аргументов.
                        null); // Порядок сортировки.
                break;
                default:
                    cursorLoader = null;
                    break;
        }
        return cursorLoader;
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

            // Заполнение TextView полученными данными.
            nameTextView.setText(data.getString(nameIndex));
            phoneTextView.setText(data.getString(phoneIndex));
            emailTextView.setText(data.getString(emailIndex));
            streetTextView.setText(data.getString(streetIndex));
            cityTextView.setText(data.getString(cityIndex));
            stateTextView.setText(data.getString(stateIndex));
            zipTextView.setText(data.getString(zipIndex));
        }
    }

    // Вызывается LoaderManager при сбросе Loader.
    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) { }
}
