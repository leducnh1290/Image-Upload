package com.ducanh.update;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity  {
    private static int countupdate = 4;
    private TextInputEditText email,password;
    private  HandlingLogin handlingLogin = new HandlingLogin ();
    FirebaseDatabase database = FirebaseDatabase.getInstance ();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance ();
    public static  CheckBox remember;
    private Button login;
    private TextView register,forgot;
    DatabaseReference myRef = database.getReference ("update");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);
        init ();
        remember.setChecked (getSharedPreferences ("USER.txt",MODE_PRIVATE).getBoolean ("REMEMBER",false));
        email.setText (getSharedPreferences (getString (R.string.file_name),MODE_PRIVATE).getString ("EMAIL REMEMBER",""));
        password.setText (getSharedPreferences (getString (R.string.file_name),MODE_PRIVATE).getString ("PASSWORD REMEMBER",""));
        try {
            myRef.addValueEventListener (new ValueEventListener () {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue ().toString ().isEmpty ()) {
                    } else {
                        Dialogupdate ();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }catch (Exception E){

        }
        register.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Register ();
            }
            });
        forgot.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Forgot ();
            }
        });
        login.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                if(email.getText ().toString ().length ()==0||password.getText ().toString ().length ()==0) {
                    Toast.makeText (getApplicationContext (), R.string.empty, Toast.LENGTH_SHORT).show ();
                    return;
                }else if(email.getText ().toString ().length ()==0&&password.getText ().toString ().length ()==0){
                    Toast.makeText (getApplicationContext (), R.string.empty, Toast.LENGTH_SHORT).show ();
                    return;
                }else{
                   sigin (email.getText ().toString (),password.getText ().toString ());
                }
            }
        });

    }
    private void Forgot(){
        Intent forgot = new Intent(Login.this,ForgotPassword.class);
        startActivity (forgot);
    }
    private void Register(){
        Intent registerActivity = new Intent (Login.this, RegisterActivity.class);
        startActivity (registerActivity);
    }
    public void check(final String email, final String password, final boolean check){
        SharedPreferences sharedPreferences = getSharedPreferences ("USER.txt",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
            if(!check){
                editor.remove ("USER");
                editor.remove ("PASSWORD");
                editor.remove ("REMEMBER");
                editor.remove ("PASSWORD REMEMBER");
                editor.remove ("EMAIL REMEMBER");
            }else{
                editor.putString ("USER",email);
                editor.putString ("PASSWORD",password);
                editor.putBoolean ("REMEMBER",check);
            }
       editor.commit ();
    }

    public void sigin(final String email, final String password) {
            firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener (new OnCompleteListener<AuthResult> () {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful ()) {
                       check (email,password,remember.isChecked ());;
                        Intent intent = new Intent (Login.this, MainActivity2.class);
                        startActivity (intent);
                      finish ();
                    } else {
                        Toast.makeText (Login.this, R.string.login_fail, Toast.LENGTH_SHORT).show ();
                    }
                }

            });
    }

        private void init () {
            email = findViewById (R.id.email);
            login = findViewById (R.id.login);
            register = findViewById (R.id.register);
            password = findViewById (R.id.pass);
            remember = findViewById (R.id.remember);
            forgot = findViewById (R.id.forgot);
        }
        private void Dialogupdate () {
            final AlertDialog dialog = new AlertDialog.Builder (this)
                    .setTitle ("Update")
                    .setMessage ("Đã có phiên bản mới vui lòng update\n\t- Cập nhập fix lỗi")
                    .setIcon (R.mipmap.ic_launcher)
                    .setPositiveButton ("Ok", new DialogInterface.OnClickListener () {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText (Login.this, "Đang tải về phiên bản mới " + which, Toast.LENGTH_LONG).show ();
                        }
                    })
                    .setCancelable (false)
                    .show ();
        }

    }

