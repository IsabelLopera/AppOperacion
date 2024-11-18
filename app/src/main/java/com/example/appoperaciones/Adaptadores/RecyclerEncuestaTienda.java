package com.example.appoperaciones.Adaptadores;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appoperaciones.EncuestaTiendaActivity;
import com.example.appoperaciones.R;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class RecyclerEncuestaTienda  extends RecyclerView.Adapter<RecyclerEncuestaTienda.ViewHolder> {
    ArrayList<JSONObject> lista;
    Context context;
    private  Calendar cldr = Calendar.getInstance();
    private int hour;
    private int minutes;

    public RecyclerEncuestaTienda(ArrayList<JSONObject> lista, Context context) {
        this.context = context;
        this.lista = lista;
        this.hour = cldr.get(Calendar.HOUR_OF_DAY);
        this.minutes = cldr.get(Calendar.MINUTE);

    }


    @Override
    public RecyclerEncuestaTienda.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_encuestatienda, null, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerEncuestaTienda.ViewHolder holder, int position) {


            JSONObject item = lista.get(position);
            try {

            holder.descripcion.setText(item.getString("descripcion"));
            holder.customTextWatcher.updatePosition(holder.ediText, position);
            holder.descripcion.setTextColor(ContextCompat.getColor(context, R.color.gris));
            holder.descripcion.setTypeface(Typeface.DEFAULT);
            holder.ediText.setVisibility(View.GONE);
            holder.textView.setVisibility(View.GONE);
            holder.layout_radioGroup.setVisibility(View.GONE);
            holder.observacionadi.setText(item.getString("observacionadi"));
                if(item.getBoolean("isobs")){
                    holder.observacionadi.setVisibility(View.VISIBLE);
                }else{
                    holder.observacionadi.setVisibility(View.GONE);
                }
            // Verificar si el elemento tiene tipo de respuesta
            if (item.has("tiporespuesta")) {
                String tipoRespuesta = item.getString("tiporespuesta");
                switch (tipoRespuesta) {
                    case "VNDA":
                        handleVNDA(holder, item);
                        break;
                    case "EDITXT":
                        handleEDITXT(holder, item);
                        break;
                    case "SPD":
                        handleSPD(holder,item);
                        break;
                    case "TIME":
                        handleTIME(holder, item);
                        break;
                    default:
                        // Manejar otro tipo de respuesta o lanzar una excepción si es necesario
                }
            }

               } catch (JSONException e) {
                    e.printStackTrace();
                }

    }


    public ArrayList<JSONObject> getLista() {
        return lista;
    }


    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView descripcion ,textView;
        RadioGroup radioGroup;
        EditText observacionadi,ediText;
        CustomTextWatcher customTextWatcher = new CustomTextWatcher();
        CardView cardView ;
        LinearLayout layout_radioGroup;
        CheckBox checkbox;

        public ViewHolder(View itemView) {
            super(itemView);
            descripcion = itemView.findViewById(R.id.descripcion);
            radioGroup = itemView.findViewById(R.id.radioGroup);
            observacionadi = itemView.findViewById(R.id.observacionadi);
            ediText=itemView.findViewById(R.id.ediText);
            textView=itemView.findViewById(R.id.textView);
            ediText.addTextChangedListener(customTextWatcher);
            cardView = itemView.findViewById(R.id.card_view);
            layout_radioGroup =itemView.findViewById(R.id.layout_radioGroup);
            checkbox = itemView.findViewById(R.id.checkbox);
            observacionadi.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                    try {
                        lista.get(getAdapterPosition()).put("observacionadi", s.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String texto = editable.toString();
                    if (!TextUtils.isEmpty(texto)) {
                        Drawable drawable = observacionadi.getBackground(); // get current EditText drawable
                        drawable.setColorFilter(Color.parseColor("#146CF2"), PorterDuff.Mode.SRC_ATOP);
                        observacionadi.setBackground(drawable);
                    }
                }
            });
        }
    }

    public class CustomTextWatcher implements TextWatcher {

        private int position;
        private EditText editText;

        public void updatePosition(EditText editText, int position) {
            this.editText = editText;
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence s, int i, int i2, int i3) {
            try {
                lista.get(position).put("observacion", s.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {
            // Validar el contenido del EditText después de que cambie el texto
            String texto = editable.toString();
            if (!TextUtils.isEmpty(texto)) {
                Drawable drawable = editText.getBackground(); // get current EditText drawable
                drawable.setColorFilter(Color.parseColor("#146CF2"), PorterDuff.Mode.SRC_ATOP);
                editText.setBackground(drawable);
            }

        }

    }


    // Método para manejar la respuesta de tipo "VNDA"
    private void handleVNDA(ViewHolder holder, JSONObject item) throws JSONException {

        try{
            holder.layout_radioGroup.setVisibility(View.VISIBLE);
            double valorEscala = item.getDouble("valorescala");
            double valorDefecto = item.getDouble("valordefecto");
            double valorInicial = item.getDouble("valorinicial");
            double valorFinal = item.getDouble("valorfinal");

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


            holder.checkbox.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                try{

                    if (isChecked) {

                        item.put("noaplica", true);
                        item.put("isobs",false);
                       // item.put("valordefecto", -1);
                        holder.observacionadi.setVisibility(View.GONE);
                        holder.radioGroup.setVisibility(View.GONE);


                    }else{
                        item.put("noaplica", false);
                        item.put("isobs",true);
                        holder.observacionadi.setVisibility(View.VISIBLE);
                        holder.radioGroup.setVisibility(View.VISIBLE);
                        //RadioButton radioButton = (RadioButton) holder.radioGroup.getChildAt(0);
                        //radioButton.setChecked(true);
                        //   SeleccionarRadio(holder,1,valorInicial,numOpciones,valorEscala);
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }


            });
            holder.checkbox.setChecked(item.getBoolean("noaplica"));

            holder.radioGroup.setOnCheckedChangeListener((radioGroup, checkedId) -> {
                try {
                    int respuestaIndex = holder.radioGroup.indexOfChild(holder.radioGroup.findViewById(checkedId)) + 1;
                    //RadioButton radioButton = holder.radioGroup.findViewById(checkedId);
                    //int respuestaIndex = Integer.parseInt(radioButton.getText().toString());

                    double valorseleccionado = valorInicial + (respuestaIndex - 1) * valorEscala;
                    // Actualizar el valor de respuesta seleccionada en el objeto Pregunta
                    double numeroEnMedio = valorInicial + (numOpciones / 2) * valorEscala;

                    if(!item.getBoolean("noaplica")){
                        if (valorseleccionado <= numeroEnMedio) {
                            holder.observacionadi.setVisibility(View.VISIBLE);
                            item.put("isobs",true);

                        }else{
                            holder.observacionadi.setVisibility(View.GONE);
                            item.put("isobs",false);

                        }
                    }


                    item.put("valordefecto", valorseleccionado);


                    // Validar si el campo de observación está diligenciado
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            });


            int opcionIndex = (int) ((valorDefecto - valorInicial) / valorEscala); // Calcular el índice de la opción
            System.out.println("indice: "+opcionIndex);
            if (opcionIndex >= 0 && opcionIndex < holder.radioGroup.getChildCount()) {
                RadioButton radioButton = (RadioButton) holder.radioGroup.getChildAt(opcionIndex);
                radioButton.setChecked(true);
            }

        }catch (JSONException e){
            e.printStackTrace();
        }



    }

    // Método para manejar la respuesta de tipo "EDITXT"
    private void handleEDITXT(ViewHolder holder, JSONObject item) {
        try{
            holder.ediText.setVisibility(View.VISIBLE);
            holder.ediText.setText(item.getString("observacion"));
        }catch (JSONException e){
            e.printStackTrace();
        }


    }

    // Método para manejar la respuesta de tipo "SPD"
    private void handleSPD(RecyclerEncuestaTienda.ViewHolder holder,JSONObject item) {
        try {
            item.put("isobs",false);
            holder.descripcion.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            holder.descripcion.setTypeface(Typeface.DEFAULT_BOLD);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    // Método para manejar la respuesta de tipo "TIME"
    private void handleTIME(ViewHolder holder, JSONObject item) {
        try{
            item.put("isobs",false);
            holder.textView.setVisibility(View.VISIBLE);
            holder.textView.setText(hour + ":" + minutes);
            item.put("observacion",hour + ":" + minutes);

            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // time picker dialog
                    TimePickerDialog  picker = new TimePickerDialog(context,
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker timePicker, int sHour, int sMinute) {
                                    try{
                                        hour =sHour;
                                        minutes =sMinute;
                                        String hora = sHour + ":" + sMinute;
                                        holder.textView.setText(hora);
                                        item.put("observacion",hora);
                                    }catch (JSONException e){
                                        e.printStackTrace();
                                    }



                                }

                            }, hour, minutes, true);

                    picker.show();
                }
            });
        }catch (JSONException e){
            e.printStackTrace();
        }


    }

}
