package com.ducanh.update;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ducanh.NetworkChecked.NetworkChecked;
import com.ducanh.update.Encrypt.MD5;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity2 extends AppCompatActivity implements View.OnClickListener {
    private static final String STATE_COUNTER = "counter";
    private int mCounter;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private NetworkChecked isNetworkConnected = new NetworkChecked (this);
    private DatabaseReference myref = FirebaseDatabase.getInstance ().getReference ();
    private ImageView hinhanh, avatar;
    private MD5 md5 = new MD5 ();
    private ActivityResultLauncher<Intent> mActivityResultLaucher = registerForActivityResult (
            new ActivityResultContracts.StartActivityForResult (), new ActivityResultCallback<ActivityResult> () {
                @Override
                public void onActivityResult(ActivityResult result) {
               if(result.getResultCode ()==RESULT_OK){
                   Intent intent = result.getData ();
                   image2 = result.getData ().getData ();
                   hinhbyte = Img_to_byte (uriToBitmap (intent.getData ()));
                   Toast.makeText (MainActivity2.this, "Chọn ảnh thành công vui lòng nhập pass ảnh và nhấn up ảnh", Toast.LENGTH_SHORT).show ();
                   hinhanh.setImageBitmap (uriToBitmap (result.getData ().getData ()));
                   linkhinhve = null;
               }
                }
            });
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance ();
    private FirebaseUser firebaseUser = firebaseAuth.getCurrentUser ();
    private byte[] hinhbyte;
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance ();
    private StorageReference storageRef = firebaseStorage.getReference ();
    private Button fideimage, btmdown, edit_profile;
    private String name;
    private UserProfile data = new UserProfile ();
    private TextInputEditText ten;
    private Button chonanh, logout, upanh;
    TextView user_name;
    Intent intent2;
    Bitmap bitmap;
    private String linkhinh, linkhinhve;
    private Uri image2;
    int REQUEST = 1;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main2);
        init ();
        setview ();
        progressDialog = new ProgressDialog (this);
        btmdown.setVisibility (View.INVISIBLE);
        hinhanh.setImageResource (R.drawable.nodata);
        try {
            getdatauser (firebaseUser);
        } catch (Exception E) {

        }
        sharedPreferences = getSharedPreferences ("USER.txt", MODE_PRIVATE);
        editor = sharedPreferences.edit ();
        intent2 = new Intent (this, Login.class);
        if (firebaseAuth.getInstance ().getCurrentUser () != null) {
            if (firebaseUser.getDisplayName () != null) {
                Toast.makeText (MainActivity2.this, "Welcome Back " + firebaseUser.getDisplayName (), Toast.LENGTH_SHORT).show ();
            } else {
                Toast.makeText (MainActivity2.this, "Hello " + firebaseUser.getEmail (), Toast.LENGTH_SHORT).show ();
            }
        } else {
            checksigin ();
        }
        if (!isNetworkConnected.isNetworkConnected ()) {
            Dialogupdate ();
        }

    }

    public void showDialog(Activity activity, String msg) {
        final Dialog dialog = new Dialog (activity);
        dialog.requestWindowFeature (Window.FEATURE_NO_TITLE);
        dialog.setContentView (R.layout.dialog);
        final EditText name = dialog.findViewById (R.id.name);
        Button ok = dialog.findViewById (R.id.save);
        dialog.show ();
        ok.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder ()
                        .setDisplayName (name.getText ().toString ())
                        .build ();
                firebaseUser.updateProfile (profileUpdates)
                        .addOnCompleteListener (new OnCompleteListener<Void> () {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful ()) {
                                    getdatauser (firebaseUser);
                                    Toast.makeText (getApplicationContext (), "Thành công update tên", Toast.LENGTH_SHORT).show ();
                                    dialog.cancel ();
                                }
                            }
                        });
            }
        });
    }

    private void Dialogupdate() {
        final AlertDialog dialog = new AlertDialog.Builder (this)
                .setTitle ("No Internet Connection !")
                .setMessage ("Vui lòng kết nối Internet để tiếp tục")
                .setCancelable (false)
                .setIcon (R.mipmap.ic_launcher)
                .setPositiveButton ("Ok", new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!isNetworkConnected.isNetworkConnected ()) {
                            Dialogupdate ();
                        }
                    }
                })
                .setCancelable (true)
                .show ();
    }

    private void xacnhan() {
        if (linkhinhve != null && name != null) {
            if (isNetworkConnected.isNetworkConnected ()) {
                final AlertDialog dialog = new AlertDialog.Builder (this)
                        .setTitle ("Thông Báo Xác nhận!")
                        .setMessage ("Xác nhận lưu ảnh này ?")
                        .setIcon (R.mipmap.ic_launcher)
                        .setPositiveButton ("Ok", new DialogInterface.OnClickListener () {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                download (linkhinhve, name);
                            }
                        })
                        .setNegativeButton ("Huỷ", new DialogInterface.OnClickListener () {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show ();
            } else {
                Dialogupdate ();
            }
        } else {
            Toast.makeText (MainActivity2.this, "Vui lòng nhập pass ảnh trước khi tải", Toast.LENGTH_SHORT).show ();
        }
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

    private void checksigin() {
        boolean check = sharedPreferences.getBoolean ("REMEMBER", false);
        if (check) {
            editor.remove ("EMAIL REMEMBER");
            editor.remove ("PASSWORD REMEMBER");
        } else {
            startActivity (intent2);
            finishAffinity ();
        }
    }

    public void getdatauser(FirebaseUser fb) {
        if (firebaseAuth.getCurrentUser () != null && firebaseUser.getPhotoUrl () != null && firebaseUser.getDisplayName () != null) {
            user_name.setText (data.Name (fb));
            Picasso.with (MainActivity2.this).load (data.Image_User (fb)).placeholder (R.drawable.ic_person_black_24dp).into (avatar);
        } else if(firebaseAuth.getCurrentUser ()!=null && firebaseUser.getDisplayName ()!=null&&firebaseUser.getPhotoUrl ()==null) {
            user_name.setText (firebaseUser.getDisplayName ());;
            avatar.setImageResource (R.drawable.ic_person_black_24dp);
        }else if(firebaseAuth.getCurrentUser ()!=null && firebaseUser.getPhotoUrl ()!=null&&firebaseUser.getDisplayName ()==null){
            user_name.setText (firebaseUser.getEmail ());
            Picasso.with (MainActivity2.this).load (firebaseUser.getPhotoUrl ()).placeholder (R.drawable.ic_person_black_24dp).into(avatar);
        }
    }

    public void init() {
        logout = findViewById (R.id.logout);
        hinhanh = findViewById (R.id.hinh);
        btmdown = findViewById (R.id.downlaod);
        user_name = findViewById (R.id.user_name);
        avatar = findViewById (R.id.user_avatar);
        fideimage = findViewById (R.id.chonanh);
        chonanh = findViewById (R.id.chonanh);
        ten = findViewById (R.id.ten);
        edit_profile = findViewById (R.id.edit_profile);
        upanh = findViewById (R.id.upanh);
    }

    public void setview() {
        logout.setOnClickListener (this);
        chonanh.setOnClickListener (this);
        btmdown.setOnClickListener (this);
        upanh.setOnClickListener (this);
        edit_profile.setOnClickListener (this);
    }

    private void logout(String email, String password) {
        if (!isNetworkConnected.isNetworkConnected ()) {
            Dialogupdate ();
        } else {
            firebaseAuth.signOut ();
            if(email!=""&&password!=""){
                editor.putString ("EMAIL REMEMBER", email);
                editor.putString ("PASSWORD REMEMBER", password);
            }
            editor.remove ("USER");
            editor.remove ("PASSWORD");
            editor.commit ();
            startActivity (intent2);
            finishAffinity ();
        }
    }

    public static Bitmap convertStringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode ("hello", Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray (encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage ();
            return null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId ()) {
            case R.id.logout:
                logout (sharedPreferences.getString ("USER", ""), sharedPreferences.getString ("PASSWORD", ""));
                break;
            case R.id.edit_profile:
                if (isNetworkConnected.isNetworkConnected ()) {
                  //  showDialog (this, "Hello");
                    Intent intent2 = new Intent (MainActivity2.this, ProfileEdit.class);
                    startActivity (intent2);
                } else {
                    Dialogupdate ();
                }
                break;
            case R.id.chonanh:
                if (!isNetworkConnected.isNetworkConnected ()) {
                    Dialogupdate ();
                } else {
                    if (ten.getText ().toString ().trim ().length () != 0) {
                        upanh.setEnabled (false);
                        chonanh.setEnabled (false);
                        ten.setEnabled (false);
                        edit_profile.setEnabled (false);
                        logout.setEnabled (false);
                        Loading ();
                        myref.child (ten.getText ().toString ()).addValueEventListener (new ValueEventListener () {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                try {
                                    linkhinhve = snapshot.getValue ().toString ();
                                    btmdown.setVisibility (View.VISIBLE);
                                    Picasso.with (getApplicationContext ()).load (snapshot.getValue ().toString ()).placeholder (R.drawable.load).into (hinhanh);
                                    name = ten.getText ().toString ();
                                    Toast.makeText (MainActivity2.this, "Thành công lấy ảnh với password: " + ten.getText ().toString (), Toast.LENGTH_SHORT).show ();
                                    chonanh.setEnabled (true);
                                    upanh.setEnabled (true);
                                    edit_profile.setEnabled (true);
                                    logout.setEnabled (true);
                                    ten.setEnabled (true);
                                    ten.setText ("");
                                    progressDialog.dismiss ();
                                } catch (Exception E) {
                                    Toast.makeText (MainActivity2.this, R.string.no_image_find, Toast.LENGTH_SHORT).show ();
                                    chonanh.setEnabled (true);
                                    upanh.setEnabled (true);
                                    edit_profile.setEnabled (true);
                                    logout.setEnabled (true);
                                    ten.setEnabled (true);
                                    progressDialog.dismiss ();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
//                        myref.child (ten.getText ().toString ()).addValueEventListener (new ValueEventListener () {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                try {
//                                    linkhinhve = snapshot.getValue ().toString ();
//                                    Picasso.with (getApplicationContext ()).load (snapshot.getValue ().toString ()).placeholder (R.drawable.load).into (hinhanh);
//                                    name=ten.getText ().toString ();
//                                    Toast.makeText (MainActivity2.this, "Thành công lấy ảnh với password: " +ten.getText ().toString (), Toast.LENGTH_SHORT).show ();
//                                    chonanh.setEnabled (true);
//                                    upanh.setEnabled (true);
//                                    edit_profile.setEnabled (true);
//                                    logout.setEnabled (true);
//                                    ten.setEnabled (true);
//                                    ten.setText ("");
//                                } catch (Exception E) {
//
//                                    Toast.makeText (MainActivity2.this, R.string.no_image_find, Toast.LENGTH_SHORT).show ();
//                                    chonanh.setEnabled (true);
//                                    upanh.setEnabled (true);
//                                    edit_profile.setEnabled (true);
//                                   logout.setEnabled (true);
//                                  ten.setEnabled (true);
//                                }
//                            }
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//
//                            }
//                        });
                    } else {
                        Toast.makeText (MainActivity2.this, R.string.pass_image, Toast.LENGTH_SHORT).show ();
                    }
                }
                break;
            case R.id.upanh:
                if (ten.getText ().toString ().trim ().length () != 0) {
                    if (hinhbyte != null) {
                        if (!isNetworkConnected.isNetworkConnected ()) {
                            Dialogupdate ();
                        } else {
                            Loading ();
                            upanh.setEnabled (false);
                            chonanh.setEnabled (false);
                            ten.setEnabled (false);
                            Calendar calendar = Calendar.getInstance ();
                            final StorageReference storageReference = storageRef.child ("image-" + ten.getText ().toString () + firebaseUser.getDisplayName () + "-" + calendar.getTimeInMillis () + ".png");
                            final UploadTask uploadTask = storageReference.putBytes (hinhbyte);
                            uploadTask.addOnFailureListener (new OnFailureListener () {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    chonanh.setEnabled (false);
                                    upanh.setEnabled (false);
                                    edit_profile.setEnabled (false);
                                    logout.setEnabled (false);
                                    ten.setEnabled (false);
                                    Toast.makeText (MainActivity2.this, "Lỗi", Toast.LENGTH_SHORT).show ();
                                }
                            }).addOnSuccessListener (new OnSuccessListener<UploadTask.TaskSnapshot> () {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    storageReference.getDownloadUrl ().addOnSuccessListener (new OnSuccessListener<Uri> () {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            linkhinh = uri.toString ();
                                            HashMap<String, Object> map = new HashMap<> ();
                                            map.put (ten.getText ().toString (), linkhinh);
                                            myref.updateChildren (map);
                                            myref.updateChildren (map).addOnCompleteListener (new OnCompleteListener<Void> () {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful ()) {
                                                        progressDialog.dismiss ();
                                                        Toast.makeText (MainActivity2.this, "Up ảnh thành công. Với password là " + ten.getText ().toString (), Toast.LENGTH_SHORT).show ();
                                                        chonanh.setEnabled (true);
                                                        upanh.setEnabled (true);
                                                        linkhinh = null;
                                                        edit_profile.setEnabled (true);
                                                        logout.setEnabled (true);
                                                        ten.setEnabled (true);
                                                        ten.setText ("");
                                                        hinhanh.setImageResource (R.drawable.nodata);
                                                    } else {
                                                        progressDialog.dismiss ();
                                                        Toast.makeText (MainActivity2.this, "Lưu dữ liệu thất bại.Vui lòng thử lại sau !", Toast.LENGTH_SHORT).show ();
                                                        chonanh.setEnabled (true);
                                                        upanh.setEnabled (true);
                                                        edit_profile.setEnabled (true);
                                                        logout.setEnabled (true);
                                                        ten.setEnabled (true);
                                                    }
                                                }
                                            });
                                        }
                                    });

                                }
                            });
                        }
                    } else {
                        Toast.makeText (MainActivity2.this, "Vui lòng không nhập gì để chọn ảnh trước", Toast.LENGTH_SHORT).show ();
                    }
                } else {
                    Intent intent = new Intent ();
                    intent.setType ("image/*");
                    intent.setAction (Intent.ACTION_GET_CONTENT);
                    mActivityResultLaucher.launch (intent);
                }
                break;
            case R.id.downlaod:
                if (isExternalStorageReadable ()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission (Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            checkAndRequestPermissions ();
                        }
                        // Permisson don't granted and dont show dialog again.
                        else {
                            xacnhan ();
                        }
                    } else {
                        xacnhan ();
                    }
                }
                break;
        }
    }

private void Loading(){
    progressDialog.show ();
    progressDialog.setContentView (R.layout.loading_upload);
    progressDialog.getWindow ().setBackgroundDrawableResource (android.R.color.transparent);
}
    private void checkAndRequestPermissions() {
        String[] permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        ArrayList<String> listPermissionsNeeded = new ArrayList<> ();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission (this, permission) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add (permission);
            }
        }
        if (!listPermissionsNeeded.isEmpty ()) {
            ActivityCompat.requestPermissions (this, listPermissionsNeeded.toArray (new String[listPermissionsNeeded.size ()]), 1);
        }
    }

    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState ();
        if (Environment.MEDIA_MOUNTED.equals (state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals (state)) {
            return true;
        }
        return false;
    }

    public void download(String url, String name) {
        if (url != null && name != null) {
            DownloadManager.Request request = new DownloadManager.Request (Uri.parse (url));
            request.setAllowedNetworkTypes (DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            request.setTitle ("Tải ảnh với password là : " + name);
            request.setDescription ("Dowload file" + name + ".jpg");
            request.setNotificationVisibility (DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir (Environment.DIRECTORY_DOWNLOADS, String.valueOf ("Password:" + name + "-" + System.currentTimeMillis () + ".jpg"));
            final Toast download = Toast.makeText (getApplicationContext (), "Ảnh đã được lưu về theo đường dẫn " + Environment.getExternalStorageDirectory () +
                    "/Download/Password: " + name + " - " + System.currentTimeMillis () +
                    ".jpg", Toast.LENGTH_LONG);
            download.show ();
            Handler handler = new Handler ();
            handler.postDelayed (new Runnable () {
                @Override
                public void run() {
                    download.cancel ();
                }
            }, 5000);
            DownloadManager downloadManager = (DownloadManager) getSystemService (Context.DOWNLOAD_SERVICE);
            if (downloadManager != null) {
                downloadManager.enqueue (request);
                linkhinhve = null;
                btmdown.setVisibility (View.INVISIBLE);
                ten.setText ("");
                name = null;
            }
        } else {
            Toast.makeText (MainActivity2.this, "Vui lòng nhập pass ảnh trước khi tải", Toast.LENGTH_SHORT).show ();
        }
    }
}
