package com.ducanh.update;

import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;;


public class  UserProfile {
    String username;
    Uri user_image;
    public String Name(FirebaseUser fb){
        try {
            this.username = fb.getDisplayName ();
        }catch (Exception E){
            username = "";
        }
        return username;
    }
    public Uri Image_User(FirebaseUser fb){
        try {
           this.user_image = fb.getPhotoUrl ();
        }catch (Exception E){
            user_image = Uri.EMPTY;
        }
        return user_image;
    }
}
