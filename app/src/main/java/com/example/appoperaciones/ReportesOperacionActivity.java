package com.example.appoperaciones;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.appoperaciones.Fragmentos.BloqueoTiendaFragment;
import com.example.appoperaciones.Fragmentos.ConsultaBloqueosFragment;
import com.example.appoperaciones.Fragmentos.EgresosFragment;
import com.example.appoperaciones.Fragmentos.EncuestaTiendaFragment;
import com.example.appoperaciones.Fragmentos.EstadisticaPlatFragment;
import com.example.appoperaciones.Fragmentos.EstadisticaPromFragment;
import com.example.appoperaciones.Fragmentos.EstadoPedidoTFragment;
import com.example.appoperaciones.Fragmentos.EstadoTiendasFragment;
import com.example.appoperaciones.Fragmentos.HorarioAdminFragment;
import com.example.appoperaciones.Fragmentos.InicioFragment;
import com.example.appoperaciones.Fragmentos.OperacionVentasFragment;
import com.example.appoperaciones.Fragmentos.ReporteGeneralFragment;
import com.example.appoperaciones.Fragmentos.ResultEncuestaFragment;
import com.example.appoperaciones.Servicios.GenerarReporteOperacion;
import com.example.appoperaciones.Servicios.GenerarReporteOperacionVenta;
import com.example.appoperaciones.Servicios.ObtenerEncuestaTiendasAPP;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.JsonObject;

import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class ReportesOperacionActivity extends AppCompatActivity {
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawer;
    private FirebaseAuth mAuth;
    private GoogleSignInClient googleSignInClient;
    private  SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Menu menu;
    private Dialog dialog;



    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportes_operacion);
        // drawer es el layout del activity que contiene el Navigation Drawer y los activities con los que está vinculado
        drawer = findViewById(R.id.drawer_layout);

        // navigationView es el menú Navigation Drawer
        NavigationView navigationView = findViewById(R.id.nav_view);
        drawerToggle = new ActionBarDrawerToggle(this, drawer,R.string.open,R.string.close);
        drawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        menu = navigationView.getMenu();

        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new InicioFragment()).commit();
            navigationView.setCheckedItem(R.id.home);
        }

        sharedPref = this.getSharedPreferences("SESIONES", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NotNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.home:{
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new InicioFragment()).commit();
                        break;
                    }
                    case R.id.reporteGeneral:{
                        GenerarReporteGeneral();
                        break;

                    }
                    case R.id.estado_tienda:{
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new EstadoTiendasFragment()).commit();

                        break;
                    }

                    case R.id.operacion_venta:{
                       GenerarReporteOperacionVenta();

                        break;
                    }

                    case R.id.estado_pedido:{
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new EstadoPedidoTFragment()).commit();


                        break;
                    }

                    case R.id.horario_admin:{
                       getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HorarioAdminFragment()).commit();
                        break;
                    }

                    case R.id.configurar_tiendas:{
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new BloqueoTiendaFragment()).commit();

                        break;
                    }

                    case R.id.egresos:{
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new EgresosFragment()).commit();

                        break;
                    }

                    case R.id.bloqueos:{
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ConsultaBloqueosFragment()).commit();
                        break;
                    }

                    case R.id.estadistica_promocion:{
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new EstadisticaPromFragment()).commit();
                        break;
                    }

                    case R.id.estadistica_plataforma:{
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new EstadisticaPlatFragment()).commit();
                        break;
                    }

                    case R.id.encuesta_tienda:{
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new EncuestaTiendaFragment()).commit();

                        break;
                    }
                    case R.id.resultado_encuesta:{
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ResultEncuestaFragment()).commit();

                        break;
                    }




                    case R.id.cerrar_sesion:{
                        logout();
                        break;
                    }


                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

    }

    private void logout() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if (currentUser != null) {
            mAuth.signOut();
        }
        if(account != null) {
            googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(Task<Void> task) {
                    if(!task.isSuccessful()){
                        Toast.makeText(getApplicationContext(),"No se pudo cerrar la sesion en google" , Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        editor.putString("rol","");
        editor.putString("permisos","");
        editor.apply();
        Main();

    }



    private  void  Main(){
        Intent intent = new Intent(ReportesOperacionActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }

    }


    private void verificarPermisos(String permisos) {

        try {
            JSONArray jsonArray = new JSONArray(permisos);
            for(int i= 0;jsonArray.length() > i;i++){
                int resourceId = getResources().getIdentifier(jsonArray.getString(i), "id", getPackageName());
                MenuItem menuItem = menu.findItem(resourceId);

                if (menuItem != null) {
                    menuItem.setVisible(true);
                }

            }
            System.out.println(jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    @Override
    protected void onStart() {
        super.onStart();
        ObtenerRol();


    }

    // Interfaz para manejar los resultados de la verificación de permisos
    interface OnPermisosVerificadosListener {
        void onPermisosVerificados(boolean tienePermisos);
    }


     void  ObtenerRol(){

         try {
             String rol = sharedPref.getString("rol","");
             List<String> pm = new ArrayList<>();
             JSONArray jsonArray = new JSONArray(rol);
             int totalConsultas = jsonArray.length();
             AtomicInteger consultasCompletadas = new AtomicInteger(0);

             for (int i = 0; i < jsonArray.length(); i++) {
                 String rolId = jsonArray.getString(i);
                 db.collection("Roles").document(rolId).get()
                         .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                             @Override
                             public void onSuccess(DocumentSnapshot documentSnapshot) {
                                 if (documentSnapshot.exists()) {
                                     pm.addAll((List<String>) documentSnapshot.get("permisos"));
                                     // Verificar si todas las consultas han terminado
                                     if (consultasCompletadas.incrementAndGet() == totalConsultas) {
                                         // Se han completado todas las consultas, ejecutar el código final
                                         editor.putString("permisos", pm.toString());
                                         verificarPermisos(pm.toString());
                                     }
                                 }
                             }
                         });
             }
         } catch (JSONException e) {
             e.printStackTrace();
         }



    }


   private void  GenerarReporteGeneral(){
       try {
           GenerarReporteOperacion service = new GenerarReporteOperacion(this);
           Dialog dialog = new Dialog(this, R.style.CustomAlertDialog);
           dialog.setContentView(R.layout.custom_dialog);
           service.setAsyncTaskListener(new AsyncTaskListener() {
                   @Override
                   public void showProgressBar() {
                       if(!dialog.isShowing()){
                           dialog.show();
                       }

                   }

                   @Override
                   public void CloseProgressBar() {
                       if(dialog.isShowing()){
                           dialog.dismiss();
                       }
                   }

                   @Override
                   public void datos(String resp) {

                       if (!resp.contains("Error en servicio")) {
                           ReporteGeneralFragment fragment = ReporteGeneralFragment.newInstance(resp, "");
                           getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                       } else {
                           Toast.makeText(getApplicationContext(), resp, Toast.LENGTH_LONG).show();
                       }
                   }
               });
               service.execute();

           } catch (Exception e) {
               Toast.makeText(this,"Error: "+e.getMessage(),Toast.LENGTH_LONG).show();
           }
   }


    private void GenerarReporteOperacionVenta(){
        try {
            GenerarReporteOperacionVenta service = new GenerarReporteOperacionVenta(this);
            Dialog dialog = new Dialog(this, R.style.CustomAlertDialog);
            dialog.setContentView(R.layout.custom_dialog);
            service.setAsyncTaskListener(new AsyncTaskListener() {
                @Override
                public void showProgressBar() {
                    if(!dialog.isShowing()){
                        dialog.show();
                    }

                }

                @Override
                public void CloseProgressBar() {
                    if(dialog.isShowing()){
                        dialog.dismiss();
                    }
                }

                @Override
                public void datos(String resp) {
                    if (!resp.contains("Error en servicio")) {
                        OperacionVentasFragment fragment = OperacionVentasFragment.newInstance(resp, "");
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                    } else {
                        Toast.makeText(getApplicationContext(), resp, Toast.LENGTH_LONG).show();
                    }
                }
            });
            service.execute();

        } catch (Exception e) {
            Toast.makeText(this,"Error: "+e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }



   public interface AsyncTaskListener {
        void showProgressBar();
        void CloseProgressBar();
        void datos(String result);
    }


    public interface AsyncTaskListener2 {
        void datos(String result);
    }

}