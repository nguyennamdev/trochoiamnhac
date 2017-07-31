package com.wordpress.nguyenvannamdev.trochoiamnhac.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;



/**
 * Created by NAM COI on 2/25/2017.
 */

public class UserSQLiteHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "User_Info.db";
    public static final String TABLE_NAME = "tblUser";
    public static final String COL_1 = "User_Name";
    public static final String COL_2 = "High_Score";
    public static final String COL_3 = "User_Hint";

    public UserSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //create table
        sqLiteDatabase.execSQL("create table if not exists " + TABLE_NAME + " (" + COL_1 + " text primary key, "
                + COL_2 + " int, " + COL_3 + " int)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean insertData(User user) {
        Boolean result = true;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_1, user.getUser_name());
        cv.put(COL_2, user.getHigh_score());
        cv.put(COL_3, user.getHint());
        long r = db.insert(TABLE_NAME, null, cv);
        if (r == -1) {
            result = false;
        }
        return result;
    }

    public boolean updateData(User user){
        Boolean result = true;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_1, user.getUser_name());
        cv.put(COL_2, user.getHigh_score());
        cv.put(COL_3, user.getHint());
        String str = COL_1 + "=" + "'" + user.getUser_name()+"'";
        long r = db.update(TABLE_NAME,cv,str,null);
        if(r == -1){
            result = false;
        }
        return result;
    }


    public int countSize(){
        SQLiteDatabase db = this.getWritableDatabase();
        String SQLStr = "Select Count(*) From " + TABLE_NAME + ";";
        Cursor cursor = db.rawQuery(SQLStr,null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return  count;

    }

    public User getUserByName(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String SQLStr = "Select " + COL_1 + ", " + COL_2 + ", " + COL_3 + " from " + TABLE_NAME + " where " + COL_1 + "='" + name + "'";
        User user = new User();
        try {
            Cursor cursor = db.rawQuery(SQLStr, null);
            if (cursor.moveToFirst()) {
                user.setUser_name(cursor.getString(cursor.getColumnIndex(COL_1)));
                user.setHigh_score(cursor.getInt(cursor.getColumnIndex(COL_2)));
                user.setHint(cursor.getInt(cursor.getColumnIndex(COL_3)));
            }

            if(cursor.getCount() == 0){
                user = null;
                return user;
            }
            cursor.close();
            db.close();
            return user;
        } catch (SQLException ex) {
            Log.d("Khong co du lieu", "get all");
            return null;

        }

    }



}
