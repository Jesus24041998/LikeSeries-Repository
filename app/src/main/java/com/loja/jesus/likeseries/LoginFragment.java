package com.loja.jesus.likeseries;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;


public class LoginFragment extends Fragment  {
    //Declaración de elementos del login
    private Button isesion;
    //Login
    private EditText temail,tcontra;

    private FirebaseAuth mAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //Variables declaradas login
        View vista = inflater.inflate(R.layout.fragment_login,container,false);
        isesion = vista.findViewById(R.id.login);
        temail=vista.findViewById(R.id.temail);
        tcontra=vista.findViewById(R.id.tcontrasena);
        mAuth = FirebaseAuth.getInstance();
        isesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.login:
                         if (!temail.getText().toString().equals("")) {
                         if (!tcontra.getText().toString().equals("")) {
                             LoginYRegistro logi = new LoginYRegistro();
                             logi.verifySignInLink(temail.getText().toString(), tcontra.getText().toString(),false);
                         } else {
                         Toast.makeText(getContext(),
                         "Contraseña vacia", Toast.LENGTH_LONG).show();
                         }
                         } else {
                         Toast.makeText(getContext(),
                         "Email vacio", Toast.LENGTH_LONG).show();
                         tcontra.setText("");
                         }
                        break;
                }

            }
        });
        return vista;
    }

    }

//}
