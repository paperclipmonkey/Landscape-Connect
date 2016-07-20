package uk.co.threeequals.landscapeconnect;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.InputType;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.content.ContentProvider;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;


/**
 * Shows questionnaires in the questionnaires activity
 * Loaded as the default page then uses an adapter coupled to a ViewList to show questionnaires
 */
public class QuestionnairesFragment extends Fragment {
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private final String TAG = "QuestionnairesFragment";
    private MaterialDialog dialog;
    private ListView listView;
    private QuestionnairesAdapter questionnairesAdapter;

    private final View.OnClickListener fabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Close the FAB menu
            FloatingActionMenu fabMenu = (FloatingActionMenu) v.getParent();
            fabMenu.close(true);

            switch (v.getId()) {
                //Link button pressed
                case R.id.fab_link:
                    new MaterialDialog.Builder(getContext())
                            .title(R.string.add_code_dialog_title)
                            .content(R.string.add_code_dialog_content)
                            .inputType(InputType.TYPE_CLASS_TEXT)
                            .input(R.string.add_code_dialog_hint, R.string.add_code_dialog_prefill, new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                    addNewQs(input.toString());
                                }
                            }).show();
                    break;
                //QR button pressed
                case R.id.fab_qr:
                    getFabQr();
                    break;
                case R.id.fab_website:
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(getString(R.string.questionnaire_gallery_url)));
                    startActivity(i);
                    break;
            }
        }
    };

    final String[] projection = {"_id"};


    private final LoaderManager.LoaderCallbacks loaderCB = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int arg0, Bundle cursor) {
            return new CursorLoader(
                    getActivity(),                                          // Parent activity context
                    ContentProvider.createUri(Questionnaire.class, null),   // Table to query
                    projection,                                             // Projection to return
                    null,                                                   // Selection clause
                    null,                                                   // Selection arguments
                    "DateAdded DESC"                                        // Default sort order
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
            ((CursorAdapter) listView.getAdapter()).swapCursor(cursor);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> arg0) {
            ((CursorAdapter) listView.getAdapter()).swapCursor(null);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View base = inflater.inflate(R.layout.fragment_questionnaires, container, false);

        // Setup cursor adapter
        questionnairesAdapter = new QuestionnairesAdapter(getContext(), null, true);

        listView = (ListView) base.findViewById(R.id.qslist);
        // Attach cursor adapter to the ListView
        listView.setAdapter(questionnairesAdapter);

        TextView emptyView = (TextView) base.findViewById(R.id.emptyQuestionnaires);
        listView.setEmptyView(emptyView);
        emptyView.setMovementMethod(LinkMovementMethod.getInstance());//HTML linking

        getActivity().getSupportLoaderManager().initLoader(QuestionnairesActivity.QUESTIONNAIRES_FRAGMENT, null, loaderCB);

        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            int selectedCount = 0;

            @Override
            public boolean onCreateActionMode(android.view.ActionMode actionMode, Menu menu) {
                // Inflate the menu for the CAB
                MenuInflater inflater = actionMode.getMenuInflater();
                inflater.inflate(R.menu.questionnaires_actions, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(android.view.ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(android.view.ActionMode actionMode, MenuItem menuItem) {
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
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
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
                View v = listView.getChildAt(i);

                v.setSelected(b);

                LCLog.d(TAG, "Setting selected " + i + " to:" + b);

                actionMode.setTitle(selectedCount + getActivity().getString(R.string.space_selected));

                //final long[] ids = listView.getCheckedItemIds();
                //questionnairesAdapter.notifyDataSetChanged();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // ListView Clicked item index
                Cursor c = (Cursor) adapterView.getItemAtPosition(position);
                Questionnaire questionnaire = Questionnaire.newInstance(c);

                Intent intent = new Intent(getContext(), QuestionnaireActivity.class);
                intent.putExtra("id", questionnaire.getId());
                startActivity(intent);
            }
        });

        //Registered handlers for FAB events
        final com.github.clans.fab.FloatingActionButton fabButton1 = (com.github.clans.fab.FloatingActionButton) base.findViewById(R.id.fab_link);
        final com.github.clans.fab.FloatingActionButton fabButton2 = (com.github.clans.fab.FloatingActionButton) base.findViewById(R.id.fab_qr);
        final com.github.clans.fab.FloatingActionButton fabButton3 = (com.github.clans.fab.FloatingActionButton) base.findViewById(R.id.fab_website);
        fabButton1.setOnClickListener(fabClickListener);
        fabButton2.setOnClickListener(fabClickListener);
        fabButton3.setOnClickListener(fabClickListener);
        fabButton2.setMax(100);

        //Check if we were started with an install intent
        Intent intent = getActivity().getIntent();
        if(intent != null) {
            Uri data = intent.getData();
            if(data != null) {
                Log.i(TAG, "Installing Questionnaire from URL: " + data.toString());
                addNewQs(data.toString());
            }
        }

        return base;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dialog = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().getSupportLoaderManager().destroyLoader(QuestionnairesActivity.QUESTIONNAIRES_FRAGMENT);


  //      questionnairesAdapter = null;
    }

    private void getFabQr() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.CAMERA)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                //TODO Add explanation
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);

            } else {
                // No explanation needed, we can request the permission.
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            }
        } else {
            IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
            integrator.setPrompt(getActivity().getString(R.string.scan_qr_code));
            integrator.setBeepEnabled(true);
            integrator.initiateScan();
        }
    }

    private void showHideInstallProgress(boolean show) {
        if (dialog != null && !show) {
            dialog.dismiss();
            dialog = null;
        } else {
            dialog = new MaterialDialog.Builder(getContext())
                    .title(R.string.downloading_questionnaire)
                    .content(R.string.please_wait)
                    .progress(true, 0)
                    .show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getFabQr();
                } else {
                    LCLog.d(TAG, "Permission denied");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            default:
                LCLog.d(TAG, "Unknown request code");
        }
    }


    private void addNewQs(String url) {
        if (!url.startsWith("http", 0)) {
            url = getString(R.string.base_url) + getString(R.string.download_url_fragment) + url.toUpperCase();
        }
        LCLog.d(TAG, "Downloading JSON: " + url);
        new DownloadToString().execute(url);
        showHideInstallProgress(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                LCLog.d(TAG, "Cancelled from fragment");
            } else {
                LCLog.d(TAG, "Scanned from fragment: " + result.getContents());
                addNewQs(result.getContents());
            }
        }
    }

    /**
     * Background Async Task to download file
     */
    class DownloadToString extends AsyncTask<String, String, String> {
        // Output stream
        final ByteArrayOutputStream output = new ByteArrayOutputStream();

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.setRequestProperty("User-Agent", getString(R.string.user_agent_http));

                connection.connect();

                // download the file
                InputStream input = new BufferedInputStream(connection.getInputStream(),
                        8192);

                byte data[] = new byte[1024];


                while ((count = input.read(data)) != -1) {
                    // writing data to buffer
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                LCLog.e(TAG, "Error: ", e);
            }

            return null;
        }

        /**
         * After completing background task
         * save the questionnaire to Db
         **/
        @Override
        protected void onPostExecute(String file_url) {
            try {
                String parsedString = output.toString("UTF-8");
                Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                Questionnaire questionnaire = gson.fromJson(parsedString, Questionnaire.class);
                questionnaire.saveQuestionnaire();
                showHideInstallProgress(false);//Hide the progress spinner when fully finished.
            } catch (Exception e) {
                Toast toast = Toast.makeText(getContext(), R.string.failed_to_download, Toast.LENGTH_SHORT);
                toast.show();
                LCLog.e(TAG, "Failed to install questionnaire", e);
                showHideInstallProgress(false);//Hide the progress spinner when fully finished.
            }
        }
    }
}