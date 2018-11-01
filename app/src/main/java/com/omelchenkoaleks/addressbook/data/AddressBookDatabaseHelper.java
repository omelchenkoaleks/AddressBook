package com.omelchenkoaleks.addressbook.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.omelchenkoaleks.addressbook.data.DatabaseDescription.Contact;

/**
 * Создает базу данных и используется для работы с ней.
 * Класс дает возможность AddressBookContentProvider обращаться к созданной базе данных.
 */
public class AddressBookDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "AddressBook.db";
    private static final int DATABASE_VERSION = 1;

    /**
     * Конструктор.
     * Объект Context, в котором создается или открывается база данных.
     * Имя базы данных.
     * Объект CursorFactory = null означает, что мы хотим использовать CursorFactory (объект) по умолчанию.
     * Номер версии базы данных.
     */
    public AddressBookDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Создаем таблицу contacts при создании базы данных.
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Команда SQL для создания таблицы contacts.
        final String CREATE_CONTACTS_TABLE =
                "CREATE TABLE " + Contact.TABLE_NAME + "(" + Contact._ID +
                        " integer primary key, " + Contact.COLUMN_NAME +
                        " TEXT, " + Contact.COLUMN_PHONE + " TEXT, " + Contact.COLUMN_EMAIL +
                        " TEXT, " + Contact.COLUMN_STREET + " TEXT, " + Contact.COLUMN_CITY +
                        " TEXT, " + Contact.COLUMN_STATE + " TEXT, " + Contact.COLUMN_ZIP + " TEXT);";
        // Создаем таблицу contacts.
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // В этом методе обычно определяется способ обновления при изменении схемы базы данных.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // В нашем приложении обновлять базу данных не нужно будет, поэтому этот метод пуст.
    }
}
