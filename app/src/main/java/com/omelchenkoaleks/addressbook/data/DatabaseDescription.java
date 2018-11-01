package com.omelchenkoaleks.addressbook.data;

import android.net.Uri;

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
}
