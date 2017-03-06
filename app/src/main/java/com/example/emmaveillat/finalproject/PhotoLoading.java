package com.example.emmaveillat.finalproject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import static com.example.emmaveillat.finalproject.R.id.imgView;

/**
 * This class helps the user to take a image from the galery or from the camera. Then it displays it to confirm his choice.
 * @author emmaveillat
 */
public class PhotoLoading extends Activity {

    /**
     * integer to access the galery
     */
    private static int RESULT_LOAD_IMG = 1;

    /**
     * Path of the chosen bitmap
     */
    static String imgDecodableString;

    /**
     * Button to confirm the choice
     */
    Button choice;

    /**
     * The chosen bitmap
     */
    public Bitmap pictureToUse;

    /**
     * The bitmap displayed
     */
    public ImageView imageView;

    /**
     * integer to access the camera
     */
    private static final int CAMERA_REQUEST = 1888;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_loading);

        choice = (Button) findViewById(R.id.choice);
        choice.setOnClickListener(blistener);

        //Starts camera
        Button photoButton = (Button) this.findViewById(R.id.camera);
        photoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });
    }

    /**
     * fucntion to access the galery with a new view
     * @param view view used for the galery
     */
    public void chargementImage(View view) {
        Intent galerie = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galerie, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // if the user wants to access the galery
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data) {
                //gets the bitmaps he chooses in touching it
                Uri selectedImage = data.getData();
                pictureToUse = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

                //gets the path of the bitmap
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();

                imageView = (ImageView) findViewById(imgView);
                imageView.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));


            }
            // if the user wants to access the camera
            else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK && null != data) {
                //saves the photo in the galery and access to it
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imgDecodableString = "newPhoto" + Math.random();
                MediaStore.Images.Media.insertImage(getContentResolver(), photo, imgDecodableString, "photo from camera");

                chargementImage(imageView);

            } else {
                Toast.makeText(this, "Vous n'avez pas choisi d'image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Quelque chose a mal fonctionné, veuillez réessayer.", Toast.LENGTH_LONG).show();
        }

    }

    /**
     * function which allows the others classes to pick the bitmap thanks to its path
     * @return the chosen bitmap
     */
    protected static Bitmap scaleImage() {
        Bitmap nad = BitmapFactory.decodeFile(imgDecodableString);
        return nad;
    }


    private View.OnClickListener blistener = new View.OnClickListener(){
        public void onClick(View v){
            switch (v.getId()) {
                // If a picture has been chosen in the gallery, opens general menu
                case R.id.choice :
                    if (pictureToUse != null){
                        Intent second = new Intent(PhotoLoading.this, GeneralMenu.class);
                        startActivity(second);}
                    break;

                default:
                    break;
            }
        }
    };
}

