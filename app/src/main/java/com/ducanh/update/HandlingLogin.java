package com.ducanh.update;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import com.ducanh.update.Login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import android.content.SharedPreferences;
import com.google.firebase.auth.FirebaseAuth;
public class HandlingLogin {
    public static boolean status = false;
    public  static  FirebaseAuth firebaseAuth = FirebaseAuth.getInstance ();
    public boolean status(){
        try{
            if(Login.remember.isChecked ()){
                status = true;
            }else{
                status = false;
            }        }catch (Exception E){
        }
        return status;
    }
}
