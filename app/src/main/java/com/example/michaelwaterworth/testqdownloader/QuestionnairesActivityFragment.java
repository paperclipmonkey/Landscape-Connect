package com.example.michaelwaterworth.testqdownloader;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.view.ActionMode;
import android.text.InputType;
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
import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;


/**
 * Created by michaelwaterworth on 16/08/15. Copyright Michael Waterworth
 */
public class QuestionnairesActivityFragment extends Fragment {
    static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    protected QuestionnairesActivityFragment mThis;
    protected ActionMode mActionMode;
    protected MaterialDialog dialog;
    private View.OnClickListener fabClickListener = new View.OnClickListener() {
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
                        .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT)
                        .input(R.string.add_code_dialog_hint, R.string.add_code_dialog_prefill, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                addNewQs(input.toString());
                            }
                        }).show();
                break;
            //QR button pressed
            case R.id.fab_qr:
                getFabQr();
                break;
        }
        }
    };

    public QuestionnairesActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mThis = this;
        final View base = inflater.inflate(R.layout.fragment_list, container, false);
        final ListView listView = (ListView) base.findViewById(R.id.qslist);

        // Setup cursor adapter
        QuestionnairesAdapter todoAdapter = new QuestionnairesAdapter(getContext(), null, true);
        // Attach cursor adapter to the ListView
        listView.setAdapter(todoAdapter);

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
                ((QuestionnairesAdapter)listView.getAdapter()).swapCursor(cursor);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> arg0) {
                ((QuestionnairesAdapter)listView.getAdapter()).swapCursor(null);
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
                //ListView listView = (ListView) base.findViewById(R.id.qslist);
                //listView.getChildAt(i).setSelected(b);
                actionMode.setTitle(selectedCount + getActivity().getString(R.string.space_selected));
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // ListView Clicked item index
                Cursor c = (Cursor)adapterView.getItemAtPosition(position);
                Questionnaire questionnaire = Questionnaire.newInstance(c);
                Intent intent = new Intent(getActivity(), SectionsActivity.class);
                intent.putExtra("id", questionnaire.getId());
                startActivity(intent);
            }
        });

        //Registed handlers for FAB events
        FloatingActionMenu floatingActionMenu = (FloatingActionMenu) base.findViewById(R.id.fab_menu);

        final com.github.clans.fab.FloatingActionButton fabButton1 = (com.github.clans.fab.FloatingActionButton) base.findViewById(R.id.fab_link);
        final com.github.clans.fab.FloatingActionButton fabButton2 = (com.github.clans.fab.FloatingActionButton) base.findViewById(R.id.fab_qr);
        fabButton1.setOnClickListener(fabClickListener);
        fabButton2.setOnClickListener(fabClickListener);

        fabButton2.setMax(100);


        return base;
    }

    private void getFabQr(){
        Log.d("TAG", "Get 1");
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.CAMERA)) {

                // Show an expanation to the user *asynchronously* -- don't block
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
            IntentIntegrator integrator = IntentIntegrator.forSupportFragment(mThis);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
            integrator.setPrompt(getActivity().getString(R.string.scan_qr_code));
            integrator.setBeepEnabled(true);
            integrator.initiateScan();
        }
    }

    public void showHideProgress(){
        if(dialog != null){
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
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("TAG", "Permission Granted");
                    getFabQr();
                } else {
                    Log.d("TAG", "Permission denied");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
            default:
                Log.d("TAG", "Unknown request code");
        }
    }


    public void addNewQs(String url){
        new DownloadToString().execute("http://3equals.co.uk/lc-json/1.json");
        showHideProgress();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.d("QRSCAN", "Cancelled from fragment");
            } else {
                Log.d("QRSCAN", "Scanned from fragment: " + result.getContents());
                //TODO - Check is URL
                addNewQs(result.getContents());
            }
        }
    }

    public void parseJson(String string){
        showHideProgress();
        try {
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            Questionnaire questionnaire = gson.fromJson(string, Questionnaire.class);
            questionnaire.save();
        } catch (Exception e){
            //TODO Add a message to the user here
            Log.d("Error",e.getLocalizedMessage());
        }
    }


    /**
     * Background Async Task to download file
     * */
    class DownloadToString extends AsyncTask<String, String, String> {
        // Output stream
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        /**
         * Before starting background thread Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //showDialog(progress_bar_type);
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();

                // this will be useful so that you can show a typical 0-100%
                // progress bar

                int lenghtOfFile = conection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to buffer
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            //pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            //dismissDialog(progress_bar_type);
            try {
                parseJson(output.toString("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }
}