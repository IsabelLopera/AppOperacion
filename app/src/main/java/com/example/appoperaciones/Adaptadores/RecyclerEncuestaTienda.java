package com.example.appoperaciones.Adaptadores;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.ColorStateList;
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
import androidx.core.widget.CompoundButtonCompat;
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

    public interface OnRespuestaChangeListener {
        void onRespuestaChanged();
    }
    private OnRespuestaChangeListener respuestaChangeListener;

    public void setOnRespuestaChangeListener(OnRespuestaChangeListener listener) {
        this.respuestaChangeListener = listener;
    }

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
            holder.customObservacionWatcher.updatePosition(holder.observacionadi, position);
            holder.descripcion.setTextColor(ContextCompat.getColor(context, R.color.gris));
            holder.descripcion.setTypeface(Typeface.DEFAULT);
            holder.ediText.setVisibility(View.GONE);
            holder.textView.setVisibility(View.GONE);
            holder.layout_radioGroup.setVisibility(View.GONE);
            holder.observacionadi.setText(item.optString("observacionadi", ""));
            if(item.optBoolean("isobs", false)){
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
        CustomObservacionWatcher customObservacionWatcher = new CustomObservacionWatcher();
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
            observacionadi.addTextChangedListener(customObservacionWatcher);
            cardView = itemView.findViewById(R.id.card_view);
            layout_radioGroup =itemView.findViewById(R.id.layout_radioGroup);
            checkbox = itemView.findViewById(R.id.checkbox);
        }
    }

    public class CustomObservacionWatcher implements TextWatcher {
        private int position;
        private EditText editText;

        public void updatePosition(EditText editText, int position) {
            this.editText = editText;
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence s, int i, int i1, int i2) {
            if (position >= 0 && position < lista.size()) {
                try {
                    lista.get(position).put("observacionadi", s.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            String texto = editable.toString();
            if (editText != null && !TextUtils.isEmpty(texto)) {
                Drawable drawable = editText.getBackground();
                if (drawable != null) {
                    drawable.setColorFilter(Color.parseColor("#146CF2"), PorterDuff.Mode.SRC_ATOP);
                    editText.setBackground(drawable);
                }
            }
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
        try {
            holder.layout_radioGroup.setVisibility(View.VISIBLE);
            double valorEscala = item.getDouble("valorescala");
            double valorDefecto = item.getDouble("valordefecto");
            double valorInicial = item.getDouble("valorinicial");
            double valorFinal = item.getDouble("valorfinal");

            int numOpciones = (int) Math.round((valorFinal - valorInicial) / valorEscala) + 1;

            // 1. Desvincular listeners antes de manipular vistas para evitar disparos accidentales en reciclado
            holder.radioGroup.setOnCheckedChangeListener(null);
            holder.checkbox.setOnCheckedChangeListener(null);

            // 2. Limpiar vistas previas
            holder.radioGroup.removeAllViews();

            int[][] states = new int[][] {
                new int[] { android.R.attr.state_checked },
                new int[] { -android.R.attr.state_checked }
            };
            int[] redColors = new int[] {
                Color.parseColor("#D32F2F"), // Rojo cuando está seleccionado
                Color.parseColor("#8E8E8E")  // Gris cuando está desmarcado
            };
            int[] normalColors = new int[] {
                Color.parseColor("#146CF2"), // Azul cuando está seleccionado
                Color.parseColor("#8E8E8E")  // Gris cuando está desmarcado
            };
            ColorStateList redTint = new ColorStateList(states, redColors);
            ColorStateList normalTint = new ColorStateList(states, normalColors);

            for (int i = 0; i < numOpciones; i++) {
                RadioButton radioButton = new RadioButton(context);
                radioButton.setId(View.generateViewId());
                double opcionValor = valorInicial + (i * valorEscala);

                if (opcionValor == Math.floor(opcionValor)) {
                    radioButton.setText(String.valueOf((int) opcionValor));
                } else {
                    radioButton.setText(String.valueOf(opcionValor));
                }

                if (opcionValor == 0.0) {
                    CompoundButtonCompat.setButtonTintList(radioButton, redTint);
                } else {
                    CompoundButtonCompat.setButtonTintList(radioButton, normalTint);
                }

                holder.radioGroup.addView(radioButton);
            }

            // 3. Marcar la opción seleccionada ANTES de enlazar el listener
            int opcionIndex = (int) Math.round((valorDefecto - valorInicial) / valorEscala);
            if (opcionIndex >= 0 && opcionIndex < holder.radioGroup.getChildCount()) {
                RadioButton radioButton = (RadioButton) holder.radioGroup.getChildAt(opcionIndex);
                radioButton.setChecked(true);
            } else {
                holder.radioGroup.clearCheck();
            }

            // 4. Configurar estado del CheckBox de No Aplica y visibilidad de observación
            boolean noAplica = item.optBoolean("noaplica", false);
            holder.checkbox.setChecked(noAplica);
            if (noAplica) {
                holder.radioGroup.setVisibility(View.GONE);
                holder.observacionadi.setVisibility(View.GONE);
                holder.observacionadi.setError(null);
            } else {
                holder.radioGroup.setVisibility(View.VISIBLE);
                boolean reqObs = (numOpciones <= 2) ? (valorDefecto < valorFinal) : (valorDefecto < (valorInicial + valorFinal) / 2.0);
                holder.observacionadi.setVisibility(reqObs ? View.VISIBLE : View.GONE);
            }

            actualizarEstilosRadioButtons(holder.radioGroup, valorInicial, valorEscala);

            // 5. Vincular listener de CheckBox
            holder.checkbox.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                try {
                    item.put("noaplica", isChecked);
                    if (isChecked) {
                        item.put("isobs", false);
                        holder.observacionadi.setVisibility(View.GONE);
                        holder.observacionadi.setError(null);
                        holder.radioGroup.setVisibility(View.GONE);
                        if (holder.observacionadi.isFocused()) {
                            holder.observacionadi.clearFocus();
                        }
                    } else {
                        holder.radioGroup.setVisibility(View.VISIBLE);
                        int checkedId = holder.radioGroup.getCheckedRadioButtonId();
                        if (checkedId != -1) {
                            View checkedChild = holder.radioGroup.findViewById(checkedId);
                            if (checkedChild != null) {
                                int rIndex = holder.radioGroup.indexOfChild(checkedChild) + 1;
                                double vSel = valorInicial + (rIndex - 1) * valorEscala;
                                boolean req = (numOpciones <= 2) ? (vSel < valorFinal) : (vSel < (valorInicial + valorFinal) / 2.0);
                                holder.observacionadi.setVisibility(req ? View.VISIBLE : View.GONE);
                                item.put("isobs", req);
                            }
                        }
                    }
                    if (respuestaChangeListener != null) {
                        respuestaChangeListener.onRespuestaChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });

            // 6. Vincular listener de RadioGroup
            holder.radioGroup.setOnCheckedChangeListener((radioGroup, checkedId) -> {
                if (checkedId == -1) {
                    return;
                }
                View checkedChild = radioGroup.findViewById(checkedId);
                if (checkedChild == null) {
                    return;
                }
                int respuestaIndex = radioGroup.indexOfChild(checkedChild) + 1;
                if (respuestaIndex <= 0) {
                    return;
                }
                double valorseleccionado = valorInicial + (respuestaIndex - 1) * valorEscala;

                try {
                    item.put("valordefecto", valorseleccionado);

                    if (!item.optBoolean("noaplica", false)) {
                        boolean requiereObs;
                        if (numOpciones <= 2) {
                            requiereObs = (valorseleccionado < valorFinal);
                        } else {
                            requiereObs = (valorseleccionado < (valorInicial + valorFinal) / 2.0);
                        }

                        if (requiereObs) {
                            holder.observacionadi.setVisibility(View.VISIBLE);
                            item.put("isobs", true);

                            // Limpiar foco del ítem anterior para evitar que el NestedScrollView se devuelva
                            if (context instanceof Activity) {
                                View currentFocus = ((Activity) context).getCurrentFocus();
                                if (currentFocus != null && currentFocus != holder.observacionadi) {
                                    currentFocus.clearFocus();
                                }
                            }
                            holder.observacionadi.post(() -> {
                                holder.observacionadi.requestFocus();
                            });
                        } else {
                            holder.observacionadi.setVisibility(View.GONE);
                            item.put("isobs", false);
                            holder.observacionadi.setError(null);
                            if (holder.observacionadi.isFocused()) {
                                holder.observacionadi.clearFocus();
                            }
                        }
                    }

                    actualizarEstilosRadioButtons(radioGroup, valorInicial, valorEscala);

                    if (respuestaChangeListener != null) {
                        respuestaChangeListener.onRespuestaChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void actualizarEstilosRadioButtons(RadioGroup radioGroup, double valorInicial, double valorEscala) {
        for (int k = 0; k < radioGroup.getChildCount(); k++) {
            View child = radioGroup.getChildAt(k);
            if (child instanceof RadioButton) {
                RadioButton rb = (RadioButton) child;
                double v = valorInicial + (k * valorEscala);
                if (rb.isChecked()) {
                    if (v == 0.0) {
                        rb.setTextColor(Color.parseColor("#D32F2F"));
                        rb.setTypeface(Typeface.DEFAULT_BOLD);
                    } else {
                        rb.setTextColor(Color.parseColor("#146CF2"));
                        rb.setTypeface(Typeface.DEFAULT_BOLD);
                    }
                } else {
                    rb.setTextColor(Color.parseColor("#616161"));
                    rb.setTypeface(Typeface.DEFAULT);
                }
            }
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
