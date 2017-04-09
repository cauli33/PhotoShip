package com.example.emmaveillat.finalproject;

import android.graphics.Bitmap;
import android.graphics.Color;

public class MyBitmap {
    public Bitmap bmp;
    public int width;
    public int height;
    public int[] pixels;

    public MyBitmap(Bitmap bitmap){
        bmp = bitmap;
        width = bmp.getWidth();
        height = bmp.getHeight();
        pixels = new int[width*height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
    }

    public MyBitmap toGray(int[] valMap){
        int[] pixelsGray = new int[height * width];
        Bitmap bmpGray = bmp.copy(Bitmap.Config.ARGB_8888, true); /* Je copie la bitmap en entrée (ce sera la bitmap initial) et la fait modifiable */
        int lvl;
        for (int i = 0; i < width * height; i++) {   /* boucles sur tout le tableau de pixels (bitmap initial) */
            lvl = valMap[i];
            pixelsGray[i] = Color.rgb(lvl,lvl,lvl);
        }
        bmpGray.setPixels(pixelsGray, 0, width, 0, 0, width, height);
        MyBitmap gray = new MyBitmap(bmpGray);
        return gray;
    }

    public MyBitmap sepia(int[] valMap){
        int r, g, lvl;
        int[] pixelsSepia = new int[height * width];
        int depth = 20;
        //Applies the mask on the bitmap depending on its levels of red, blue and green
        for (int i = 0; i < width * height; i++) {   /* boucles sur tout le tableau de pixels (bitmap initial) */
            /* Je récupère les valeurs RGB du pixel dans le bitmap initial */
            lvl = valMap[i];
            r = lvl + (depth *2);
            g = lvl + depth;
            if(r > 255) {
                r = 255;
            }
            if(g > 255) {
                g = 255;
            }
            pixelsSepia[i] = Color.rgb(r, g, lvl);
        }
        Bitmap bmpSepia = bmp.copy(Bitmap.Config.ARGB_8888, true);
        bmpSepia.setPixels(pixelsSepia, 0, width, 0, 0, width, height);
        MyBitmap sepia = new MyBitmap(bmpSepia);
        return sepia;
    }

    public MyBitmap histogramEqualization(BitmapList memory){
        if (memory.validHistogram == 0){
            memory.findHistogram();
        }
        int[] pixelsEqualized = new int[height * width];
        int[] cumulHistogram = memory.histogram;
        int[] valMap = memory.valMap;
        for (int i = 1; i < 256; i++) {
            cumulHistogram[i] += cumulHistogram[i - 1];
        }
        float[] pixelHSV = new float[3];
        int pixel;
        float newValue;
        for (int i = 0; i < width * height; i++) {
            pixel = pixels[i];
            newValue = (float) (cumulHistogram[valMap[i]]) / (width * height);
            Color.colorToHSV(pixel, pixelHSV);
            pixelHSV[2] = newValue;
            pixelsEqualized[i] = Color.HSVToColor(pixelHSV);
        }
        Bitmap bmpEqualized = bmp.copy(Bitmap.Config.ARGB_8888, true);
        bmpEqualized.setPixels(pixelsEqualized, 0, width, 0, 0, width, height);
        MyBitmap equalized = new MyBitmap(bmpEqualized);
        return equalized;
    }

    public MyBitmap dynamicExtension(BitmapList memory){
        if (memory.validHistogram == 0){
            memory.findHistogram();
        }
        int[] pixelsExtension = new int[height * width];
        int[] histogram = memory.histogram;
        int[] valMap = memory.valMap;
        int k = 0;
        while (histogram[k] == 0) {
            k++;
        }
        int min = k;

        k = 255;
        while (histogram[k] == 0) {
            k--;
        }
        int max = k;
        float[] pixelHSV = new float[3];
        int pixel;
        float newValue;
        for (int i = 0; i < width * height; i++) {
            pixel = pixels[i];
            newValue = (float)(valMap[i] - min)/(max - min);
            Color.colorToHSV(pixel, pixelHSV);
            pixelHSV[2] = newValue;
            pixelsExtension[i] = Color.HSVToColor(pixelHSV);
        }
        Bitmap bmpExtension = bmp.copy(Bitmap.Config.ARGB_8888, true);
        bmpExtension.setPixels(pixelsExtension, 0, width, 0, 0, width, height);
        MyBitmap extension = new MyBitmap(bmpExtension);
        return extension;
    }
}
