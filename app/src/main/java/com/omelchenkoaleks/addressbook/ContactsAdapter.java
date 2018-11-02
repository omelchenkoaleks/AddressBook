package com.omelchenkoaleks.addressbook;

import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.omelchenkoaleks.addressbook.data.DatabaseDescription.Contact;

/**
 * Поставляет данные компоненту RecyclerView класса ContactsFragment.
 */
public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    /**
     * Интерфейс реализуется ContactsFragment для обработки
     * прикосновения к элементу в списке RecyclerView.
     */
    public interface ContactClickListener {
        void onClick(Uri contactUri);
    }

    /**
     * Вложенный субкласс RecyclerView.ViewHolder используется
     * для реализации паттерна View–Holder в контексте RecyclerView.
     */

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView textView;
        private long rowID;

        // Настройка объекта ViewHolder элемента RecyclerView.
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(android.R.id.text1);

            // Присоединение слушателя к itemView.
            itemView.setOnClickListener(
                    new View.OnClickListener() {
                        // Выполняется при щелчке на контакте в ViewHolder @Override
                        public void onClick(View view) {
                            clickListener.onClick(Contact.buildContactUri(rowID));
                        }
                    }
            );
        }

        // Идентификатор записи базы данных для контакта в ViewHolder.
        public void setRowID(long rowID) {
            this.rowID = rowID;
        }
    }

    // Переменные экземпляров ContactsAdapter.
    private Cursor cursor = null;
    private final ContactClickListener clickListener;

    // Конструктор.
    public ContactsAdapter(ContactClickListener clickListener) {
        this.clickListener = clickListener;
    }

    // Подготовка нового элемента списка и его объекта ViewHolder.
    @NonNull
    @Override
    public ContactsAdapter.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        // Заполнение макета android.R.layout.simple_list_item_1.
        View view = LayoutInflater.from(parent.getContext())
                .inflate( android.R.layout.simple_list_item_1, parent, false);

        // ViewHolder текущего элемента.
        return new ViewHolder(view);
    }

    // Назначает текст элемента списка.
    @Override
    public void onBindViewHolder(@NonNull ContactsAdapter.ViewHolder holder, int position) {
        cursor.moveToPosition(position);
        holder.setRowID(cursor.getLong(cursor.getColumnIndex(Contact._ID)));
        holder.textView.setText(cursor.getString(cursor.getColumnIndex( Contact.COLUMN_NAME)));
    }

    // Возвращает количество элементов, предоставляемых адаптером.
    @Override
    public int getItemCount() {
        return (cursor != null) ? cursor.getCount() : 0;
    }

    // Текущий объект Cursor адаптера заменяется новым.
    public void swapCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
    }
}
