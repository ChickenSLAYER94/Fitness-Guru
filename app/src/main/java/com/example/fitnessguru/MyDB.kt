package com.example.fitnessguru

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDB(context:Context): SQLiteOpenHelper(context, "USERDB", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        //create table for adding information to the database
        //here for database I choose SQlite for storing and retrieving data
        db?.execSQL("CREATE TABLE STEPCOUNTER(USERID INTEGER PRIMARY KEY AUTOINCREMENT, UserSetGoal TEXT, CalBurned TEXT, DistanceTravelled TEXT, Progression TEXT, RemainingSteplimit TEXT)")
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
    }
}