package com.example.appoperaciones;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.appoperaciones.Adaptadores.RecyclerEncuestaTienda;
import com.example.appoperaciones.Adaptadores.RecyclerResultadoDetalle;
import com.example.appoperaciones.Adaptadores.RecyclerResultadoEncuesta;
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
    private TextView tvrecibe;
    private TextView tvfirmaRecibe;
    private TextView tvfirmaInspector;
    private TextView tvmedidas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado_encuesta);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String data_json = getIntent().getStringExtra("datos");
        tienda = getIntent().getStringExtra("tienda");
        fecha = getIntent().getStringExtra("fecha");
        empleado = RecyclerResultadoEncuesta.corregirEncoding(getIntent().getStringExtra("empleado"));
        descripcion = RecyclerResultadoEncuesta.corregirEncoding(getIntent().getStringExtra("descripcion"));
        porcentajetotal = getIntent().getDoubleExtra("porcentajetotal",0);
        String nombreRecibe = RecyclerResultadoEncuesta.corregirEncoding(getIntent().getStringExtra("nombre_recibe"));
        String cargoRecibe = RecyclerResultadoEncuesta.corregirEncoding(getIntent().getStringExtra("cargo_recibe"));
        String medidasCorrectivas = RecyclerResultadoEncuesta.corregirEncoding(getIntent().getStringExtra("medidas_correctivas"));
        boolean tieneFirmaRecibe = getIntent().getBooleanExtra("tiene_firma_recibe", false);
        boolean tieneFirmaInspector = getIntent().getBooleanExtra("tiene_firma_inspector", false);
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
        tvrecibe = findViewById(R.id.recibe);
        tvfirmaRecibe = findViewById(R.id.tv_firma_recibe);
        tvfirmaInspector = findViewById(R.id.tv_firma_inspector);
        tvmedidas = findViewById(R.id.medidas_correctivas);

        tvdescripcion.setText(descripcion);
        tvempleado.setText(Html.fromHtml("<b>Auditor:</b> " + empleado));
        tvfecha.setText(Html.fromHtml("<b>Fecha:</b> " + fecha));
        tvtienda.setText(Html.fromHtml("<b>Tienda:</b> " + tienda));

        if (nombreRecibe != null && !nombreRecibe.trim().isEmpty()) {
            String textoRecibe = "<b>Recibió visita:</b> " + nombreRecibe;
            if (cargoRecibe != null && !cargoRecibe.trim().isEmpty()) {
                textoRecibe += " (" + cargoRecibe + ")";
            }
            tvrecibe.setText(Html.fromHtml(textoRecibe));
            tvrecibe.setVisibility(View.VISIBLE);
        } else {
            tvrecibe.setText(Html.fromHtml("<b>Recibió visita:</b> <font color='#888888'>No registrado</font>"));
            tvrecibe.setVisibility(View.VISIBLE);
        }

        String statusFirmaRecibe = tieneFirmaRecibe ? "<font color='#2E7D32'><b>✓ Firmado</b></font>" : "<font color='#D32F2F'><b>✕ Sin firma</b></font>";
        String statusFirmaInspector = tieneFirmaInspector ? "<font color='#2E7D32'><b>✓ Firmado</b></font>" : "<font color='#D32F2F'><b>✕ Sin firma</b></font>";

        tvfirmaRecibe.setText(Html.fromHtml("<b>Firma Tienda:</b> " + statusFirmaRecibe));
        tvfirmaInspector.setText(Html.fromHtml("<b>Firma Auditor:</b> " + statusFirmaInspector));

        if (medidasCorrectivas != null && !medidasCorrectivas.trim().isEmpty()) {
            tvmedidas.setText(Html.fromHtml("<b>Medidas correctivas:</b> " + medidasCorrectivas));
            tvmedidas.setVisibility(View.VISIBLE);
        } else {
            tvmedidas.setVisibility(View.GONE);
        }


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
            String rango = "";
            String color = "#000000";
            if (porcentajetotal <= 60.0) {
                rango = "BAJO";
                color = "#D32F2F";
            } else if (porcentajetotal <= 89.0) {
                rango = "MEDIO";
                color = "#F57C00";
            } else {
                rango = "ALTO";
                color = "#388E3C";
            }

            String textoPorcentaje = (decimal == 0.0) ? String.valueOf((int)porcentajetotal) : String.valueOf(porcentajetotal);
            tvporcentaje.setText(Html.fromHtml("<b>Porcentaje:</b> " + textoPorcentaje + "%  -  <font color='" + color + "'><b>" + rango + "</b></font>"));
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
