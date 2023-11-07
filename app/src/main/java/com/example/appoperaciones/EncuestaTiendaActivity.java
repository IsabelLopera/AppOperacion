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
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appoperaciones.Adaptadores.RecyclerEncuestaTienda;
import com.example.appoperaciones.Servicios.InsertarDetalleEncuestaTiendasAPP;
import com.example.appoperaciones.Servicios.InsertarEncabezadoEncuestaTiendasAPP;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class EncuestaTiendaActivity extends AppCompatActivity {
    private Type type = new TypeToken<ArrayList<JsonObject>>(){}.getType();
    ArrayList<JsonObject> datos = new ArrayList<>();
    RecyclerView recyclerView;
    RecyclerEncuestaTienda recyclerEncuestaTienda;
    Button enviarEncuestas;
    String idtienda;
    ArrayList<JsonObject> ListElement = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encuesta_tienda);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String data_json = getIntent().getStringExtra("datos");
        String encabezadotxt = getIntent().getStringExtra("encabezado");
        idtienda = getIntent().getStringExtra("idtienda");

        datos = new Gson().fromJson(data_json, type);
        recyclerView = findViewById(R.id.recyclerview);
        enviarEncuestas = findViewById(R.id.enviarEncuesta);
        TextView encabezado = findViewById(R.id.encabezado);
        encabezado.setText(encabezadotxt);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerEncuestaTienda = new RecyclerEncuestaTienda(datos, this);
        recyclerView.setAdapter(recyclerEncuestaTienda);

        enviarEncuestas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListElement = recyclerEncuestaTienda.getLista();
                int position = 0;
                boolean abrirDialog = true;
                for(int i =0; ListElement.size() > i; i++){
                    JsonObject jsonObject = ListElement.get(i);
                    if (jsonObject.get("valordefecto").getAsDouble() <= 3){
                        if(jsonObject.has("observacionadi")){
                            if(jsonObject.get("observacionadi").getAsString().equals("")){
                                abrirDialog =false;
                                position = i;
                                break;
                            }
                        }else{
                            abrirDialog =false;

                        }
                    }else{
                        jsonObject.addProperty("observacionadi","");
                    }


                }

                    // Posicionar el RecyclerView en el primer elemento sin diligenciar
                if (abrirDialog) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("¿Estas seguro de enviar la encuesta?");
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            InsertarEncabezadoEncuestaTiendas();
                            dialogInterface.dismiss();
                        }
                    });
                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.show();
                }else{
                    recyclerView.smoothScrollToPosition(position);
                    /*View itemView = recyclerView.getLayoutManager().findViewByPosition(position);
                    if (itemView != null) {

                        EditText editTextObser = itemView.findViewById(R.id.observacionadi);
                        editTextObser.setHintTextColor(Color.RED);
                    }*/
                    Toast.makeText(getApplicationContext(), "Debe diligenciar el campo observacion", Toast.LENGTH_LONG).show();

                }

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

                    for (JsonObject item : ListElement) {
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

                        String idencuestadetalle = item.get("idencuestadetalle").getAsString();
                        String observacionadi = item.get("observacionadi").getAsString();
                        String valorseleccionado = "";

                        double valor = item.get("valordefecto").getAsDouble();
                        if (valor == Math.floor(valor)) {
                            valorseleccionado = String.valueOf((int)valor);
                        }else{
                            valorseleccionado = String.valueOf(valor);
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
        service.execute(idtienda);

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