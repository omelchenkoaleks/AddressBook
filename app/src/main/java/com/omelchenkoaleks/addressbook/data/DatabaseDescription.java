package com.omelchenkoaleks.addressbook.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Содержит описание таблицы contacts базы данных.
 * Открытые статические поля, используются классами ContentProvider and ContentResolver.
 * Вложенный класс Contact определяет статические поля для имени таблицы базы данных,
 * Uri для обращения к таблице через ContentProvider, имен столбцов таблицы, а также
 * содержит статический метод для создания объекта Uri, который ссылается на конкретный контакт
 * в базе данных.
 */
public class DatabaseDescription {

    // Имя ContentProvider: совпадает с именем пакета - поле нужно для получения ContentProvider
    public static final String AUTHORITY =
            "com.omelchenkoaleks.addressbook.data";

    // Базовый URI для взаимодействия с ContentProvider
    private static final Uri BASE_CONTENT_URI =
            Uri.parse("content://" + AUTHORITY);

    // Вложенный класс, который будет определять содержимое таблицы contacts.
    // Вместо того, чтобы определять константу _id (первичный ключ), идентифицирующий строку,
    // мы реализуем интерфейс BaseColumns, который определяет константу _ID со значением "id".
    public static final class Contact implements BaseColumns {

        // Задаем имя таблицы.
        public static final String TABLE_NAME = "contacts";

        // объект Uri для обращения к таблице через ContentProvider
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_NAME).build();

        // Имена столбцов таблицы.
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PHONE = "phone";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_STREET = "street";
        public static final String COLUMN_CITY = "city";
        public static final String COLUMN_STATE = "state";
        public static final String COLUMN_ZIP = "zip";

        // Метод создает Uri для конктретного контакта.
        public static Uri buildContactUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
