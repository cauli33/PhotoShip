package com.example.emmaveillat.finalproject;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.Toast;


public class BitmapList {
    public int current;
    private int maxsize = 64;
    MyBitmap [] list = new MyBitmap[maxsize];
    public int maxknown;
    private int deletable = 16;

    public BitmapList(MyBitmap first){
        list[0] = first;
        current = 0;
        maxknown = 0;
    }

    private void freespace(){
        for (int i=0; i<maxsize - deletable; i++){
            list[i] = list[deletable+i];

        }
        current = maxsize - deletable;
    }

    public MyBitmap getPrevious(){
        current--;
        return list[current];
    }

    public MyBitmap getCurrent(){
        return list[current];
    }

    public MyBitmap getNext(){
        current++;
        return list[current];
    }

    public void setNext(MyBitmap bmp){
        if (current == maxsize){
            freespace();
        }
        else{
            current++;
            list[current] = bmp;
            maxknown = current;
        }
    }
}
