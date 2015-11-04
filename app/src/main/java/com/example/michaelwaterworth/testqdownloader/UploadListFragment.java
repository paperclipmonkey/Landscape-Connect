package com.example.michaelwaterworth.testqdownloader;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.activeandroid.content.ContentProvider;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mThis = this;
        final View base = inflater.inflate(R.layout.fragment_upload, container, false);
        final ListView listView = (ListView) base.findViewById(R.id.uploadlist);

        // Setup cursor adapter
        UploadsAdapter adapter = new UploadsAdapter(getContext(), null, true);
        // Attach cursor adapter to the ListView
        listView.setAdapter(adapter);

        final String[] projection = {"_id", "DateAdded", "Questionnaire", "Photo", "Complete"};

        getActivity().getSupportLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int arg0, Bundle cursor) {
                return new CursorLoader(getActivity(),
                        ContentProvider.createUri(Response.class, null),
                        projection, "Complete = 1", null, null
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

//        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
//        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
//            int selectedCount = 0;
//
//            @Override
//            public boolean onCreateActionMode(android.view.ActionMode actionMode, Menu menu) {
//                // Inflate the menu for the CAB
//                MenuInflater inflater = actionMode.getMenuInflater();
//                inflater.inflate(R.menu.menu_list, menu);
//                return true;
//            }
//
//            @Override
//            public boolean onPrepareActionMode(android.view.ActionMode actionMode, Menu menu) {
//                return false;
//            }
//
//            @Override
//            public boolean onActionItemClicked(android.view.ActionMode actionMode, MenuItem menuItem) {
//                final long[] ids = listView.getCheckedItemIds();
//
//                switch (menuItem.getItemId()) {
//                    case R.id.action_delete:
//                        new MaterialDialog.Builder(getContext())
//                                .title(R.string.delete_dialog_title)
//                                .content(R.string.delete_dialog_content)
//                                .positiveText(R.string.delete_dialog_positive)
//                                .negativeText(R.string.delete_dialog_negative)
//                                .onPositive(new MaterialDialog.SingleButtonCallback() {
//                                    @Override
//                                    public void onClick(MaterialDialog dialog, DialogAction which) {
//                                        //Delete the IDs specified
//                                        Questionnaire.deleteFromIds(ids);
//                                    }
//                                })
//                                .show();
//                        actionMode.finish(); // Action picked, so close the CAB
//                        return true;
//                    default:
//                        return false;
//                }
//            }
//
//            @Override
//            public void onDestroyActionMode(android.view.ActionMode actionMode) {
//                selectedCount = 0;
//            }
//
//            @Override
//            public void onItemCheckedStateChanged(android.view.ActionMode actionMode, int i, long l, boolean b) {
//                if (b) {
//                    selectedCount++;
//                } else {
//                    selectedCount--;
//                }
//                //ListView listView = (ListView) base.findViewById(R.id.qslist);
//                //listView.getChildAt(i).setSelected(b);
//                actionMode.setTitle(selectedCount + getActivity().getString(R.string.space_selected));
//            }
//        });

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                // ListView Clicked item index
//                Cursor c = (Cursor)adapterView.getItemAtPosition(position);
//                Questionnaire questionnaire = Questionnaire.newInstance(c);
//                Intent intent = new Intent(getActivity(), SectionsActivity.class);
//                intent.putExtra("id", questionnaire.getId());
//                startActivity(intent);
//            }
//        });

        return base;
    }
}