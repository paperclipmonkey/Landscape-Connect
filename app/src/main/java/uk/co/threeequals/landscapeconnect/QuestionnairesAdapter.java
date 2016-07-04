package uk.co.threeequals.landscapeconnect;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Attaches Questionnaires to Lists
 * Adapter supplies stylised Data
 */
class QuestionnairesAdapter extends CursorAdapter {

    public QuestionnairesAdapter(Context context, Cursor c, boolean autoReQuery) {
        super(context, c, autoReQuery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.fragment_questionnaires_row, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Questionnaire ti = Questionnaire.newInstance(cursor);
        TextView title = (TextView) view.findViewById(R.id.qs_row_title);
        TextView description = (TextView) view.findViewById(R.id.qs_row_description);
//        TextView dateView = (TextView) view.findViewById(R.id.qs_row_date);

        title.setText(ti.getTitle());
        description.setText(ti.getDescription());

//        dateView.setText(context.getString(R.string.installed) + DateUtils.getRelativeTimeSpanString(ti.getDateAdded().getTimeInMillis()));
    }
}