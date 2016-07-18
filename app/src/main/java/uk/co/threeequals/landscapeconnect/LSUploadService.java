package uk.co.threeequals.landscapeconnect;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadServiceBroadcastReceiver;

import java.util.List;

/**
 * Uploads data from the app to the website
 * Run as a background service with sticky foreground - Shows notification during upload
 */
public class LSUploadService extends Service {
    private final String TAG = "LSUploadService";
    private Response uploadingResponse;
    private int completedUploads = 0;

    private final UploadServiceBroadcastReceiver uploadReceiver =
            new UploadServiceBroadcastReceiver() {

                @Override
                public void onProgress(UploadInfo uploadInfo) {
//                    LCLog.i(TAG, "The progress of the upload with ID "
//                            + uploadId + " is: " + progress);
                }

                @Override
                public void onError(final UploadInfo uploadInfo, final Exception exception) {
                    LCLog.e(TAG, "Error in upload with ID: " + uploadInfo.getUploadId() + ". "
                            + exception.getLocalizedMessage(), exception);
                    redoOrNotify();
                }

                @Override
                public void onCancelled(UploadInfo uploadInfo) {
                    LCLog.e(TAG, "Cancelled upload with ID: " + uploadInfo.getUploadId());
                    redoOrNotify();
                }

                @Override
                public void onCompleted(UploadInfo uploadInfo,
                                        ServerResponse serverResponse) {
                    String serverResponseMessage = new String(serverResponse.getBodyAsString());
                    try {
                        JsonObject resp = new JsonParser().parse(serverResponseMessage).getAsJsonObject();

                        if(serverResponse.getHttpCode() != 200) {//Success HTTP status code
                            throw(new Exception("Wrong status code"));
                        }

                        //Parse the response into JSON
                        if(!resp.get("status").getAsString().equals("success")) {
                            throw(new Exception("Wrong success code"));
                        }
                        Log.i(TAG, "Upload with ID " + uploadInfo.getUploadId()
                                + " has been completed with HTTP " + serverResponse.getHttpCode()
                                + ". Response from server: " + serverResponseMessage);

                        removeUploaded();

                    } catch(Exception e) {
                        LCLog.e(TAG, "Error: ", e);
                        LCLog.e(TAG, "UploadId: " + uploadInfo.getUploadId());
                        LCLog.e(TAG, "Response: " + serverResponseMessage);
                    }
                    redoOrNotify();
                }
            };

    public LSUploadService() {
        Log.i(TAG, "Constructor");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "bound");
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uploadReceiver.unregister(this);
        Log.i(TAG, "In onDestroy");
    }

    private void checkForPendingUploads() {
//        new android.os.Handler().postDelayed(
//            new Runnable() {
//                public void run() {
//                    Log.i(TAG, "Timer ending");
//                    stopSelf();
//                }
//            },
//            30000);

        List<Response> responseList = Response.getFinishedResponses();
        Log.i("Upload", "Waiting responses: " + responseList.size());
        if (responseList.size() > 0) {
            //Pop one off the stack and upload it
            if(NetworkChangeReceiver.isOnline(this)) {//End upload process if known to be offline
                uploadingResponse = responseList.get(0);
                upload(getApplicationContext(), uploadingResponse);
                return;
            }
        }

        stopSelf();
    }

    /**
     * Do our work in onCreate - Turns this in to a Singleton.
     * All new calls whilst running go to 'onStartCommand'
     * Stopped with a call to stopSelf()
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        uploadReceiver.register(this);
        checkForPendingUploads();
    }

    private void redoOrNotify() {
        Log.d(TAG, "redoOrNotify");
        if (Response.getFinishedResponses().size() > 0) {
            checkForPendingUploads();
        } else if (completedUploads > 0) {
            //Notify the user how many uploads have completed and all done.
            buildSuccessNotification();
        }
    }

    private void removeUploaded() {
        Log.d(TAG, "removeUploaded");
        completedUploads++;//Add to count
        uploadingResponse.deleteFull();
    }

    private void buildSuccessNotification() {
        Log.d(TAG, "buildSuccessNotification");

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.status_logo)
                        .setContentTitle("" + completedUploads + getString(R.string.upload_successful))
                        .setContentText(getString(R.string.upload_thanks));

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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Start a new upload
     *
     * @param context Context used to start service
     */
    private void upload(Context context, Response response) {
        Log.d(TAG, "Uploading");
        final MultipartUploadRequest request = new MultipartUploadRequest(context,
                response.getId() + "",//Long used to keep track of db
                response.questionnaire.getUploadUrl());

        //Add files for upload
        try {
            String filename = response.photo.substring(response.photo.indexOf(":") + 1);
            request.addFileToUpload(filename, "photo", response.photo, "image/jpeg");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Add all data for upload
        List<SectionResponse> responseList = response.getSectionResponses();
        for (SectionResponse sectionResponse : responseList) {
            List<QuestionResponse> questionResponses = sectionResponse.getQuestionResponses();
            for (QuestionResponse questionResponse : questionResponses) {
                if(questionResponse.rData != null) {//Don't upload empty data
                    request.addParameter("data[" + sectionResponse.getSectionId() + "]" + "[" + questionResponse.question.getQuestionId() + "]", questionResponse.rData);
                }
            }
        }

        /**
         * Response object
        public Long timestamp;//Date & Time to be completed
        public Double lat;//Date & Time to be completed
        public Double lng;//Date & Time to be completed
        public Float locAcc;//Location accuracy
        public Questionnaire questionnaire;
        public String photo;//File address to photo
        public Boolean finished;
        private int id;
         */

        try {
            request.addParameter("timestamp", "" + response.getDateCompleted().getTimeInMillis());
            request.addParameter("uuid", response.uuid);
            request.addParameter("questionnaire", response.questionnaire.getServerId());

            request.addParameter("lat", response.lat.toString());
            request.addParameter("lng", response.lng.toString());
            request.addParameter("locAcc", response.locAcc.toString());
        } catch(Exception e){
            LCLog.e(TAG, "Failed to add params to upload", e);
        }

        try {
            request.startUpload(); //Start upload service and display the notification
        } catch (Exception e) {
            LCLog.e("AndroidUploadService", e.getLocalizedMessage(), e); //You will end up here only if you pass an incomplete UploadRequest
        }
    }
}