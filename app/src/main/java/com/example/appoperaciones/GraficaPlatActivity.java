package com.example.appoperaciones;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GraficaPlatActivity extends AppCompatActivity {

    private BarChart chart;
    ArrayList<JsonObject> lista_datos = new ArrayList<>();
    private Type type = new TypeToken<ArrayList<JsonObject>>(){}.getType();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafica_plat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String datos =  getIntent().getStringExtra("datos");
        lista_datos = new Gson().fromJson(datos,type );// Obtener tu ArrayList<Object> con los datos
        String rango_fecha = getIntent().getStringExtra("fechas");
        String tipo_grafica = getIntent().getStringExtra("tipo");

        chart = findViewById(R.id.chart);
        TextView textFecha = findViewById(R.id.textFecha);
        TextView textTipo = findViewById(R.id.textTipo);
        textFecha.setText(rango_fecha);
        textTipo.setText(tipo_grafica);
        try{

        Legend legend = chart.getLegend();
        legend.setEnabled(true); // Habilitar la leyenda
        legend.setForm(Legend.LegendForm.SQUARE); // Establecer la forma de la leyenda (cuadrados)
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
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

        float totalVentas = 0;
        float total_cantidadpedidos= 0;

        for (int i = 0; i < lista_datos.size(); i++) {
            JsonObject objeto = lista_datos.get(i);
            if(tipo_grafica.toLowerCase().equals("porcentaje / total de venta")){
                float totalVenta = objeto.get("totalventa").getAsFloat();
                totalVentas += totalVenta;
            }else if(tipo_grafica.toLowerCase().equals("porcentaje / cantidad de pedidos")){
                float cantidadpedidos = objeto.get("cantidadpedidos").getAsFloat();
                total_cantidadpedidos += cantidadpedidos;
            }

        }


        for (int i = 0; i < lista_datos.size(); i++) {
            JsonObject obj = lista_datos.get(i);
            int color = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
            colors.add(color);
            LegendEntry entry = new LegendEntry();
            entry.formColor = color; // Color asociado a la barra
            float values = 0;

            if(tipo_grafica.toLowerCase().equals("total de venta")){
                values = obj.get("totalventa").getAsFloat();

            }else if(tipo_grafica.toLowerCase().equals("cantidad de pedidos")){
                values = obj.get("cantidadpedidos").getAsFloat();
            }else if(tipo_grafica.toLowerCase().equals("porcentaje / total de venta")){
                float totalVenta = obj.get("totalventa").getAsFloat();
                float porcentaje = (float) ((totalVenta / totalVentas) * 100);
                values = porcentaje;

            }else if (tipo_grafica.toLowerCase().equals("porcentaje / cantidad de pedidos")){
                float cantidadpedidos = obj.get("cantidadpedidos").getAsFloat();
                float porcentaje = (float) ((cantidadpedidos / total_cantidadpedidos) * 100);
                values = porcentaje;
            }
            entry.label = obj.get("fuente").getAsString(); // Nombre de la barra
            legendEntries.add(entry);
            entries.add(new BarEntry(i,values));
            labels.add(obj.get("fuente").getAsString());
        }

        legend.setCustom(legendEntries);

        BarDataSet dataSet = new BarDataSet(entries, "Histogram");
        dataSet.setColors(colors);
        dataSet.setValueTextSize(12f);
        if(tipo_grafica.toLowerCase().contains("porcentaje")) {
            dataSet.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return String.format("%.1f%%", value);
                }
            });
        }
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


        }catch (Exception e){
            System.out.println(e);
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