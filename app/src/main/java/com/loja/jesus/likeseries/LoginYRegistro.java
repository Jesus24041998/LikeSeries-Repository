package com.loja.jesus.likeseries;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginYRegistro extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private FirebaseAuth mAuth;
    private Button enviar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_yregistro);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mAuth = FirebaseAuth.getInstance();
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }
    public void SharedPreferenceRegistro()
    {
        SharedPreferences prefs =
                getSharedPreferences("preferenciasderegistro",this.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("Registro",true);
        editor.commit();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        cerrarPagina();
    }

    /**
     * Pide permiso para volveraverificar la app
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    /**
     * Método que se encarga de volveraverificar la pantalla del login/registro para evitar la toolbar nos vuelva a dicho login ya que no tendria sentino una vez registrado o logeado
     */
    public void cerrarPagina()
    {
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences prefs =
                getSharedPreferences("preferenciasderegistro",this.MODE_PRIVATE);

        Boolean registro = prefs.getBoolean("Registro", false);
        if(registro == false) {
            FirebaseUser user = mAuth.getCurrentUser();
            // El usuario existe
            if (user != null) {
                System.out.println(user.getEmail().toString());
                verifySignInLink(user.getEmail(), null, true);
            }
        }
     }

    public void verifySignInLink(String email, final String password, final Boolean logeado) {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        String emailLink = intent.getData().toString();

        // Confirm the link is a sign-in with email link.
        if (auth.isSignInWithEmailLink(emailLink)) {
            auth.signInWithEmailLink(email, emailLink)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            String TAG="";
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Successfully signed in with email link!");
                                AuthResult result = task.getResult();

                                if(logeado==true)
                                {
                                    updateUI();
                                }
                                else
                                {
                                    logearUsuarioFirebase(result.getUser().getEmail(),password);
                                }


                                // You can access the new user via result.getUser()
                                // Additional user info profile *not* available via:
                                // result.getAdditionalUserInfo().getProfile() == null
                                // You can check if the user is new or existing:
                                // result.getAdditionalUserInfo().isNewUser()
                            } else {
                                Log.e(TAG, "Error signing in with email link", task.getException());

                            }
                        }
                    });
        }
        // [END auth_verify_sign_in_link]
    }
    private void logearUsuarioFirebase(String email,String password)
    {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        String TAG = "";
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            Intent intent = new Intent(getApplication(), SplashScreen.class);
                            intent.putExtra("cambioclase", true);
                            updateUI();
                        } else {
                            // No se registra correctamente
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            try {
                                throw task.getException();
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(),
                                        "Error al logear usuario", Toast.LENGTH_LONG).show();

                            }
                        }
                    }
                });
    }

    /**
     * Método que se encarga de abrir la nueva pantalla de la actividad principal una vez logeado
     */
    public void updateUI()
    {

        FirebaseUser user = mAuth.getCurrentUser();
        String usuarionombre = user.getDisplayName();
        String uid = user.getUid();
        String email = user.getEmail();
        Intent intent = new Intent(getApplication(), SplashScreen.class);
        intent.putExtra("cambioclase", true);
        intent.putExtra("nombre", usuarionombre);
        intent.putExtra("uid", uid);
        intent.putExtra("email",email);
        startActivity(intent);
        LoginYRegistro l = new LoginYRegistro();
        l.cerrarPagina();
    }

    /**
     * Método que abre una alerta para volver a enviar el código de verificación
     */
    private void noVerificado()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext(),android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        View view = getLayoutInflater().inflate(R.layout.volveraverificar, null);
        enviar = view.findViewById(R.id.enviar);
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser user = auth.getCurrentUser();

                user.sendEmailVerification()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                String TAG = "";
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "Email sent.");

                                }
                            }
                        });
            }
        });
        AlertDialog alerta = builder.create();
        alerta.show();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Devuelve la hoja a la que el position apunta
         * @param position
         * @return Fragment
         */
        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Fragment fragment =new Fragment();
            switch (position)
            {
                case 0:
                    LoginFragment login = new LoginFragment();
                    fragment = login;
                    break;

                case 1:
                    RegistroFragment registro = new RegistroFragment();
                    fragment = registro;
                    break;
            }
            return fragment;
        }
        /**
         * Número de páginas
         * @return int
         */
        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }
    }
}
