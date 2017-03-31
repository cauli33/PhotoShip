package com.example.emmaveillat.finalproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/**
 * This class applies a sepia filter on the bitmap.
 * @author emmaveillat
 */
public class SepiaMenu extends AppCompatActivity {

    /**
     * the bitmap modified
     */
    static Bitmap picture;

    /**
     * Bitmaps used to be modifies
     */
    Bitmap newPic, pictureToUse;

    /**
     * Buttons to save, reset or apply the filter
     */
    Button save, sepia, reset;

    /**
     * The bitmap displayed in the menu
     */
    ImageView img;

    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sepia_menu);

        //Gets picture Bitmap chosen in the gallery
        pictureToUse = PhotoLoading.scaleImage();

        picture = pictureToUse.copy(Bitmap.Config.ARGB_8888, true);
        newPic = picture.copy(Bitmap.Config.ARGB_8888, true);

        img = (ImageView) findViewById(R.id.picture);
        img.setImageBitmap(picture);

        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(blistener);

        sepia = (Button) findViewById(R.id.sepia);
        sepia.setOnClickListener(blistener);

        reset = (Button) findViewById(R.id.reset);
        reset.setOnClickListener(blistener);
    }

    //gère le click sur une action de l'ActionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save:
                //save();
                return true;
            case R.id.action_delete:
                //delete();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        //ajoute les entrées de menu_test à l'ActionBar
        getMenuInflater().inflate(R.menu.menu_test, menu);
        return true;
    }

    /**
     * function which tranforms the bitmap colors in sepia
     * @param bmpOriginal the original bitmap
     */
    public void toSepia(Bitmap bmpOriginal) {
        int width, height, r, g, b, c, gry;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();
        int depth = 20;
        Canvas canvas = new Canvas(newPic);
        Paint paint = new Paint();
        //Defines a matrix to create a sepia mask
        ColorMatrix cm = new ColorMatrix();
        cm.setScale(.3f, .3f, .3f, 1.0f);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        canvas.drawBitmap(bmpOriginal, 0, 0, paint);
        //Applies the mask on the bitmap depending on its levels of red, blue and green
        for(int x=0; x < width; x++) {
            for(int y=0; y < height; y++) {
                c = bmpOriginal.getPixel(x, y);

                r = Color.red(c);
                g = Color.green(c);
                b = Color.blue(c);

                gry = (r + g + b) / 3;
                r = g = b = gry;

                r = r + (depth * 2);
                g = g + depth;

                if(r > 255) {
                    r = 255;
                }
                if(g > 255) {
                    g = 255;
                }
                newPic.setPixel(x, y, Color.rgb(r, g, b));
            }
        }
    }


    private View.OnClickListener blistener = new View.OnClickListener(){
        public void onClick(View v){
            switch (v.getId()) {

                //Undoes changes by getting the original picture back
                case R.id.reset:
                    picture = pictureToUse.copy(Bitmap.Config.ARGB_8888, true);
                    img.setImageBitmap(picture);
                    break;

                //Saves image in the gallery
                case R.id.save:
                    MediaStore.Images.Media.insertImage(getContentResolver(), newPic, PhotoLoading.imgDecodableString + "_sepia" , "");
                    Intent second = new Intent(SepiaMenu.this, PhotoLoading.class);
                    startActivity(second);
                    break;

                //Applicates sepia filter
                case R.id.sepia:
                    toSepia(picture);
                    img.setImageBitmap(newPic);
                    break;

                default:
                    break;
            }
        }
    };
}
