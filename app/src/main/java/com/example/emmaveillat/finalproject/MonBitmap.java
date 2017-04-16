package com.example.emmaveillat.finalproject;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.nio.IntBuffer;
import java.util.Arrays;

/**
 * La classe MonBitmap est une classe définissant le type d'image propre à l'application. Elle
 * contient en paramètres l'image en elle-même, sa taille, la tableau de pixels correspondant,
 * un témoin si un filtre est appliqué, son histogramme ainsi qu'une map à appliquer pour les
 * convolutions. Elle permet également l'application de filtres simples (passage en niveaux de gris,
 * filtre sépia ...), de convolutions (flou gaussien, filtre laplacien ...), de gestion
 * d'histogramme ou encore de modification de l'image en elle-même comme le rognage.
 */
public class MonBitmap {

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
     * initialisé à null.
     */
    public int[] valMap = null;

    /**
     * le tableau servant à gérer les bordures lors des calculs de convolution, initialisé à null.
     */
    public int[] borders = null;

    /**
     * le tableau servant à calculer le filtre de Sobel, initialisé à null;
     */
    public int[] mapSobel = null;

    //public int[] basicColors = null;
    //public int[] colors = null;

    /**
     * fonction qui crée une image utilisable par l'application à partir d'une bitmap classique et
     * d'un filtre. Elle récupère donc sa hauteur, sa largeur et ses pixels dans un tableau et
     * évidemment le filtre appliqué.
     * @param bitmap l'image à manipuler
     * @param filt le filtre utilisé
     */
    public MonBitmap(Bitmap bitmap, int filt){
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
    public MonBitmap copy(){
        return new MonBitmap(bmp, filter);
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
    public MonBitmap toGray(){
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
        MonBitmap gray = new MonBitmap(bmpGray, 1);
        gray.histogram = histogram;
        gray.valMap = valMap;
        return gray;
    }

    /**
     * fonction qui permet d'inverser les couleurs 'une image (filtre négatif). En récupérant chaque
     * valeur des canaux RGB, on effectue la différence de 255 par celles-ci afin d'obtenir les
     * valeurs contraires.
     * @return l'image avec ses couleurs inversées
     */
    public MonBitmap inverted(){
        int r, g, b, pixel;
        int[] pixelsInv = new int[height * width];
        for (int i = 0; i < width * height; i++) {
            //on soustrait à 255 chaque valeur de rouge, bleu et vert pour trouver leur valeur
            // inverse, puis on les remet dans le pixel correspondant.
            pixel = pixels[i];
            r = 255 - Color.red(pixel);
            g = 255 - Color.green(pixel);
            b = 255 - Color.blue(pixel);
            pixelsInv[i] = Color.rgb(r, g, b);
        }
        //ajout de la nouvelle image dans la liste servant d'historique de modifications.
        Bitmap bmpInv = bmp.copy(Bitmap.Config.ARGB_8888, true);
        bmpInv.setPixels(pixelsInv, 0, width, 0, 0, width, height);
        MonBitmap inverted = new MonBitmap(bmpInv, 13);
        return inverted;
    }

    //TODO commentaire et javadoc
    public Bitmap scale(float factor){
        if (factor < 0.1){
            factor = 0.1f;
        }
        int newHeight = (int) (height * factor);
        int newWidth = (int) (width * factor);
        return Bitmap.createScaledBitmap(bmp, newWidth, newHeight, false);
    }

    /**
     * fonction qui applique un filtre sépia sur toute l'image. À partir des valeurs de rouge et de
     * vert de chaque pixel, elle calcule les valeurs équivalentes en sépia (la valeur de bleu est
     * ainsi écrasée).
     * @return l'image avec un filtre sépia
     */
    public MonBitmap sepia(){
        //initialisation d'entiers et du tableau contenant les pixels après application du filtre.
        int r, g, lvl;
        int[] pixelsSepia = new int[height * width];
        int depth = 20;
        //application du filtre sur chaque pixel suivant les valeurs de rouge, de vert, de valMap et
        // de depth.
        for (int i = 0; i < width * height; i++) {
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
        //copie de l'image dans la liste servant d'historique de modifications
        Bitmap bmpSepia = bmp.copy(Bitmap.Config.ARGB_8888, true);
        bmpSepia.setPixels(pixelsSepia, 0, width, 0, 0, width, height);
        MonBitmap sepia = new MonBitmap(bmpSepia, 2);
        return sepia;
    }

    /**
     * fonction qui égalise l'histogramme de l'image à transformer. En utilisant l'espace HSV, on
     * peut ainsi améliorer les contrastes de l'image si celle-ci est trop sombre.
     * @return l'image avec son histogramme égalisé
     */
    public MonBitmap histogramEqualization(){
        //si l'histogramme n'a pas déjà été trouvé, le calculer
        if (histogram == null){findHistogram();}
        int[] pixelsEqualized = new int[height * width];
        //calcul de l'histogramme cumulé
        for (int i = 1; i < 256; i++) {
            histogram[i] += histogram[i - 1];
        }
        float[] pixelHSV = new float[3];
        int pixel;
        float newValue;
        //calcul des valeurs de l'histogramme égalisé par rapport à son histogramme cumulé et à
        // valMap
        for (int i = 0; i < width * height; i++) {
            pixel = pixels[i];
            newValue = (float) (histogram[valMap[i]]) / (width * height);
            Color.colorToHSV(pixel, pixelHSV);
            pixelHSV[2] = newValue;
            pixelsEqualized[i] = Color.HSVToColor(pixelHSV);
        }
        //copie de l'image dans la liste servant d'historique de modifications
        Bitmap bmpEqualized = bmp.copy(Bitmap.Config.ARGB_8888, true);
        bmpEqualized.setPixels(pixelsEqualized, 0, width, 0, 0, width, height);
        MonBitmap equalized = new MonBitmap(bmpEqualized, 3);
        return equalized;
    }

    /**
     * fonction qui calcule une extension linéaire de dynamiques de l'image. Cela équivaut à
     * améliorer la luminosité de l'image grâce à la manipulation de son histogramme.
     * @return l'image modifiée avec une extension linéaire de dynamiques
     */
    public MonBitmap dynamicExtension(){
        //si l'histogramme n'a pas déjà été trouvé, le calculer.
        if (histogram == null){findHistogram();}
        int[] pixelsExtension = new int[height * width];

        //détermination des valeurs min et max de l'histogramme
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
        //calcul pour chaque pixel de sa valeur après extension linéaire de dynamiques
        for (int i = 0; i < width * height; i++) {
            pixel = pixels[i];
            newValue = (float)(valMap[i] - min)/(max - min);
            Color.colorToHSV(pixel, pixelHSV);
            pixelHSV[2] = newValue;
            pixelsExtension[i] = Color.HSVToColor(pixelHSV);
        }
        //copie de l'image dans la liste servant d'historique de modifications
        Bitmap bmpExtension = bmp.copy(Bitmap.Config.ARGB_8888, true);
        bmpExtension.setPixels(pixelsExtension, 0, width, 0, 0, width, height);
        MonBitmap extension = new MonBitmap(bmpExtension, 4);
        return extension;
    }

    /**
     * fonction qui retourne l'image dans le sens contraire des aiguilles d'une montre. On
     * réattribue chaque pixel à la position équivalente dans l'image retournée via leurs tableaux.
     * @return l'image retournée vers la gauche
     */
    public MonBitmap rotateLeft(){
        int[] pixelsLeft = new int[height * width];
        for (int y = 0; y < height; ++y) {
            for (int x = 0 ; x < width ; ++x) {
                pixelsLeft[( width - x - 1) * height + y] = pixels[y * width + x];
            }
        }
        //copie de l'image dans la liste servant d'historique de modifications
        Bitmap bmpLeft = Bitmap.createBitmap(pixelsLeft, height, width, Bitmap.Config.ARGB_8888);
        MonBitmap left = new MonBitmap(bmpLeft, filter);
        return left;
    }

    /**
     * fonction qui retourne l'image dans le sens des aiguilles d'une montre. On réattribue chaque
     * pixel à la position correspondante dans l'image retournée via leurs tableaux.
     * @return l'image retournée vers la droite
     */
    public MonBitmap rotateRight(){
        int[] pixelsRight = new int[height * width];
        for (int y = 0; y < height; ++y) {
            for (int x = 0 ; x < width ; ++x) {
                pixelsRight[(x+1)*height - y - 1] = pixels[y * width + x];
            }
        }
        //copie de l'image dans la liste servant d'historique de modifications
        Bitmap bmpRight = Bitmap.createBitmap(pixelsRight, height, width, Bitmap.Config.ARGB_8888);
        MonBitmap right = new MonBitmap(bmpRight, filter);
        return right;
    }

    /**
     * fonction qui retourne l'image suivant un axe horizontal (reflet dans l'eau). On réattribue
     * chaque pixel à la position correspondante dans l'image retournée via leurs tableaux.
     * @return l'image retournée horizontalement
     */
    public MonBitmap fliplr(){
        int[] pixelsFlip = new int[width * height];
        for (int y = 0; y < height; ++y) {
            for (int x = 0 ; x < width ; ++x) {
                pixelsFlip[(y+1) * width - x - 1] = pixels[y * width + x];
            }
        }
        //copie de l'image dans la liste servant d'historique de modifications
        Bitmap bmpFliplr = Bitmap.createBitmap(pixelsFlip, width, height, Bitmap.Config.ARGB_8888);
        MonBitmap fliplr = new MonBitmap(bmpFliplr, filter);
        return fliplr;
    }

    /**
     * fonction qui retourne l'image suivant un axe vertical (reflet dans un miroir). On réattribue
     * chaque pixel à la position correspondante dans l'image retournée via leurs tableaux.
     * @return l'image retournée verticalement
     */
    public MonBitmap flipud(){
        int[] pixelsFlip = new int[width * height];
        for (int y = 0; y < height; ++y) {
            for (int x = 0 ; x < width ; ++x) {
                pixelsFlip[(height - y - 1) * width + x] = pixels[y * width + x];
            }
        }
        //copie de l'image dans la liste servant d'historique de modifications
        Bitmap bmpFlipud = Bitmap.createBitmap(pixelsFlip, width, height, Bitmap.Config.ARGB_8888);
        MonBitmap flipud = new MonBitmap(bmpFlipud, filter);
        return flipud;
    }

    //TODO what x2 ?
    public MonBitmap lowerRes() {
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
            MonBitmap lower = new MonBitmap(bmpLower, filter);
        }
        return this;
    }

    private MonBitmap convolutionBlur(int[][] mask, int factor, int filter) {
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
        MonBitmap conv = new MonBitmap(bmpConv, filter);
        return conv;
    }

    public MonBitmap moyenne(int n) {
        //Creates a matrix full of ones and applicates convolution (and divides by the number of pixels used)
        int[][] mask = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                mask[i][j] = 1;
            }
        }
        return convolutionBlur(mask, n*n, 5);
    }

    public MonBitmap gauss(){
        //Creates gaussian matrix and applicates convolution
        int[][] mask ={{1,2,3,2,1},{2,6,8,6,2},{3,8,10,8,3},{2,6,8,6,2},{1,2,3,2,1}};
        return convolutionBlur(mask, 98, 6);
    }

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


    public MonBitmap sobel(){
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
        MonBitmap sobel = new MonBitmap(bmpSobel, 7);
        return sobel;
    }

    public MonBitmap laplacien(){
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
        MonBitmap laplacien = new MonBitmap(bmpLapla, 8);
        return laplacien;
    }

    public MonBitmap applyFilter(int filterToUse, int v1, int v2, int v3){
        if (filterToUse == 1){
            return selectHue(v1, v2);
        }
        if (filterToUse == 2){
            return filterHue(v1);
        }
        if (filterToUse == 3){
            return cartoonBorders(1F - (float)v1/100F);
        }
        if (filterToUse == 4){
            return changeHSV(v1, v2, v3);
        }
        return null;
    }

    private MonBitmap selectHue(int gap, int hue){
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
        MonBitmap select = new MonBitmap(bmpSelect, 10);
        return select;
    }

    public MonBitmap filterHue(int hue){
        int[] pixelsFilter = new int[width * height];
        float[] pixelHSV = new float[3];

        for (int i = 0; i < width*height; ++i) {
            int pixel = pixels[i];
            Color.colorToHSV(pixel, pixelHSV);
            pixelHSV[0] = hue;
            pixelsFilter[i] = Color.HSVToColor(pixelHSV);
        }
        Bitmap bmpFilter = Bitmap.createBitmap(pixelsFilter, width, height, Bitmap.Config.ARGB_8888);
        MonBitmap filter = new MonBitmap(bmpFilter, 11);
        return filter;
    }

    public MonBitmap changeHSV(int hue, int sat, int val){
        int[] pixelsFilter = new int[width * height];
        float[] pixelHSV = new float[3];
        for (int i = 0; i < width*height; ++i) {
            int pixel = pixels[i];
            Color.colorToHSV(pixel, pixelHSV);
            pixelHSV[0] = pixelHSV[0] + hue;
            if (pixelHSV[0] < 0.0f) {
                pixelHSV[0] += 360.0f;
            } else if (pixelHSV[0] > 360.0f) {
                pixelHSV[0] -= 360.0f;
            }

            pixelHSV[1] = pixelHSV[1] + sat;
            if (pixelHSV[1] < 0.0f) {
                pixelHSV[1] += 1.0f;
            } else if (pixelHSV[1] > 1.0f) {
                pixelHSV[1] -= 1.0f;
            }
             pixelHSV[2] = pixelHSV[2] + val;
            if (pixelHSV[2] < 0.0f) {
                pixelHSV[2] += 1.0f;
            } else if (pixelHSV[2] > 1.0f) {
                pixelHSV[2] -= 1.0f;
            }
            pixelsFilter[i] = Color.HSVToColor(pixelHSV);
        }
        Bitmap bmpFilter = Bitmap.createBitmap(pixelsFilter, width, height, Bitmap.Config.ARGB_8888);
        MonBitmap filter = new MonBitmap(bmpFilter, 11);
        return filter;
    }

    public MonBitmap visualCrop(int[] toCrop){
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
        MonBitmap crop = new MonBitmap(bmpCrop, filter);
        return crop;
    }

    public MonBitmap crop(int[] toCrop){
        int toCropUp = toCrop[0] * height / 100;
        int toCropDown = height - toCrop[1] * height / 100;
        int toCropLeft = toCrop[2] * width / 100;
        int toCropRight = width - toCrop[3] * width / 100;
        int newWidth = toCropRight - toCropLeft;
        int newHeight = toCropDown - toCropUp;
        Bitmap bmpCrop = Bitmap.createBitmap(bmp, toCropLeft,toCropUp,newWidth, newHeight);
        MonBitmap crop = new MonBitmap(bmpCrop, filter);
        return crop;
    }


    // Code de Pratik sur stackoverflow.com
    private MonBitmap colorDodgeBlend(MonBitmap layer) {
        Bitmap base = bmp.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap blend = layer.bmp.copy(Bitmap.Config.ARGB_8888, false);

        IntBuffer buffBase = IntBuffer.allocate(width * height);
        base.copyPixelsToBuffer(buffBase);
        buffBase.rewind();

        IntBuffer buffBlend = IntBuffer.allocate(layer.width * layer.height);
        blend.copyPixelsToBuffer(buffBlend);
        buffBlend.rewind();

        IntBuffer buffOut = IntBuffer.allocate(width * height);
        buffOut.rewind();

        while (buffOut.position() < buffOut.limit()) {

            int filterInt = buffBlend.get();
            int srcInt = buffBase.get();

            int redValueFilter = Color.red(filterInt);
            int greenValueFilter = Color.green(filterInt);
            int blueValueFilter = Color.blue(filterInt);

            int redValueSrc = Color.red(srcInt);
            int greenValueSrc = Color.green(srcInt);
            int blueValueSrc = Color.blue(srcInt);

            int redValueFinal = colordodge(redValueFilter, redValueSrc);
            int greenValueFinal = colordodge(greenValueFilter, greenValueSrc);
            int blueValueFinal = colordodge(blueValueFilter, blueValueSrc);


            int pixel = Color.argb(255, redValueFinal, greenValueFinal, blueValueFinal);


            buffOut.put(pixel);
        }

        buffOut.rewind();

        base.copyPixelsFromBuffer(buffOut);
        blend.recycle();

        return new MonBitmap(base, 17);
    }

    private int colordodge(int in1, int in2) {
        float image = (float)in2;
        float mask = (float)in1;
        return ((int) ((image == 255) ? image:Math.min(255, (((long)mask << 8 ) / (255 - image)))));
    }

    public MonBitmap pencilSketch(){
        MonBitmap gray = toGray();
        MonBitmap inverted = gray.inverted();
        MonBitmap gauss = inverted.gauss();
        return gray.colorDodgeBlend(gauss);
    }

    public MonBitmap closestNeighbor(int newWidth, int newHeight){
        float factX = (float) newWidth/width;
        float factY = (float) newHeight/height;

        int[] pixelsCN = new int [newWidth*newHeight];
        for (int x = 0; x < newWidth; x++) {
            for (int y = 0; y < newHeight; y++) {
                pixelsCN[y * newWidth + x] = pixels[(int)(y / factY) * width + (int)(x / factX)];
            }
        }
        Bitmap bmpCN = Bitmap.createBitmap(pixelsCN, newWidth, newHeight, Bitmap.Config.ARGB_8888);
        MonBitmap cn = new MonBitmap(bmpCN, 19);
        return cn;
    }

    private void findAreaColor(int x, int y, int width, int height, int[] sumRGB, int countcolors){
        int index = y*width+x;
        borders[index]=countcolors;
        int pixel = pixels[index];
        sumRGB[0] += Color.red(pixel);
        sumRGB[1] += Color.green(pixel);
        sumRGB[2] += Color.blue(pixel);
        sumRGB[3] ++;
        if ((x>0) && (borders[index-1]==0)){findAreaColor(x-1, y, width, height, sumRGB, countcolors);}
        if ((x<width-1)&&(borders[index+1]==0)){findAreaColor(x+1, y, width, height, sumRGB, countcolors);}
        if ((y>0)&&(borders[index-width]==0)){findAreaColor(x, y-1, width, height, sumRGB, countcolors);}
        if ((y<height-1)&&(borders[index+width]==0)){findAreaColor(x, y+1, width, height, sumRGB, countcolors);}
    }

    public MonBitmap cartoon() {
        borders = new int[width * height];
        mapSobel = this.sobel().pixels.clone();
        int pixel, r, g, b;
        //Fills mapBorders with -1 at borders
        for (int i=0; i<3*width; i ++){
            borders[i] = -1;
            borders[width*height - 1 - i] = -1;
        }
        for (int y=3; y<height -3; y++){
            for (int x=0; x<3; x++) {
                borders[y * width + x] = -1;
                borders[(y+1) * width - x - 1] = -1;
            }
        }
        for (int i = 0; i < width * height; i++) {
            pixel = mapSobel[i];
            r = Color.red(pixel);
            g = Color.green(pixel);
            b = Color.blue(pixel);
            if (r + b + g > 30) {
                borders[i] = -1;
            }
        }
        int[] colors = new int[width*height];
        int[] basicColors = new int[1000];
        int countBasicColors = 0;
        int countColors = 1;
        int[] sumRGB = new int[4];
        int color;
        int i;
        int dr, dg, db;
        boolean resume;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (borders[y * width + x] == 0) {
                    Arrays.fill(sumRGB, 0);
                    findAreaColor(x, y, width, height, sumRGB, countColors);
                    r = sumRGB[0] / sumRGB[3];
                    g = sumRGB[1] / sumRGB[3];
                    b = sumRGB[2] / sumRGB[3];
                    if (countBasicColors == 0){
                        basicColors[countBasicColors] = Color.rgb(r, g, b);
                        colors[countColors] = countBasicColors;
                        countBasicColors++;
                    }
                    else {
                        i = 0;
                        resume = true;
                        while ((i < countBasicColors)&&(resume)){
                            color = basicColors[i];
                            dr = r - Color.red(color);
                            dg = g - Color.green(color);
                            db = b - Color.blue(color);
                            if (2*dr*dr + 4*dg*dg + 3*db*db < 5000) {
                                colors[countColors] = i;
                                resume = false;
                            }
                            i++;
                        }
                        if (resume) {
                            basicColors[countBasicColors] = Color.rgb(r, g, b);
                            colors[countColors] = countBasicColors;
                            countBasicColors++;
                        }
                    }
                    countColors++;
                }
            }
        }

        int[] pixelsCartoon = new int[width* height];
        for (int j = 0; j < width * height; j++) {
            if (borders[j] != -1) {
                pixelsCartoon[j] = basicColors[colors[borders[j]]];
            }
            else{
                pixel = pixels[j];
                r = Color.red(pixel);
                g = Color.green(pixel);
                b = Color.blue(pixel);
                i = 0;
                resume = true;
                while ((i < countBasicColors)&&(resume)){
                    color = basicColors[i];
                    dr = r - Color.red(color);
                    dg = g - Color.green(color);
                    db = b - Color.blue(color);
                    if (2*dr*dr + 4*dg*dg + 3*db*db < 5000) {
                        pixelsCartoon[j] = color;
                        resume = false;
                    }
                    i++;
                }
                if (resume){
                    pixelsCartoon[j]=pixels[j];
                }
            }
        }
        /*int color;
        int[] sumRGB = new int[4];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (borders[y * width + x] == 0) {
                    Arrays.fill(sumRGB, 0);
                    findAreaColor(x, y, width, height, sumRGB);
                    r = sumRGB[0] / sumRGB[3];
                    g = sumRGB[1] / sumRGB[3];
                    b = sumRGB[2] / sumRGB[3];
                    color = Color.rgb(r, g, b);
                    paintArea(x, y, width, height, color);
                }
            }
        }*/
        Bitmap bmpCartoon = Bitmap.createBitmap(pixelsCartoon, width, height, Bitmap.Config.ARGB_8888);
        MonBitmap cartoon = new MonBitmap(bmpCartoon, 14);
        cartoon.borders = this.borders.clone();
        cartoon.mapSobel = this.mapSobel.clone();
        return cartoon;
    }

    public MonBitmap cartoonBorders(float lvl){
        float[] hsv = new float[3];
        int[] pixelsCartoon = pixels.clone();
        for (int i = 0; i < width * height; i++) {
            if (borders[i] == -1) {
                Color.colorToHSV(mapSobel[i], hsv);
                if (hsv[2] > lvl) {
                    int v = (int) (100 * (1 - hsv[2]));
                    pixelsCartoon[i] = Color.rgb(v, v, v);
                }
            }
        }
        Bitmap bmpCartoon = Bitmap.createBitmap(pixelsCartoon, width, height, Bitmap.Config.ARGB_8888);
        MonBitmap cartoon = new MonBitmap(bmpCartoon, 14);
        cartoon.borders = this.borders.clone();
        cartoon.mapSobel = this.mapSobel.clone();
        return cartoon;
    }

    public MonBitmap fingerApply(MonBitmap toApply, int startX, int startY, int endX, int endY){
        if (startX < endX){
            return fingerApply(toApply, endX, endY, startX, startY);
        }
        for (int i = startX*startY; i < endX * endY; i++ ) {
            toApply.pixels[i] = Color.rgb(255, 255, 255);
        }
        /*int range = Math.min(width,height)/10;
        int[] pixelsFinger = pixels.clone();
        int x = startX;
        int y = startY;
        int startI, startJ;
        if (endX == startX){
            if (x<range){
                startI = x - range;
            }
            else{startI = -range;}
            if (y<range){
                startJ = y - range;
            }
            else{startJ = -range;}
            for (int i=startI; i<Math.min(range,width - x); i++){
                for (int j=startJ; j<Math.min(range, height - y); j++){
                    pixelsFinger[(y+j) * width + (x+i)] = toApply.pixels[(y+j) * width + (x+i)];
                }
            }
        }
        else {
            int step = (endY - startY) * range / (endX - startX);
            int maxX = Math.min(width, endX);
            boolean resume = true;
            while (x < maxX) {
                if (x < range) {
                    startI = x - range;
                } else {
                    startI = -range;
                }
                if (y < range) {
                    startJ = y - range;
                } else {
                    startJ = -range;
                }
                for (int i = startI; i < Math.min(range, width - x); i++) {
                    for (int j = startJ; j < Math.min(range, height - y); j++) {
                        pixelsFinger[(y + j) * width + (x + i)] = toApply.pixels[(y + j) * width + (x + i)];
                    }
                }
                x += range;
                y += step;
            }
        }
        Bitmap bmpFinger = Bitmap.createBitmap(pixelsFinger, width, height, Bitmap.Config.ARGB_8888);
        MonBitmap finger = new MonBitmap(bmpFinger, 18);
        return finger;*/
        return toApply;
    }
}