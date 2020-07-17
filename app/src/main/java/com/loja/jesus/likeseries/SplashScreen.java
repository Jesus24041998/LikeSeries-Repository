package com.loja.jesus.likeseries;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreen extends Activity {
    // Tiempo de duracion de la pantalla de carga en segundos
    private static final long SPLASH_SCREEN_DELAY = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
// Orientacion de la actividad
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
// Oculto la barra de titulo
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash_screen);

        //Segun le llege a nuestra clase por el extras abriremos
        Bundle datos = this.getIntent().getExtras();
        Boolean cambioclase = datos.getBoolean("cambioclase");
        String no = datos.getString("nombre");
        String ui = datos.getString("uid");
        String email = datos.getString("email");
        cambiarActividad(cambioclase);
        Preferencias(no,ui,email);
    }

    /**
     * Sencillamente , este método se encargará de mandar al Splash una u otra clase , ya que vamos a utilizar el Splash screen tanto para registrarnos como logearnos directamente en la app
     * @param cambioclase
     */
    private void cambiarActividad(Boolean cambioclase) {
        if (cambioclase == true) {
            //Una vez logeado salta este metodo
            SplashS(Like.class,false);
        }
        else
        {
            //Una vez registrado salta este metodo
            SplashS(RegistradoCorrectamente.class,true);
            LoginYRegistro log = new LoginYRegistro();
            log.SharedPreferenceRegistro();
        }
    }
    private void Preferencias(String nombre,String uid,String email)
    {
        SharedPreferences prefs =
                getSharedPreferences("preferencias",this.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("nombre", nombre);
        editor.putString("uid", uid);
        editor.putString("email",email);
        editor.commit();
    }
    /**
     * Le llega por parametro la clase a la que se va a iniciar y si se esta registrando se le pasa un falso para evitar que coja las preferencias ya que cuando registramos nos interesa que entre en la clase de Login/Registro
     * @param clase
     * @param registro
     */
        private void SplashS(final Class clase, final Boolean registro)
        {
            final TimerTask task = new TimerTask() {
                @Override
                public void run() {


                    Intent mainIntent = new Intent().setClass(
                            SplashScreen.this,clase);

            if(registro==false) {
                SharedPreferences prefs = getSharedPreferences("preferencias", getApplicationContext().MODE_PRIVATE);

                String nombre = prefs.getString("nombre", "");
                mainIntent.putExtra("nombre", nombre);
                String uid = prefs.getString("uid", "");
                mainIntent.putExtra("uid", uid);
                String email = prefs.getString("email", "");
                mainIntent.putExtra("email", email);
        }
                    startActivity(mainIntent);
// Close the activity so the user won't able to go back this
// activity pressing Back button
                    finish();
                }
            };
// Lanzo en timer
            Timer timer = new Timer();
            timer.schedule(task, SPLASH_SCREEN_DELAY);
        }
}
