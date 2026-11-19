package com.example.appoperaciones;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appoperaciones.Adaptadores.RecyclerEncuestaTienda;
import com.example.appoperaciones.Clases.SignatureView;
import com.example.appoperaciones.Servicios.InsertarDetalleEncuestaTiendasAPP;
import com.example.appoperaciones.Servicios.InsertarEncabezadoEncuestaTiendasAPP;
import com.example.appoperaciones.Servicios.ObtenerEmpleadosTiendaAPP;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.sql.SQLTransactionRollbackException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;

public class EncuestaTiendaActivity extends AppCompatActivity {
    private Type type = new TypeToken<ArrayList<JsonObject>>(){}.getType();
    ArrayList<JsonObject> datos = new ArrayList<>();
    RecyclerView recyclerView;
    NestedScrollView nestedScrollView;
    public static  RecyclerEncuestaTienda recyclerEncuestaTienda;
    Button enviarEncuestas;
    LinearLayout layoutTotal;
    TextView tvTotalPorcentaje, tvRangoCumplimiento;
    AutoCompleteTextView etNombreRecibe;
    EditText etCargoRecibe, etMedidasCorrectivas;
    SignatureView signatureView, signatureViewInspector;
    Button btnLimpiarFirma, btnLimpiarFirmaInspector;
    double currentTotalScore = 0.0;
    String currentRango = "BAJO";
    String idtienda;
    String idempleado;
    String idencuesta;
    boolean is_observ;
    ArrayList<JSONObject> ListElement = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encuesta_tienda);
        nestedScrollView = findViewById(R.id.nested_scroll_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String data_json = getIntent().getStringExtra("datos");
        String nom_tienda = getIntent().getStringExtra("nom_tienda");
        is_observ = getIntent().getBooleanExtra("is_observ",false);
        idtienda = getIntent().getStringExtra("idtienda");
        idempleado = getIntent().getStringExtra("idempleado");
        idencuesta = getIntent().getStringExtra("idencuesta");
        if ("13".equals(idencuesta)) {
            getSupportActionBar().setTitle("Inspección de Seguridad Alimentaria");
        }

        datos = new Gson().fromJson(data_json, type);
        Collections.sort(datos, new Comparator<JsonObject>() {
            @Override
            public int compare(JsonObject obj1, JsonObject obj2) {
                int clave1 = obj1.get("orden").getAsInt();
                int clave2 = obj2.get("orden").getAsInt();

                // Comparar los valores numéricos de "clave" y devolver el resultado de la comparación
                return Integer.compare(clave1, clave2);
            }
        });

        ArrayList<JSONObject> datosJSONObject = new ArrayList<>();
        SharedPreferences draftPrefs = getSharedPreferences("draft_encuestas", MODE_PRIVATE);
        String draftKey = "draft_" + (idencuesta != null ? idencuesta : "0") + "_" + (idtienda != null ? idtienda : "0");

        String savedJson = null;
        if (savedInstanceState != null && savedInstanceState.containsKey("saved_lista")) {
            savedJson = savedInstanceState.getString("saved_lista");
        } else if (draftPrefs.contains(draftKey)) {
            savedJson = draftPrefs.getString(draftKey, null);
        }

        if (savedJson != null && !savedJson.isEmpty()) {
            try {
                org.json.JSONArray savedArray = new org.json.JSONArray(savedJson);
                for (int i = 0; i < savedArray.length(); i++) {
                    datosJSONObject.add(savedArray.getJSONObject(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                savedJson = null;
                datosJSONObject.clear();
            }
        }

        if (savedJson == null || datosJSONObject.isEmpty()) {
            try {
                for (JsonObject jsonObject : datos) {
                    JSONObject J = new JSONObject(jsonObject.toString());
                    J.put("noaplica",false);
                    String tiporespuesta = J.optString("tiporespuesta", "");
                    if (tiporespuesta.equals("VNDA")) {
                        double vDefecto = J.optDouble("valordefecto", 1.0);
                        double vFinal = J.optDouble("valorfinal", 1.0);
                        double vEscala = J.optDouble("valorescala", 1.0);
                        double vInicial = J.optDouble("valorinicial", 0.0);
                        int numOpciones = (int) Math.round((vFinal - vInicial) / (vEscala > 0 ? vEscala : 1.0)) + 1;
                        boolean reqObs = (numOpciones <= 2) ? (vDefecto < vFinal) : (vDefecto < (vInicial + vFinal) / 2.0);
                        J.put("isobs", reqObs);
                    } else {
                        J.put("isobs", is_observ);
                    }
                    J.put("observacionadi","");
                    J.put("observacion","");
                    datosJSONObject.add(J);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        recyclerView = findViewById(R.id.recyclerview);
        enviarEncuestas = findViewById(R.id.enviarEncuesta);
        layoutTotal = findViewById(R.id.layout_total);
        tvTotalPorcentaje = findViewById(R.id.tv_total_porcentaje);
        tvRangoCumplimiento = findViewById(R.id.tv_rango_cumplimiento);
        etNombreRecibe = findViewById(R.id.et_nombre_recibe);
        etCargoRecibe = findViewById(R.id.et_cargo_recibe);
        etMedidasCorrectivas = findViewById(R.id.et_medidas_correctivas);
        signatureView = findViewById(R.id.signature_view);
        btnLimpiarFirma = findViewById(R.id.btn_limpiar_firma);
        signatureViewInspector = findViewById(R.id.signature_view_inspector);
        btnLimpiarFirmaInspector = findViewById(R.id.btn_limpiar_firma_inspector);

        String savedNombreRecibe = null;
        String savedCargoRecibe = null;
        String savedMedidas = null;

        if (savedInstanceState != null) {
            savedNombreRecibe = savedInstanceState.getString("saved_nombre_recibe", null);
            savedCargoRecibe = savedInstanceState.getString("saved_cargo_recibe", null);
            savedMedidas = savedInstanceState.getString("saved_medidas_correctivas", null);
        } else if (draftPrefs.contains(draftKey + "_nombre")) {
            savedNombreRecibe = draftPrefs.getString(draftKey + "_nombre", null);
            savedCargoRecibe = draftPrefs.getString(draftKey + "_cargo", null);
            savedMedidas = draftPrefs.getString(draftKey + "_medidas", null);
        }

        if (savedNombreRecibe != null && etNombreRecibe != null) {
            etNombreRecibe.setText(savedNombreRecibe);
        }
        if (savedCargoRecibe != null && etCargoRecibe != null) {
            etCargoRecibe.setText(savedCargoRecibe);
        }
        if (savedMedidas != null && etMedidasCorrectivas != null) {
            etMedidasCorrectivas.setText(savedMedidas);
        }

        if (etNombreRecibe != null) {
            etNombreRecibe.setThreshold(1);
            etNombreRecibe.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String seleccion = (String) parent.getItemAtPosition(position);
                    if (seleccion != null) {
                        if (seleccion.contains(" - ")) {
                            String[] partes = seleccion.split(" - ", 2);
                            etNombreRecibe.setText(partes[0].trim(), false);
                            etNombreRecibe.setSelection(partes[0].trim().length());
                            if (etCargoRecibe != null) {
                                etCargoRecibe.setText(partes[1].trim());
                            }
                        } else {
                            etNombreRecibe.setText(seleccion, false);
                            etNombreRecibe.setSelection(seleccion.length());
                        }
                    }
                }
            });
            etNombreRecibe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (etNombreRecibe.getAdapter() != null && etNombreRecibe.getText().toString().isEmpty()) {
                        etNombreRecibe.showDropDown();
                    }
                }
            });
        }

        if (idtienda != null && !idtienda.isEmpty()) {
            cargarEmpleadosTienda(idtienda);
        }

        if (btnLimpiarFirma != null && signatureView != null) {
            btnLimpiarFirma.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signatureView.clear();
                }
            });
        }

        if (btnLimpiarFirmaInspector != null && signatureViewInspector != null) {
            btnLimpiarFirmaInspector.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signatureViewInspector.clear();
                }
            });
        }

        TextView nombre_tienda = findViewById(R.id.nombre_tienda);
        nombre_tienda.setText(nom_tienda);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
       // recyclerView.setHasFixedSize(true);
        recyclerEncuestaTienda = new RecyclerEncuestaTienda(datosJSONObject, this);
        recyclerEncuestaTienda.setOnRespuestaChangeListener(new RecyclerEncuestaTienda.OnRespuestaChangeListener() {
            @Override
            public void onRespuestaChanged() {
                calcularTotalYRango();
                guardarBorrador();
            }
        });
        recyclerView.setAdapter(recyclerEncuestaTienda);

        calcularTotalYRango();

        enviarEncuestas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (obtenerPosicion() != -1) {
                    return;
                }

                String nombreRecibe = etNombreRecibe != null ? etNombreRecibe.getText().toString().trim() : "";
                if (nombreRecibe.isEmpty()) {
                    Toast.makeText(view.getContext(), "Por favor ingrese el nombre de quien recibe la visita.", Toast.LENGTH_SHORT).show();
                    if (etNombreRecibe != null) {
                        etNombreRecibe.setError("Ingrese el nombre de quien recibe");
                        etNombreRecibe.requestFocus();
                        scrollToView(etNombreRecibe);
                    }
                    return;
                }

                if (signatureView != null && !signatureView.isSigned()) {
                    Toast.makeText(view.getContext(), "Por favor capture la firma de quien recibe la visita.", Toast.LENGTH_SHORT).show();
                    scrollToView(signatureView);
                    return;
                }

                if (signatureViewInspector != null && !signatureViewInspector.isSigned()) {
                    Toast.makeText(view.getContext(), "Por favor capture la firma de quien realiza la inspección.", Toast.LENGTH_SHORT).show();
                    scrollToView(signatureViewInspector);
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("¿Estas seguro de enviar la encuesta?");
                if (layoutTotal != null && layoutTotal.getVisibility() == View.VISIBLE) {
                    builder.setMessage("Calificación: " + currentTotalScore + "%\nRango: " + currentRango);
                }
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        InsertarEncabezadoEncuestaTiendas();
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();
            }
        });
    }

    public void calcularTotalYRango() {
        if (recyclerEncuestaTienda == null || recyclerEncuestaTienda.getLista() == null) return;
        double totalPorcentaje = 0.0;
        boolean tienePorcentajes = false;

        for (JSONObject item : recyclerEncuestaTienda.getLista()) {
            if (item.has("porcentaje") && item.has("tiporespuesta")) {
                try {
                    String tipo = item.getString("tiporespuesta");
                    if (tipo.equals("VNDA")) {
                        double porcentaje = item.optDouble("porcentaje", 0);
                        if (porcentaje > 0) {
                            tienePorcentajes = true;
                            boolean noaplica = item.optBoolean("noaplica", false);
                            if (!noaplica) {
                                double valor = item.optDouble("valordefecto", 0);
                                if (valor == 1.0) {
                                    totalPorcentaje += porcentaje;
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        if (tienePorcentajes && layoutTotal != null) {
            layoutTotal.setVisibility(View.VISIBLE);
            double totalRedondeado = Math.round(totalPorcentaje * 10.0) / 10.0;
            currentTotalScore = totalRedondeado;

            if (totalRedondeado <= 60.0) {
                currentRango = "BAJO";
                tvRangoCumplimiento.setText("Rango: BAJO");
                tvRangoCumplimiento.setTextColor(Color.parseColor("#D32F2F"));
            } else if (totalRedondeado <= 89.0) {
                currentRango = "MEDIO";
                tvRangoCumplimiento.setText("Rango: MEDIO");
                tvRangoCumplimiento.setTextColor(Color.parseColor("#F57C00"));
            } else {
                currentRango = "ALTO";
                tvRangoCumplimiento.setText("Rango: ALTO");
                tvRangoCumplimiento.setTextColor(Color.parseColor("#388E3C"));
            }
            tvTotalPorcentaje.setText("Total: " + totalRedondeado + "%");
        } else if (layoutTotal != null) {
            layoutTotal.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        guardarBorrador();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (recyclerEncuestaTienda != null && recyclerEncuestaTienda.getLista() != null) {
            outState.putString("saved_lista", recyclerEncuestaTienda.getLista().toString());
        }
        if (etNombreRecibe != null) {
            outState.putString("saved_nombre_recibe", etNombreRecibe.getText().toString());
        }
        if (etCargoRecibe != null) {
            outState.putString("saved_cargo_recibe", etCargoRecibe.getText().toString());
        }
        if (etMedidasCorrectivas != null) {
            outState.putString("saved_medidas_correctivas", etMedidasCorrectivas.getText().toString());
        }
    }

    private void guardarBorrador() {
        if (idencuesta == null || idtienda == null) return;
        if (recyclerEncuestaTienda == null || recyclerEncuestaTienda.getLista() == null) return;
        try {
            SharedPreferences draftPrefs = getSharedPreferences("draft_encuestas", MODE_PRIVATE);
            String draftKey = "draft_" + idencuesta + "_" + idtienda;
            SharedPreferences.Editor editor = draftPrefs.edit();
            editor.putString(draftKey, recyclerEncuestaTienda.getLista().toString());
            if (etNombreRecibe != null) {
                editor.putString(draftKey + "_nombre", etNombreRecibe.getText().toString());
            }
            if (etCargoRecibe != null) {
                editor.putString(draftKey + "_cargo", etCargoRecibe.getText().toString());
            }
            if (etMedidasCorrectivas != null) {
                editor.putString(draftKey + "_medidas", etMedidasCorrectivas.getText().toString());
            }
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void limpiarBorrador() {
        if (idencuesta == null || idtienda == null) return;
        SharedPreferences draftPrefs = getSharedPreferences("draft_encuestas", MODE_PRIVATE);
        String draftKey = "draft_" + idencuesta + "_" + idtienda;
        draftPrefs.edit()
                .remove(draftKey)
                .remove(draftKey + "_nombre")
                .remove(draftKey + "_cargo")
                .remove(draftKey + "_medidas")
                .apply();
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                guardarBorrador();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        guardarBorrador();
        super.onBackPressed();
    }


    public int obtenerPosicion() {
        ArrayList<JSONObject> list = new ArrayList<>();
        try {
            for (int i = 0; i < recyclerEncuestaTienda.getLista().size(); i++) {
                JSONObject element = recyclerEncuestaTienda.getLista().get(i);

                boolean isobs = element.optBoolean("isobs", false);
                String tiporespuesta = element.optString("tiporespuesta", "");
                String obs = element.optString("observacion", "");
                String obs_adi = element.optString("observacionadi", "");
                boolean noaplica = element.optBoolean("noaplica", false);
                String descripcion = element.optString("descripcion", "");

                if (noaplica) {
                    element.put("valordefecto", -1);
                    element.put("isobs", false);
                } else {
                    if (isobs) {
                        if (obs_adi.trim().isEmpty()) {
                            dirigirACampoFaltante(i, descripcion, "Por favor complete la observación requerida para este ítem.");
                            return i;
                        }
                    } else {
                        element.put("observacionadi", "");
                    }

                    if (tiporespuesta.equals("EDITXT")) {
                        if (obs.trim().isEmpty()) {
                            dirigirACampoFaltante(i, descripcion, "Por favor complete este campo requerido.");
                            return i;
                        }
                    }
                }

                list.add(element);
            }

            ListElement = list;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return -1;
    }

    private void dirigirACampoFaltante(final int pos, final String descripcion, final String mensaje) {
        Toast.makeText(getApplicationContext(), mensaje + "\n(" + descripcion + ")", Toast.LENGTH_LONG).show();

        if (nestedScrollView == null) return;

        recyclerView.scrollToPosition(pos);

        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                View targetView = null;
                RecyclerView.ViewHolder vh = recyclerView.findViewHolderForAdapterPosition(pos);
                if (vh != null && vh.itemView != null) {
                    targetView = vh.itemView;
                } else if (recyclerView.getLayoutManager() != null) {
                    targetView = recyclerView.getLayoutManager().findViewByPosition(pos);
                }

                if (targetView != null) {
                    int scrollY = targetView.getTop() + recyclerView.getTop();
                    nestedScrollView.smoothScrollTo(0, Math.max(0, scrollY - 60));

                    View observacionadi = targetView.findViewById(R.id.observacionadi);
                    View ediText = targetView.findViewById(R.id.ediText);

                    if (observacionadi != null && observacionadi.getVisibility() == View.VISIBLE) {
                        observacionadi.requestFocus();
                        if (observacionadi instanceof EditText) {
                            ((EditText) observacionadi).setError(mensaje);
                        }
                    } else if (ediText != null && ediText.getVisibility() == View.VISIBLE) {
                        ediText.requestFocus();
                        if (ediText instanceof EditText) {
                            ((EditText) ediText).setError(mensaje);
                        }
                    } else {
                        targetView.requestFocus();
                    }
                } else {
                    int totalCount = recyclerEncuestaTienda != null ? recyclerEncuestaTienda.getItemCount() : 1;
                    int scrollY = recyclerView.getTop() + (recyclerView.getHeight() * pos) / Math.max(1, totalCount);
                    nestedScrollView.smoothScrollTo(0, Math.max(0, scrollY - 60));
                }
            }
        }, 150);
    }

    private void scrollToView(View target) {
        if (target == null || nestedScrollView == null) return;
        int scrollY = target.getTop();
        View parent = (View) target.getParent();
        while (parent != null && parent != nestedScrollView) {
            scrollY += parent.getTop();
            if (parent.getParent() instanceof View) {
                parent = (View) parent.getParent();
            } else {
                break;
            }
        }
        nestedScrollView.smoothScrollTo(0, Math.max(0, scrollY - 60));
    }

    public void InsertarEncabezadoEncuestaTiendas(){
        //dialogo de confirmacion
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Proceso finalizado.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                limpiarBorrador();
                finish();
                dialogInterface.dismiss();
            }
        });
        builder.setCancelable(false);
//dialogo de carga...
        Dialog dialog = new Dialog(this, R.style.CustomAlertDialog);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.show();
        InsertarEncabezadoEncuestaTiendasAPP service =new InsertarEncabezadoEncuestaTiendasAPP(this);
        service.setAsyncTaskListener(result -> {
            if (!result.contains("Error en servicio")) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String idempleadoencuesta = jsonObject.getString("idempleadoencuesta");
                    // Recorrer la lista de elementos
                    int count = 0;

                    for (JSONObject item : ListElement) {

                        String valorseleccionado = "";
                        double valor = item.getDouble("valordefecto");
                        String  observacion = item.getString("observacion");

                        if(observacion.isEmpty()){
                            if (valor == Math.floor(valor)) {
                                valorseleccionado = String.valueOf((int)valor);
                            }else{
                                valorseleccionado = String.valueOf(valor);
                            }
                        }else{
                            valorseleccionado = observacion;
                        }

                        item.put("valor",valorseleccionado);
                        item.put("idempleadoencuesta",idempleadoencuesta);


                    }

                    InsertarDetalleEncuestaTiendasAPP service2 =new InsertarDetalleEncuestaTiendasAPP(getApplicationContext());
                    service2.setAsyncTaskListener(new AsyncTaskListener() {

                        @Override
                        public void datos(String result) {

                            if(dialog.isShowing()){
                                dialog.dismiss();
                            }
                            builder.setMessage(result);
                            builder.show();

                        }
                    });

                    service2.execute(ListElement.toString()).get();


                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Error en el servicio", Toast.LENGTH_LONG).show();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            }else{
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            }
        });

        String nombreRecibe = etNombreRecibe != null ? etNombreRecibe.getText().toString().trim() : "";
        String cargoRecibe = etCargoRecibe != null ? etCargoRecibe.getText().toString().trim() : "";
        String medidasCorrectivas = etMedidasCorrectivas != null ? etMedidasCorrectivas.getText().toString().trim() : "";
        String firmaBase64 = signatureView != null ? signatureView.getBase64Signature() : "";
        String firmaInspectorBase64 = signatureViewInspector != null ? signatureViewInspector.getBase64Signature() : "";

        service.execute(idtienda, idempleado, idencuesta, nombreRecibe, cargoRecibe, medidasCorrectivas, firmaBase64, firmaInspectorBase64);

    }

    private void cargarEmpleadosTienda(String tiendaId) {
        ObtenerEmpleadosTiendaAPP service = new ObtenerEmpleadosTiendaAPP(this, new ObtenerEmpleadosTiendaAPP.OnEmpleadosObtenidosListener() {
            @Override
            public void onResultado(String result) {
                if (result != null && !result.startsWith("Error")) {
                    try {
                        org.json.JSONArray jsonArray = new org.json.JSONArray(result);
                        List<String> nombresConCargo = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            org.json.JSONObject emp = jsonArray.getJSONObject(i);
                            String nombre = emp.optString("nombre", "");
                            String cargo = emp.optString("cargo", "");
                            if (!nombre.isEmpty()) {
                                if (!cargo.isEmpty()) {
                                    nombresConCargo.add(nombre + " - " + cargo);
                                } else {
                                    nombresConCargo.add(nombre);
                                }
                            }
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                EncuestaTiendaActivity.this,
                                android.R.layout.simple_dropdown_item_1line,
                                nombresConCargo
                        );
                        if (etNombreRecibe != null) {
                            etNombreRecibe.setAdapter(adapter);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        service.execute(tiendaId);
    }

    public interface AsyncTaskListener {
        void datos(String result);
    }

/*

    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("¿Esta seguro de realizar esta encuesta?");
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            ObtenerEncuestaDetalleTiendas();
            dialogInterface.dismiss();
        }
    });
                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {

        }
    });
                    builder.show();
*/
}