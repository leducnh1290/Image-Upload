package com.ducanh.update;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
   TextInputEditText email,pass;
   ProgressDialog progressDialog;
   FirebaseAuth firebaseAuth = FirebaseAuth.getInstance ();
   Button register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_register);
        progressDialog = new ProgressDialog (this);
        Init ();
        register.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                if(!email.getText ().toString ().isEmpty ()&&!email.getText ().toString ().isEmpty ()){
                    firebaseAuth.createUserWithEmailAndPassword (email.getText ().toString (),pass.getText ().toString ()).addOnCompleteListener (new OnCompleteListener<AuthResult> () {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Loading ();
                            if(task.isSuccessful ()){
                                progressDialog.cancel ();
                                Toast.makeText (RegisterActivity.this,"Register Succesfully with email "+email.getText ().toString (),Toast.LENGTH_SHORT).show ();
                            }else{
                                progressDialog.cancel ();
                                Toast.makeText (RegisterActivity.this,email.getText ().toString ()+" is not Complete",Toast.LENGTH_SHORT).show ();
                            }
                        }
                    });
                }else{
                    Toast.makeText (RegisterActivity.this, "Vui lòng nhập hợp lệ",Toast.LENGTH_SHORT).show ();
                }
            }
        });
    }
    private void Loading() {
        progressDialog.show ();
        progressDialog.setContentView (R.layout.loading_upload);
        progressDialog.setCancelable (false);
        progressDialog.getWindow ().setBackgroundDrawableResource (android.R.color.transparent);
    }
    private void Init(){
      email = findViewById (R.id.emailregister);
      pass = findViewById (R.id.passregister);
      register = findViewById(R.id.btnregister);
    }
}
