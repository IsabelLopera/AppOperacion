package com.example.appoperaciones.Adaptadores;

import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appoperaciones.R;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RecyclerEstadopAdapter  extends RecyclerView.Adapter<RecyclerEstadopAdapter.ViewHolder>{
    ArrayList<JsonObject> lista;
    private static final int VIEW_TYPE_ONE = 1;
    private static final int VIEW_TYPE_TWO = 2;
    private int estadoActual =  Estado.VISTA_UNO;

    public RecyclerEstadopAdapter(ArrayList<JsonObject> lista) {

        this.lista = lista;
    }

    @Override
    public int getItemViewType(int position) {

        if (estadoActual == Estado.VISTA_UNO) {
            return VIEW_TYPE_ONE;
        } else {
            return VIEW_TYPE_TWO;
        }
    }



    public void setEstadoActual(int estado) {
        this.estadoActual = estado;
        notifyDataSetChanged();
         ; // Asegurar que las vistas se actualicen
    }

    @NotNull
    @Override
    public RecyclerEstadopAdapter.ViewHolder  onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        if (viewType == VIEW_TYPE_ONE) {
            view = inflater.inflate(R.layout.item_estpedido_res, null, false);
        } else {
            view = inflater.inflate(R.layout.item_estadopedidos, null, false);

        }
        return new ViewHolder(view);
    }




    @Override
    public void onBindViewHolder(RecyclerEstadopAdapter.ViewHolder holder, int position) {
        try {

            JSONObject jsonObject = new JSONObject(lista.get(position).toString());
            String estado = jsonObject.getString("estatus");

            if(holder.getItemViewType() == VIEW_TYPE_ONE) {
                holder.estado.setText(estado);
                holder.direccion.setText(jsonObject.getString("direccion"));
                holder.tiempo_total.setText(jsonObject.getString("tiempototal"));
                holder.transaccion.setText(jsonObject.getString("transaccion"));
                holder.tiempoenruta.setText(jsonObject.getString("tiempoenruta"));

                holder.estado.setBackgroundColor(Color.parseColor(ColorEstado1(estado)));
                holder.direccion.setBackgroundColor(Color.parseColor(ColorEstado1(estado)));
                holder.tiempo_total.setBackgroundColor(Color.parseColor(ColorEstado1(estado)));
                holder.transaccion.setBackgroundColor(Color.parseColor(ColorEstado1(estado)));
                holder.tiempoenruta.setBackgroundColor(Color.parseColor(ColorEstado1(estado)));
            }else{

                holder.estado.setText(estado);
                holder.direccion.setText(Html.fromHtml("<b>Dirección: </b>"+jsonObject.getString("direccion")));
                holder.tiempo_total.setText(Html.fromHtml("<b>Tiempo Total: </b>"+jsonObject.getString("tiempototal")));
                holder.transaccion.setText(Html.fromHtml("<b>Pedido # </b>"+jsonObject.getString("transaccion")));
                holder.tiempoenruta.setText(Html.fromHtml("<b>Tiempo en ruta: </b>"+jsonObject.getString("tiempoenruta")));
                holder.estado.setBackgroundColor(Color.parseColor(ColorEstado2(estado)));
                String domiciliario  =jsonObject.getString("domiciliario");
                if(domiciliario == "null"){
                    domiciliario =  "";
                }
                holder.domiciliario.setText(Html.fromHtml("<b>Domiciliario: </b>"+domiciliario));
                holder.origen.setText(Html.fromHtml("<b>Origen: </b>"+jsonObject.getString("tomadordepedido")));
                holder.nombrecompleto.setText(Html.fromHtml("<b>Cliente: </b>"+jsonObject.getString("nombrecompleto")));

            }







        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return lista.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombrecompleto,estado,direccion,origen, tiempo_total,transaccion,tiempoenruta,domiciliario;


        public ViewHolder(@NotNull View itemView) {
            super(itemView);

            nombrecompleto = itemView.findViewById(R.id.nombrecompleto);
            estado = itemView.findViewById(R.id.estado);
            direccion = itemView.findViewById(R.id.direccion);
            origen = itemView.findViewById(R.id.origen);
            tiempo_total = itemView.findViewById(R.id.tiempo_total);
            transaccion = itemView.findViewById(R.id.transaccion);
            tiempoenruta = itemView.findViewById(R.id.tiempoenruta);
            domiciliario = itemView.findViewById(R.id.domiciliario);


        }
    }

    private String ColorEstado1(String estado){
        String color = "#FFFFFFFF";
        switch (estado){
            case  "Esperando":
                color = "#F6E6B6";
                break;
            case "En Cocina":
                color = "#F1BF8D";
                break;
            case "Programado":
                color = "#F69696";
                break;
            case "Finalizado":
                color = "#C0C2ED";
                break;

            case "En Ruta":
                color = "#C0F3D0";
                break;




        }

        return  color;

    }

    private String ColorEstado2(String estado){
        String color = "#313132";
        switch (estado){
            case  "Esperando":
                color = "#FFCD3A";
             break;
            case "En Cocina":
                color = "#CD853F";
                break;
            case "Programado":
                color = "#FF0000";
                break;
            case "Finalizado":
                color = "#9EA1F6";
                break;

            case "En Ruta":
                color = "#36EE6E";
                break;




        }

        return  color;

    }

    public void actualizarDatos(ArrayList<JsonObject> nuevosDatos) {
        lista.clear(); // Limpiamos la lista actual
        lista.addAll(nuevosDatos); // Agregamos los nuevos datos a la lista
        notifyDataSetChanged(); // Notificamos al adaptador que los datos han cambiado y se debe actualizar la vista
    }
    public class Estado {
        public static final int VISTA_UNO = 1;
        public static final int VISTA_DOS = 2;
    }

}
