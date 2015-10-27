package com.example.michaelwaterworth.testqdownloader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Attaches Data objects to Lists.
 * Adapter supplies stylised Data
 */
class QsAdapter extends BaseAdapter {
    private final List<Qs> mTasks;

    public QsAdapter(List<Qs> tasks) {
        mTasks = tasks;
    }

    @Override
    public int getCount() {
        return mTasks.size();
    }

    @Override
    public Qs getItem(int i) {
        return mTasks.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     * Render a view of the object for the list
     * @param position Location in list
     * @param view Base view
     * @param parent Parent view
     * @return Assembled view object
     */
    public View getView(int position, View view, ViewGroup parent) {

        if (view == null) {
            LayoutInflater vi = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.qs_row, null);
        }

        Qs ti = mTasks.get(position);
        TextView title = (TextView) view.findViewById(R.id.qs_row_title);
        TextView description = (TextView) view.findViewById(R.id.qs_row_description);
        TextView dateView = (TextView) view.findViewById(R.id.qs_row_date);

        title.setText(ti.getName());
        description.setText(ti.getDescription());

        SimpleDateFormat format = new SimpleDateFormat("d/M hh:mm a", Locale.UK);
        String date = format.format(ti.getDateAdded().getTime());
        dateView.setText(date);
        return view;
    }

    @Override
    public void notifyDataSetChanged(){
        super.notifyDataSetChanged();

    }

}