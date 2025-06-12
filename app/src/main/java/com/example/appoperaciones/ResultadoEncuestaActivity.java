package com.example.appoperaciones;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.example.appoperaciones.Adaptadores.RecyclerEncuestaTienda;
import com.example.appoperaciones.Adaptadores.RecyclerResultadoDetalle;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ResultadoEncuestaActivity extends AppCompatActivity {

    private Type type = new TypeToken<ArrayList<JsonObject>>() {}.getType();
    private ArrayList<JsonObject> datos = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerResultadoDetalle resultadoDetalle;
    private String empleado;
    private String tienda;
    private String fecha;
    private String descripcion;
    private double porcentajetotal;
    private double valorinicial;
    private double valorfinal;



    private TextView tvpuntuacion;
    private TextView tvporcentaje;
    private TextView tvfecha;
    private TextView tvempleado;
    private TextView tvdescripcion;
    private TextView tvtienda;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado_encuesta);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String data_json = getIntent().getStringExtra("datos");
        tienda = getIntent().getStringExtra("tienda");
        fecha = getIntent().getStringExtra("fecha");
        empleado = getIntent().getStringExtra("empleado");
        descripcion = getIntent().getStringExtra("descripcion");
        porcentajetotal = getIntent().getDoubleExtra("porcentajetotal",0);
        valorinicial = 0;
        valorfinal = 0;

        datos = new Gson().fromJson(data_json, type);
        Collections.sort(datos, new Comparator<JsonObject>() {
            @Override
            public int compare(JsonObject obj1, JsonObject obj2) {
                int clave1 = obj1.get("orden").getAsInt();
                int clave2 = obj2.get("orden").getAsInt();
                valorinicial = obj2.get("valor_inicial").getAsDouble();
                valorfinal = obj2.get("valor_final").getAsDouble();
                // Comparar los valores numéricos de "clave" y devolver el resultado de la comparación
                return Integer.compare(clave1, clave2);
            }
        });


        tvpuntuacion = findViewById(R.id.puntuacion);
        tvporcentaje = findViewById(R.id.porcentaje);
        recyclerView = findViewById(R.id.recyclerview);
        tvdescripcion = findViewById(R.id.descripcion);
        tvempleado = findViewById(R.id.empleado);
        tvfecha = findViewById(R.id.fecha);
        tvtienda = findViewById(R.id.tienda);

        tvdescripcion.setText(descripcion);
        tvempleado.setText(empleado);
        tvfecha.setText(fecha);
        tvtienda.setText(tienda);


        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        resultadoDetalle = new RecyclerResultadoDetalle(datos, this);
        recyclerView.setAdapter(resultadoDetalle);

        if(valorinicial > 0 && valorfinal >0){
            if (valorinicial == (int) valorinicial && valorfinal == (int) valorfinal) {
                // Si los valores tienen un decimal cero, convertirlos a enteros
                int valorInicialEntero = (int) valorinicial;
                int valorFinalEntero = (int) valorfinal;
                tvpuntuacion.setText(Html.fromHtml("<b>Puntuacion:</b> Del " + valorInicialEntero + " al " + valorFinalEntero));
            } else {
                // Si los valores tienen un decimal distinto de cero, mostrarlos tal como están
                tvpuntuacion.setText(Html.fromHtml("<b>Puntuacion:</b> Del " + valorinicial + " al " + valorfinal));
            }

        }
        if(porcentajetotal != 0){
            double decimal = porcentajetotal - Math.floor(porcentajetotal);

            if (decimal == 0.0) {
                tvporcentaje.setText(Html.fromHtml("<b>Porcentaje:</b> "+(int)porcentajetotal+"/100 %"));
            } else {
                tvporcentaje.setText(Html.fromHtml("<b>Porcentaje:</b> "+porcentajetotal+"/100 %"));
            }

        }
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


}
