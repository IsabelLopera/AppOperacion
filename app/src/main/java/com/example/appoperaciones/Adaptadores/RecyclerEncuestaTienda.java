package com.example.appoperaciones.Adaptadores;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appoperaciones.R;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class RecyclerEncuestaTienda  extends RecyclerView.Adapter<RecyclerEncuestaTienda.ViewHolder> {
    ArrayList<JsonObject> lista;
    Context context;



    public RecyclerEncuestaTienda(ArrayList<JsonObject> lista, Context context) {
        this.context = context;
        this.lista = lista;
    }


    @Override
    public RecyclerEncuestaTienda.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_encuestatienda, null, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerEncuestaTienda.ViewHolder holder, int position) {

        holder.descripcion.setText(lista.get(position).get("descripcion").getAsString());
        holder.customTextWatcher.updatePosition(holder.getAdapterPosition());
        if (lista.get(position).has("observacionadi")) {
            holder.observacionadi.setText(lista.get(position).get("observacionadi").getAsString());

        }else{
            holder.observacionadi.setText("");
        }

        double valorEscala = lista.get(position).get("valorescala").getAsDouble();
        double valorDefecto = lista.get(position).get("valordefecto").getAsDouble();
        double valorInicial = lista.get(position).get("valorinicial").getAsDouble();
        double valorFinal = lista.get(position).get("valorfinal").getAsDouble();

        int numOpciones = (int) ((valorFinal - valorInicial / valorEscala)) + 1;
        holder.radioGroup.removeAllViews();
        for (int i = 0; i < numOpciones; i++) {
            RadioButton radioButton = new RadioButton(context);
            double opcionValor = valorInicial + (i * valorEscala);

            if (opcionValor == Math.floor(opcionValor)) {
                radioButton.setText(String.valueOf((int) opcionValor));
            } else {
                radioButton.setText(String.valueOf(opcionValor));
            }
            holder.radioGroup.addView(radioButton);
        }


        holder.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                // Obtener el índice de la respuesta seleccionada
                int respuestaIndex = radioGroup.indexOfChild(radioGroup.findViewById(checkedId)) + 1;
                double valorseleccionado = valorInicial + (respuestaIndex - 1) * valorEscala;
                // Actualizar el valor de respuesta seleccionada en el objeto Pregunta
                double numeroEnMedio = valorInicial + (numOpciones / 2) * valorEscala;

                if (valorseleccionado <= numeroEnMedio) {
                    holder.observacionadi.setVisibility(View.VISIBLE);

                } else {
                    holder.observacionadi.setVisibility(View.GONE);
                }
                lista.get(position).addProperty("valordefecto", valorseleccionado);
                // Validar si el campo de observación está diligenciado
            }
        });


        int opcionIndex = (int) ((valorDefecto - valorInicial) / valorEscala); // Calcular el índice de la opción
        if (opcionIndex >= 0 && opcionIndex < holder.radioGroup.getChildCount()) {
            RadioButton radioButton = (RadioButton) holder.radioGroup.getChildAt(opcionIndex);
            radioButton.setChecked(true);
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
        TextView descripcion;
        RadioGroup radioGroup;
        EditText observacionadi;
        CustomTextWatcher customTextWatcher = new CustomTextWatcher();

        public ViewHolder(View itemView) {
            super(itemView);
            descripcion = itemView.findViewById(R.id.descripcion);
            radioGroup = itemView.findViewById(R.id.radioGroup);
            observacionadi = itemView.findViewById(R.id.observacionadi);
            observacionadi.addTextChangedListener(customTextWatcher);
        }
    }

    public class CustomTextWatcher implements TextWatcher {

        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence s, int i, int i2, int i3) {
            lista.get(position).addProperty("observacionadi", s.toString());
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // no op
        }
    }

}
