package com.google.sample.cloudvision;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by cx61 on 2017-06-06.
 */

public class DBManager extends SQLiteOpenHelper {

    public DBManager(Context context, String name, CursorFactory factory,
                     int version) {
        super(context, name, factory, version);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        //DB에 테이블 생성하기
        String sql = "CREATE TABLE member" + "(num INTEGER PRIMARY KEY AUTOINCREMENT," +
                " nameE text, nameK text ,calorie integer, Carbohydrate integer, protein integer, fat integer );";
        //sql문 실행하기
        db.execSQL(sql);
        Log.d(TAG, "테이블 생성");
    }
    SQLiteDatabase rwDB;

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS member;");
        //새로 생성될 수 있도록 onCreate() 메소드를 생성한다.
        onCreate(db);
    }

    protected void input(String nameE, String nameK, int cal, int car, int pro, int fat){
        rwDB = getWritableDatabase();
        rwDB.execSQL("INSERT INTO member VALUES(NULL,'" + nameE +"','" + nameK +"','" + cal +"','" + car +"','" + pro +"','"+ fat + "');");
       // Log.d(TAG, "값 입력");
    }

    protected Cursor search(String data)
    {
      //  Log.d(TAG, "호출");
        rwDB = getReadableDatabase();
        Cursor cursor = rwDB.rawQuery("SELECT * FROM member", null);
        //cursor.moveToFirst();


        if(cursor==null)
        {
           // Log.d(TAG, "뭔가 잘못됨");
        }

        if(cursor.getCount()>0) {

            while (cursor.moveToNext()){
                Log.d(TAG,cursor.getString(1));
                if (data.equals(cursor.getString(1))){
                    Log.d(TAG, "진입");
                    return cursor;
                }
            }
        }
        rwDB.close();
        return null;
    }

    protected Cursor searchK(String data)
    {
      //  Log.d(TAG, "호출");
        rwDB = getReadableDatabase();
        Cursor cursor = rwDB.rawQuery("SELECT * FROM member", null);
        //cursor.moveToFirst();


        if(cursor==null)
        {
        //    Log.d(TAG, "뭔가 잘못됨");
        }

        if(cursor.getCount()>0) {

            while (cursor.moveToNext()){
                Log.d(TAG,cursor.getString(2));
                if (data.equals(cursor.getString(2))){
                 //   Log.d(TAG, "진입");
                    return cursor;
                }
            }
        }
        rwDB.close();
        return null;
    }

    protected void moveToList(List data) {
       // Log.d(TAG, "호출");
        rwDB = getReadableDatabase();
        Cursor cursor = rwDB.rawQuery("SELECT * FROM member", null);

        while (cursor.moveToNext()) {

            data.add(cursor.getString(2));
        }

    }


}
