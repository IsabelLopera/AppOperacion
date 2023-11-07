package com.example.appoperaciones;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GraficaPromActivity extends AppCompatActivity {

    private BarChart chart;
    ArrayList<JsonObject> lista_datos = new ArrayList<>();
    private Type type = new TypeToken<ArrayList<JsonObject>>(){}.getType();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafica_prom);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String datos =  getIntent().getStringExtra("datos");
        lista_datos = new Gson().fromJson(datos,type );// Obtener tu ArrayList<Object> con los datos
        String rango_fecha = getIntent().getStringExtra("fechas");
        chart = findViewById(R.id.chart);
        TextView textFecha = findViewById(R.id.textFecha);
        textFecha.setText(rango_fecha);


        Legend legend = chart.getLegend();
        legend.setEnabled(true); // Habilitar la leyenda
        legend.setForm(Legend.LegendForm.SQUARE); // Establecer la forma de la leyenda (cuadrados)
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setDrawInside(false);

// Opcional: Ajustar el margen y el tamaño de la leyenda
        legend.setFormSize(8f);
        legend.setTextSize(12f);
        legend.setXEntrySpace(12f);
        legend.setYEntrySpace(8f);
        legend.setWordWrapEnabled(true);

        List<LegendEntry> legendEntries = new ArrayList<>();

      // Establecer las entradas personalizadas en la leyenda

        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        Random random = new Random();

        ArrayList<Integer> colors = new ArrayList<>();

        for (int i = 0; i < lista_datos.size(); i++) {
            JsonObject obj = lista_datos.get(i);
            int color = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
            colors.add(color);
            LegendEntry entry = new LegendEntry();
            entry.formColor = color; // Color asociado a la barra
            entry.label = obj.get("promocion").getAsString(); // Nombre de la barra
            legendEntries.add(entry);


            entries.add(new BarEntry(i,obj.get("total").getAsInt()));
            labels.add(obj.get("promocion").getAsString());
        }

        legend.setCustom(legendEntries);

        BarDataSet dataSet = new BarDataSet(entries, "Histogram");
        dataSet.setColors(colors);
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.5f); // Ancho de las barras (0.9f = 90% del espacio disponible)
        chart.setData(barData);
        //chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels)); // Etiquetas del eje X
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM); // Posición del eje X
        //chart.getXAxis().setLabelRotationAngle(45);
        chart.getAxisLeft().setAxisMinimum(0f); // Valor mínimo del eje Y
        chart.getAxisRight().setEnabled(false); // Desactivar el eje Y de la derecha
        chart.getDescription().setEnabled(false);
        chart.setFitBars(true); // Ajustar el tamaño de las barras automáticamente
        chart.invalidate(); // Refrescar el gráfico
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