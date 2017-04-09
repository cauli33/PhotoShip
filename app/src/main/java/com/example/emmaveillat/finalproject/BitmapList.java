package com.example.emmaveillat.finalproject;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.Toast;


public class BitmapList {
    public int current;
    private int maxsize = 30;
    MyBitmap [] list = new MyBitmap[maxsize];
    int maxknown;
    private int deletable = 10;
    int [] histogramChanges = new int[maxsize];
    public int[] histogram;
    public int[] valMap;
    public int validHistogram;

    public BitmapList(MyBitmap first){
        list[0] = first;
        current = 0;
        maxknown = 0;
        findHistogram();
    }

    public void findHistogram() {
        MyBitmap bmp = list[current];
        valMap = new int[bmp.width * bmp.height];
        histogram = new int[256];
        int pixel, r, g, b, lvl;
        for (int i = 0; i < bmp.width * bmp.height; i++) {   /* boucles sur tout le tableau de pixels (bitmap initial) */
            /* Je récupère les valeurs RGB du pixel dans le bitmap initial */
            pixel = bmp.pixels[i];
            r = Color.red(pixel);
            b = Color.blue(pixel);
            g = Color.green(pixel);
            /* Je fais la moyenne de ces 3 valeurs et donne au pixel du bitmap de sortie le niveau de gris associé */
            lvl = (int) (0.299F * r + 0.587F * g + 0.114F * b);
            valMap[i] = lvl;
            histogram[lvl]++;
        }
        validHistogram = 1;

    }

    private void freespace(){
        for (int i=0; i<maxsize - deletable; i++){
            list[i] = list[deletable+i];
            histogramChanges[i] = histogramChanges[deletable+i];
        }
        current = maxsize - deletable;
    }

    public MyBitmap getPrevious(){
        /*if (current == 0){
            Toast noprevious = Toast.makeText(getApplicationContext(), "Il n'y a pas de changement à annuler", Toast.LENGTH_LONG);
            noprevious.show();
        }*/
        if (histogramChanges[current] == 1){
            validHistogram = 0;
        }
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
