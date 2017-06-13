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

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class DetailActivity extends AppCompatActivity {

    Uri photoUri;
    String date;
    String dish;
    int carbohydrate=0;
    int calorie=0;
    int  protein=0;
    int fat=0;

    DBManager2 dManager;

    ArrayList<String> data = new ArrayList();

    ListView list;
    ArrayAdapter<String> adapter ;

    ImageView imgView;
    TextView textView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        imgView = (ImageView)findViewById(R.id.imageView3);
        textView = (TextView)findViewById(R.id.textView2);
        String print;
        Intent intent = getIntent();
        photoUri = Uri.parse(intent.getStringExtra("uri"));
        date = intent.getStringExtra("date");
        dish = intent.getStringExtra("dish");
        calorie=intent.getIntExtra("cal",0);
         carbohydrate=intent.getIntExtra("car",0);
        protein=intent.getIntExtra("protein",0);
        fat=intent.getIntExtra("fat",0);

        print =dish + "\ncalorie : " +Integer.toString(calorie)+"\n"+"carbohtdrate : " +  Integer.toString(carbohydrate)+"\n"+"protein : " + Integer.toString(protein)+"\n"+"fat : " +Integer.toString(fat);
        textView.setText(print);

        /*
        Log.d(TAG, photoUri.toString());
        Log.d(TAG, date.toString());
        Log.d(TAG, dish.toString());
*/


        try {

            Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
            Log.d(TAG, photoUri.toString());
            imgView.setImageBitmap(bm);

        } catch (FileNotFoundException e) {

            // TODO Auto-generated catch block

            e.printStackTrace();
        } catch (IOException e) {

            // TODO Auto-generated catch block

            e.printStackTrace();

        }





    }


}
