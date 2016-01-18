package com.example.michaelwaterworth.testqdownloader;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.alexbbb.uploadservice.AllCertificatesAndHostsTruster;
import com.alexbbb.uploadservice.MultipartUploadRequest;
import com.alexbbb.uploadservice.UploadServiceBroadcastReceiver;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Called from the Scheduler Service when there are no more scheduled tasks left
 * and data to upload. Kicks off the static upload task
 */
public class LSUploadService extends Service {
    private final String TAG = "LSUploadService";
    private Response uploadingResponse;
    private int completedUploads = 0;

    private final UploadServiceBroadcastReceiver uploadReceiver =
            new UploadServiceBroadcastReceiver() {

                // you can override this progress method if you want to get
                // the completion progress in percent (0 to 100)
                // or if you need to know exactly how many bytes have been transferred
                // override the method below this one
                @Override
                public void onProgress(String uploadId, int progress) {
                    Log.i(TAG, "The progress of the upload with ID "
                            + uploadId + " is: " + progress);
                }

                @Override
                public void onProgress(final String uploadId,
                                       final long uploadedBytes,
                                       final long totalBytes) {
                    Log.i(TAG, "Upload with ID " + uploadId +
                            " uploaded bytes: " + uploadedBytes
                            + ", total: " + totalBytes);
                }

                @Override
                public void onError(String uploadId, Exception exception) {
                    Log.e(TAG, "Error in upload with ID: " + uploadId + ". "
                            + exception.getLocalizedMessage(), exception);
                }

                @Override
                public void onCompleted(String uploadId,
                                        int serverResponseCode,
                                        String serverResponseMessage) {
                    Log.i(TAG, "Upload with ID " + uploadId
                            + " has been completed with HTTP " + serverResponseCode
                            + ". Response from server: " + serverResponseMessage);

                    Log.d(TAG, "Completed LS Upload");

                    removeUploaded();
                    redoOrNotify();

                    //If your server responds with a JSON, you can parse it
                    //from serverResponseMessage string using a library
                    //such as org.json (embedded in Android) or Google's gson
                }
            };

    //public LSUploadService() {
//        super("LSUploadService");
//    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "bound");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Upload Service");

        uploadReceiver.register(this);

        checkForPendingUploads();

        Intent notificationIntent = new Intent(this, QuestionnairesActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.hi_res_icon);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Landscape Connect")
                .setTicker("Uploading")
                .setContentText("Saved views")
                .setSmallIcon(R.drawable.hi_res_icon)
                .setLargeIcon(
                        Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentIntent(pendingIntent)
                .setOngoing(true).build();
//                .addAction(android.R.drawable.ic_media_previous,
//                        "Previous", previousIntent)
//                .addAction(android.R.drawable.ic_media_play, "Play",
//                        playIntent)
//                .addAction(android.R.drawable.ic_media_next, "Next",
//                        nextIntent).build();
        startForeground(0, notification);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uploadReceiver.unregister(this);
        Log.i(TAG, "In onDestroy");
    }

    private void checkForPendingUploads() {
        List<Response> responseList = Response.getFinishedResponses();
        Log.d("Upload", "Waiting responses: " + responseList.size());
        if (responseList.size() > 0) {
            //Pop one off the stack and upload it
            uploadingResponse = responseList.get(0);
            upload(getApplicationContext(), uploadingResponse);
        } else {
            stop();
        }
    }

    private void stop(){
        stopSelf();
    }

    private void redoOrNotify(){
        Log.d(TAG, "redoOrNotify");
        if(Response.getFinishedResponses().size() > 0){
            checkForPendingUploads();
        } else if(completedUploads > 0){
            //Notify the user how many uploads have completed and all done.
            buildSuccessNotification();
        }
    }

    private void removeUploaded(){
        Log.d(TAG, "removeUploaded");
        completedUploads++;//Add to count
        uploadingResponse.deleteFull();
    }

    private void buildSuccessNotification() {
        Log.d(TAG, "buildSuccessNotification");

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.hi_res_icon)
                        .setContentTitle("" + completedUploads + " upload(s) successful")
                        .setContentText("Thanks for completing the questionnaire");

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, QuestionnairesActivity.class);

        mBuilder.setAutoCancel(true);//Automatically dismiss on click

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(QuestionnairesActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);


        // mId allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());
    }

    /**
     * Start a new upload
     *
     * @param context Context used to start service
     */
    private void upload(Context context, Response response) {
        Log.d(TAG, "Uploading");
        AllCertificatesAndHostsTruster.apply();
        final MultipartUploadRequest request = new MultipartUploadRequest(context,
                response.getId() + "",//Long used to keep track of db
                context.getString(R.string.base_url) + context.getString(R.string.upload_path));
    /*
     * parameter-name: is the name of the parameter that will contain file's data.
     * Pass "uploaded_file" if you're using the test PHP script
     *
     * custom-file-name.extension: is the file name seen by the server.
     * E.g. value of $_FILES["uploaded_file"]["name"] of the test PHP script
     */

        //Add files for upload
        try {
            String filename = response.photo.substring(response.photo.indexOf(":") + 1);
            request.addFileToUpload(filename, "file", response.photo, "image/jpeg");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //Add all data for upload
        List<SectionResponse> responseList = response.getSectionResponses();
        for (SectionResponse sectionResponse : responseList) {
            List<QuestionResponse> questionResponses = sectionResponse.getQuestionResponses();
            for (QuestionResponse questionResponse : questionResponses) {
                request.addParameter(sectionResponse.title + "/" + questionResponse.question.getTitle(), questionResponse.rData);
            }
        }

//        //configure the notification
//        request.setNotificationConfig(R.drawable.app_icon_silhouette,
//                context.getString(R.string.app_name),
//                context.getString(R.string.uploading_toast),
//                context.getString(R.string.uploading_success),
//                context.getString(R.string.upload_failed),
//                true);//Clear on success

        // set the intent to perform when the user taps on the upload notification.
        // currently tested only with intents that launches an activity
        // if you comment this line, no action will be performed when the user taps on the notification

//        UploadNotificationConfig notificationConfig = new UploadNotificationConfig();
//        notificationConfig.setClickIntent(new Intent(context.getApplicationContext(), QuestionnairesActivity.class).putExtra("upload", "intent"));
//        request.setNotificationConfig(notificationConfig);

        try {
            //Start upload service and display the notification
            request.startUpload();

        } catch (Exception exc) {
            //You will end up here only if you pass an incomplete UploadRequest
            Log.e("AndroidUploadService", exc.getLocalizedMessage(), exc);
        }
    }


//    public class UploadReceiver extends BroadcastReceiver {
//
//        public UploadReceiver(){
//            super();
//            Log.d(TAG, "creating Upload Receiver");
//        }
//
//        /**
//         * Receiving status notifications from the system.
//         * If complete call onCompleted
//         * @param context
//         * @param intent
//         */
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Log.d(TAG, "onReceive");
//            if (intent != null) {
//                //if (com.alexbbb.uploadservice.LSUploadService.getActionBroadcast().equals(intent.getAction())) {
//                final int status = intent.getIntExtra(com.alexbbb.uploadservice.UploadService.STATUS, 0);
//                final String uploadId = intent.getStringExtra(com.alexbbb.uploadservice.UploadService.UPLOAD_ID);
//
//                switch (status) {
//                    case com.alexbbb.uploadservice.UploadService.STATUS_COMPLETED:
//                        final int responseCode = intent.getIntExtra(com.alexbbb.uploadservice.UploadService.SERVER_RESPONSE_CODE, 0);
//                        final String responseMsg = intent.getStringExtra(com.alexbbb.uploadservice.UploadService.SERVER_RESPONSE_MESSAGE);
//                        onCompleted(context, uploadId, responseCode, responseMsg);
//                        break;
//
//                    default:
//                        break;
//                }
//                //}
//            }
//        }
//
//        /**
//         * Once the upload has completed check the response
//         * @param context
//         * @param uploadId
//         * @param serverResponseCode
//         * @param serverResponseMessage
//         */
//        public void onCompleted(Context context,
//                                String uploadId,
//                                int serverResponseCode,
//                                String serverResponseMessage) {
//            Log.i(TAG, "Upload with ID " + uploadId
//                    + " has completed with HTTP " + serverResponseCode
//                    + ". Response from server: " + serverResponseMessage);
//
//            Log.d(TAG, "Server code:" + serverResponseCode);
//            if (serverResponseCode == 200) {
//                //try {
//                Log.d(TAG, "Got here");
//
//                //JSONObject jsonObject = new JSONObject(serverResponseMessage);
//
//
//                //Start the upload service...
////                Intent serviceIntent = new Intent(context, LSUploadService.class);
////                context.startService(serviceIntent);
//                removeUploaded();
//                redoOrNotify();
//
//                //buildSuccessNotification(context, jsonObject);
//
//                //deleteResponse(context);
////            } catch (JSONException e) {
////                e.printStackTrace();
////            }
//            }
//        }
//    }
}