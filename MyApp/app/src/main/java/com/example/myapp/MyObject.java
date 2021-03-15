package com.example.myapp;

import android.icu.text.DateFormat;
import android.icu.util.Calendar;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Date;

@IgnoreExtraProperties
public class MyObject {
    @Exclude
    public String key;

    public String name;
    public MyLocation location;
    public Date end;

    public MyObject()
    {

    }


    public MyObject(String name, double latitude, double longitude,Date end)
    {
        this.name=name;
        this.location= new MyLocation(latitude,longitude);
        this.end=new Date();
        this.end=end;

    }
}
