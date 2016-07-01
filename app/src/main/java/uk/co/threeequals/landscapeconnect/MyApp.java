package uk.co.threeequals.landscapeconnect;

import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import net.gotev.uploadservice.UploadService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Base class for the App which initialises all of the libraries
 */
public class MyApp extends Application{//extends com.activeandroid.app.Application
    public static final String SEENINTRO = "firstRun";
    public static final String SHAREDPREFERENCES = "lc";


    @Override
    public void onCreate() {
        super.onCreate();

        Configuration.Builder config = new Configuration.Builder(this);
        config.addModelClasses(Questionnaire.class, Section.class, Question.class, Choice.class, QuestionResponse.class, Response.class);
        ActiveAndroid.initialize(config.create());
        //<!--android:value="uk.co.threeequals.landscapeconnect.Questionnaire, uk.co.threeequals.landscapeconnect.Section, uk.co.threeequals.landscapeconnect.Question, uk.co.threeequals.landscapeconnect.Choice, uk.co.threeequals.landscapeconnect.QuestionResponse, uk.co.threeequals.landscapeconnect.Response"/>-->


        Iconify.with(new FontAwesomeModule());

        // setup the broadcast action namespace string which will
        // be used to notify upload status.
        // Gradle automatically generates proper variable as below.
        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;

        //Start the upload service...
        Intent serviceIntent = new Intent(this, LSUploadService.class);
        this.startService(serviceIntent);
    }

    /**
     * Create thumbnail file handle ready for data
     * @param response Response object to create thumb for
     * @return File handle of created file
     * @throws IOException
     */
    private static File createThumbImageFile(Response response) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.UK).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_s";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        response.thumb = "file:" + image.getAbsolutePath();
        response.save();
        return image;
    }


    public static File createImageFile(Response response) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.UK).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        response.photo = "file:" + image.getAbsolutePath();
        response.save();
        return image;
    }

    /**
     * Resize a thumbnail to 100x100 pixels
     */
    public static void resizeToThumb(Response response) {
        // Get the dimensions of the View
        int targetW = 100;
        int targetH = 100;

        String filename = response.photo.substring(response.photo.indexOf(":") + 1);

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filename, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        Log.i("SectionsFragment", "PhotoW:" + photoW + ", PhotoH:" + photoH);

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized correctly
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap imageBitmap = BitmapFactory.decodeFile(filename, bmOptions);

        if(imageBitmap == null){
            Log.i("SectionsFragment", "BitMap Null!");
            return;
        }

        Log.i("SectionsFragment", "Writing to size H: " + imageBitmap.getHeight() + ", W:" + imageBitmap.getWidth());

        //Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);
        byte[] bitmapData = bos.toByteArray();

        File thumbFile = null;
        try {
            thumbFile = MyApp.createThumbImageFile(response);
        } catch (IOException ex) {
            // Error occurred while creating the File
            Log.e("Log", ex.getLocalizedMessage());
        }

        // Continue only if the File was successfully created
        if (thumbFile != null) {
            //write the bytes in file
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(thumbFile);
                fos.write(bitmapData);
                fos.flush();
                fos.close();
                Log.i("SectionsFragment", "Successfully saved resized thumbnail");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

