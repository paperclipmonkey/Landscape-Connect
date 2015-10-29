package com.example.michaelwaterworth.testqdownloader;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.activeandroid.content.ContentProvider;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;


/**
 * Created by michaelwaterworth on 16/08/15. Copyright Michael Waterworth
 */
public class UploadListFragment extends Fragment {
    protected UploadListFragment mThis;
    protected ActionMode mActionMode;
    protected MaterialDialog dialog;

    public UploadListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View base = inflater.inflate(R.layout.fragment_upload, container, false);
        mThis = this;

        final ListView listView = (ListView) base.findViewById(R.id.qslist);

        listView.setAdapter(new SimpleCursorAdapter(getActivity(),
                R.layout.qs_row,
                null,
                new String[] { "Name", "Description" , "DateAdded"},
                new int[] { R.id.qs_row_title, R.id.qs_row_description, R.id.qs_row_date },
                0));

        final String[] projection = {"_id", "DateAdded", "Name", "Description"};

        getActivity().getSupportLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int arg0, Bundle cursor) {
                return new CursorLoader(getActivity(),
                        ContentProvider.createUri(Questionnaire.class, null),
                        projection, null, null, null
                );
            }

            @Override
            public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
                ((SimpleCursorAdapter)listView.getAdapter()).swapCursor(cursor);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> arg0) {
                ((SimpleCursorAdapter)listView.getAdapter()).swapCursor(null);
            }
        });

        // Must add the progress bar to the root of the layout
        //ViewGroup root = (ViewGroup) getActivity().findViewById(android.R.id.content);
        //root.addView(progressBar);

//        final QuestionnairesAdapter adapter = new QuestionnairesAdapter(Questionnaire.getAll());
//        // Assign adapter to ListView
//        listView.setAdapter(adapter);

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            int selectedCount = 0;

            @Override
            public boolean onCreateActionMode(android.view.ActionMode actionMode, Menu menu) {
                // Inflate the menu for the CAB
                MenuInflater inflater = actionMode.getMenuInflater();
                inflater.inflate(R.menu.menu_list, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(android.view.ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(android.view.ActionMode actionMode, MenuItem menuItem) {
                //long[] ids = listView.getCheckedItemIds();
                //List<Questionnaire> questionnaire = Questionnaire.getFromIds(ids);
                //Log.d("TAG", "Size: " + questionnaire.size());
                final long[] ids = listView.getCheckedItemIds();

                switch (menuItem.getItemId()) {
                    case R.id.action_delete:
                        new MaterialDialog.Builder(getContext())
                                .title(R.string.delete_dialog_title)
                                .content(R.string.delete_dialog_content)
                                .positiveText(R.string.delete_dialog_positive)
                                .negativeText(R.string.delete_dialog_negative)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(MaterialDialog dialog, DialogAction which) {
                                        //Delete the IDs specified
                                        Questionnaire.deleteFromIds(ids);
                                    }
                                })
                                .show();
                        actionMode.finish(); // Action picked, so close the CAB
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(android.view.ActionMode actionMode) {
                selectedCount = 0;
            }

            @Override
            public void onItemCheckedStateChanged(android.view.ActionMode actionMode, int i, long l, boolean b) {
                if (b) {
                    selectedCount++;
                } else {
                    selectedCount--;
                }
                ListView listView = (ListView) base.findViewById(R.id.qslist);
                listView.getChildAt(i).setSelected(b);
                actionMode.setTitle(selectedCount + getActivity().getString(R.string.space_selected));
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // ListView Clicked item index
                Cursor c = (Cursor) adapterView.getItemAtPosition(position);
                Questionnaire questionnaire = Questionnaire.newInstance(c);
                Log.d("Tag", questionnaire.getName());
            }
        });

        return base;
    }
}