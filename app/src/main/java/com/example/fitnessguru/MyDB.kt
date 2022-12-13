package com.example.fitnessguru

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.Date

class MyDB(context:Context): SQLiteOpenHelper(context, "USERDB", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        //create table for adding information to the database
        //here for database I choose SQlite for storing and retrieving data
        db?.execSQL("CREATE TABLE STEPCOUNTER(USERID INTEGER PRIMARY KEY AUTOINCREMENT, UserSetGoal TEXT, CalBurned TEXT, DistanceTravelled TEXT, Progression TEXT, RemainingSteplimit TEXT, Date TEXT)")
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
    }

    @SuppressLint("Range")
    fun getDetailsAccordingToDate() : ArrayList<fitnessData> {
        var list: ArrayList<fitnessData> = ArrayList()
        var USERID: Int
        var UserSetGoal: String
        var CalBurned: String
        var DistanceTravelled:String
        var Progression: String
        var RemainingSteplimit: String
        var Date: String

        val db = this.readableDatabase
        val cursor: Cursor? = db.rawQuery("SELECT DistanceTravelled, UserSetGoal, Date, MAX(USERID) FROM STEPCOUNTER GROUP by Date",null)
        if (cursor != null) {
            if(cursor.moveToFirst()){
                do {
                    USERID = cursor.getInt(cursor.getColumnIndex("USERID"))
                    UserSetGoal = cursor.getString(cursor.getColumnIndex("UserSetGoal"))
                    CalBurned = cursor.getString(cursor.getColumnIndex("CalBurned"))
                    DistanceTravelled = cursor.getString(cursor.getColumnIndex("DistanceTravelled"))
                    Progression = cursor.getString(cursor.getColumnIndex("Progression"))
                    RemainingSteplimit = cursor.getString(cursor.getColumnIndex("RemainingSteplimit"))
                    Date = cursor.getString(cursor.getColumnIndex("Date"))



                    val fitness_data = fitnessData(USERID, UserSetGoal, CalBurned,DistanceTravelled, Progression, RemainingSteplimit, Date)

                    list.add(fitness_data)

                }while (cursor.moveToNext())
            }
        }

        return list
    }
}

