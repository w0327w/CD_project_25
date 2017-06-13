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
import android.database.Cursor;
import android.graphics.Bitmap;
import android.icu.text.MessageFormat;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.util.Date;
import java.text.DateFormatSymbols;
import java.text.DateFormat;




import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class MemoryActivity extends AppCompatActivity {

    Uri photoUri;
    String dish;
    int carbohydrate=0;
    int calorie=0;
    int  protein=0;
    int fat=0;

    DBManager2 dManager;

    ArrayList<String> data = new ArrayList();

    ListView list;
    ArrayAdapter<String> adapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory);
        list=(ListView) findViewById(R.id.listview);
        dManager= new DBManager2(this, "myMember2.db", null, 3);
        long now = System.currentTimeMillis();
        java.text.SimpleDateFormat st = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());
        Date date = new Date(now);

        String str = st.format(date);

        Intent intent = getIntent();



        if(!intent.getStringExtra("uri").equals("0"))
        {

            photoUri = Uri.parse(intent.getStringExtra("uri"));
            dish=intent.getStringExtra("mainDish");
            calorie=intent.getIntExtra("cal",0);
            carbohydrate= intent.getIntExtra("car", 0);

            protein=intent.getIntExtra("protein",0);
            fat=intent.getIntExtra("fat",0);

            Log.d(TAG, "데이터 베이스 입력 전 : " + photoUri.toString());

            dManager.input(photoUri.toString(), str, intent.getStringExtra("mainDish") , intent.getIntExtra("cal", 0), intent.getIntExtra("car",0), intent.getIntExtra("protein",0),intent.getIntExtra("fat",0));

        }

            dManager.moveToList(data);

        //dManager.moveToList(foodName);

        adapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, data);


        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {

                Cursor cursor = dManager.search((String)parent.getItemAtPosition(position));

                Intent intent2;
                intent2 = new Intent(getApplicationContext(), DetailActivity.class);
             //   Log.d(TAG,"인텐트 값 넣기전 : "+  cursor.getString(1));
                intent2.putExtra("uri", cursor.getString(1));
                intent2.putExtra("date", cursor.getString(2));
                intent2.putExtra("dish", cursor.getString(3));
                intent2.putExtra("cal", cursor.getInt(4));
                intent2.putExtra("car", cursor.getInt(5));
                intent2.putExtra("protein", cursor.getInt(6));
                intent2.putExtra("fat",cursor.getInt(7));

                startActivity(intent2);

            }
        });



    }


}
