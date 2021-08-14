package com.ducanh.update;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import static android.view.View.VISIBLE;

public class ForgotPassword extends AppCompatActivity {
    private TextInputEditText email;
    private Button sendpass;
    private boolean b = false;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance ();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_forgot_password);
        Init ();
        sendpass.setEnabled (false);
        email.addTextChangedListener (new TextWatcher () {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
              if(email.getText ().toString ().trim ().isEmpty ()){
                  sendpass.setEnabled (false);
              }else{
                  sendpass.setEnabled (true);
              }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        findViewById (R.id.btnsend).setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                                  resetPassword (email.getText ().toString ());
            }
        });
    }
    private void Init(){
        email = findViewById (R.id.sendemail);
        sendpass = findViewById(R.id.btnsend);
    }
    private void resetPassword(final String email) {
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPassword.this, "Reset email instructions sent to " + email, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ForgotPassword.this, email + " does not exist", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
