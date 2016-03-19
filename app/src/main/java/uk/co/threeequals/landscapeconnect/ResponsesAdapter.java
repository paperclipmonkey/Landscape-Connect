package uk.co.threeequals.landscapeconnect;

import android.content.Context;
import android.database.Cursor;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Attaches Response objects to Lists.
 * Styles pending uploads ready for display
 */
class ResponsesAdapter extends CursorAdapter {
    String TAG = "ResponsesAdapter";

    public ResponsesAdapter(Context context, Cursor c, boolean autoReQuery) {
        super(context, c, autoReQuery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.fragment_upload_response, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Response response = Response.newInstance(cursor);
        TextView title = (TextView) view.findViewById(R.id.response_row_title);
        TextView description = (TextView) view.findViewById(R.id.response_row_description);
        TextView dateView = (TextView) view.findViewById(R.id.response_row_date);

        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.response_progressBar);

        ImageView imageView = (ImageView) view.findViewById(R.id.response_row_image);

        Picasso.with(context).load(response.thumb).into(imageView);

        if(response.percentUploaded > 0) {
            progressBar.setProgress(response.percentUploaded);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }

//        Uri uri = Uri.parse(response.photo);
//        imageView.setImageURI(uri);
//        MyApp.loadBitmap(uri,imageView);
//        imageView.setImageBitmap(MyApp.decodeSampledBitmapFromUri(context.getResources(), uri, 100, 100));

        title.setText(response.questionnaire.getName());
        description.setText(response.questionnaire.getDescription());

        dateView.setText(DateUtils.getRelativeTimeSpanString(response.getDateCompleted().getTimeInMillis()));
    }
}