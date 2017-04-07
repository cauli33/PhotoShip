package com.example.emmaveillat.finalproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Arrays;

/**
 * This class applies convolutions and blurs to a bitmap thanks to some matrix.
 * @author caulihonore
 */
public class ConvolutionMenu extends AppCompatActivity {

    /**
     * Bitmaps used to be modified
     */
    Bitmap pictureToUse, picture;

    /**
     * lenght
     */
    int length;

    /**
     * Text choosen by the user
     */
    EditText lengthText;

    /**
     * Buttons to apply functions, to save or reset a bitmap
     */
    Button save, reset, moy, sobel, gauss, lapla, cartoon;

    /**
     * The bitmap displayed in the menu
     */
    ImageView imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convolution_menu);

        // Creates textzone to choose matrix length for moyenne
        lengthText = (EditText) findViewById(R.id.newLength);
        lengthText.setHint("Notez ici la taille de la matrice (impair)");

        // Gets picture Bitmap chosen in the gallery
        pictureToUse = PhotoLoading.scaleImage();

        picture = pictureToUse.copy(Bitmap.Config.ARGB_8888, true);

        imgView = (ImageView) findViewById(R.id.picture);
        imgView.setImageBitmap(picture);

        reset = (Button) findViewById(R.id.reset);
        reset.setOnClickListener(blistener);

        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(blistener);

        moy = (Button) findViewById(R.id.moy);
        moy.setOnClickListener(blistener);

        sobel = (Button) findViewById(R.id.sobel);
        sobel.setOnClickListener(blistener);

        gauss = (Button) findViewById(R.id.gauss);
        gauss.setOnClickListener(blistener);

        lapla = (Button) findViewById(R.id.lapla);
        lapla.setOnClickListener(blistener);

        cartoon = (Button) findViewById(R.id.cartoon);
        cartoon.setOnClickListener(blistener);
    }


    public Bitmap reduce(Bitmap bmp){
        int oldWidth = bmp.getWidth();
        int oldHeight = bmp.getHeight();
        if (oldWidth*oldHeight> 1000000) {
            int newWidth, newHeight;
            float fact;
            if (oldWidth > oldHeight) {
                newWidth = 800;
                newHeight = newWidth * oldHeight / oldWidth;
                fact = oldWidth / 800F;
            } else {
                newHeight = 800;
                newWidth = newHeight * oldWidth / oldHeight;
                fact = oldHeight / 800F;
            }
            int[] mapSrc = new int[oldWidth * oldHeight];
            bmp.getPixels(mapSrc, 0, oldWidth, 0, 0, oldWidth, oldHeight);
            int[] mapDest = new int[newWidth * newHeight];
            int oldx, oldy;
            for (int y = 0; y < newHeight; y++) {
                for (int x = 0; x < newWidth; x++) {
                    oldx = (int) (x * fact);
                    oldy = (int) (y * fact);
                    mapDest[y * newWidth + x] = mapSrc[ oldy * oldWidth + oldx];
                }
            }
            Bitmap dest = Bitmap.createBitmap(newWidth,newHeight, Bitmap.Config.ARGB_8888);
            dest.setPixels(mapDest, 0, newWidth, 0, 0, newWidth, newHeight);
            return dest;
        }
        return bmp;
    }

    public void moyenne(Bitmap bmp, int n) {
        //Creates a matrix full of ones and applicates convolution (and divides by the number of pixels used)
        int[][] mask = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                mask[i][j] = 1;
            }
        }
        convolution(bmp, mask, n*n);
    }

    public void gauss(Bitmap bmp){
        //Creates gaussian matrix and applicates convolution
        int[][] mask ={{1,2,3,2,1},{2,6,8,6,2},{3,8,10,8,3},{2,6,8,6,2},{1,2,3,2,1}};
        convolution(bmp, mask, 98);
    }

    public void sobel(Bitmap bmp){
        int R,G,B;
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        //applicates convolution with hx et hy matrix
        int[][] hx = {{-1,0,1},{-2,0,2},{-1,0,1}};
        int[][] Gx = convolutionBorders(bmp,hx);

        int[][] hy = {{-1,-2,-1},{0,0,0},{1,2,1}};
        int[][] Gy = convolutionBorders(bmp,hy);

        int[] map = new int[width*height];
        int max = 0;
        for (int i=0; i<height*width; i++) {
            R = (int) Math.sqrt(Math.pow(Gx[i][0], 2) + Math.pow(Gy[i][0], 2));
            G = (int) Math.sqrt(Math.pow(Gx[i][1], 2) + Math.pow(Gy[i][1], 2));
            B = (int) Math.sqrt(Math.pow(Gx[i][2], 2) + Math.pow(Gy[i][2], 2));
            if (R>255){R=255;}
            if (G>255){G=255;}
            if (B>255){B=255;}
            map[i] = Color.rgb(R,G,B);
        }
            /*Gx[i][0] = (int) Math.sqrt(Math.pow(Gx[i][0], 2) + Math.pow(Gy[i][0], 2));
            Gx[i][1] = (int) Math.sqrt(Math.pow(Gx[i][1], 2) + Math.pow(Gy[i][1], 2));
            Gx[i][2] = (int) Math.sqrt(Math.pow(Gx[i][2], 2) + Math.pow(Gy[i][2], 2));


            if(Gx[i][0] > max) { max = Gx[i][0]; }

            if(Gx[i][1] > max) { max = Gx[i][1]; }

            if(Gx[i][2] > max) { max = Gx[i][2]; }
        }
        for (int i=0; i<height*width; i++){
            R = Gx[i][0] * 255 / max;
            G = Gx[i][1] * 255 / max;
            B = Gx[i][2] * 255 / max;
            map[i] = Color.rgb(R,G,B);
        }*/

        bmp.setPixels(map, 0, width, 0, 0, width, height);
    }

    public int[][] convolutionBorders(Bitmap bmp, int[][] mask) {
        int n = mask.length / 2;
        if (n!=0) {
            int width = bmp.getWidth();
            int height = bmp.getHeight();
            int[] pixels = new int[height * width];
            int[][] pixelsConvRGB = new int[height*width][3];
            //Gets array of every pixels from the Bitmap
            bmp.getPixels(pixels, 0, width, 0, 0, width, height);
            int sumR, sumG, sumB;
            int pixel;
            //Keeps original values for the borders
            for (int y = 0; y < n; y++) {
                for (int x = 0; x < width; x++) {
                    pixel = pixels[y*width + x];
                    pixelsConvRGB[y * width + x][0] = Color.red(pixel);
                    pixelsConvRGB[y * width + x][1] = Color.green(pixel);
                    pixelsConvRGB[y * width + x][2] = Color.blue(pixel);
                }
            }
            for (int y = height - n; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    pixel = pixels[y*width + x];
                    pixelsConvRGB[y * width + x][0] = Color.red(pixel);
                    pixelsConvRGB[y * width + x][1] = Color.green(pixel);
                    pixelsConvRGB[y * width + x][2] = Color.blue(pixel);
                }
            }
            for (int x = 0; x < n; x++) {
                for (int y = n; y < height - n; y++) {
                    pixel = pixels[y*width + x];
                    pixelsConvRGB[y * width + x][0] = Color.red(pixel);
                    pixelsConvRGB[y * width + x][1] = Color.green(pixel);
                    pixelsConvRGB[y * width + x][2] = Color.blue(pixel);
                }
            }
            for (int x = width - n; x < height; x++) {
                for (int y = n; y < height - n; y++) {
                    pixel = pixels[y*width + x];
                    pixelsConvRGB[y * width + x][0] = Color.red(pixel);
                    pixelsConvRGB[y * width + x][1] = Color.green(pixel);
                    pixelsConvRGB[y * width + x][2] = Color.blue(pixel);
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
                    pixelsConvRGB[y*width + x][0]=sumR;
                    pixelsConvRGB[y*width + x][1]=sumG;
                    pixelsConvRGB[y*width + x][2]=sumB;
                }
            }
            return pixelsConvRGB;
        }
        return null;
    }

    public int[] minmax(int[][] pixelsRGB, int width, int height, int n){
        int[] minmax = new int[2];
        minmax[0]= (pixelsRGB[1][0] + pixelsRGB[1][1] + pixelsRGB[1][2]) / 3;
        minmax[1] = (pixelsRGB[1][0] + pixelsRGB[1][1] + pixelsRGB[1][2]) / 3;
        int moy;
        for (int y = n; y < height - n; y++) {
            for (int x = n; x < width - n; x++) {
                moy = (pixelsRGB[y*width+x][0] + pixelsRGB[y*width+x][1] + pixelsRGB[y*width+x][2]) / 3;
                if (moy < minmax[0]) {
                    minmax[0] = moy;
                }
                else if (moy > minmax[1]) {
                    minmax[1] = moy;
                }
            }
        }
        return minmax;
    }

    public void convolutionLaplacien(Bitmap bmp) {
        int[][] mask = {{1,1,1},{1,-8,1},{1,1,1}};
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int[] pixels = new int[height * width];
        int[][] pixelsConvRGB = convolutionBorders(bmp, mask);
        int[] minmax = minmax(pixelsConvRGB, width, height, 1);
        int min = minmax[0];
        int max = minmax[1];
        int white = Color.rgb(255, 255, 255);
        int R, G, B;

        if (min!=max) {
            //Bordures en noir
            for (int y = 0; y < height; y++) {
                pixels[y * width] = white;
                pixels[(y + 1) * width - 1] = white;
            }
            for (int x = 1; x < width - 1; x++) {
                pixels[x] = white;
                pixels[(height - 1) * width + x] = white;
            }
            int newmoy, moy;
            for (int y = 1; y < height - 1; y++) {
                for (int x = 1; x < width - 1; x++) {
                    R = pixelsConvRGB[y * width + x][0] - min;
                    G = pixelsConvRGB[y * width + x][1] - min;
                    B = pixelsConvRGB[y * width + x][2] - min;
                    moy = (R + G + B) / 3;
                    if (moy == 0){
                        pixels[y*width+x] = Color.rgb(0,0,0);
                    }
                    else {
                        newmoy = ((R + G + B) / 3) * 255 / (max - min);
                        R = (R - min) * newmoy / moy;
                        G = (G - min) * newmoy / moy;
                        B = (B - min) * newmoy / moy;
                        pixels[y * width + x] = Color.rgb(R, G, B);
                    }
                }
            }
            bmp.setPixels(pixels, 0, width, 0, 0, width, height);
        }
    }

    public void convolution(Bitmap bmp, int[][] mask, int factor) {
        int n = mask.length / 2;
        if (n!=0) {
            int width = bmp.getWidth();
            int height = bmp.getHeight();
            int[] pixels = new int[height * width];
            int[] pixelsConv = new int[height * width];
            //Gets array of every pixels from the Bitmap
            bmp.getPixels(pixels, 0, width, 0, 0, width, height);
            int A, R, G, B;
            int sumR, sumG, sumB;
            //Keeps original values for the borders
            for (int y = 0; y < n; y++) {
                for (int x = 0; x < width; x++) {
                    pixelsConv[y * width + x] = pixels[y * width + x];
                }
            }
            for (int y = height - n; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    pixelsConv[y * width + x] = pixels[y * width + x];
                }
            }
            for (int x = 0; x < n; x++) {
                for (int y = n; y < height - n; y++) {
                    pixelsConv[y * width + x] = pixels[y * width + x];
                }
            }
            for (int x = width - n; x < height; x++) {
                for (int y = n; y < height - n; y++) {
                    pixelsConv[y * width + x] = pixels[y * width + x];
                }
            }
            //Convolution avoiding borders
            for (int y = n; y < height - n; y++) {
                for (int x = n; x < width - n; x++) {
                    sumR = 0;
                    sumG = 0;
                    sumB = 0;
                    A = Color.alpha(pixels[y*width+x]);
                    for (int j = -n; j <= n; j++) {
                        for (int i = -n; i <= n; i++) {
                            float coef_mask = mask[j + n][i + n];
                            int pixel = pixels[(y + j) * width + x + i];
                            //For every RGB componants, multiplies by convolution matrix coefficient
                            sumR += coef_mask * Color.red(pixel);
                            sumG += coef_mask * Color.green(pixel);
                            sumB += coef_mask * Color.blue(pixel);
                        }
                    }

                    //If value is too low, sets to 0, if too up, sets to 255

                    R = sumR/factor;
                    if(R < 0) { R = 0; }
                    else if(R > 255) { R = 255; }

                    G = sumG/factor;
                    if(G < 0) { G = 0; }
                    else if(G > 255) { G = 255; }

                    B = sumB/factor;
                    if(B < 0) { B = 0; }
                    else if(B > 255) { B = 255; }
                    pixelsConv[y * width + x] = Color.argb(A, R, G, B);
                }
            }
            bmp.setPixels(pixelsConv, 0, width, 0, 0, width, height);
        }
    }

    public void findAreaColor(int[] src, int[] borders, int x, int y, int width, int height, int[] sumRGB, int nArea){
        int index = y*width+x;
        borders[index]=nArea;
        int pixel = src[index];
        sumRGB[0] += Color.red(pixel);
        sumRGB[1] += Color.green(pixel);
        sumRGB[2] += Color.blue(pixel);
        sumRGB[3] ++;
        if ((x>0) && (borders[index-1]==0)){findAreaColor(src, borders, x-1, y, width, height, sumRGB, nArea);}
        if ((x<width-1)&&(borders[index+1]==0)){findAreaColor(src, borders, x+1, y, width, height, sumRGB, nArea);}
        if ((y>0)&&(borders[index-width]==0)){findAreaColor(src, borders, x, y-1, width, height, sumRGB, nArea);}
        if ((y<height-1)&&(borders[index+width]==0)){findAreaColor(src, borders, x, y+1, width, height, sumRGB, nArea);}
    }

    public void cartoon(Bitmap bmp, Bitmap sobel) {
        try {
            int w = bmp.getWidth();
            int h = bmp.getHeight();
            int mapBorders[] = new int[w * h];
            int mapSrc[] = new int[w * h];
            bmp.getPixels(mapSrc, 0, w, 0, 0, w, h);
            int mapSobel[] = new int[w * h];
            sobel.getPixels(mapSobel, 0, w, 0, 0, w, h);
            int pixel, red, green, blue;
            //Fills mapBorders with -1 at borders
            for (int i = 0; i < w * h; i++) {
                pixel = mapSobel[i];
                red = Color.red(pixel);
                green = Color.green(pixel);
                blue = Color.blue(pixel);
                if (red + blue + green > 10) {
                    mapBorders[i] = -1;
                }
            }

            int[] colors = new int[w*h];
            int[] basicColors = new int[100];
            int countBasicColors = 0;
            int countColors = 0;
            int[] sumRGB = new int[4];
            float[] pixelHSV = new float[3];
            int i;
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    if (mapBorders[y * w + x] == 0) {
                        Arrays.fill(sumRGB, 0);
                        findAreaColor(mapSrc, mapBorders, x, y, w, h, sumRGB, countColors);
                        red = sumRGB[0] / sumRGB[3];
                        green = sumRGB[1] / sumRGB[3];
                        blue = sumRGB[2] / sumRGB[3];
                        i = 0;
                        while ((i < countBasicColors) && (red + green + blue - Color.green(basicColors[i]) - Color.blue(basicColors[i]) - Color.red(basicColors[i]) > 50)){i++;}
                        if (i<countBasicColors){
                            colors[countColors] = i;
                        }
                        else{
                            basicColors[countBasicColors] = Color.rgb(red, green, blue);
                            colors[countColors] = countBasicColors;
                            countBasicColors++;
                        }
                        countColors++;
                    }
                }
            }
            for (int j = 0; j < w * h; j++) {
                if (mapBorders[j] == -1) {
                    Color.colorToHSV(mapSobel[j], pixelHSV);
                    if (pixelHSV[2] > 0.5) {
                        int v = (int) (100 * (1 - pixelHSV[2]));
                        mapSrc[j] = Color.rgb(v, v, v);
                    }
                }
                else{
                    mapSrc[j] = basicColors[colors[mapBorders[j]]];
                }
            }
            bmp.setPixels(mapSrc, 0, w, 0, 0, w, h);

        } catch (StackOverflowError e) {
            AlertDialog.Builder reducedialog = new AlertDialog.Builder(ConvolutionMenu.this);
            reducedialog.setTitle("Échec")
                    .setMessage("Il se peut que la qualité de l'image soit trop élevée. Voulez-vous réduire la qualité?")
                    .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            picture = reduce(picture);
                            imgView.setImageBitmap(picture);
                        }
                    });

            AlertDialog alert = reducedialog.create();
            alert.show();
        }
    }


    private View.OnClickListener blistener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {

                //Undoes changes by getting the original picture back
                case R.id.reset:
                    picture = pictureToUse.copy(Bitmap.Config.ARGB_8888, true);
                    imgView.setImageBitmap(picture);
                    break;

                //Applicates 'moyenne' filter
                case R.id.moy:
                    try {
                        length = Integer.valueOf(lengthText.getText().toString());

                        if (length % 2 == 0) {
                            Toast evennotodd = Toast.makeText(getApplicationContext(), "Le paramètre du filtre moyenneur doit être impair", Toast.LENGTH_LONG);
                            evennotodd.show();
                        } else {
                            moyenne(picture, length);
                            imgView.setImageBitmap(picture);
                        }
                    }
                    catch(Exception e){
                        Toast novalue = Toast.makeText(getApplicationContext(), "Aucune valeur saisie", Toast.LENGTH_LONG);
                        novalue.show();
                    }
                    break;

                //Applicates Gaussian blur
                case R.id.gauss:
                    gauss(picture);
                    imgView.setImageBitmap(picture);
                    break;

                //Applicates Sobel filter
                case R.id.sobel:
                    sobel(picture);
                    imgView.setImageBitmap(picture);
                    break;

                //Applicates Laplacien filter
                case R.id.lapla:
                    convolutionLaplacien(picture);
                    imgView.setImageBitmap(picture);
                    break;

                case R.id.cartoon:
                    Bitmap sobel = picture.copy(Bitmap.Config.ARGB_8888, true);
                    sobel(sobel);
                    cartoon(picture,sobel);
                    imgView.setImageBitmap(picture);
                    break;

                //Saves image in the gallery
                case R.id.save:
                    try {
                        MediaStore.Images.Media.insertImage(getContentResolver(), picture, PhotoLoading.imgDecodableString + "_convolution", "");
                        Intent second = new Intent(ConvolutionMenu.this, PhotoLoading.class);
                        startActivity(second);
                        break;
                    } catch (Exception e){
                        e.printStackTrace();
                    }

                default:
                    break;
            }
        }
    };
}
