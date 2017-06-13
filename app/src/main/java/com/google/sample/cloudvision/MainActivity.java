/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.sample.cloudvision;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String CLOUD_VISION_API_KEY = "AIzaSyBSsLrjrfutiLbylfcmVbSBdsaVgNwtbYA";
    public static final String FILE_NAME = "temp.jpg";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";

    String mCurrentPhotoPath;

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int GALLERY_PERMISSIONS_REQUEST = 0;
    private static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;
    static final int REQUEST_IMAGE_CROP = 4;

    private TextView mImageDetails;
    private ImageView mMainImage;
    Uri photoUri, albumUri= null;


    String[] Array = new String[10];
    Float term;
    int num=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        Log.d(TAG, "1");
        for(int num=0; num<10; num++)
            Array[num]="curry";
        Intent intent = new Intent(this, FoodActivity.class);
        intent.putExtra("Array",Array);
        this.startActivity(intent);
*/
        isCheck();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder
                        .setMessage(R.string.dialog_select_prompt)
                        .setPositiveButton(R.string.dialog_select_gallery, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startGalleryChooser();
                            }
                        })
                        .setNegativeButton(R.string.dialog_select_camera, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startCamera();
                            }
                        });
                builder.create().show();
            }
        });

        mImageDetails = (TextView) findViewById(R.id.image_details);
        mMainImage = (ImageView) findViewById(R.id.main_image);



    }


    public void onButtonClicked(View v) {
        Intent intent = new Intent(this, MemoryActivity.class);
        intent.putExtra("uri", "0");
        startActivity(intent);
    }

    public boolean isCheck() {
    final int PERMISSION = 1;

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

        ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                },PERMISSION);
    }

        return false;
}
    public void startGalleryChooser() {
        if (PermissionUtils.requestPermission(this, GALLERY_PERMISSIONS_REQUEST, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select a photo"),
                    GALLERY_IMAGE_REQUEST);
        }
    }

    public void startCamera() {
        if (PermissionUtils.requestPermission(
                this,
                CAMERA_PERMISSIONS_REQUEST,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(intent.resolveActivity(getPackageManager())!=null)
            {
                File photoFile = null;
                try {
                    photoFile = createImageFile();

                } catch (IOException ex) {
                    // Toast.makeText(getApplicationContext(), "createImageFile Failed", Toast.LENGTH_LONG).show();
                }
                if(photoFile!=null) {
                    photoUri = Uri.fromFile(photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
                }
            }


        }
    }

    private File createImageFile() throws IOException
    {
        String imageFIleName = "tmp_"+ String.valueOf(System.currentTimeMillis());
        String albumName = "myapp/";

        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), albumName);


        File file = File.createTempFile(imageFIleName, ".jpg", storageDir);

        Toast.makeText(getApplicationContext(), Environment.getExternalStorageDirectory().toString(), Toast.LENGTH_LONG).show();
        mCurrentPhotoPath = file.getAbsolutePath();
        return file;
    }

    public File getCameraFile() {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(dir, FILE_NAME);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            File albumFile = null;
            try{
                albumFile = createImageFile();

            }catch(IOException e){
                e.printStackTrace();
            }
            if(albumFile != null){
                albumUri = Uri.fromFile(albumFile);
            }

            photoUri = data.getData();
            cropImage(/*Uri.parse(data.getStringExtra("output"))*/ data.getData());
        } else if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
           // Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            cropImage(null);
            //uploadImage(photoUri);
        }else if(requestCode ==  REQUEST_IMAGE_CROP && resultCode == RESULT_OK){
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(data.getData());
                this.sendBroadcast(mediaScanIntent);

                uploadImage(data.getData());



        }   }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, CAMERA_PERMISSIONS_REQUEST, grantResults)) {
                    startCamera();
                }
                break;
            case GALLERY_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, GALLERY_PERMISSIONS_REQUEST, grantResults)) {
                    startGalleryChooser();
                }
                break;
        }
    }

    public void uploadImage(Uri uri) {
        Toast.makeText(getApplicationContext(), "결과를 기다리는중...", Toast.LENGTH_LONG).show();
        if (uri != null) {
            try {
                // scale the image to save on bandwidth
                photoUri = uri;
                Bitmap bitmap =
                        scaleBitmapDown(
                                MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
                                1200);

                callCloudVision(bitmap);
                //Toast.makeText(getApplicationContext(), Array[0], Toast.LENGTH_LONG).show();
              //  mMainImage.setImageBitmap(bitmap);

            } catch (IOException e) {
                Log.d(TAG, "Image picking failed because " + e.getMessage());
                Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "Image picker gave us a null image.");
            Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
        }
    }

    private void callCloudVision(final Bitmap bitmap) throws IOException {
        // Switch text to loading
        mImageDetails.setText(R.string.loading_message);

        // Do the real work in an async task, because we need to use the network anyway
        new AsyncTask<Object, Void, String>() {
            @Override
            protected String doInBackground(Object... params) {
                try {
                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                    VisionRequestInitializer requestInitializer =
                            new VisionRequestInitializer(CLOUD_VISION_API_KEY) {
                                /**
                                 * We override this so we can inject important identifying fields into the HTTP
                                 * headers. This enables use of a restricted cloud platform API key.
                                 */
                                @Override
                                protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                                        throws IOException {
                                    super.initializeVisionRequest(visionRequest);

                                    String packageName = getPackageName();
                                    visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                                    String sig = PackageManagerUtils.getSignature(getPackageManager(), packageName);

                                    visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
                                }
                            };

                    Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
                    builder.setVisionRequestInitializer(requestInitializer);

                    Vision vision = builder.build();

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                            new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
                        AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

                        // Add the image
                        Image base64EncodedImage = new Image();
                        // Convert the bitmap to a JPEG
                        // Just in case it's a format that Android understands but Cloud Vision
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                        byte[] imageBytes = byteArrayOutputStream.toByteArray();

                        // Base64 encode the JPEG
                        base64EncodedImage.encodeContent(imageBytes);
                        annotateImageRequest.setImage(base64EncodedImage);

                        // add the features we want
                        annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                            Feature labelDetection = new Feature();
                            labelDetection.setType("LABEL_DETECTION");
                            labelDetection.setMaxResults(10);
                            add(labelDetection);
                        }});

                        // Add the list of one thing to the request
                        add(annotateImageRequest);
                    }});

                    Vision.Images.Annotate annotateRequest =
                            vision.images().annotate(batchAnnotateImagesRequest);
                    // Due to a bug: requests to Vision API containing large images fail when GZipped.
                    annotateRequest.setDisableGZipContent(true);
                    Log.d(TAG, "created Cloud Vision request object, sending request");

                    BatchAnnotateImagesResponse response = annotateRequest.execute();

                    return convertResponseToString(response);



                } catch (GoogleJsonResponseException e) {
                    Log.d(TAG, "failed to make API request because " + e.getContent());
                } catch (IOException e) {
                    Log.d(TAG, "failed to make API request because of other IOException " +
                            e.getMessage());
                }
                return "Cloud Vision API request failed. Check logs for details.";
            }

            protected void onPostExecute(String result) {
                mImageDetails.setText(result);
            }
        }.execute();
    }

    public Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }



        private String convertResponseToString(BatchAnnotateImagesResponse response) {
            String message = "I found these things:\n\n";
            Intent intent = new Intent(this, FoodActivity.class);
            intent.putExtra("uri", photoUri.toString());

            List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();
            if (labels != null) {
                for (EntityAnnotation label : labels) {
                  // message += String.format(Locale.US, "%.3f: %s", label.getScore(), label.getDescription());
                   // message += "\n";
                    term = label.getScore();
                    Array[num]=label.getDescription();
                    num++;
                   //Toast.makeText(getApplicationContext(), Array[num]=label.getDescription(), Toast.LENGTH_LONG).show();
                   // Log.d(TAG,label.getDescription() );
                }

                for(;num<10;num++)
                {
                    Array[num]="0";
                }
                Log.d(TAG, Integer.toString(num));






        } else {
            message += "nothing";
        }
           // Intent intent = new Intent(this, FoodActivity.class);

            //for(int n=0; n<10; n++)
            /*
                Log.d(TAG, Array[0]);
                Log.d(TAG, Array[1]);
                Log.d(TAG, Array[2]);
            Log.d(TAG, Array[3]);
            Log.d(TAG, Array[4]);
            Log.d(TAG, Array[5]);
            Log.d(TAG, Array[6]);
            Log.d(TAG, Array[7]);
            Log.d(TAG, Array[8]);
            Log.d(TAG, Array[9]);
            */
            intent.putExtra("Array",Array);
            Log.d(TAG,"URI IS"+  photoUri.toString());



            this.startActivity(intent);
            this.finish();

        return message;
    }

    private void cropImage(Uri photoURI) {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");

        cropIntent.setDataAndType(photoUri, "image/*");
        cropIntent.putExtra("scale", true);

        if (photoURI != null) {
            cropIntent.putExtra("output", albumUri);
        } else
        {
            cropIntent.putExtra("output", photoUri);
        }

        startActivityForResult(cropIntent,  REQUEST_IMAGE_CROP );
    }
}
