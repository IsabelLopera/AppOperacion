package com.example.appoperaciones.Adaptadores;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appoperaciones.EncuestaTiendaActivity;
import com.example.appoperaciones.R;
import com.example.appoperaciones.ReportesOperacionActivity;
import com.example.appoperaciones.ResultadoEncuestaActivity;
import com.example.appoperaciones.Servicios.AprobarEgresoServicio;
import com.example.appoperaciones.Servicios.ObtenerEgresosServicio;
import com.example.appoperaciones.Servicios.ObtenerResultadoEncuestaOperacionDet;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class RecyclerResultadoEncuesta  extends  RecyclerView.Adapter<RecyclerResultadoEncuesta.ViewHolder>{
    private List<JsonObject> lista;
    private Context context;
    private String tienda;

    public RecyclerResultadoEncuesta(List<JsonObject> lista, Context context,String tienda) {
        this.lista =  lista;
        this.context =context;
        this.tienda = tienda;
    }


    @Override
    public RecyclerResultadoEncuesta.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_resultadoencuesta, null, false);
        return new ViewHolder(view);
    }

    public static String corregirEncoding(String texto) {
        if (texto == null || texto.isEmpty()) return "";
        try {
            if (texto.contains("Ã")) {
                return new String(texto.getBytes(java.nio.charset.StandardCharsets.ISO_8859_1), java.nio.charset.StandardCharsets.UTF_8);
            }
        } catch (Exception ignored) {}
        return texto;
    }

    @Override
    public void onBindViewHolder(RecyclerResultadoEncuesta.ViewHolder holder, int position) {
        try {
            JSONObject jsonObject = new JSONObject(lista.get(position).toString());

            holder.nombre.setText(corregirEncoding(jsonObject.getString("nombre_empleado")));
            holder.fechahora.setText(jsonObject.getString("fecha_hora"));
            holder.tipoencuesta.setText(corregirEncoding(jsonObject.getString("descripcion_encuesta")));
            double porcentaje;
            if(jsonObject.has("porcentaje_total")){
                porcentaje = jsonObject.getDouble("porcentaje_total");
            }else{
                porcentaje = 0;
            }

            if(porcentaje != 0){
                double decimal = porcentaje - Math.floor(porcentaje);
                String rango = "";
                String color = "#000000";
                if (porcentaje <= 60.0) {
                    rango = "BAJO";
                    color = "#D32F2F";
                } else if (porcentaje <= 89.0) {
                    rango = "MEDIO";
                    color = "#F57C00";
                } else {
                    rango = "ALTO";
                    color = "#388E3C";
                }

                String textoPorcentaje = (decimal == 0.0) ? String.valueOf((int)porcentaje) : String.valueOf(porcentaje);
                holder.porcentaje.setText(Html.fromHtml("<b>Calificación:</b> " + textoPorcentaje + "%  -  <font color='" + color + "'><b>" + rango + "</b></font>"));
            }else{
                holder.porcentaje.setText("");
            }

            String nombreRecibe = corregirEncoding(jsonObject.optString("nombre_recibe", ""));
            String cargoRecibe = corregirEncoding(jsonObject.optString("cargo_recibe", ""));
            String medidasCorrectivas = corregirEncoding(jsonObject.optString("medidas_correctivas", ""));
            boolean tieneFirmaRecibe = jsonObject.optBoolean("tiene_firma_recibe", false);
            boolean tieneFirmaInspector = jsonObject.optBoolean("tiene_firma_inspector", false);

            if (!nombreRecibe.isEmpty()) {
                String textoRecibe = "<b>Recibió:</b> " + nombreRecibe;
                if (!cargoRecibe.isEmpty()) {
                    textoRecibe += " (" + cargoRecibe + ")";
                }
                holder.recibeItem.setText(Html.fromHtml(textoRecibe));
                holder.recibeItem.setVisibility(View.VISIBLE);
            } else {
                holder.recibeItem.setVisibility(View.GONE);
            }

            if (tieneFirmaRecibe && tieneFirmaInspector) {
                holder.firmasItem.setText(Html.fromHtml("<font color='#2E7D32'><b>✓ Firmas registradas (2/2)</b></font>"));
                holder.firmasItem.setVisibility(View.VISIBLE);
            } else if (tieneFirmaRecibe || tieneFirmaInspector) {
                holder.firmasItem.setText(Html.fromHtml("<font color='#F57C00'><b>⚠ 1 firma registrada</b></font>"));
                holder.firmasItem.setVisibility(View.VISIBLE);
            } else {
                holder.firmasItem.setText(Html.fromHtml("<font color='#888888'>Sin firmas registradas</font>"));
                holder.firmasItem.setVisibility(View.VISIBLE);
            }

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        int  idempeladoencuesta = jsonObject.getInt("idempleadoencuesta");
                        String fecha =jsonObject.getString("fecha_hora");
                        String empleado  = corregirEncoding(jsonObject.getString("nombre_empleado"));
                        String descripcion  = corregirEncoding(jsonObject.getString("descripcion_encuesta"));

                        createSimpleDialog(idempeladoencuesta, empleado, fecha, descripcion, porcentaje,
                                nombreRecibe, cargoRecibe, medidasCorrectivas, tieneFirmaRecibe, tieneFirmaInspector);
                    } catch (JSONException e) {
                        System.out.println(e);
                    }

                }
            });

        } catch (JSONException e) {
            System.out.println(e);
        }




    }

    public void createSimpleDialog(int idempeladoencuesta, String empleado, String fecha, String descripcion, double porcentaje,
                               String nombreRecibe, String cargoRecibe, String medidasCorrectivas, boolean tieneFirmaRecibe, boolean tieneFirmaInspector) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Confirmacion")
                .setMessage(Html.fromHtml("Si desea abrir la encuesta seleccione <b>Continuar</b>"))
                .setPositiveButton("Continuar",
                        (dialog, which) -> {
                            try {
                                Dialog dg = new Dialog(context, R.style.CustomAlertDialog);
                                dg.setContentView(R.layout.custom_dialog);
                                dg.show();
                                ObtenerResultadoEncuestaOperacionDet operacionDet = new ObtenerResultadoEncuestaOperacionDet(context);
                                operacionDet.setAsyncTaskListener(new ReportesOperacionActivity.AsyncTaskListener() {
                                    @Override
                                    public void showProgressBar() {
                                        if(!dg.isShowing()){
                                            dg.show();
                                        }
                                    }

                                    @Override
                                    public void CloseProgressBar() {
                                        if(dg.isShowing()){
                                            dg.dismiss();
                                        }
                                    }

                                    @Override
                                    public void datos(String result) {
                                        if (!result.contains("Error en servicio")) {

                                            Gson gson = new Gson();
                                            // Definir el tipo de datos que queremos convertir
                                            Type tipoLista = new TypeToken<List<JsonObject>>(){}.getType();
                                            // Convertir la cadena JSON en una lista de objetos Empleado
                                            List<JsonObject> listaResultados = gson.fromJson(result, tipoLista);
                                            Intent intent= new Intent(context, ResultadoEncuestaActivity.class);
                                            intent.putExtra("datos",result);
                                            intent.putExtra("descripcion",descripcion);
                                            intent.putExtra("tienda",tienda);
                                            intent.putExtra("empleado",empleado);
                                            intent.putExtra("fecha", fecha);
                                            intent.putExtra("porcentajetotal",porcentaje);
                                            intent.putExtra("nombre_recibe", nombreRecibe);
                                            intent.putExtra("cargo_recibe", cargoRecibe);
                                            intent.putExtra("medidas_correctivas", medidasCorrectivas);
                                            intent.putExtra("tiene_firma_recibe", tieneFirmaRecibe);
                                            intent.putExtra("tiene_firma_inspector", tieneFirmaInspector);
                                            context.startActivity(intent);


                                        }else{
                                            Toast.makeText(context,result,Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                                operacionDet.execute(String.valueOf(idempeladoencuesta));
                            }catch (Exception e ) {
                                System.out.println(e);
                            }

                            dialog.dismiss();

                        })
                .setNegativeButton("Cancelar", null);

        builder.create();
        builder.show();
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombre,fechahora,tipoencuesta,porcentaje,recibeItem,firmasItem;
        CardView cardView;
        public ViewHolder(View itemView) {
            super(itemView);

            nombre = itemView.findViewById(R.id.nombre);
            fechahora = itemView.findViewById(R.id.fechahora);
            tipoencuesta = itemView.findViewById(R.id.tipoencuesta);
            cardView =  itemView.findViewById(R.id.card_view);
            porcentaje =  itemView.findViewById(R.id.porcentaje);
            recibeItem = itemView.findViewById(R.id.recibe_item);
            firmasItem = itemView.findViewById(R.id.firmas_item);
        }
    }
}
