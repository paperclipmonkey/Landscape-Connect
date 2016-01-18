package com.example.michaelwaterworth.testqdownloader;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.activeandroid.content.ContentProvider;


/**
 * Created by michaelwaterworth on 16/08/15. Copyright Michael Waterworth
 */
public class UploadListFragment extends Fragment {

    public UploadListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View base = inflater.inflate(R.layout.fragment_upload, container, false);
        final ListView listView = (ListView) base.findViewById(R.id.uploadlist);

        // Setup cursor adapter
        UploadsAdapter adapter = new UploadsAdapter(getContext(), null, true);
        // Attach cursor adapter to the ListView
        listView.setAdapter(adapter);

        final String[] projection = {"_id"};

        getActivity().getSupportLoaderManager().initLoader(QuestionnairesActivity.UPLOAD_FRAGMENT, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int arg0, Bundle cursor) {
                return new CursorLoader(getActivity(),
                        ContentProvider.createUri(Response.class, null),
                        projection, "Finished = 1", null, null
                );
            }

            @Override
            public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
                ((CursorAdapter)listView.getAdapter()).swapCursor(cursor);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> arg0) {
                ((CursorAdapter)listView.getAdapter()).swapCursor(null);
            }
        });

        return base;
    }
}