package com.example.michaelwaterworth.testqdownloader;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Attaches Data objects to Lists.
 * Adapter supplies stylised Data
 */
class UploadsAdapter extends CursorAdapter {

    public UploadsAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.response_row, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Response ti = Response.newInstance(cursor);
        TextView title = (TextView) view.findViewById(R.id.response_row_title);
        TextView description = (TextView) view.findViewById(R.id.response_row_description);
        TextView dateView = (TextView) view.findViewById(R.id.response_row_date);

        ImageView imageView = (ImageView) view.findViewById(R.id.response_row_image);


        Uri uri = Uri.parse(ti.photo);

        imageView.setImageURI(uri);

        title.setText(ti.questionnaire.getName());
        description.setText(ti.questionnaire.getDescription());

        dateView.setText(DateUtils.getRelativeTimeSpanString(ti.getDateCompleted().getTimeInMillis() / 1000));
    }
}