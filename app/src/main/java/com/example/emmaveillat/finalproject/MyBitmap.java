package com.example.emmaveillat.finalproject;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * La classe MyBitmap est une classe définissant le type d'image propre à l'application. Elle
 * contient en paramètres l'image en elle-même, sa taille, la tableau de pixels correspondant,
 * un témoin si un filtre est appliqué, son histogramme ainsi qu'une map à appliquer pour les
 * convolutions. Elle permet également l'application de filtres simples (passage en niveaux de gris,
 * filtre sépia ...), de convolutions (flou gaussien, filtre laplacien ...), de gestion
 * d'histogramme ou encore de modification de l'image en elle-même comme le rognage.
 */
public class MyBitmap {

    /**
     * l'image utilisée.
     */
    public Bitmap bmp;

    /**
     * la largeur de l'image.
     */
    public int width;

    /**
     * la hauteur de l'image.
     */
    public int height;

    /**
     * le tableau de pixels correspondants à l'image.
     */
    public int[] pixels;

    /**
     * le filtre utilisé.
     */
    public int filter;

    /**
     * l'histogramme de l'image, initialisé à null.
     */
    public int[] histogram = null;

    /**
     * le tableau correspondant à celui des pixels une fois l'image passée en niveaus de gris,
     * initialisée à null.
     */
    public int[] valMap = null;

    /**
     * fonction qui crée une image utilisable par l'application à partir d'une bitmap classique et
     * d'un filtre. Elle récupère donc sa hauteur, sa largeur et ses pixels dans un tableau et
     * évidemment le filtre appliqué.
     * @param bitmap l'image à manipuler
     * @param filt le filtre utilisé
     */
    public MyBitmap(Bitmap bitmap, int filt){
        bmp = bitmap;
        width = bmp.getWidth();
        height = bmp.getHeight();
        pixels = new int[width*height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        filter = filt;
    }

    /**
     * fonction qui copie une image existante. Elle est ainsi créée en passant en paramètres dans le
     * constructeur l'image à dupliquer avec son filtre.
     * @return l'image copiée
     */
    public MyBitmap copy(){
        return new MyBitmap(bmp, filter);
    }

    /**
     * fonction qui récupère l'histogramme de l'image que l'utilisateur manipule actuellement.
     * Pour cela, elle parcourt le tableau de pixels de l'image, et place, dans le tableau
     * histogram, à la position correspondante le niveau de gris des pixels lus dans le tableau.
     */
    public void findHistogram() {
        //initialisation des tableaux valMap et histogram
        valMap = new int[width * height];
        histogram = new int[256];
        //initialisation des futures valeurs de rouge, vert et bleu du pixel, ainsi que du niveau de
        //gris associé
        int pixel, r, g, b, lvl;
        for (int i = 0; i < width * height; i++) {
            //récupération des valeurs RGB du pixel dans l'image initiale
            pixel = pixels[i];
            r = Color.red(pixel);
            b = Color.blue(pixel);
            g = Color.green(pixel);
            //moyenne des 3 valeurs et donne le niveau de gris associé
            lvl = (int) (0.299F * r + 0.587F * g + 0.114F * b);
            valMap[i] = lvl;
            histogram[lvl]++;
        }
    }

    /**
     * fonction qui transforme l'image en niveaux de gris associés. Elle parcourt chaque pixel de
     * l'image à transformer, calcule la moyenne des canaux RGB et retourne l'image grisée.
     * @return l'image en niveaux de gris
     */
    public MyBitmap toGray(){
        //si l'histogramme n'est pas déjà défini dans le tableau histogram, il est calculé via la
        //fonction findHistogram().
        if (histogram == null){findHistogram();}
        int[] pixelsGray = new int[height * width];
        //copie de l'image à transformer en la rendant "modifiable" grâce au booléen true.
        Bitmap bmpGray = bmp.copy(Bitmap.Config.ARGB_8888, true);
        int lvl;
        for (int i = 0; i < width * height; i++) {
            //récupération des niveaux de gris à partir du tableau valMap
            lvl = valMap[i];
            pixelsGray[i] = Color.rgb(lvl,lvl,lvl);
        }
        bmpGray.setPixels(pixelsGray, 0, width, 0, 0, width, height);
        //création d'une image utilisable par l'application grâce à l'image transformée en niveaux
        //de gris et au filtre gris associé à l'entier 1.
        MyBitmap gray = new MyBitmap(bmpGray, 1);
        gray.histogram = histogram;
        gray.valMap = valMap;
        return gray;
    }

    /**
     *
     * @param factor
     * @return
     */
    public Bitmap scale(float factor){
        if (factor < 0.1){
            factor = 0.1f;
        }
        int newHeight = (int) (height * factor);
        int newWidth = (int) (width * factor);
        return Bitmap.createScaledBitmap(bmp, newWidth, newHeight, false);
    }

    /**
     *
     * @return
     */
    public MyBitmap sepia(){
        if (histogram == null){findHistogram();}
        int r, g, lvl;
        int[] pixelsSepia = new int[height * width];
        MyBitmap gray = this.toGray();
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

    /**
     *
     * @return
     */
    public MyBitmap histogramEqualization(){
        if (histogram == null){findHistogram();}
        int[] pixelsEqualized = new int[height * width];
        for (int i = 1; i < 256; i++) {
            histogram[i] += histogram[i - 1];
        }
        float[] pixelHSV = new float[3];
        int pixel;
        float newValue;
        for (int i = 0; i < width * height; i++) {
            pixel = pixels[i];
            newValue = (float) (histogram[valMap[i]]) / (width * height);
            Color.colorToHSV(pixel, pixelHSV);
            pixelHSV[2] = newValue;
            pixelsEqualized[i] = Color.HSVToColor(pixelHSV);
        }
        Bitmap bmpEqualized = bmp.copy(Bitmap.Config.ARGB_8888, true);
        bmpEqualized.setPixels(pixelsEqualized, 0, width, 0, 0, width, height);
        MyBitmap equalized = new MyBitmap(bmpEqualized, 3);
        return equalized;
    }

    /**
     *
     * @return
     */
    public MyBitmap dynamicExtension(){
        if (histogram == null){findHistogram();}
        int[] pixelsExtension = new int[height * width];
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

    /**
     *
     * @return
     */
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

    /**
     *
     * @return
     */
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

    /**
     *
     * @return
     */
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

    /**
     *
     * @return
     */
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

    /**
     *
     * @return
     */
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

    /**
     *
     * @param mask
     * @param factor
     * @param filter
     * @return
     */
    private MyBitmap convolutionBlur(int[][] mask, int factor, int filter) {
        int n = mask.length / 2;
        int[] pixelsConv = new int[height * width];
        int white = Color.rgb(255,255,255);
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

    /**
     *
     * @param n
     * @return
     */
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

    /**
     *
     * @return
     */
    public MyBitmap gauss(){
        //Creates gaussian matrix and applicates convolution
        int[][] mask ={{1,2,3,2,1},{2,6,8,6,2},{3,8,10,8,3},{2,6,8,6,2},{1,2,3,2,1}};
        return convolutionBlur(mask, 98, 6);
    }

    /**
     *
     * @param mask
     * @return
     */
    public int[][] convolutionBorders(int[][] mask) {
        int n = mask.length / 2;
        int[][] pixelsConvRGB = new int[height * width][3];
        int sumR, sumG, sumB;
        int pixel;
        //Keeps original values for the borders
        for (int y = 0; y < n; y++) {
            for (int x = 0; x < width; x++) {
                pixel = pixels[y * width + x];
                pixelsConvRGB[y * width + x][0] = Color.red(pixel);
                pixelsConvRGB[y * width + x][1] = Color.green(pixel);
                pixelsConvRGB[y * width + x][2] = Color.blue(pixel);
                pixelsConvRGB[(height - y - 1) * width + x][0] = Color.red(pixel);
                pixelsConvRGB[(height - y - 1) * width + x][1] = Color.green(pixel);
                pixelsConvRGB[(height - y - 1) * width + x][2] = Color.blue(pixel);
            }
        }
        for (int y = height - n; y < height - n; y++) {
            for (int x = 0; x < n; x++) {
                pixel = pixels[y * width + x];
                pixelsConvRGB[y * width + x][0] = Color.red(pixel);
                pixelsConvRGB[y * width + x][1] = Color.green(pixel);
                pixelsConvRGB[y * width + x][2] = Color.blue(pixel);
                pixelsConvRGB[(y + 1) * width - x - 1][0] = Color.red(pixel);
                pixelsConvRGB[(y + 1) * width - x - 1][1] = Color.green(pixel);
                pixelsConvRGB[(y + 1) * width - x - 1][2] = Color.blue(pixel);
            }
        }
        //Convolution avoiding borders
        float coef_mask;
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
                if (sumR < 0) {
                    sumR = -sumR;
                }
                if (sumG < 0) {
                    sumG = -sumG;
                }
                if (sumB < 0) {
                    sumB = -sumB;
                }
                pixelsConvRGB[y * width + x][0] = sumR;
                pixelsConvRGB[y * width + x][1] = sumG;
                pixelsConvRGB[y * width + x][2] = sumB;
            }
        }
        return pixelsConvRGB;
    }

    /**
     *
     * @return
     */
    public MyBitmap sobel(){
        int R,G,B;
        //applicates convolution with hx et hy matrix
        int[][] hx = {{-1,0,1},{-2,0,2},{-1,0,1}};
        int[][] Gx = convolutionBorders(hx);

        int[][] hy = {{-1,-2,-1},{0,0,0},{1,2,1}};
        int[][] Gy = convolutionBorders(hy);

        int[][] norm = new int[width*height][3];

        int[] pixelsSobel = new int[width*height];
        int max = 0;
        for (int i=0; i<height*width; i++) {
            norm[i][0] = (int) Math.sqrt(Math.pow(Gx[i][0], 2) + Math.pow(Gy[i][0], 2));
            norm[i][1] = (int) Math.sqrt(Math.pow(Gx[i][1], 2) + Math.pow(Gy[i][1], 2));
            norm[i][2] = (int) Math.sqrt(Math.pow(Gx[i][2], 2) + Math.pow(Gy[i][2], 2));
            if (norm[i][0] > max){max = norm[i][0];}
            if (norm[i][1] > max){max = norm[i][1];}
            if (norm[i][2] > max){max = norm[i][2];}
        }
        if (max!=0){
            for (int i=0; i<height*width; i++) {
                R = norm[i][0] * 255 / max;
                G = norm[i][1] * 255 / max;
                B = norm[i][2] * 255 / max;
                pixelsSobel[i] = Color.rgb(R,G,B);
            }
        }
        Bitmap bmpSobel = Bitmap.createBitmap(pixelsSobel, width, height, Bitmap.Config.ARGB_8888);
        MyBitmap sobel = new MyBitmap(bmpSobel, 7);
        return sobel;
    }

    /**
     *
     * @return
     */
    public MyBitmap laplacien(){
        int R,G,B;
        //applicates convolution with hx et hy matrix
        int[][] mask = {{1,1,1},{1,-8,1},{1,1,1}};
        int[][] Gl = convolutionBorders(mask);

        int[] pixelsLapla = new int[width*height];
        int max = 0;
        for (int i=0; i<height*width; i++) {
            if (Gl[i][0] > max){max = Gl[i][0];}
            if (Gl[i][1] > max){max = Gl[i][1];}
            if (Gl[i][2] > max){max = Gl[i][2];}
        }
        if (max!=0){
            for (int i=0; i<height*width; i++) {
                R = Gl[i][0] * 255 / max;
                G = Gl[i][1] * 255 / max;
                B = Gl[i][2] * 255 / max;
                pixelsLapla[i] = Color.rgb(R,G,B);
            }
        }
        Bitmap bmpLapla = Bitmap.createBitmap(pixelsLapla, width, height, Bitmap.Config.ARGB_8888);
        MyBitmap laplacien = new MyBitmap(bmpLapla, 8);
        return laplacien;
    }

    /**
     *
     * @param filterToUse
     * @param v1
     * @param v2
     * @param v3
     * @return
     */
    public MyBitmap applyFilter(int filterToUse, int v1, int v2, int v3){
        if (filterToUse == 1){
            return selectHue(v1, v2);
        }
        if (filterToUse == 2){
            return filterHue(v1);
        }
        return null;
    }

    /**
     *
     * @param gap
     * @param hue
     * @return
     */
    private MyBitmap selectHue(int gap, int hue){
        int[] pixelsSelect = pixels.clone();
        //Array to stock pixel hsv values
        float[] pixelHSV = new float[3];

        int index = 0;
        //3 cases: each one changes the way we choose the pixels we change to gray and the one we keep colored
        //Chosen hue value is not too close to 0 or 360
        if ((hue >= gap)&&(hue<=360F - gap)){
            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    int pixel = pixelsSelect[index];
                    Color.colorToHSV(pixel, pixelHSV);
                    float pixelHue = pixelHSV[0];
                    //Get pixels out of the gap defined around hue value in gray
                    if ((pixelHue >= hue + gap) || (pixelHue <= hue - gap)) {
                        int red = Color.red(pixel);
                        int blue = Color.blue(pixel);
                        int green = Color.green(pixel);
                        int gray = (red + blue + green) / 3;
                        pixelsSelect[index] = Color.rgb(gray, gray, gray);
                    }
                    index++;
                }
            }
        }
        //Chosen hue value is close to 0
        else if (hue < gap){
            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    int pixel = pixelsSelect[index];
                    Color.colorToHSV(pixel, pixelHSV);
                    float pixelHue = pixelHSV[0];
                    if ((pixelHue >= hue + gap) && (pixelHue <= hue + 360F - gap)) {
                        int red = Color.red(pixel);
                        int blue = Color.blue(pixel);
                        int green = Color.green(pixel);
                        int gray = (red + blue + green) / 3;
                        pixelsSelect[index] = Color.rgb(gray, gray, gray);
                    }
                    index++;
                }
            }
        }
        //Chosen value is close to 360
        else{
            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    int pixel = pixelsSelect[index];
                    Color.colorToHSV(pixel, pixelHSV);
                    float pixelHue = pixelHSV[0];
                    if ((pixelHue >= hue -360F + gap) && (pixelHue <= hue - gap)) {
                        int red = Color.red(pixel);
                        int blue = Color.blue(pixel);
                        int green = Color.green(pixel);
                        int gray = (red + blue + green) / 3;
                        pixelsSelect[index] = Color.rgb(gray, gray, gray);
                    }
                    index++;
                }
            }
        }
        Bitmap bmpSelect = Bitmap.createBitmap(pixelsSelect, width, height, Bitmap.Config.ARGB_8888);
        MyBitmap select = new MyBitmap(bmpSelect, 10);
        return select;
    }

    /**
     *
     * @param hue
     * @return
     */
    public MyBitmap filterHue(int hue){
        int[] pixelsFilter = new int[width * height];
        float[] pixelHSV = new float[3];

        for (int i = 0; i < width*height; ++i) {
            int pixel = pixels[i];
            Color.colorToHSV(pixel, pixelHSV);
            pixelHSV[0] = hue;
            pixelsFilter[i] = Color.HSVToColor(pixelHSV);
        }
        Bitmap bmpFilter = Bitmap.createBitmap(pixelsFilter, width, height, Bitmap.Config.ARGB_8888);
        MyBitmap filter = new MyBitmap(bmpFilter, 11);
        return filter;
    }

    /**
     *
     * @param toCrop
     * @return
     */
    public MyBitmap visualCrop(int[] toCrop){
        int[] pixelsCrop = pixels.clone();
        int toCropUp = toCrop[0] * height / 100;
        int toCropDown = height - toCrop[1] * height / 100;
        int toCropLeft = toCrop[2] * width / 100;
        int toCropRight = width - toCrop[3] * width / 100;

        for (int y = toCropUp; y < toCropUp + 4; ++y) {
            for (int x = toCropLeft ; x < toCropRight ; ++x) {
                pixelsCrop[ y * width + x ] = Color.rgb(0,0,0);
            }
        }
        for (int y = toCropDown - 4; y < toCropDown; ++y) {
            for (int x = toCropLeft; x < toCropRight; ++x) {
                pixelsCrop[ y * width + x ] = Color.rgb(0,0,0);
            }
        }
        for (int x = toCropLeft; x < toCropLeft + 4; ++x) {
            for (int y = toCropUp; y < toCropDown; ++y) {
                pixelsCrop[ y * width + x ] = Color.rgb(0,0,0);
            }
        }
        for (int x = toCropRight - 4; x < toCropRight; ++x) {
            for (int y = toCropUp; y < toCropDown; ++y) {
                pixelsCrop[ y * width + x ] = Color.rgb(0,0,0);
            }
        }
        Bitmap bmpCrop = Bitmap.createBitmap(pixelsCrop, width, height, Bitmap.Config.ARGB_8888);
        MyBitmap crop = new MyBitmap(bmpCrop, filter);
        return crop;
    }

    /**
     *
     * @param toCrop
     * @return
     */
    public MyBitmap crop(int[] toCrop){
        int toCropUp = toCrop[0] * height / 100;
        int toCropDown = height - toCrop[1] * height / 100;
        int toCropLeft = toCrop[2] * width / 100;
        int toCropRight = width - toCrop[3] * width / 100;
        int newWidth = toCropRight - toCropLeft;
        int newHeight = toCropDown - toCropUp;
        Bitmap bmpCrop = Bitmap.createBitmap(bmp, toCropLeft,toCropUp,newWidth, newHeight);
        MyBitmap crop = new MyBitmap(bmpCrop, filter);
        return crop;
    }

}
