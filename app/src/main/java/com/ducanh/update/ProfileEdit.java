package com.ducanh.update;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Calendar;

public class ProfileEdit extends AppCompatActivity {
    private static final int REQUEST = 1;
    Button save,image;
    Bitmap bitmap;
    Context context;
    private ProgressDialog progressDialog;
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance ();
    private StorageReference storageRef = firebaseStorage.getReferenceFromUrl ("gs://cachua-69f3b.appspot.com/avatar");
    ImageView xemtruoc;
    byte[] hinhbyte;
      TextInputEditText name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_profile_edit);
        Init ();
        Context context;
        progressDialog = new ProgressDialog(this);
        image.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                    chonanh ();
            }
        });
       save.setOnClickListener (new View.OnClickListener () {
           @Override
           public void onClick(View v) {
               Loading ();
               if(name.getText ().toString ().trim ().trim ().length ()>0&&hinhbyte==null){
                   save.setEnabled (false);
                   image.setEnabled (false);
                   FirebaseUser user = FirebaseAuth.getInstance ().getCurrentUser ();
                   final UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder ()
                           .setDisplayName (name.getText ().toString ())
                           .build ();
                   user.updateProfile (profileUpdates)
                           .addOnCompleteListener (new OnCompleteListener<Void> () {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                   if (task.isSuccessful ()) {
                                       Toast.makeText (ProfileEdit.this, "Thành công update tên", Toast.LENGTH_SHORT).show ();
                                       Intent intent = new Intent (ProfileEdit.this, MainActivity2.class);
                                       startActivity (intent);
                                       progressDialog.cancel ();
                                   }
                               }
                           });
               }else if(name.getText ().toString ().trim ().length ()==0&&hinhbyte!=null){
                   Toast.makeText (ProfileEdit.this, "Đang cập nhập...", Toast.LENGTH_SHORT).show ();
                   save.setEnabled (false);
                   image.setEnabled (false);
                          upanhtosever ();
               }else if(name.getText ().toString ().trim ().length ()>0&&hinhbyte!=null){
                   Toast.makeText (ProfileEdit.this, "Đang cập nhập...", Toast.LENGTH_SHORT).show ();
                   upnameandiamge(name.getText ().toString (),hinhbyte);
                   save.setEnabled (false);
                   image.setEnabled (false);

               }else{
                   Toast.makeText (ProfileEdit.this, "Vui lòng nhập cho hợp lệ", Toast.LENGTH_SHORT).show ();
               }
          }
       });
           }
    @Override
    protected void onActivityResult(int requestCode, final int resultCode, @Nullable final Intent data) {
        if (requestCode == REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selected = data.getData ();
            hinhbyte = Img_to_byte (uriToBitmap (selected));
            Toast.makeText (ProfileEdit.this, "Thành công chọn ảnh !", Toast.LENGTH_SHORT).show ();
            xemtruoc.setImageURI (selected);
        }
        super.onActivityResult (requestCode, resultCode, data);
    }
        public void Init () {
        xemtruoc = findViewById(R.id.xemtruoc);
            save = findViewById (R.id.save);
            name = findViewById (R.id.name);
            image = findViewById (R.id.link_iamge);
        }
    private Bitmap uriToBitmap(Uri selectedFileUri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    getContentResolver ().openFileDescriptor (selectedFileUri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor ();
            bitmap = BitmapFactory.decodeFileDescriptor (fileDescriptor);

            parcelFileDescriptor.close ();
        } catch (IOException e) {
            e.printStackTrace ();
        }
        return bitmap;
    }
    public byte[] Img_to_byte(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream ();
        bitmap.compress (Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray ();
        return byteArray;
    }
        public void chonanh(){
            Intent intent = new Intent ();
            intent.setType ("image/*");
            intent.setAction (Intent.ACTION_GET_CONTENT);
            startActivityForResult (Intent.createChooser (intent,
                    "Select Picture"), REQUEST);


               }
               // Update avatar and name to sever
private  void upnameandiamge(final String name  , byte[] hinh){
    Calendar calendar = Calendar.getInstance ();
    final StorageReference storageReference = storageRef.child ("avatar-"+FirebaseAuth.getInstance ().getCurrentUser ().getDisplayName ()+"-"+calendar.getTimeInMillis () + ".png");
    final UploadTask uploadTask = storageReference.putBytes (hinhbyte);
    uploadTask.addOnFailureListener (new OnFailureListener () {
        @Override
        public void onFailure(@NonNull Exception e) {
            Toast.makeText (ProfileEdit.this, "Lỗi", Toast.LENGTH_SHORT).show ();
        }
    }).addOnSuccessListener (new OnSuccessListener<UploadTask.TaskSnapshot> () {
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            storageReference.getDownloadUrl ().addOnSuccessListener (new OnSuccessListener<Uri> () {
                @Override
                public void onSuccess(Uri uri) {
                    FirebaseUser user = FirebaseAuth.getInstance ().getCurrentUser ();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder ()
                            .setPhotoUri (uri)
                            .setDisplayName (name)
                            .build ();
                    user.updateProfile (profileUpdates)
                            .addOnCompleteListener (new OnCompleteListener<Void> () {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful ()) {
                                        Toast.makeText (ProfileEdit.this, "Thành công update tên và avatar", Toast.LENGTH_SHORT).show ();
                                        Intent intent = new Intent (ProfileEdit.this, MainActivity2.class);
                                        startActivity (intent);
                                        progressDialog.cancel ();
                                    }else{
                                        Toast.makeText (ProfileEdit.this, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show ();
                                        save.setEnabled (true);
                                        image.setEnabled (true);
                                        progressDialog.cancel ();
                                    }
                                }
                            });
                }
            });
        }
    });
}
    private void Loading() {
        progressDialog.show ();
        progressDialog.setContentView (R.layout.loading_upload);
        progressDialog.setCancelable (false);
        progressDialog.getWindow ().setBackgroundDrawableResource (android.R.color.transparent);
    }
    public void upanhtosever() {
        Calendar calendar = Calendar.getInstance ();
        final StorageReference storageReference = storageRef.child ("avatar-" + FirebaseAuth.getInstance ().getCurrentUser ().getDisplayName () + "-" + calendar.getTimeInMillis () + ".png");
        final UploadTask uploadTask = storageReference.putBytes (hinhbyte);
        uploadTask.addOnFailureListener (new OnFailureListener () {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText (ProfileEdit.this, "Lỗi", Toast.LENGTH_SHORT).show ();
            }
        }).addOnSuccessListener (new OnSuccessListener<UploadTask.TaskSnapshot> () {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText (ProfileEdit.this, "Up ảnh thành công", Toast.LENGTH_SHORT).show ();
                storageReference.getDownloadUrl ().addOnSuccessListener (new OnSuccessListener<Uri> () {
                    @Override
                    public void onSuccess(Uri uri) {
                        FirebaseUser user = FirebaseAuth.getInstance ().getCurrentUser ();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder ()
                                .setPhotoUri (uri)
                                .build ();
                        user.updateProfile (profileUpdates)
                                .addOnCompleteListener (new OnCompleteListener<Void> () {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful ()) {
                                            Toast.makeText (ProfileEdit.this, "Thành công update avatar", Toast.LENGTH_SHORT).show ();
                                            Intent intent = new Intent (ProfileEdit.this, MainActivity2.class);
                                            startActivity (intent);
                                            progressDialog.cancel ();
                                        } else {
                                            Toast.makeText (ProfileEdit.this, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show ();
                                            save.setEnabled (true);
                                            image.setEnabled (true);
                                            progressDialog.cancel ();
                                        }
                                    }
                                });
                    }
                });
            }
        });
    }
}
