package com.ducanh.update;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import java.io.IOException;
import java.io.InputStream;

public class rulesMain extends Fragment {
   public TextView dieukhoan2;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate (R.layout.dieukhoan_text,container,false);
        dieukhoan2 = view.findViewById (R.id.textdieukhoan);
        InputStream is = null;
        try {
            is = getActivity ().getAssets ().open (getString (R.string.rules) + ".txt");
        } catch (IOException e) {
            e.printStackTrace ();
        }
        int size = 0;
        try {
            size = is.available ();
        } catch (IOException e) {
            e.printStackTrace ();
        }

        byte[] buffer = new byte[size];
        try {
            is.read (buffer);
        } catch (IOException e) {
            e.printStackTrace ();
        }
        try {
            is.close ();
        } catch (IOException e) {
            e.printStackTrace ();
        }

        String str = new String (buffer);
        str = str.replace ("old string", "new string");
        dieukhoan2.setText (str);
        return view;
    }
}
