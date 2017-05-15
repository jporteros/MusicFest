package es.upsa.mimo.musicfest.Helpers;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

/**
 * Created by Javier on 15/05/2017.
 */

public class ImagesHelper {

    private FirebaseStorage storage;
    private DatabaseReference mDatabase;
    /**
     * Metodo para cargar una imagen de usuario en el Storage
     * @param b - Bitmap de la imagen
     * @param uid - Id del usuario
     */
    public void uploadImage(Bitmap b, final String uid) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Log.d("UPLOADIMAGE", "ANTES IF");
        if(b!=null){
            storage = FirebaseStorage.getInstance();

            StorageReference storageRef = storage.getReferenceFromUrl("gs://musicfest-d1726.appspot.com");

            StorageReference imagesRef = storageRef.child("images/users/" + uid + ".jpg");

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.JPEG, 50, stream);
            byte[] byteArray = stream.toByteArray();

            // upload to firebase
            UploadTask uploadTask = imagesRef.putBytes(byteArray);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d("UserDao_uploadImage", "Mal subida la imagen");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d("onSuccess","Onsuccess");
                    @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    mDatabase.child("users").child(uid).child("img").setValue(downloadUrl.toString());
                }
            });

        }
    }
}
