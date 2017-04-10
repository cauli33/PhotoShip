package com.example.emmaveillat.finalproject;

import android.graphics.Bitmap;
import android.graphics.Color;

public class MyBitmap {
    public Bitmap bmp;
    public int width;
    public int height;
    public int[] pixels;
    public int filter;

    public MyBitmap(Bitmap bitmap, int filt){
        bmp = bitmap;
        width = bmp.getWidth();
        height = bmp.getHeight();
        pixels = new int[width*height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        filter = filt;
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
        MyBitmap gray = new MyBitmap(bmpGray, 1);
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
        MyBitmap sepia = new MyBitmap(bmpSepia, 2);
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
        MyBitmap equalized = new MyBitmap(bmpEqualized, 3);
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
        MyBitmap extension = new MyBitmap(bmpExtension, 4);
        return extension;
    }

    public MyBitmap rotateLeft(){
        int[] pixelsLeft = new int[height * width];
        for (int y = 0; y < height; ++y) {
            for (int x = 0 ; x < width ; ++x) {
                pixelsLeft[( width - x - 1) * height + y] = pixels[y * width + x];
            }
        }
        Bitmap bmpLeft = Bitmap.createBitmap(pixelsLeft, height, width, Bitmap.Config.ARGB_8888);
        MyBitmap left = new MyBitmap(bmpLeft, filter);
        return left;
    }

    public MyBitmap rotateRight(){
        int[] pixelsRight = new int[height * width];
        for (int y = 0; y < height; ++y) {
            for (int x = 0 ; x < width ; ++x) {
                pixelsRight[(x+1)*height - y - 1] = pixels[y * width + x];
            }
        }
        Bitmap bmpRight = Bitmap.createBitmap(pixelsRight, height, width, Bitmap.Config.ARGB_8888);
        MyBitmap right = new MyBitmap(bmpRight, filter);
        return right;
    }

    public MyBitmap fliplr(){
        int[] pixelsFlip = new int[width * height];
        for (int y = 0; y < height; ++y) {
            for (int x = 0 ; x < width ; ++x) {
                pixelsFlip[(y+1) * width - x - 1] = pixels[y * width + x];
            }
        }
        Bitmap bmpFliplr = Bitmap.createBitmap(pixelsFlip, height, width, Bitmap.Config.ARGB_8888);
        MyBitmap fliplr = new MyBitmap(bmpFliplr, filter);
        return fliplr;
    }

    public MyBitmap flipud(){
        int[] pixelsFlip = new int[width * height];
        for (int y = 0; y < height; ++y) {
            for (int x = 0 ; x < width ; ++x) {
                pixelsFlip[(height - y - 1) * width + x] = pixels[y * width + x];
            }
        }
        Bitmap bmpFlipud = Bitmap.createBitmap(pixelsFlip, height, width, Bitmap.Config.ARGB_8888);
        MyBitmap flipud = new MyBitmap(bmpFlipud, filter);
        return flipud;
    }

    public MyBitmap lowerRes() {
        if (width * height > 1000000) {
            int newWidth, newHeight;
            float fact;
            if (width > height) {
                newWidth = 800;
                newHeight = newWidth * height / width;
                fact = width / 800F;
            } else {
                newHeight = 800;
                newWidth = newHeight * width / height;
                fact = height / 800F;
            }
            int[] pixelsLower = new int[newWidth * newHeight];
            int oldx, oldy;
            for (int y = 0; y < newHeight; y++) {
                for (int x = 0; x < newWidth; x++) {
                    oldx = (int) (x * fact);
                    oldy = (int) (y * fact);
                    pixelsLower[y * newWidth + x] = pixels[oldy * width + oldx];
                }
            }
            Bitmap bmpLower = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);
            bmpLower.setPixels(pixelsLower, 0, newWidth, 0, 0, newWidth, newHeight);
            MyBitmap lower = new MyBitmap(bmpLower, filter);
        }
        return this;
    }

    private MyBitmap convolutionBlur(int[][] mask, int factor, int filter) {
        int n = mask.length / 2;
        int[] pixelsConv = new int[height * width];
        int white = Color.rgb(0,0,0);
        int R, G, B, sumR, sumG, sumB, pixel;
        float coef_mask;
        //Bordures haut et bas en blanc
        for (int y = 0; y < n; y++) {
            for (int x = 0; x < width; x++) {
                pixelsConv[y * width + x] = white;
                pixelsConv[(height - y - 1) * width + x] = white;
            }
        }
        //Bordures gauche et droite en blanc
        for (int x = 0; x < n; x++) {
            for (int y = n; y < height - n; y++) {
                pixelsConv[y * width + x] = white;
                pixelsConv[(y + 1) * width - x - 1] = white;
            }
        }
        //Convolution
        for (int y = n; y < height - n; y++) {
            for (int x = n; x < width - n; x++) {
                sumR = 0;
                sumG = 0;
                sumB = 0;
                for (int j = -n; j <= n; j++) {
                    for (int i = -n; i <= n; i++) {
                        coef_mask = mask[j + n][i + n];
                        pixel = pixels[(y + j) * width + x + i];
                        //For every RGB componants, multiplies by convolution matrix coefficient
                        sumR += coef_mask * Color.red(pixel);
                        sumG += coef_mask * Color.green(pixel);
                        sumB += coef_mask * Color.blue(pixel);
                    }
                }
                R = sumR / factor;
                G = sumG / factor;
                B = sumB / factor;

                pixelsConv[y * width + x] = Color.rgb(R, G, B);
            }
        }
        Bitmap bmpConv = Bitmap.createBitmap(pixelsConv, width, height, Bitmap.Config.ARGB_8888);
        MyBitmap conv = new MyBitmap(bmpConv, filter);
        return conv;
    }

    public MyBitmap moyenne(int n) {
        //Creates a matrix full of ones and applicates convolution (and divides by the number of pixels used)
        int[][] mask = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                mask[i][j] = 1;
            }
        }
        return convolutionBlur(mask, n*n, 5);
    }

    public MyBitmap gauss(){
        //Creates gaussian matrix and applicates convolution
        int[][] mask ={{1,2,3,2,1},{2,6,8,6,2},{3,8,10,8,3},{2,6,8,6,2},{1,2,3,2,1}};
        return convolutionBlur(mask, 98, 6);
    }
    
    public MyBitmap sobel() {
        int R, G, B;
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        //applicates convolution with hx et hy matrix
        int[][] hx = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
        //int[][] hx = {{0,0,0},{-1,0,1},{0,0,0}};
        int[][] Gx = sobelConvolutionAux(bmp, hx);

        int[][] hy = {{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}};
        //int[][] hy = {{0,-1,0},{0,0,0},{0,1,0}};
        int[][] Gy = sobelConvolutionAux(bmp, hy);

        int[] map = new int[width * height];

        for (int i = 0; i < height * width; i++) {
            R = (int) Math.sqrt(Math.pow(Gx[0][i], 2) + Math.pow(Gy[0][i], 2));
            G = (int) Math.sqrt(Math.pow(Gx[1][i], 2) + Math.pow(Gy[1][i], 2));
            B = (int) Math.sqrt(Math.pow(Gx[2][i], 2) + Math.pow(Gy[2][i], 2));

            if (R < 0) {
                R = 0;
            } else if (R > 255) {
                R = 255;
            }

            if (G < 0) {
                G = 0;
            } else if (G > 255) {
                G = 255;
            }

            if (B < 0) {
                B = 0;
            } else if (B > 255) {
                B = 255;
            }
            map[i] = Color.rgb(R, G, B);
        }
        bmp.setPixels(map, 0, width, 0, 0, width, height);
    }


}
