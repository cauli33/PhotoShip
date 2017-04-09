package com.example.emmaveillat.finalproject;

import android.graphics.Bitmap;
import android.graphics.Color;

public class MyBitmap {
    public Bitmap bmp;
    public int width;
    public int height;
    private int[] pixels;
    public int[] histogram;
    public int[] histogramMap;

    public MyBitmap(Bitmap bitmap, int[] histo, int[] map){
        bmp = bitmap;
        width = bmp.getWidth();
        height = bmp.getHeight();
        pixels = new int[width*height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        if (histo == null){
            findHistogram();
        }
        else{
            histogram = histo;
            histogramMap = map;
        }
    }

    public Bitmap getBitmap(){
        return bmp;
    }

    private void findHistogram(){
        histogramMap = new int[width*height];
        histogram = new int[255];
        int pixel, r, g, b, lvl;
        for (int i = 0; i < width * height; i++) {   /* boucles sur tout le tableau de pixels (bitmap initial) */
            /* Je récupère les valeurs RGB du pixel dans le bitmap initial */
            pixel = pixels[i];
            r = Color.red(pixel);
            b = Color.blue(pixel);
            g = Color.green(pixel);
            /* Je fais la moyenne de ces 3 valeurs et donne au pixel du bitmap de sortie le niveau de gris associé */
            lvl = (int) (0.299F*r + 0.587F*g + 0.114F*b);
            histogramMap[i] = lvl;
            histogram[lvl]++;
        }
    }

    public MyBitmap toGray(){
        int[] pixelsGray = new int[height * width];
        Bitmap bmpGray = bmp.copy(Bitmap.Config.ARGB_8888, true); /* Je copie la bitmap en entrée (ce sera la bitmap initial) et la fait modifiable */
        int lvl;
        for (int i = 0; i < width * height; i++) {   /* boucles sur tout le tableau de pixels (bitmap initial) */
            lvl = histogramMap[i];
            pixelsGray[i] = Color.rgb(lvl,lvl,lvl);
        }
        bmpGray.setPixels(pixelsGray, 0, width, 0, 0, width, height);
        MyBitmap gray = new MyBitmap(bmpGray, histogram, histogramMap);
        return gray;
    }

    public MyBitmap sepia(){
        int r, g, b, pixel, lvl;
        int[] pixelsSepia = new int[height * width];
        int depth = 20;
        //Applies the mask on the bitmap depending on its levels of red, blue and green
        for (int i = 0; i < width * height; i++) {   /* boucles sur tout le tableau de pixels (bitmap initial) */
            /* Je récupère les valeurs RGB du pixel dans le bitmap initial */
            pixel = pixels[i];
            r = Color.red(pixel);
            b = Color.blue(pixel);
            g = Color.green(pixel);
            /* Je fais la moyenne de ces 3 valeurs et donne au pixel du bitmap de sortie le niveau de gris associé */
            lvl = (int) (0.299F*r + 0.587F*g + 0.114F*b);
            r = g = b = lvl;
            r = r + (depth * 2);
            g = g + depth;
            if(r > 255) {
                r = 255;
            }
            if(g > 255) {
                g = 255;
            }
            pixelsSepia[i] = Color.rgb(r, g, b);
        }
        Bitmap bmpSepia = bmp.copy(Bitmap.Config.ARGB_8888, true);
        bmpSepia.setPixels(pixelsSepia, 0, width, 0, 0, width, height);
        MyBitmap sepia = new MyBitmap(bmpSepia);
        return sepia;
    }
}
