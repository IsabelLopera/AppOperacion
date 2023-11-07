package com.example.appoperaciones.Adaptadores;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.appoperaciones.R;
import com.google.gson.JsonObject;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RecyclerHorarioAdmin extends  RecyclerView.Adapter<RecyclerHorarioAdmin.ViewHolder> {
    ArrayList<JsonObject> lista;
    Context context;
    private TextView emptyTextView;

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_horarioadmin, null, false);
        return new ViewHolder(view);
    }


    public RecyclerHorarioAdmin(ArrayList<JsonObject> lista, Context context,TextView emptyTextView) {
        this.lista =  lista;
        this.context =context;
        this.emptyTextView = emptyTextView;
    }




    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        try {
            JSONObject jsonObject = new JSONObject(lista.get(position).toString());
            holder.nombre.setText(jsonObject.getString("nombre"));
            holder.fechahora.setText(Html.fromHtml("<b>Fecha y hora: </b>"+jsonObject.getString("fechahora")));
            holder.tienda.setText(Html.fromHtml("<b>Tienda: </b>"+jsonObject.getString("tienda")));
            holder.tipoevento.setText(Html.fromHtml("<b>Evento: </b>"+jsonObject.getString("tipoevento")));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }



    public void updateEmptyView() {
        if (getItemCount() == 0) {
            emptyTextView.setVisibility(View.VISIBLE);
        } else {
            emptyTextView.setVisibility(View.GONE);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView nombre,fechahora,tienda,tipoevento;


        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            nombre = itemView.findViewById(R.id.nombre);
            fechahora = itemView.findViewById(R.id.fechahora);
            tienda = itemView.findViewById(R.id.tienda);
            tipoevento = itemView.findViewById(R.id.tipoevento);
        }
    }
}
