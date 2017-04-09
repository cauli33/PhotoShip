package com.example.emmaveillat.finalproject;

import android.graphics.Bitmap;
import android.widget.Toast;


public class BitmapList {
    public int current;
    private int maxsize = 30;
    MyBitmap [] list = new MyBitmap[maxsize];
    int maxknown;
    private int deletable = 10;

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
        /*if (current == 0){
            Toast noprevious = Toast.makeText(getApplicationContext(), "Il n'y a pas de changement à annuler", Toast.LENGTH_LONG);
            noprevious.show();
        }*/
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
