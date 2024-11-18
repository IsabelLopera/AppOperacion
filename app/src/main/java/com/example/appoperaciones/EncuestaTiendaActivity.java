package com.example.appoperaciones;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appoperaciones.Adaptadores.RecyclerEncuestaTienda;
import com.example.appoperaciones.Servicios.InsertarDetalleEncuestaTiendasAPP;
import com.example.appoperaciones.Servicios.InsertarEncabezadoEncuestaTiendasAPP;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.sql.SQLTransactionRollbackException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;

public class EncuestaTiendaActivity extends AppCompatActivity {
    private Type type = new TypeToken<ArrayList<JsonObject>>(){}.getType();
    ArrayList<JsonObject> datos = new ArrayList<>();
    RecyclerView recyclerView;
    public static  RecyclerEncuestaTienda recyclerEncuestaTienda;
    Button enviarEncuestas;
    String idtienda;
    String idempleado;
    String idencuesta;
    boolean is_observ;
    ArrayList<JSONObject> ListElement = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encuesta_tienda);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String data_json = getIntent().getStringExtra("datos");
        String nom_tienda = getIntent().getStringExtra("nom_tienda");
        is_observ = getIntent().getBooleanExtra("is_observ",false);
        idtienda = getIntent().getStringExtra("idtienda");
        idempleado = getIntent().getStringExtra("idempleado");
        idencuesta = getIntent().getStringExtra("idencuesta");
        System.out.println(data_json);
        datos = new Gson().fromJson(data_json, type);
        Collections.sort(datos, new Comparator<JsonObject>() {
            @Override
            public int compare(JsonObject obj1, JsonObject obj2) {
                int clave1 = obj1.get("orden").getAsInt();
                int clave2 = obj2.get("orden").getAsInt();

                // Comparar los valores numéricos de "clave" y devolver el resultado de la comparación
                return Integer.compare(clave1, clave2);
            }
        });

        ArrayList<JSONObject> datosJSONObject = new ArrayList<>();
        try {

            for (JsonObject jsonObject : datos) {
                JSONObject J = new JSONObject(jsonObject.toString());
                J.put("noaplica",false);
                J.put("isobs",is_observ);
                J.put("observacionadi","");
                J.put("observacion","");
                datosJSONObject.add(J);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        recyclerView = findViewById(R.id.recyclerview);
        enviarEncuestas = findViewById(R.id.enviarEncuesta);
        TextView nombre_tienda = findViewById(R.id.nombre_tienda);
        nombre_tienda.setText(nom_tienda);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
       // recyclerView.setHasFixedSize(true);
        recyclerEncuestaTienda = new RecyclerEncuestaTienda(datosJSONObject, this);
        recyclerView.setAdapter(recyclerEncuestaTienda);

        enviarEncuestas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("¿Estas seguro de enviar la encuesta?");
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(obtenerPosicion() == -1){
                               InsertarEncabezadoEncuestaTiendas();
                            }
                            dialogInterface.dismiss();
                        }
                    });
                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.show();


            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        //definimos la accion al pulsar la fecla para ir hacia la actividad anterior.
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);


    }


    public int obtenerPosicion() {
        ArrayList<JSONObject>  list  = new ArrayList<>();
        try{
            for (int i = 0; i < recyclerEncuestaTienda.getLista().size(); i++) {
                JSONObject element = recyclerEncuestaTienda.getLista().get(i);

                boolean isobs = element.getBoolean("isobs");
                String  tiporespuesta = element.getString("tiporespuesta");
                String obs = element.getString("observacion");
                String obs_adi = element.getString("observacionadi");
                boolean noaplica = element.getBoolean("noaplica");

                    if (isobs) {
                        if (obs_adi.isEmpty()) {
                            recyclerView.smoothScrollToPosition(i);
                            Toast.makeText(getApplicationContext(), "Complete todos los campos requeridos.", Toast.LENGTH_SHORT).show();
                            return i;
                        }
                    }else{
                        element.put("observacionadi", "");
                    }

                    if(tiporespuesta.equals("EDITXT")){
                        if (obs.isEmpty()) {
                            recyclerView.smoothScrollToPosition(i);
                            Toast.makeText(getApplicationContext(), "Complete todos los campos requeridos.", Toast.LENGTH_SHORT).show();
                            return i;
                        }
                    }

                    if(noaplica){
                        element.put("valordefecto", -1);
                    }


                list.add(element);
            }

            ListElement = list;
            System.out.println(ListElement);

        }catch (JSONException e){
         e.printStackTrace();
        }

        return -1;
    }

    public void InsertarEncabezadoEncuestaTiendas(){
        //dialogo de confirmacion
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Proceso finalizado.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
                dialogInterface.dismiss();
            }
        });
        builder.setCancelable(false);
//dialogo de carga...
        Dialog dialog = new Dialog(this, R.style.CustomAlertDialog);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.show();
        InsertarEncabezadoEncuestaTiendasAPP service =new InsertarEncabezadoEncuestaTiendasAPP(this);
        service.setAsyncTaskListener(result -> {
            if (!result.contains("Error en servicio")) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String idempleadoencuesta = jsonObject.getString("idempleadoencuesta");
                            // Recorrer la lista de elementos
                    int count = 0;

                    for (JSONObject item : ListElement) {
                        InsertarDetalleEncuestaTiendasAPP service2 =new InsertarDetalleEncuestaTiendasAPP(getApplicationContext());
                        service2.setAsyncTaskListener(new AsyncTaskListener() {

                            @Override
                            public void datos(String count,String result) {
                                if(Integer.parseInt(count) == (ListElement.size()-1)){
                                    if(dialog.isShowing()){
                                        dialog.dismiss();
                                    }
                                    builder.setMessage(result);
                                    builder.show();
                                }
                            }
                        });

                        String idencuestadetalle = item.getString("idencuestadetalle");
                        String observacionadi = item.getString("observacionadi");
                        String valorseleccionado = "";
                        double valor = item.getDouble("valordefecto");
                        String  observacion = item.getString("observacion");

                        if(observacion.isEmpty()){
                            if (valor == Math.floor(valor)) {
                                valorseleccionado = String.valueOf((int)valor);
                            }else{
                                valorseleccionado = String.valueOf(valor);
                            }
                        }else{
                            valorseleccionado = observacion;
                        }


                        service2.execute(idempleadoencuesta,idencuestadetalle,valorseleccionado,observacionadi, String.valueOf(count++)).get();


                    }


                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Error en el servicio", Toast.LENGTH_LONG).show();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            }else{
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            }
        });

        service.execute(idtienda,idempleado,idencuesta);

    }

    public interface AsyncTaskListener {
        void datos(String count,String result);
    }

/*

    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("¿Esta seguro de realizar esta encuesta?");
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            ObtenerEncuestaDetalleTiendas();
            dialogInterface.dismiss();
        }
    });
                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {

        }
    });
                    builder.show();
*/
}