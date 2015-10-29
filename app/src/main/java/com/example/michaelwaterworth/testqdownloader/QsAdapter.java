package com.example.michaelwaterworth.testqdownloader;

import android.content.Context;
import android.database.Cursor;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Attaches Data objects to Lists.
 * Adapter supplies stylised Data
 */
class QsAdapter extends CursorAdapter {

    public QsAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.qs_row, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Qs ti = Qs.newInstance(cursor);
        TextView title = (TextView) view.findViewById(R.id.qs_row_title);
        TextView description = (TextView) view.findViewById(R.id.qs_row_description);
        TextView dateView = (TextView) view.findViewById(R.id.qs_row_date);

        title.setText(ti.getName());
        description.setText(ti.getDescription());

        dateView.setText(DateUtils.getRelativeTimeSpanString(ti.getDateAdded().getTimeInMillis()));
    }

    @Override
    public void notifyDataSetChanged(){
        super.notifyDataSetChanged();

    }

}