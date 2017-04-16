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
public class MaBitmap {

    /**
     * l'image utilisée.
     */
    public Bitmap bmp;

    /**
     * la largeur de l'image.
     */
    public int largeur;

    /**
     * la hauteur de l'image.
     */
    public int hauteur;

    /**
     * le tableau de pixels correspondants à l'image.
     */
    public int[] pixels;

    /**
     * le filtre utilisé.
     */
    public int filtre;

    /**
     * l'histogramme de l'image, initialisé à null.
     */
    public int[] histogramme = null;

    /**
     * le tableau correspondant à celui des pixels une fois l'image passée en niveaus de gris,
     * initialisé à null.
     */
    public int[] valMap = null;

    /**
     * le tableau servant à gérer les bordures lors des calculs de convolution, initialisé à null.
     */
    public int[] bordures = null;

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
    public MaBitmap(Bitmap bitmap, int filt){
        bmp = bitmap;
        largeur = bmp.getWidth();
        hauteur = bmp.getHeight();
        pixels = new int[largeur*hauteur];
        bmp.getPixels(pixels, 0, largeur, 0, 0, largeur, hauteur);
        filtre = filt;
    }

    /**
     * fonction qui copie une image existante. Elle est ainsi créée en passant en paramètres dans le
     * constructeur l'image à dupliquer avec son filtre.
     * @return l'image copiée
     */
    public MaBitmap copie(){
        return new MaBitmap(bmp, filtre);
    }

    //TODO changer nom fonction
    /**
     * fonction qui récupère l'histogramme de l'image que l'utilisateur manipule actuellement.
     * Pour cela, elle parcourt le tableau de pixels de l'image, et place, dans le tableau
     * histogram, à la position correspondante le niveau de gris des pixels lus dans le tableau.
     */
    public void findHistogram() {
        //initialisation des tableaux valMap et histogram
        valMap = new int[largeur * hauteur];
        histogramme = new int[256];
        //initialisation des futures valeurs de rouge, vert et bleu du pixel, ainsi que du niveau de
        //gris associé
        int pixel, r, g, b, lvl;
        for (int i = 0; i < largeur * hauteur; i++) {
            //récupération des valeurs RGB du pixel dans l'image initiale
            pixel = pixels[i];
            r = Color.red(pixel);
            b = Color.blue(pixel);
            g = Color.green(pixel);
            //moyenneur des 3 valeurs et donne le niveau de gris associé
            lvl = (int) (0.299F * r + 0.587F * g + 0.114F * b);
            valMap[i] = lvl;
            histogramme[lvl]++;
        }
    }

    /**
     * fonction qui transforme l'image en niveaux de gris associés. Elle parcourt chaque pixel de
     * l'image à transformer, calcule la moyenneur des canaux RGB et retourne l'image grisée.
     * @return l'image en niveaux de gris
     */
    public MaBitmap enGris(){
        //si l'histogramme n'est pas déjà défini dans le tableau histogram, il est calculé via la
        //fonction findHistogram().
        if (histogramme == null){findHistogram();}
        int[] pixelsGray = new int[hauteur * largeur];
        //copie de l'image à transformer en la rendant "modifiable" grâce au booléen true.
        Bitmap bmpGray = bmp.copy(Bitmap.Config.ARGB_8888, true);
        int lvl;
        for (int i = 0; i < largeur * hauteur; i++) {
            //récupération des niveaux de gris à partir du tableau valMap
            lvl = valMap[i];
            pixelsGray[i] = Color.rgb(lvl,lvl,lvl);
        }
        bmpGray.setPixels(pixelsGray, 0, largeur, 0, 0, largeur, hauteur);
        //création d'une image utilisable par l'application grâce à l'image transformée en niveaux
        //de gris et au filtre gris associé à l'entier 1.
        MaBitmap gray = new MaBitmap(bmpGray, 1);
        gray.histogramme = histogramme;
        gray.valMap = valMap;
        return gray;
    }

    /**
     * fonction qui permet d'inverser les couleurs 'une image (filtre négatif). En récupérant chaque
     * valeur des canaux RGB, on effectue la différence de 255 par celles-ci afin d'obtenir les
     * valeurs contraires.
     * @return l'image avec ses couleurs inversées
     */
    public MaBitmap inverser(){
        int r, g, b, pixel;
        int[] pixelsInv = new int[hauteur * largeur];
        for (int i = 0; i < largeur * hauteur; i++) {
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
        bmpInv.setPixels(pixelsInv, 0, largeur, 0, 0, largeur, hauteur);
        MaBitmap inverted = new MaBitmap(bmpInv, 13);
        return inverted;
    }

    //TODO changer nom fonction
    //TODO commentaire et javadoc
    public Bitmap scale(float facteur){
        if (facteur < 0.1){
            facteur = 0.1f;
        }
        int nh = (int) (hauteur * facteur);
        int nl = (int) (largeur * facteur);
        return Bitmap.createScaledBitmap(bmp, nl, nh, false);
    }

    /**
     * fonction qui applique un filtre sépia sur toute l'image. À partir des valeurs de rouge et de
     * vert de chaque pixel, elle calcule les valeurs équivalentes en sépia (la valeur de bleu est
     * ainsi écrasée).
     * @return l'image avec un filtre sépia
     */
    public MaBitmap sepia(){
        //initialisation d'entiers et du tableau contenant les pixels après application du filtre.
        int r, g, lvl;
        int[] pixelsSepia = new int[hauteur * largeur];
        int temoin = 20;
        //application du filtre sur chaque pixel suivant les valeurs de rouge, de vert, de valMap et
        //du temoin.
        for (int i = 0; i < largeur * hauteur; i++) {
            lvl = valMap[i];
            r = lvl + (temoin *2);
            g = lvl + temoin;
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
        bmpSepia.setPixels(pixelsSepia, 0, largeur, 0, 0, largeur, hauteur);
        MaBitmap sepia = new MaBitmap(bmpSepia, 2);
        return sepia;
    }

    /**
     * fonction qui égalise l'histogramme de l'image à transformer. En utilisant l'espace HSV, on
     * peut ainsi améliorer les contrastes de l'image si celle-ci est trop sombre.
     * @return l'image avec son histogramme égalisé
     */
    public MaBitmap egalisationHistogramme(){
        //si l'histogramme n'a pas déjà été trouvé, le calculer
        if (histogramme == null){findHistogram();}
        int[] pixelsEqualized = new int[hauteur * largeur];
        //calcul de l'histogramme cumulé
        for (int i = 1; i < 256; i++) {
            histogramme[i] += histogramme[i - 1];
        }
        float[] pixelHSV = new float[3];
        int pixel;
        float newValue;
        //calcul des valeurs de l'histogramme égalisé par rapport à son histogramme cumulé et à
        // valMap
        for (int i = 0; i < largeur * hauteur; i++) {
            pixel = pixels[i];
            newValue = (float) (histogramme[valMap[i]]) / (largeur * hauteur);
            Color.colorToHSV(pixel, pixelHSV);
            pixelHSV[2] = newValue;
            pixelsEqualized[i] = Color.HSVToColor(pixelHSV);
        }
        //copie de l'image dans la liste servant d'historique de modifications
        Bitmap bmpEqualized = bmp.copy(Bitmap.Config.ARGB_8888, true);
        bmpEqualized.setPixels(pixelsEqualized, 0, largeur, 0, 0, largeur, hauteur);
        MaBitmap equalized = new MaBitmap(bmpEqualized, 3);
        return equalized;
    }

    /**
     * fonction qui calcule une extension linéaire de dynamiques de l'image. Cela équivaut à
     * améliorer la luminosité de l'image grâce à la manipulation de son histogramme.
     * @return l'image modifiée avec une extension linéaire de dynamiques
     */
    public MaBitmap extensionDynamiques(){
        //si l'histogramme n'a pas déjà été trouvé, le calculer.
        if (histogramme == null){findHistogram();}
        int[] pixelsExtension = new int[hauteur * largeur];

        //détermination des valeurs min et max de l'histogramme
        int k = 0;
        while (histogramme[k] == 0) {
            k++;
        }
        int min = k;

        k = 255;
        while (histogramme[k] == 0) {
            k--;
        }
        int max = k;

        float[] pixelHSV = new float[3];
        int pixel;
        float nValeur;
        //calcul pour chaque pixel de sa valeur après extension linéaire de dynamiques
        for (int i = 0; i < largeur * hauteur; i++) {
            pixel = pixels[i];
            nValeur = (float)(valMap[i] - min)/(max - min);
            Color.colorToHSV(pixel, pixelHSV);
            pixelHSV[2] = nValeur;
            pixelsExtension[i] = Color.HSVToColor(pixelHSV);
        }
        //copie de l'image dans la liste servant d'historique de modifications
        Bitmap bmpExtension = bmp.copy(Bitmap.Config.ARGB_8888, true);
        bmpExtension.setPixels(pixelsExtension, 0, largeur, 0, 0, largeur, hauteur);
        MaBitmap extension = new MaBitmap(bmpExtension, 4);
        return extension;
    }

    /**
     * fonction qui retourne l'image dans le sens contraire des aiguilles d'une montre. On
     * réattribue chaque pixel à la position équivalente dans l'image retournée via leurs tableaux.
     * @return l'image retournée vers la gauche
     */
    public MaBitmap rotationGauche(){
        int[] pixelsGauche = new int[hauteur * largeur];
        for (int y = 0; y < hauteur; ++y) {
            for (int x = 0 ; x < largeur ; ++x) {
                pixelsGauche[( largeur - x - 1) * hauteur + y] = pixels[y * largeur + x];
            }
        }
        //copie de l'image dans la liste servant d'historique de modifications
        Bitmap bmpGauche = Bitmap.createBitmap(pixelsGauche, hauteur, largeur, Bitmap.Config.ARGB_8888);
        MaBitmap gauche = new MaBitmap(bmpGauche, filtre);
        return gauche;
    }

    /**
     * fonction qui retourne l'image dans le sens des aiguilles d'une montre. On réattribue chaque
     * pixel à la position correspondante dans l'image retournée via leurs tableaux.
     * @return l'image retournée vers la droite
     */
    public MaBitmap rotationDroit(){
        int[] pixelsRight = new int[hauteur * largeur];
        for (int y = 0; y < hauteur; ++y) {
            for (int x = 0 ; x < largeur ; ++x) {
                pixelsRight[(x+1)*hauteur - y - 1] = pixels[y * largeur + x];
            }
        }
        //copie de l'image dans la liste servant d'historique de modifications
        Bitmap bmpRight = Bitmap.createBitmap(pixelsRight, hauteur, largeur, Bitmap.Config.ARGB_8888);
        MaBitmap right = new MaBitmap(bmpRight, filtre);
        return right;
    }

    /**
     * fonction qui retourne l'image suivant un axe horizontal (reflet dans l'eau). On réattribue
     * chaque pixel à la position correspondante dans l'image retournée via leurs tableaux.
     * @return l'image retournée horizontalement
     */
    public MaBitmap miroirHorizontal(){
        int[] pixelsMH = new int[largeur * hauteur];
        for (int y = 0; y < hauteur; ++y) {
            for (int x = 0 ; x < largeur ; ++x) {
                pixelsMH[(y+1) * largeur - x - 1] = pixels[y * largeur + x];
            }
        }
        //copie de l'image dans la liste servant d'historique de modifications
        Bitmap bmpMH = Bitmap.createBitmap(pixelsMH, largeur, hauteur, Bitmap.Config.ARGB_8888);
        MaBitmap miroirH = new MaBitmap(bmpMH, filtre);
        return miroirH;
    }

    /**
     * fonction qui retourne l'image suivant un axe vertical (reflet dans un miroir). On réattribue
     * chaque pixel à la position correspondante dans l'image retournée via leurs tableaux.
     * @return l'image retournée verticalement
     */
    public MaBitmap miroirVertical(){
        int[] pixelsMV = new int[largeur * hauteur];
        for (int y = 0; y < hauteur; ++y) {
            for (int x = 0 ; x < largeur ; ++x) {
                pixelsMV[(hauteur - y - 1) * largeur + x] = pixels[y * largeur + x];
            }
        }
        //copie de l'image dans la liste servant d'historique de modifications
        Bitmap bmpMV = Bitmap.createBitmap(pixelsMV, largeur, hauteur, Bitmap.Config.ARGB_8888);
        MaBitmap miroirV = new MaBitmap(bmpMV, filtre);
        return miroirV;
    }

    //TODO changer nom
    //TODO what x2 ?
    public MaBitmap lowerRes() {
        if (largeur * hauteur > 1000000) {
            int nl, nh;
            float fact;
            if (largeur > hauteur) {
                nl = 800;
                nh = nl * hauteur / largeur;
                fact = largeur / 800F;
            } else {
                nh = 800;
                nl = nh * largeur / hauteur;
                fact = hauteur / 800F;
            }
            int[] pixelsSous = new int[nl * nh];
            int vX, vY;
            for (int y = 0; y < nh; y++) {
                for (int x = 0; x < nl; x++) {
                    vX = (int) (x * fact);
                    vY = (int) (y * fact);
                    pixelsSous[y * nl + x] = pixels[vY * largeur + vX];
                }
            }
            Bitmap bmpSous = Bitmap.createBitmap(nl, nh, Bitmap.Config.ARGB_8888);
            bmpSous.setPixels(pixelsSous, 0, nl, 0, 0, nl, nh);
            MaBitmap sous = new MaBitmap(bmpSous, filtre);
        }
        return this;
    }

    //TODO com + javadoc
    private MaBitmap convolution(int[][] masque, int fact, int filt) {
        int n = masque.length / 2;
        int[] pixelsConv = new int[hauteur * largeur];
        int blanc = Color.rgb(255,255,255);
        int R, G, B, sommeR, sommeG, sommeB, pixel;
        float coef_masque;
        //Bordures haut et bas en blanc
        for (int y = 0; y < n; y++) {
            for (int x = 0; x < largeur; x++) {
                pixelsConv[y * largeur + x] = blanc;
                pixelsConv[(hauteur - y - 1) * largeur + x] = blanc;
            }
        }
        //Bordures gauche et droite en blanc
        for (int x = 0; x < n; x++) {
            for (int y = n; y < hauteur - n; y++) {
                pixelsConv[y * largeur + x] = blanc;
                pixelsConv[(y + 1) * largeur - x - 1] = blanc;
            }
        }
        //Convolution
        for (int y = n; y < hauteur - n; y++) {
            for (int x = n; x < largeur - n; x++) {
                sommeR = 0;
                sommeG = 0;
                sommeB = 0;
                for (int j = -n; j <= n; j++) {
                    for (int i = -n; i <= n; i++) {
                        coef_masque = masque[j + n][i + n];
                        pixel = pixels[(y + j) * largeur + x + i];
                        //For every RGB componants, multiplies by convolution matrix coefficient
                        sommeR += coef_masque * Color.red(pixel);
                        sommeG += coef_masque * Color.green(pixel);
                        sommeB += coef_masque * Color.blue(pixel);
                    }
                }
                R = sommeR / fact;
                G = sommeG / fact;
                B = sommeB / fact;

                pixelsConv[y * largeur + x] = Color.rgb(R, G, B);
            }
        }
        Bitmap bmpConv = Bitmap.createBitmap(pixelsConv, largeur, hauteur, Bitmap.Config.ARGB_8888);
        MaBitmap conv = new MaBitmap(bmpConv, filt);
        return conv;
    }

    //TODO com + javadoc

    //TODO changer nom
    /**
     * fonction qui applique un filtre moyenneur via la fonction de convolution. On définit un
     * masque (matrice) entièrement à 1 qu'on applique à l'image dans la fonction de convolution.
     * @param n taille du masque à appliquer
     * @return l'image avec le filtre moyenneur
     */
    public MaBitmap moyenneur(int n) {
        int[][] masque = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {masque[i][j] = 1;}
        }
        return convolution(masque, n*n, 5);
    }

    /**
     * fonction qui applique un filtre gaussien sur l'image. Pour ce faire, on définit un masque
     * (matrice) précis à appliquer via la fonction de convolution.
     * @return l'image avec un filtre gaussien
     */
    public MaBitmap gauss(){
        int[][] masque ={{1,2,3,2,1},{2,6,8,6,2},{3,8,10,8,3},{2,6,8,6,2},{1,2,3,2,1}};
        return convolution(masque, 98, 6);
    }

    public int[][] convolutionBorders(int[][] masque) {
        int n = masque.length / 2;
        int[][] pixelsConvRGB = new int[hauteur * largeur][3];
        int sommeR, sommeG, sommeB;
        int pixel;
        //Keeps original values for the borders
        for (int y = 0; y < n; y++) {
            for (int x = 0; x < largeur; x++) {
                pixel = pixels[y * largeur + x];
                pixelsConvRGB[y * largeur + x][0] = Color.red(pixel);
                pixelsConvRGB[y * largeur + x][1] = Color.green(pixel);
                pixelsConvRGB[y * largeur + x][2] = Color.blue(pixel);
                pixelsConvRGB[(hauteur - y - 1) * largeur + x][0] = Color.red(pixel);
                pixelsConvRGB[(hauteur - y - 1) * largeur + x][1] = Color.green(pixel);
                pixelsConvRGB[(hauteur - y - 1) * largeur + x][2] = Color.blue(pixel);
            }
        }
        for (int y = hauteur - n; y < hauteur - n; y++) {
            for (int x = 0; x < n; x++) {
                pixel = pixels[y * largeur + x];
                pixelsConvRGB[y * largeur + x][0] = Color.red(pixel);
                pixelsConvRGB[y * largeur + x][1] = Color.green(pixel);
                pixelsConvRGB[y * largeur + x][2] = Color.blue(pixel);
                pixelsConvRGB[(y + 1) * largeur - x - 1][0] = Color.red(pixel);
                pixelsConvRGB[(y + 1) * largeur - x - 1][1] = Color.green(pixel);
                pixelsConvRGB[(y + 1) * largeur - x - 1][2] = Color.blue(pixel);
            }
        }
        //Convolution avoiding borders
        float coef_masque;
        for (int y = n; y < hauteur - n; y++) {
            for (int x = n; x < largeur - n; x++) {
                sommeR = 0;
                sommeG = 0;
                sommeB = 0;
                for (int j = -n; j <= n; j++) {
                    for (int i = -n; i <= n; i++) {
                        coef_masque = masque[j + n][i + n];
                        pixel = pixels[(y + j) * largeur + x + i];
                        //For every RGB componants, multiplies by convolution matrix coefficient
                        sommeR += coef_masque * Color.red(pixel);
                        sommeG += coef_masque * Color.green(pixel);
                        sommeB += coef_masque * Color.blue(pixel);
                    }
                }
                if (sommeR < 0) {
                    sommeR = -sommeR;
                }
                if (sommeG < 0) {
                    sommeG = -sommeG;
                }
                if (sommeB < 0) {
                    sommeB = -sommeB;
                }
                pixelsConvRGB[y * largeur + x][0] = sommeR;
                pixelsConvRGB[y * largeur + x][1] = sommeG;
                pixelsConvRGB[y * largeur + x][2] = sommeB;
            }
        }
        return pixelsConvRGB;
    }

    public MaBitmap sobel(){
        int R,G,B;
        //applicates convolution with hx et hy matrix
        int[][] hx = {{-1,0,1},{-2,0,2},{-1,0,1}};
        int[][] Gx = convolutionBorders(hx);

        int[][] hy = {{-1,-2,-1},{0,0,0},{1,2,1}};
        int[][] Gy = convolutionBorders(hy);

        int[][] norme = new int[largeur*hauteur][3];

        int[] pixelsSobel = new int[largeur*hauteur];
        int max = 0;
        for (int i=0; i<hauteur*largeur; i++) {
            norme[i][0] = (int) Math.sqrt(Math.pow(Gx[i][0], 2) + Math.pow(Gy[i][0], 2));
            norme[i][1] = (int) Math.sqrt(Math.pow(Gx[i][1], 2) + Math.pow(Gy[i][1], 2));
            norme[i][2] = (int) Math.sqrt(Math.pow(Gx[i][2], 2) + Math.pow(Gy[i][2], 2));
            if (norme[i][0] > max){max = norme[i][0];}
            if (norme[i][1] > max){max = norme[i][1];}
            if (norme[i][2] > max){max = norme[i][2];}
        }
        if (max!=0){
            for (int i=0; i<hauteur*largeur; i++) {
                R = norme[i][0] * 255 / max;
                G = norme[i][1] * 255 / max;
                B = norme[i][2] * 255 / max;
                pixelsSobel[i] = Color.rgb(R,G,B);
            }
        }
        Bitmap bmpSobel = Bitmap.createBitmap(pixelsSobel, largeur, hauteur, Bitmap.Config.ARGB_8888);
        MaBitmap sobel = new MaBitmap(bmpSobel, 7);
        return sobel;
    }

    public MaBitmap laplacien(){
        int R,G,B;
        //applicates convolution with hx et hy matrix
        int[][] masque = {{1,1,1},{1,-8,1},{1,1,1}};
        int[][] Gl = convolutionBorders(masque);

        int[] pixelsLapla = new int[largeur*hauteur];
        int max = 0;
        for (int i=0; i<hauteur*largeur; i++) {
            if (Gl[i][0] > max){max = Gl[i][0];}
            if (Gl[i][1] > max){max = Gl[i][1];}
            if (Gl[i][2] > max){max = Gl[i][2];}
        }
        if (max!=0){
            for (int i=0; i<hauteur*largeur; i++) {
                R = Gl[i][0] * 255 / max;
                G = Gl[i][1] * 255 / max;
                B = Gl[i][2] * 255 / max;
                pixelsLapla[i] = Color.rgb(R,G,B);
            }
        }
        Bitmap bmpLapla = Bitmap.createBitmap(pixelsLapla, largeur, hauteur, Bitmap.Config.ARGB_8888);
        MaBitmap laplacien = new MaBitmap(bmpLapla, 8);
        return laplacien;
    }

    public MaBitmap applyFilter(int filtUtil, int v1, int v2, int v3){
        if (filtUtil == 1){
            return selectHue(v1, v2);
        }
        if (filtUtil == 2){
            return filterHue(v1);
        }
        if (filtUtil == 3){
            return cartoonBorders(1F - (float)v1/100F);
        }
        if (filtUtil == 4){
            return changeHSV(v1, v2, v3);
        }
        return null;
    }

    private MaBitmap selectHue(int pas, int teinte){
        int[] pixelsSelect = pixels.clone();
        //Array to stock pixel hsv values
        float[] pixelHSV = new float[3];

        int index = 0;
        //3 cases: each one changes the way we choose the pixels we change to gray and the one we keep colored
        //Chosen teinte value is not too close to 0 or 360
        if ((teinte >= pas)&&(teinte<=360F - pas)){
            for (int y = 0; y < hauteur; ++y) {
                for (int x = 0; x < largeur; ++x) {
                    int pixel = pixelsSelect[index];
                    Color.colorToHSV(pixel, pixelHSV);
                    float teintePixel = pixelHSV[0];
                    //Get pixels out of the gap defined around teinte value in gray
                    if ((teintePixel >= teinte + pas) || (teintePixel <= teinte - pas)) {
                        int rouge = Color.red(pixel);
                        int bleu = Color.blue(pixel);
                        int vert = Color.green(pixel);
                        int gris = (rouge + bleu + vert) / 3;
                        pixelsSelect[index] = Color.rgb(gris, gris, gris);
                    }
                    index++;
                }
            }
        }
        //Chosen teinte value is close to 0
        else if (teinte < pas){
            for (int y = 0; y < hauteur; ++y) {
                for (int x = 0; x < largeur; ++x) {
                    int pixel = pixelsSelect[index];
                    Color.colorToHSV(pixel, pixelHSV);
                    float pixelHue = pixelHSV[0];
                    if ((pixelHue >= teinte + pas) && (pixelHue <= teinte + 360F - pas)) {
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
            for (int y = 0; y < hauteur; ++y) {
                for (int x = 0; x < largeur; ++x) {
                    int pixel = pixelsSelect[index];
                    Color.colorToHSV(pixel, pixelHSV);
                    float pixelHue = pixelHSV[0];
                    if ((pixelHue >= teinte -360F + pas) && (pixelHue <= teinte - pas)) {
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
        Bitmap bmpSelect = Bitmap.createBitmap(pixelsSelect, largeur, hauteur, Bitmap.Config.ARGB_8888);
        MaBitmap select = new MaBitmap(bmpSelect, 10);
        return select;
    }

    public MaBitmap filterHue(int teinte){
        int[] pixelsFilt = new int[largeur * hauteur];
        float[] pixelHSV = new float[3];

        for (int i = 0; i < largeur*hauteur; ++i) {
            int pixel = pixels[i];
            Color.colorToHSV(pixel, pixelHSV);
            pixelHSV[0] = teinte;
            pixelsFilt[i] = Color.HSVToColor(pixelHSV);
        }
        Bitmap bmpFilt = Bitmap.createBitmap(pixelsFilt, largeur, hauteur, Bitmap.Config.ARGB_8888);
        MaBitmap filtre = new MaBitmap(bmpFilt, 11);
        return filtre;
    }

    public MaBitmap changeHSV(int teinte, int sat, int val){
        int[] pixelsFilt = new int[largeur * hauteur];
        float[] pixelHSV = new float[3];
        for (int i = 0; i < largeur*hauteur; ++i) {
            int pixel = pixels[i];
            Color.colorToHSV(pixel, pixelHSV);
            pixelHSV[0] = pixelHSV[0] + teinte;
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
            pixelsFilt[i] = Color.HSVToColor(pixelHSV);
        }
        Bitmap bmpFilt = Bitmap.createBitmap(pixelsFilt, largeur, hauteur, Bitmap.Config.ARGB_8888);
        MaBitmap filtre = new MaBitmap(bmpFilt, 11);
        return filtre;
    }

    public MaBitmap visualCrop(int[] zone){
        int[] pixelsRogne = pixels.clone();
        int RogneHaut = zone[0] * hauteur / 100;
        int RogneBas = hauteur - zone[1] * hauteur / 100;
        int RogneGauche = zone[2] * largeur / 100;
        int RogneDroite = largeur - zone[3] * largeur / 100;

        for (int y = RogneHaut; y < RogneHaut + 4; ++y) {
            for (int x = RogneGauche ; x < RogneDroite ; ++x) {
                pixelsRogne[ y * largeur + x ] = Color.rgb(0,0,0);
            }
        }
        for (int y = RogneBas - 4; y < RogneBas; ++y) {
            for (int x = RogneGauche; x < RogneDroite; ++x) {
                pixelsRogne[ y * largeur + x ] = Color.rgb(0,0,0);
            }
        }
        for (int x = RogneGauche; x < RogneGauche + 4; ++x) {
            for (int y = RogneHaut; y < RogneBas; ++y) {
                pixelsRogne[ y * largeur + x ] = Color.rgb(0,0,0);
            }
        }
        for (int x = RogneDroite - 4; x < RogneDroite; ++x) {
            for (int y = RogneHaut; y < RogneBas; ++y) {
                pixelsRogne[ y * largeur + x ] = Color.rgb(0,0,0);
            }
        }
        Bitmap bmprogne = Bitmap.createBitmap(pixelsRogne, largeur, hauteur, Bitmap.Config.ARGB_8888);
        MaBitmap rogne = new MaBitmap(bmprogne, filtre);
        return rogne;
    }

    public MaBitmap crop(int[] zone){
        int RogneHaut = zone[0] * hauteur / 100;
        int RogneBas = hauteur - zone[1] * hauteur / 100;
        int RogneGauche = zone[2] * largeur / 100;
        int RogneDroite = largeur - zone[3] * largeur / 100;
        int nl = RogneDroite - RogneGauche;
        int nh = RogneBas - RogneHaut;
        Bitmap bmpRogne = Bitmap.createBitmap(bmp, RogneGauche,RogneHaut,nl, nh);
        MaBitmap rogne = new MaBitmap(bmpRogne, filtre);
        return rogne;
    }

    // Code de Pratik sur stackoverflow.com
    private MaBitmap colorDodgeBlend(MaBitmap calque) {
        Bitmap base = bmp.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap mélange = calque.bmp.copy(Bitmap.Config.ARGB_8888, false);

        IntBuffer buffBase = IntBuffer.allocate(largeur * hauteur);
        base.copyPixelsToBuffer(buffBase);
        buffBase.rewind();

        IntBuffer buffMelange = IntBuffer.allocate(calque.largeur * calque.hauteur);
        mélange.copyPixelsToBuffer(buffMelange);
        buffMelange.rewind();

        IntBuffer buffO = IntBuffer.allocate(largeur * hauteur);
        buffO.rewind();

        while (buffO.position() < buffO.limit()) {

            int filtEntier = buffMelange.get();
            int srcEntier = buffBase.get();

            int filtRouge = Color.red(filtEntier);
            int filtVert = Color.green(filtEntier);
            int filtBleu = Color.blue(filtEntier);

            int srcRouge = Color.red(srcEntier);
            int srcVert = Color.green(srcEntier);
            int srcBleu = Color.blue(srcEntier);

            int finalRouge = colordodge(filtRouge, srcRouge);
            int finalVert = colordodge(filtVert, srcVert);
            int finalBleu = colordodge(filtBleu, srcBleu);


            int pixel = Color.argb(255, finalRouge, finalVert, finalBleu);


            buffO.put(pixel);
        }

        buffO.rewind();

        base.copyPixelsFromBuffer(buffO);
        mélange.recycle();

        return new MaBitmap(base, 17);
    }

    private int colordodge(int in1, int in2) {
        float image = (float)in2;
        float masque = (float)in1;
        return ((int) ((image == 255) ? image:Math.min(255, (((long)masque << 8 ) / (255 - image)))));
    }

    /**
     * fonction qui applique un effet crayon sur l'image. On applique successivement les fonctions
     * de niveaux de gris, la fonction pour inverser les couleurs et le filtre gaussien.
     * On applique finalement la fonction TODO
     * @return l'image en effet crayon
     */
    public MaBitmap pencilSketch(){
        MaBitmap gris = enGris();
        MaBitmap inverse = gris.inverser();
        MaBitmap gauss = inverse.gauss();
        return gris.colorDodgeBlend(gauss);
    }

    public MaBitmap closestNeighbor(int nl, int nh){
        float factX = (float) nl/largeur;
        float factY = (float) nh/hauteur;

        int[] pixelsCN = new int [nl*nh];
        for (int x = 0; x < nl; x++) {
            for (int y = 0; y < nh; y++) {
                pixelsCN[y * nl + x] = pixels[(int)(y / factY) * largeur + (int)(x / factX)];
            }
        }
        Bitmap bmpCN = Bitmap.createBitmap(pixelsCN, nl, nh, Bitmap.Config.ARGB_8888);
        MaBitmap cn = new MaBitmap(bmpCN, 19);
        return cn;
    }

    private void findAreaColor(int x, int y, int l, int h, int[] sommeRGB, int cmpCouleur){
        int index = y*l+x;
        bordures[index]=cmpCouleur;
        int pixel = pixels[index];
        sommeRGB[0] += Color.red(pixel);
        sommeRGB[1] += Color.green(pixel);
        sommeRGB[2] += Color.blue(pixel);
        sommeRGB[3] ++;
        if ((x>0) && (bordures[index-1]==0)){findAreaColor(x-1, y, l, h, sommeRGB, cmpCouleur);}
        if ((x<l-1)&&(bordures[index+1]==0)){findAreaColor(x+1, y, l, h, sommeRGB, cmpCouleur);}
        if ((y>0)&&(bordures[index-l]==0)){findAreaColor(x, y-1, l, h, sommeRGB, cmpCouleur);}
        if ((y<h-1)&&(bordures[index+l]==0)){findAreaColor(x, y+1, l, h, sommeRGB, cmpCouleur);}
    }

    public MaBitmap cartoon() {
        bordures = new int[largeur * hauteur];
        mapSobel = this.sobel().pixels.clone();
        int pixel, r, g, b;
        //Fills mapBorders with -1 at borders
        for (int i=0; i<3*largeur; i ++){
            bordures[i] = -1;
            bordures[largeur*hauteur - 1 - i] = -1;
        }
        for (int y=3; y<hauteur -3; y++){
            for (int x=0; x<3; x++) {
                bordures[y * largeur + x] = -1;
                bordures[(y+1) * largeur - x - 1] = -1;
            }
        }
        for (int i = 0; i < largeur * hauteur; i++) {
            pixel = mapSobel[i];
            r = Color.red(pixel);
            g = Color.green(pixel);
            b = Color.blue(pixel);
            if (r + b + g > 30) {
                bordures[i] = -1;
            }
        }
        int[] couleurs = new int[largeur*hauteur];
        int[] couleurBase = new int[1000];
        int cmpBaseC = 0;
        int cmpCouleur = 1;
        int[] sommeRGB = new int[4];
        int couleur;
        int i;
        int dr, dg, db;
        boolean resume;
        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                if (bordures[y * largeur + x] == 0) {
                    Arrays.fill(sommeRGB, 0);
                    findAreaColor(x, y, largeur, hauteur, sommeRGB, cmpCouleur);
                    r = sommeRGB[0] / sommeRGB[3];
                    g = sommeRGB[1] / sommeRGB[3];
                    b = sommeRGB[2] / sommeRGB[3];
                    if (cmpBaseC == 0){
                        couleurBase[cmpBaseC] = Color.rgb(r, g, b);
                        couleurs[cmpCouleur] = cmpBaseC;
                        cmpBaseC++;
                    }
                    else {
                        i = 0;
                        resume = true;
                        while ((i < cmpBaseC)&&(resume)){
                            couleur = couleurBase[i];
                            dr = r - Color.red(couleur);
                            dg = g - Color.green(couleur);
                            db = b - Color.blue(couleur);
                            if (2*dr*dr + 4*dg*dg + 3*db*db < 5000) {
                                couleurs[cmpCouleur] = i;
                                resume = false;
                            }
                            i++;
                        }
                        if (resume) {
                            couleurBase[cmpBaseC] = Color.rgb(r, g, b);
                            couleurs[cmpCouleur] = cmpBaseC;
                            cmpBaseC++;
                        }
                    }
                    cmpCouleur++;
                }
            }
        }

        int[] pixelsCartoon = new int[largeur* hauteur];
        for (int j = 0; j < largeur * hauteur; j++) {
            if (bordures[j] != -1) {
                pixelsCartoon[j] = couleurBase[couleurs[bordures[j]]];
            }
            else{
                pixel = pixels[j];
                r = Color.red(pixel);
                g = Color.green(pixel);
                b = Color.blue(pixel);
                i = 0;
                resume = true;
                while ((i < cmpBaseC)&&(resume)){
                    couleur = couleurBase[i];
                    dr = r - Color.red(couleur);
                    dg = g - Color.green(couleur);
                    db = b - Color.blue(couleur);
                    if (2*dr*dr + 4*dg*dg + 3*db*db < 5000) {
                        pixelsCartoon[j] = couleur;
                        resume = false;
                    }
                    i++;
                }
                if (resume){
                    pixelsCartoon[j]=pixels[j];
                }
            }
        }
        /*int couleur;
        int[] sommeRGB = new int[4];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (borders[y * width + x] == 0) {
                    Arrays.fill(sommeRGB, 0);
                    findAreaColor(x, y, width, height, sommeRGB);
                    r = sommeRGB[0] / sommeRGB[3];
                    g = sommeRGB[1] / sommeRGB[3];
                    b = sommeRGB[2] / sommeRGB[3];
                    couleur = Color.rgb(r, g, b);
                    paintArea(x, y, width, height, couleur);
                }
            }
        }*/
        Bitmap bmpCartoon = Bitmap.createBitmap(pixelsCartoon, largeur, hauteur, Bitmap.Config.ARGB_8888);
        MaBitmap cartoon = new MaBitmap(bmpCartoon, 14);
        cartoon.bordures = this.bordures.clone();
        cartoon.mapSobel = this.mapSobel.clone();
        return cartoon;
    }

    //TODO javadoc et comms
    public MaBitmap cartoonBorders(float lvl){
        float[] hsv = new float[3];
        int[] pixelsCartoon = pixels.clone();
        for (int i = 0; i < largeur * hauteur; i++) {
            if (bordures[i] == -1) {
                Color.colorToHSV(mapSobel[i], hsv);
                if (hsv[2] > lvl) {
                    int v = (int) (100 * (1 - hsv[2]));
                    pixelsCartoon[i] = Color.rgb(v, v, v);
                }
            }
        }
        Bitmap bmpCartoon = Bitmap.createBitmap(pixelsCartoon, largeur, hauteur, Bitmap.Config.
                ARGB_8888);
        MaBitmap cartoon = new MaBitmap(bmpCartoon, 14);
        cartoon.bordures = this.bordures.clone();
        cartoon.mapSobel = this.mapSobel.clone();
        return cartoon;
    }

    //TODO Finir application avec doigt
    public MaBitmap fingerApply(MaBitmap applique, int departX, int departY, int finX, int finY){
        if (departX < finX){
            return fingerApply(applique, finX, finY, departX, departY);
        }
        for (int i = departX*departY; i < finX * finY; i++ ) {
            applique.pixels[i] = Color.rgb(255, 255, 255);
        }
        /*int range = Math.min(width,height)/10;
        int[] pixelsFinger = pixels.clone();
        int x = departX;
        int y = departY;
        int startI, startJ;
        if (finX == departX){
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
                    pixelsFinger[(y+j) * width + (x+i)] = applique.pixels[(y+j) * width + (x+i)];
                }
            }
        }
        else {
            int step = (finY - departY) * range / (finX - departX);
            int maxX = Math.min(width, finX);
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
                        pixelsFinger[(y + j) * width + (x + i)] = applique.pixels[(y + j) * width + (x + i)];
                    }
                }
                x += range;
                y += step;
            }
        }
        Bitmap bmpFinger = Bitmap.createBitmap(pixelsFinger, width, height, Bitmap.Config.ARGB_8888);
        MaBitmap finger = new MaBitmap(bmpFinger, 18);
        return finger;*/
        return applique;
    }
}
