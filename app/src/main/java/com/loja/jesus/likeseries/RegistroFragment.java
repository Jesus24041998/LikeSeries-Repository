package com.loja.jesus.likeseries;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import static android.support.constraint.Constraints.TAG;


public class RegistroFragment extends Fragment {
    //Registro
    private Button registrar;
    private EditText temailregistro, tcontrasenaregistro, tnombre;
    //Mensajes y acuerdos
    private CheckBox recibir, acuerdolegal;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registro, container, false);
        tnombre = view.findViewById(R.id.nombre);
        temailregistro = view.findViewById(R.id.temailregistro);
        tcontrasenaregistro = view.findViewById(R.id.tcontrasenaregistro);


        acuerdolegal = view.findViewById(R.id.acuerdolegal);
        recibir = view.findViewById(R.id.mensajes);


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        registrar = view.findViewById(R.id.registrar);

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Partimos de que el acuerdo legal esta checheado
                if (!temailregistro.getText().toString().equals("")) {
                    if (!tcontrasenaregistro.getText().toString().equals("")) {
                        if (acuerdolegal.isChecked() == true) {
                            //Registramos un usuario con firebase
                            registrarUsuarioFirebase();
                            Intent intent = new Intent(getActivity(), SplashScreen.class);
                            intent.putExtra("cambioclase", false);
                            startActivity(intent);
                        }
                        //Este else simplemente ejecuta un Toast que indica que el acuerdo legal es necesario , y pone el texto de color rojo
                        else {

                            tcontrasenaregistro.setText("");
                            Toast.makeText(getContext(),
                                    "Acuerdo Legal necesario", Toast.LENGTH_LONG).show();
                            acuerdolegal.setTextColor(getResources().getColor(R.color.rojo));
                        }
                    } else {
                        tcontrasenaregistro.setText("");
                        Toast.makeText(getContext(),
                                "Contraseña requerida", Toast.LENGTH_LONG).show();
                    }
                } else {
                    tcontrasenaregistro.setText("");
                    Toast.makeText(getContext(),
                            "Email requerido", Toast.LENGTH_LONG).show();
                    temailregistro.setTextColor(getResources().getColor(R.color.rojo));
                }
            }
        });
        return view;
    }




    /**
     * Este usuario sera registrado exitosamente en la nube gracias a FireBase Authentication y posteriormente ingresado en FireStore
     */

    private void registrarUsuarioFirebase() {

        mAuth.createUserWithEmailAndPassword(temailregistro.getText().toString(), tcontrasenaregistro.getText().toString())
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        final String TAG = "";
                        if (task.isSuccessful()) {
                            // Registrado correctamente
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            enviarAuthEmail1(user.getUid());

                        } else {
                            // No se registra correctamente
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                Toast.makeText(getActivity(),
                                        "Contraseña debil", Toast.LENGTH_LONG).show();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                Toast.makeText(getActivity(),
                                        "Email invalido", Toast.LENGTH_LONG).show();

                            } catch (FirebaseAuthUserCollisionException e) {
                                Toast.makeText(getActivity(),
                                        "Usuario ya registrado", Toast.LENGTH_LONG).show();

                            } catch (FirebaseAuthInvalidUserException e) {
                                Toast.makeText(getActivity(),
                                        "Usuario invalido", Toast.LENGTH_LONG).show();

                            } catch (Exception e) {
                                Toast.makeText(getActivity(),
                                        "Error al registrar usuario", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }


    /**
     * Envia un correo de verificación , a parte inserta los datos en la BD , no obstante hasta que no nos registremos no podemos entrar
     * @param uid
     */
    private void enviarAuthEmail1(final String uid) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                            //Creo un usuario y lo agrego a la base de datos de FireBase Cloud


                            Usuario usuario = new Usuario(tnombre.getText().toString(), user.getEmail(),uid ,recibir.isActivated());

                            insertarBasedeDatosFireBaseUsuario(usuario);


                            //Cierro la actividad
                            LoginYRegistro login = new LoginYRegistro();
                            login.cerrarPagina();

                        }
                    }
                });
    }
    /**
     * Este metodo se encargara de guardar nuestros datos registrados en la nube
     *
     * @param user
     */
    private void insertarBasedeDatosFireBaseUsuario(Usuario user) {
        // Creamos un usuario y lo guardamos en la base de datos
        db.collection("usuarios").document(user.getToken())
                .set(user, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }
    }
