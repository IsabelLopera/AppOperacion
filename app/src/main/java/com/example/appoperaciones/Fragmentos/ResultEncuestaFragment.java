package com.example.appoperaciones.Fragmentos;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appoperaciones.Adaptadores.RecyclerEgresos;
import com.example.appoperaciones.Adaptadores.RecyclerResultadoEncuesta;
import com.example.appoperaciones.Clases.Encuesta;
import com.example.appoperaciones.Clases.Tienda;
import com.example.appoperaciones.R;
import com.example.appoperaciones.ReportesOperacionActivity;
import com.example.appoperaciones.Servicios.CRUDTiempoPedido;
import com.example.appoperaciones.Servicios.ObtenerResultadoEncuestaOperacion;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResultEncuestaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResultEncuestaFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Spinner spinner_tipo;
    private Spinner spinner_tiendas;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView fecha1;
    private TextView fecha2;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private int Dia;
    private int Mes;
    private int Año;

    private int Dia2;
    private int Mes2;
    private int Año2;
    private Button consultar;
    private RecyclerView recyclerView;
    private TextView texttienda;

    public ResultEncuestaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ResultEncuestaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResultEncuestaFragment newInstance(String param1, String param2) {
        ResultEncuestaFragment fragment = new ResultEncuestaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result_encuesta, container, false);
        spinner_tiendas = view.findViewById(R.id.tienda);
        spinner_tipo = view.findViewById(R.id.tipo);
        consultar = view.findViewById(R.id.consultar);
        fecha1 = view.findViewById(R.id.fecha1);
        fecha2 = view.findViewById(R.id.fecha2);
        texttienda = view.findViewById(R.id.texttienda);
        fecha1.setInputType(InputType.TYPE_NULL);
        fecha2.setInputType(InputType.TYPE_NULL);

        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));

        Calendar cldr = Calendar.getInstance();
        Dia = cldr.get(Calendar.DAY_OF_MONTH);
        Mes = cldr.get(Calendar.MONTH);
        Año = cldr.get(Calendar.YEAR);

        Dia2 = cldr.get(Calendar.DAY_OF_MONTH);
        Mes2 = cldr.get(Calendar.MONTH);
        Año2 = cldr.get(Calendar.YEAR);

        String fechaFormateada = sdf.format(cldr.getTime());
        fecha1.setText(fechaFormateada);
        fecha2.setText(fechaFormateada);

        fecha1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog.OnDateSetListener listenerDeDatePicker = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int año, int mes, int diaDelMes) {
                        Calendar cldr = Calendar.getInstance();
                        cldr.set(Calendar.YEAR, año);
                        cldr.set(Calendar.MONTH, mes); // No es necesario restar 1 aquí
                        cldr.set(Calendar.DAY_OF_MONTH, diaDelMes);

                        Año =año;
                        Mes = mes;
                        Dia =diaDelMes;
                        String fechaFormateada = sdf.format(cldr.getTime());
                        // Establecer la fecha formateada en tu campo de texto fecha
                        fecha1.setText(fechaFormateada);
                    }
                };

                DatePickerDialog dialogoFecha = new DatePickerDialog(getContext(), listenerDeDatePicker, Año,Mes,Dia);
                dialogoFecha.show();


            }
        });

        fecha2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog.OnDateSetListener listenerDeDatePicker = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int año, int mes, int diaDelMes) {
                        Calendar cldr = Calendar.getInstance();
                        cldr.set(Calendar.YEAR, año);
                        cldr.set(Calendar.MONTH, mes); // No es necesario restar 1 aquí
                        cldr.set(Calendar.DAY_OF_MONTH, diaDelMes);

                        Año2 =año;
                        Mes2 = mes;
                        Dia2 =diaDelMes;
                        String fechaFormateada = sdf.format(cldr.getTime());
                        // Establecer la fecha formateada en tu campo de texto fecha
                        fecha2.setText(fechaFormateada);
                    }
                };

                DatePickerDialog dialogoFecha = new DatePickerDialog(getContext(), listenerDeDatePicker, Año2,Mes2,Dia2);
                dialogoFecha.show();


            }
        });

        obtenerTiendas();
        ObtenerTipoEncuesta();
        consultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                consultar();
            }
        });
        return view;
    }

    private void consultar(){

        int idtienda = ((Tienda)spinner_tiendas.getSelectedItem()).getId();
        String idencuesta = ((Encuesta)spinner_tipo.getSelectedItem()).getId();

        if(idtienda != 0 && !idencuesta.equals("0") && !idencuesta.isEmpty()){
        texttienda.setText("");
        Dialog dialog = new Dialog(getContext(), R.style.CustomAlertDialog);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.show();
        ObtenerResultadoEncuestaOperacion obtenerResultadoEncuesta = new ObtenerResultadoEncuestaOperacion(getContext());
        obtenerResultadoEncuesta.setAsyncTaskListener(new ReportesOperacionActivity.AsyncTaskListener() {
            @Override
            public void showProgressBar() {
                if(!dialog.isShowing()){
                    dialog.show();
                }
            }

            @Override
            public void CloseProgressBar() {
                if(dialog.isShowing()){
                    dialog.dismiss();
                }
            }

            @Override
            public void datos(String result) {

                if (!result.contains("Error en servicio")) {
                    // Crear una instancia de Gson
                    Gson gson = new Gson();
                    // Definir el tipo de datos que queremos convertir
                    Type tipoLista = new TypeToken<List<JsonObject>>(){}.getType();
                    // Convertir la cadena JSON en una lista de objetos Empleado
                    List<JsonObject> listaResultados = gson.fromJson(result, tipoLista);
                    String txt = ((Tienda)spinner_tiendas.getSelectedItem()).getNombre();
                    if(listaResultados.size() == 0){
                        txt = "";
                        texttienda.setText(txt);
                        Toast.makeText(getContext(), "No se encontraron datos.", Toast.LENGTH_LONG).show();
                    }else{
                        double sum = 0;
                        for (JsonObject jsonObject : listaResultados) {
                            if(jsonObject.has("porcentaje_total")){
                                double fieldValue = jsonObject.get("porcentaje_total").getAsDouble();
                                sum += fieldValue;
                            }

                        }

                        double promedio = sum / listaResultados.size();
                        if(promedio > 0){
                            double numeroRedondeado = Math.round(promedio * 100.0) / 100.0;
                            texttienda.setText(txt+": "+numeroRedondeado+"%");
                        }else{
                            texttienda.setText(txt);
                        }
                    }

                    RecyclerResultadoEncuesta recyclerResultadoEncuesta= new RecyclerResultadoEncuesta(listaResultados,getContext(),txt);
                    recyclerView.setAdapter(recyclerResultadoEncuesta);

                }else{
                    Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
                }

            }
        });
        obtenerResultadoEncuesta.execute(idencuesta, String.valueOf(idtienda),fecha1.getText().toString(),fecha2.getText().toString());
        }else{
            Toast.makeText(getContext(),"Debes seleccionar una de las opciones",Toast.LENGTH_LONG).show();
        }
    }

    private  void ObtenerTipoEncuesta(){

        List<Encuesta> tipoList = new ArrayList<>();
        tipoList.add( new Encuesta("0","Seleccionar") );
        db.collection("Encuesta")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Itera sobre los documentos de la colección
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Obtiene el ID del documento
                            String documentId = document.getId();

                            // Obtiene el valor del campo "id_encuesta" de cada documento
                            String idEncuesta = document.getString("id_encuesta");

                            // Realiza las operaciones necesarias con el ID y el valor obtenidos
                            if (idEncuesta != null) {
                                // Hacer algo con el ID y el valor de id_encuesta
                                tipoList.add( new Encuesta(idEncuesta,documentId) );

                            } else {
                                // Maneja el caso en que el campo "id_encuesta" sea nulo
                                Log.d("TAG", "Campo 'id_encuesta' nulo para el documento ID: " + documentId);
                            }
                        }

                        ArrayAdapter<Encuesta> dataAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, tipoList);
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_tipo.setAdapter(dataAdapter);
                    } else {
                        // Maneja el error al obtener los documentos
                        Log.d("TAG", "Error al obtener documentos: ", task.getException());
                    }
                });
    }

    public ArrayList<Tienda> obtenerTiendas(){
        ArrayList<Tienda> lista = new ArrayList<>();

        try {
            lista.add(new Tienda(0,"Seleccionar"));
            String resp =new CRUDTiempoPedido(getContext()).execute().get();
            JSONArray lista_obtenida = new JSONArray(resp);
            for(int i = 0; lista_obtenida.length()> i; i++){
                JSONObject jsonObject = lista_obtenida.getJSONObject(i);
                lista.add(new Tienda(jsonObject.getInt("idtienda"),jsonObject.getString("tienda")));
            }
            ArrayAdapter<Tienda> dataAdapter = new ArrayAdapter<Tienda>(getContext(),android.R.layout.simple_spinner_item,lista);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_tiendas.setAdapter(dataAdapter);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            Toast.makeText(getContext(),"Error en el servicio",Toast.LENGTH_LONG).show();
        }
        return  lista;
    }

}