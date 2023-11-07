package com.example.appoperaciones.Adaptadores;

import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.appoperaciones.Clases.Tienda;
import com.example.appoperaciones.R;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RecyclerEstadisticaProm extends RecyclerView.Adapter<RecyclerEstadisticaProm.ViewHolder>{
    ArrayList<JsonObject> lista;

    public RecyclerEstadisticaProm.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_estadisticaprom, null, false);
        return new ViewHolder(view);
    }

    public RecyclerEstadisticaProm(ArrayList<JsonObject> lista) {
        this.lista = lista;
    }



    @Override
    public void onBindViewHolder(RecyclerEstadisticaProm.ViewHolder holder, int position) {

        try {
            JSONObject jsonObject = new JSONObject(lista.get(position).toString());
            String fecha=jsonObject.getString("fecha");
            String tienda = jsonObject.getString("nombre_tienda");
            if(fecha.isEmpty()){
                holder.fecha.setVisibility(View.GONE);
            }else{
                holder.fecha.setText(Html.fromHtml("<b>Fecha: </b>"+fecha));
            }
            if(tienda.equals("Todas")){
                holder.tienda.setVisibility(View.GONE);
            }else{
                holder.tienda.setText(Html.fromHtml("<b>Tienda: </b>"+tienda));
            }

            holder.promocion.setText(jsonObject.getString("promocion"));
            holder.contact.setText(Html.fromHtml("<b>Contact: </b>"+jsonObject.getString("contact")));
            holder.tiendavirtual.setText(Html.fromHtml("<b>Tienda virtual: </b>"+jsonObject.getString("tiendavirtual")));
            holder.total.setText(Html.fromHtml("<b>Total: </b>"+jsonObject.getString("total")));

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView promocion,tienda,contact,tiendavirtual,total,fecha;

        public ViewHolder(View itemView) {
            super(itemView);
            promocion = itemView.findViewById(R.id.promocion);
            fecha = itemView.findViewById(R.id.fecha);
            tienda = itemView.findViewById(R.id.tienda);
            contact = itemView.findViewById(R.id.contact);
            tiendavirtual = itemView.findViewById(R.id.tiendavirtual);
            total = itemView.findViewById(R.id.total);


        }
    }
}
