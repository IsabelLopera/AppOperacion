package com.example.appoperaciones.Adaptadores;

import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appoperaciones.R;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Calendar;

public class RecyclerResultadoDetalle  extends RecyclerView.Adapter<RecyclerResultadoDetalle.ViewHolder> {
    ArrayList<JsonObject> lista;
    Context context;


    public RecyclerResultadoDetalle(ArrayList<JsonObject> lista, Context context) {
        this.context = context;
        this.lista = lista;

    }


    @Override
    public RecyclerResultadoDetalle.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_resultadodetalle, null, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String item = lista.get(position).get("item").getAsString();
        String observacion = lista.get(position).get("observacion").getAsString();
        String tiporespuesta =  lista.get(position).get("tipo_respuesta").getAsString();
        String respuesta = lista.get(position).get("respuesta").getAsString();
        double valor_inicial = lista.get(position).get("valor_inicial").getAsDouble();

        holder.descripcion.setText(item);
        holder.descripcion.setTextColor(ContextCompat.getColor(context, R.color.gris));
        holder.descripcion.setTypeface(Typeface.DEFAULT);

        if (tiporespuesta.equals("SPD")) {
            holder.campoText.setVisibility(View.GONE);
            holder.descripcion.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            holder.descripcion.setTypeface(Typeface.DEFAULT_BOLD);

        }else{
            holder.campoText.setVisibility(View.VISIBLE);

            if(valor_inicial != 0){
                if(Double.parseDouble(respuesta) == valor_inicial ){
                    holder.campoText.setTextColor(ContextCompat.getColor(context, R.color.rojo));
                }else{
                    holder.campoText.setTextColor(ContextCompat.getColor(context, R.color.gris));
                }
            }
            if(respuesta.equals("-1")){
                respuesta ="N/A";
            }
            holder.campoText.setText(Html.fromHtml("<b>Respuesta:</b> "+respuesta));

        }

        if (!observacion.isEmpty()) {
            holder.observacion.setVisibility(View.VISIBLE);
            holder.observacion.setText(Html.fromHtml("<b>Observacion:</b> "+observacion));

        }else{
            holder.observacion.setVisibility(View.GONE);
        }

    }

    public ArrayList<JsonObject> getLista() {
        return lista;
    }


    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView descripcion,observacion,campoText;

        public ViewHolder(View itemView) {
            super(itemView);
            descripcion = itemView.findViewById(R.id.descripcion);
            observacion = itemView.findViewById(R.id.observacion);
            campoText=itemView.findViewById(R.id.campoText);

        }
    }





}
