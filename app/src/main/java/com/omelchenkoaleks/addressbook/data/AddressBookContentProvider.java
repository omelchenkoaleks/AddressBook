package com.omelchenkoaleks.addressbook.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.omelchenkoaleks.addressbook.R;
import com.omelchenkoaleks.addressbook.data.DatabaseDescription.Contact;

/**
 * Определяет как приложение будет работать с базой данных.
 * Определяет операции получения данных, вставки, обновления и удаления с базой данных.
 */
public class AddressBookContentProvider extends ContentProvider {

    // Будет использоваться для обращения к базе данных.
    private AddressBookDatabaseHelper dbHelper;

    // UriMatcher помогает ContentProvider определить выполняемую операцию.
    // Эта статическая переменная содержит объект класса UriMatcher и используется для
    // того, чтобы определять, какие операции должны выполняться в методах
    // query, update, delete.
    private static final UriMatcher uriMatcher =
            new UriMatcher(UriMatcher.NO_MATCH);

    // Используем эти константы для определения выполняемой операции.
    // Один контакт.
    private static final int ONE_CONTACT = 1;
    // Таблица контактов.
    private static final int CONTACTS = 2;

    // Статический блок для настройки UriMatcher объекта ContentProvider.
    // Этот блок выполнится один раз, когда AddressBookContentProvider будет загружаться в память.
    static {

        /**
         * Метод addUri получает три аргумента:
         * строка с авторитетным именем, строка с путем (URI), код int(возвращаемый
         * UriMatcher, когда идентификатор URI, переданный ContentProvider, соответствует URI,
         * хранящемуся в UriMatcher.
         */

        // Uri для контакта с заданным идентификатором.
        uriMatcher.addURI(DatabaseDescription.AUTHORITY,
                Contact.TABLE_NAME + "/#", ONE_CONTACT);

        // Uri для таблицы.
        uriMatcher.addURI(DatabaseDescription.AUTHORITY,
                Contact.TABLE_NAME, CONTACTS);
    }

    // Будет вызываться при создании AddressBookContentProvider.
    @Override
    public boolean onCreate() {
        // Создаем объект.
        dbHelper = new AddressBookDatabaseHelper(getContext());
        return true;
    }

    // Получение информации из базы данных.
    @androidx.annotation.Nullable
    @Override
    public Cursor query(@androidx.annotation.NonNull Uri uri,
                        @androidx.annotation.Nullable String[] projection,
                        @androidx.annotation.Nullable String selection,
                        @androidx.annotation.Nullable String[] selectionArgs,
                        @androidx.annotation.Nullable String sortOrder) {

        // Создаем SQLiteQueryBuilder для запроса к таблице contacts.
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(Contact.TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            // Выбираем контакт с заданным идентификатором.
            case ONE_CONTACT:
                queryBuilder.appendWhere(Contact._ID + "=" + uri.getLastPathSegment());
                break;
            // Выбираем все контакты.
            case CONTACTS:
                break;

                default:
                    throw  new UnsupportedOperationException(
                            getContext().getString(R.string.invalid_query_uri) + uri);
        }

        // Выполняем запрос для получения одного или всех контактов.
        Cursor cursor = queryBuilder.query(dbHelper.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, sortOrder);

        // Настраиваем отслеживание изменений в контенте.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @androidx.annotation.Nullable
    @Override
    public String getType(@androidx.annotation.NonNull Uri uri) {
        return null;
    }

    // Вставляем новый контакт в базу данных.
    @androidx.annotation.Nullable
    @Override
    public Uri insert(@androidx.annotation.NonNull Uri uri,
                      @androidx.annotation.Nullable ContentValues values) {

        Uri newContactUri = null;

        switch (uriMatcher.match(uri)) {
            case CONTACTS:
                // При успехе возвращается идентификатор записи нового контакта.
                long rowId = dbHelper.getWritableDatabase().insert(
                        Contact.TABLE_NAME, null, values);
                // Если контакт был вставлен, создать подходящий Uri, если нет - выдать исключение.
                if (rowId > 0) {
                    newContactUri = Contact.buildContactUri(rowId);

                    // Оповещаем наблюдателей об изменениях в базе данных.
                    getContext().getContentResolver().notifyChange(uri, null);
                } else
                    throw new SQLException(
                            getContext().getString(R.string.insert_failed) + uri);
                break;

                default:
                    throw new UnsupportedOperationException(
                            getContext().getString(R.string.invalid_insert_uri) +
                                    uri);
        }

        return newContactUri;
    }

    // Метод для удаления контакта из базы данных.
    @Override
    public int delete(@androidx.annotation.NonNull Uri uri,
                      @androidx.annotation.Nullable String selection,
                      @androidx.annotation.Nullable String[] selectionArgs) {

        int numberOfRowsDeleted;

        switch (uriMatcher.match(uri)) {
            case ONE_CONTACT:
                // Получаем из URI идентификатор контакта.
                String id = uri.getLastPathSegment();

                // Удаляем контакт.
                numberOfRowsDeleted = dbHelper.getWritableDatabase().delete(
                        Contact.TABLE_NAME, Contact._ID + "=" + id,
                        selectionArgs);
                break;

                default:
                    throw new UnsupportedOperationException(
                            getContext().getString(R.string.invalid_delete_uri) + uri);
        }

        // Оповещаем наблюдателей об изменениях в базе данных.
        if (numberOfRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numberOfRowsDeleted;
    }

    // Метод обновляет существующую запись.
    @Override
    public int update(@androidx.annotation.NonNull Uri uri,
                      @androidx.annotation.Nullable ContentValues values,
                      @androidx.annotation.Nullable String selection,
                      @androidx.annotation.Nullable String[] selectionArgs) {

        // 1 - если обновление успешно, 2 - при неудаче.
        int numberOfRowsUpdated;

        switch (uriMatcher.match(uri)) {
            case ONE_CONTACT:
                // Получаем идентификатор из Uri.
                String id = uri.getLastPathSegment();

                // Обновляем контакт.
                numberOfRowsUpdated = dbHelper.getWritableDatabase().update(
                        Contact.TABLE_NAME, values, Contact._ID + "=" + id,
                        selectionArgs);
                break;

                default:
                    throw new UnsupportedOperationException(
                            getContext().getString(R.string.invalid_update_uri) + uri);
        }

        // Если изменения были внесены оповещаем наблюдателей.
        if (numberOfRowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numberOfRowsUpdated;
    }
}
