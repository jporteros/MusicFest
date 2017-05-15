package es.upsa.mimo.musicfest.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.upsa.mimo.musicfest.Helpers.ImagesHelper;
import es.upsa.mimo.musicfest.Model.Event;
import es.upsa.mimo.musicfest.R;

import static es.upsa.mimo.musicfest.R.id.fab_camera;

public class UserDetailActivity extends AppCompatActivity {

    private static final Integer RESULT_CODE=1;
    private static final String TAG = "UserDetailActivity";
    @BindView(R.id.user_email)
    TextView user_email;
    @BindView(R.id.user_info_name)
    TextView user_name;
    @BindView(R.id.img_collapsing)
    ImageView img_collapsing;
    @BindView(R.id.fab_camera)
    FloatingActionButton fab_camera;

    private static String imagePath;
    private static Uri file;
    //private static Bitmap bitmap=null;
    private final int CAMERA_REQUEST_CODE=1;
    private final int GALERY_REQUEST_CODE=0;



    private DatabaseReference mDatabase;
    CollapsingToolbarLayout collapsingToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        ButterKnife.bind(this);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        final String userId = user.getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        String img = dataSnapshot.child("img").getValue().toString();
                        Picasso.with(img_collapsing.getContext()).load(img).into(img_collapsing);
                        Log.d(TAG,"imagen url"+img);
                        // ...
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        // ...
                    }
                });

        user_name.setText("Nombre de usuario: "+user.getDisplayName());
        user_email.setText("Email: "+user.getEmail());
        collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(user.getDisplayName());
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);

        fab_camera = (FloatingActionButton) findViewById(R.id.fab_camera);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            fab_camera.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[] { android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }




        fab_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT>=21) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UserDetailActivity.this);
                    alertDialogBuilder.setTitle("Elegir Fuente");
                    alertDialogBuilder.setPositiveButton("Camara", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (!isCameraAvailable()) {
                                Toast.makeText(getApplicationContext(), "Camara no disponible", Toast.LENGTH_LONG).show();
                            } else
                                cameraIntent();
                        }
                    });
                    alertDialogBuilder.setNegativeButton("Galeria", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            galeryIntent();
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }else{
                    new android.support.v7.app.AlertDialog.Builder(view.getContext())
                            .setTitle("Abrir Galeria")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    galeryIntent();
                                }

                            })
                            .setNegativeButton("No", null)
                            .show();
                }

            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0 || requestCode==400) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                fab_camera.setEnabled(true);
            }
        }
    }

    public void cameraIntent(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // file = Uri.fromFile(getOutputMediaFile());
        file= FileProvider.getUriForFile(getApplicationContext(),"es.upsa.mimo.musicfest.fileprovider",getOutputMediaFile());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, file);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }
    private File getOutputMediaFile(){
       /* File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "YourEventsPocket");*/
        String[] permissionsStorage = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        int permission1 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission1 != PackageManager.PERMISSION_GRANTED || permission2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,permissionsStorage, 400);
        }
        //File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "YourEventsPocket/Images");
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "MusicFest");

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                Log.d("MusicFest", "failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        //return new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");
        File imageFile= new File(mediaStorageDir, timeStamp + ".jpg");
        imagePath=imageFile.getAbsolutePath();
        return imageFile;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize=5;

                if(file!=null) {

                    // Bitmap b = BitmapFactory.decodeFile(file.getPath(), options);
                    Bitmap b = BitmapFactory.decodeFile(imagePath, options);
                    img_collapsing.setImageBitmap(b);
                    ImagesHelper imagesHelper = new ImagesHelper();
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    Log.d("UPLOADIMAGES","userid: "+userId+"bitmap: "+b);
                    imagesHelper.uploadImage(b,userId);
                  //  bitmap = b;
                }
                //uploadImage(b);
            }
        }else if(requestCode == GALERY_REQUEST_CODE){
            if(resultCode ==RESULT_OK){
                Uri image1 = data.getData();
                InputStream imageStream = null;
                try {
                    imageStream = getContentResolver().openInputStream(image1);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                if (imageStream != null) {

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize=2;

                    Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream, new Rect(-1, -1, -1, -1), options);
                   // bitmap=yourSelectedImage;
                    img_collapsing.setImageBitmap(yourSelectedImage);
                    ImagesHelper imagesHelper = new ImagesHelper();
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    Log.d("UPLOADIMAGES","userid: "+userId+"bitmap: "+yourSelectedImage);
                    imagesHelper.uploadImage(yourSelectedImage,userId);
                    //uploadImage(yourSelectedImage);
                }
            }
        }
    }
    private boolean isCameraAvailable() {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }


    //galery
    public void galeryIntent (){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALERY_REQUEST_CODE);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_OK);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
