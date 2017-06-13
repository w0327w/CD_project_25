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

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.text.Editable;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FoodActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    String [] Array;

    String mainDish="";
    DBManager dManager;
    String textView;

    ArrayList<String> foodName = new ArrayList();


    TextView tv;
    ListView list;
    ArrayAdapter<String> adapter ;
    EditText e;

    Uri photoUri;
    int carbohydrate=0;
    int calorie=0;
    int  protein=0;
    int fat=0;
    Intent intent;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intent = getIntent();

        photoUri = Uri.parse(intent.getStringExtra("uri"));
        dManager= new DBManager(this, "myMember.db", null, 2);



            Array = intent.getStringArrayExtra("Array");

        setContentView(R.layout.activity_food);
        tv = (TextView)findViewById(R.id.tv);
        list = (ListView)findViewById(R.id.lv);




        adapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, foodName);



        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {


                Log.d(TAG, (String)parent.getItemAtPosition(position));
                Cursor cursor = dManager.searchK((String)parent.getItemAtPosition(position));
                calorie+=cursor.getInt(3);
                carbohydrate+=cursor.getInt(4);

                protein+=cursor.getInt(5);
                fat+=cursor.getInt(6);

                mainDish += cursor.getString(2) +" -> cal : " + Integer.toString(cursor.getInt(3))
                        +",  car : " + Integer.toString(cursor.getInt(4))
                        +",  pro : " + Integer.toString(cursor.getInt(5))
                        +",  fat : " + Integer.toString(cursor.getInt(6)) + "\n";
                tv.setText(mainDish);
                /*
                HashMap<String, String> obj = (HashMap<String, String>) parent.getItemAtPosition(position);

                String result = obj.get("item2");
                Toast.makeText(getApplicationContext(),result, Toast.LENGTH_LONG).show();

                Intent myIntent3;
                myIntent3 = new Intent(getApplicationContext(), destination.class);
                myIntent3.putExtra("name", obj.get("item1"));
                myIntent3.putExtra("address", obj.get("item2"));
                startActivity(myIntent3);
                */

            }
        });
        e = (EditText)findViewById(R.id.editText);

        e.addTextChangedListener(new TextWatcher(){
            @Override //입력하기 전에 호출되는 API
             public void beforeTextChanged(CharSequence s, int start, int count, int after)
             {

             }

             @Override
            // EditText에 변화가 있을 때
            public void onTextChanged(CharSequence s, int start, int before, int count)
             {

             }

             @Override
            //입력이 끝났을 때
            public void afterTextChanged(Editable s) {
                 String search = s.toString();
                 if(search.length()>0)
                 {
                     list.setFilterText(search);
                 }
                 else {list.clearTextFilter();}

             }



        });








        dManager.moveToList(foodName);
        //Log.d(TAG, "리스트 내용" + foodName.get(1));






        Log.d(TAG, photoUri.toString());
/*
        for(int num=0; num<10; num++)
            Log.d(TAG, Array[num]);
*/



        searchFood();




    }

    public void onButtonClicked(View v) {
        Intent intent = new Intent(this, MemoryActivity.class);

       // Log.d(TAG, "음식명단 추가후 리스트 클릭시" + photoUri.toString() );

        intent.putExtra("uri", photoUri.toString());
        intent.putExtra("car", carbohydrate);
        intent.putExtra("cal", calorie);
        intent.putExtra("protein", protein);
        intent.putExtra("fat", fat);
        intent.putExtra("mainDish", mainDish);

        this.startActivity(intent);


    }











    public Cursor search(String data)
    {
        return dManager.search(data);
    }

    public void searchFood()
    {

        final String items[]=new String[3];
        items[0]=null;
        items[1]=null;
        items[2]=null;
        int count=0;
        final Cursor termCu[] = new Cursor[3];



        for(int num = 0;num<10; num++ ) {
          //  Log.d(TAG, Array[num]);
            termCu[count] = dManager.search(Array[num]);



            if(termCu[count]!=null) {

                items[count] = termCu[count].getString(2);
                Log.d(TAG, "추가");
                count++;
                if(count==3) {

                    break;
                }

            }else{
                Log.d(TAG, "x");

            }

        }

        if(items[0]!=null) {
            Log.d(TAG, "출력진입");
            final int[] selectedIndex = {0};
            int c=0;
            for(int num=0; num<3; num++)
            {
                if(items[num]!=null)
                    c++;
            }
            String real[]= new String[c];
            for(int num=0; num<c; num++)
            {
                real[num]= items[num];
            }

            AlertDialog.Builder dialog = new AlertDialog.Builder(FoodActivity.this);
            dialog.setTitle("맞는 음식 선택").setSingleChoiceItems(real, 0, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    selectedIndex[0] = which;
                }
            })
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d(TAG, "ITEM : " + items[selectedIndex[0]]);

                            calorie = termCu[selectedIndex[0]].getInt(3);
                            carbohydrate = termCu[selectedIndex[0]].getInt(4);

                            protein = termCu[selectedIndex[0]].getInt(5);
                            fat = termCu[selectedIndex[0]].getInt(6);

                            mainDish = termCu[selectedIndex[0]].getString(2) + " -> cal : " + Integer.toString(calorie)
                                    + ",  car : " + Integer.toString(carbohydrate)
                                    + ",  pro : " + Integer.toString(protein)
                                    + ",  fat : " + Integer.toString(fat) + "\n";
                            tv.setText(mainDish);
                        }
                    }).create().show();
        }
        else
        {
            Toast.makeText(getApplicationContext(), "탐색 불가, 수동 선택 요망", Toast.LENGTH_LONG).show();
        }













    }



}

