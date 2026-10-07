package com.example.appoperaciones.Fragmentos;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appoperaciones.Adaptadores.RecyclerEncuestaTienda;
import com.example.appoperaciones.Clases.Encuesta;
import com.example.appoperaciones.Clases.Tienda;
import com.example.appoperaciones.EncuestaTiendaActivity;
import com.example.appoperaciones.R;
import com.example.appoperaciones.ReportesOperacionActivity;
import com.example.appoperaciones.Servicios.CRUDTiempoPedido;
import com.example.appoperaciones.Servicios.ObtenerEncuestaDetalleTiendasAPP;
import com.example.appoperaciones.Servicios.ObtenerEncuestaTiendasAPP;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EncuestaTiendaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EncuestaTiendaFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView descripcion,correo_electronico,nombreusuario;
    private Spinner spinner_tiendas;
    private Spinner spinner_tipo;
    private Button realizarEncuenta ;
    private String encabezado="";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private boolean is_observ = false;
    private String nombre_tienda = "";
    private int   idempleado  = 0;

    private  SharedPreferences sharedPref;



    public EncuestaTiendaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EncuestaTiendaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EncuestaTiendaFragment newInstance(String param1, String param2) {
        EncuestaTiendaFragment fragment = new EncuestaTiendaFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_encuesta_tienda, container, false);
        descripcion = view.findViewById(R.id.descripcion);
        nombreusuario = view.findViewById(R.id.nombreusuario);
        correo_electronico =  view.findViewById(R.id.corre_electronico);
        spinner_tiendas = view.findViewById(R.id.tienda);
        spinner_tipo = view.findViewById(R.id.tipo);
        realizarEncuenta=  view.findViewById(R.id.realizar_encuesta);
        sharedPref = getContext().getSharedPreferences("SESIONES", Context.MODE_PRIVATE);

        obtenerTiendas();
        ObtenerTipoEncuesta();


        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            String nombre =  firebaseUser.getDisplayName();
            String correo =  firebaseUser.getEmail();
            nombreusuario.setText(nombre);
            correo_electronico.setText(correo);
        } else {
            nombreusuario.setText("No existe usuario logueado");
        }


        realizarEncuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Tienda item = (Tienda)spinner_tiendas.getSelectedItem();
                String idencuesta =  ((Encuesta)spinner_tipo.getSelectedItem()).id;
                int idtienda = item.getId();
                nombre_tienda = item.getNombre();
                if(idtienda != 0 && !idencuesta.equals("0") && !idencuesta.isEmpty()){
                    ObtenerEncuestaTiendas(idencuesta);
                }else{
                    Toast.makeText(getContext(),"Debes seleccionar una de las opciones",Toast.LENGTH_LONG).show();
                }
            }
        });
        return view;
    }

    public void ObtenerEncuestaTiendas(String idencuesta){
        if (idencuesta.equals("13")) {
            String mockEncabezado = "{\"descripcion\":\"INSPECCIÓN DE SEGURIDAD ALIMENTARIA\",\"Encabezado\":\"Formato de Inspección de Seguridad Alimentaria\",\"idempleado\":1}";
            MostrarDescripEncuesta(idencuesta, mockEncabezado);
            return;
        }
        String documento = sharedPref.getString("documento","");

        ObtenerEncuestaTiendasAPP service =new ObtenerEncuestaTiendasAPP(getContext());
        Dialog dialog = new Dialog(getContext(), R.style.CustomAlertDialog);
        dialog.setContentView(R.layout.custom_dialog);
        service.setAsyncTaskListener(new ReportesOperacionActivity.AsyncTaskListener() {
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
                    MostrarDescripEncuesta(idencuesta,result);
                }else{
                    Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
                }
            }
        });
        service.execute(idencuesta,documento);

    }


   private void MostrarDescripEncuesta(String idencuesta,String result){
       try {
           JSONObject datos = new JSONObject(result);
           View content = LayoutInflater.from(getContext()).inflate(R.layout.dialog, null);
           TextView titulo = content.findViewById(R.id.titulo);

           TextView encabezado = content.findViewById(R.id.encabezado);
           titulo.setText(datos.getString("descripcion"));
           String encb = datos.getString("Encabezado").replace("\\n",System.getProperty ("line.separator"));
           encabezado.setText(encb);
           idempleado = datos.getInt("idempleado");
           if(idempleado == 0){
               Toast.makeText(getContext(),"Este usuario no se encuentra registrado",Toast.LENGTH_LONG).show();
           }else{
               MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
               builder.setView(content)
                       .setCancelable(true)
                       .setNegativeButton("Cancelar", null)
                       .setPositiveButton("Continuar...", (dialogInterface, i) -> {

                           ObtenerEncuestaDetalleTiendas(idencuesta);
                       });

               builder.show();
           }

       } catch (JSONException e) {
           e.printStackTrace();
       }
;
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
            System.out.println(e);
        } catch (InterruptedException e) {
            System.out.println(e);
        } catch (JSONException e) {
            //Toast.makeText(getContext(),"Error en el servicio",Toast.LENGTH_LONG).show();
            System.out.println(e);
        }
        return  lista;
    }


    private  void ObtenerTipoEncuesta(){

        List<Encuesta> tipoList = new  ArrayList<>();
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
                            is_observ = document.getBoolean("observacion");

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



    public void ObtenerEncuestaDetalleTiendas(String idencuesta){
        if (idencuesta.equals("13")) {
            int idtienda = ((Tienda)spinner_tiendas.getSelectedItem()).id;
            String mockDetalle = getMockInspeccionAlimentaria();
            Intent intent= new Intent(getContext(), EncuestaTiendaActivity.class);
            intent.putExtra("datos", mockDetalle);
            intent.putExtra("encabezado", encabezado);
            intent.putExtra("is_observ", is_observ);
            intent.putExtra("idtienda", String.valueOf(idtienda));
            intent.putExtra("nom_tienda", nombre_tienda);
            intent.putExtra("idencuesta", idencuesta);
            intent.putExtra("idempleado", String.valueOf(idempleado));
            startActivity(intent);
            return;
        }

        ObtenerEncuestaDetalleTiendasAPP service =new ObtenerEncuestaDetalleTiendasAPP(getContext());
        Dialog dialog = new Dialog(getContext(), R.style.CustomAlertDialog);
        dialog.setContentView(R.layout.custom_dialog);
        service.setAsyncTaskListener(new ReportesOperacionActivity.AsyncTaskListener() {
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

                int idtienda = ((Tienda)spinner_tiendas.getSelectedItem()).id;
                if (!result.contains("Error en servicio")) {
                    Intent intent= new Intent(getContext(), EncuestaTiendaActivity.class);
                    intent.putExtra("datos",result);
                    intent.putExtra("encabezado",encabezado);
                    intent.putExtra("is_observ",is_observ);
                    intent.putExtra("idtienda",String.valueOf(idtienda));
                    intent.putExtra("nom_tienda", nombre_tienda);
                    intent.putExtra("idencuesta",idencuesta);
                    intent.putExtra("idempleado",String.valueOf(idempleado));
                    startActivity(intent);
                }else{
                    Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
                }
            }
        });
        service.execute(idencuesta);

    }

    private String getMockInspeccionAlimentaria() {
        return "[" +
                "{\"orden\":1,\"descripcion\":\"1. Limpieza y desinfección (40%)\",\"tiporespuesta\":\"SPD\",\"valorinicial\":0,\"valorfinal\":0,\"valorescala\":0,\"valordefecto\":0,\"alertar\":\"N\",\"valor_alertar\":\"N\",\"obligatorio\":\"N\",\"porcentaje\":0.0}," +
                "{\"orden\":2,\"descripcion\":\"1.1 Las instalaciones en el área del salón, incluidas las mesas y sillas se encuentran limpias.\",\"tiporespuesta\":\"VNDA\",\"valorinicial\":0,\"valorfinal\":1,\"valorescala\":1,\"valordefecto\":0,\"alertar\":\"N\",\"valor_alertar\":\"N\",\"obligatorio\":\"N\",\"porcentaje\":2.222}," +
                "{\"orden\":3,\"descripcion\":\"1.2 Las instalaciones en el área de lavado se encuentran limpias.\",\"tiporespuesta\":\"VNDA\",\"valorinicial\":0,\"valorfinal\":1,\"valorescala\":1,\"valordefecto\":0,\"alertar\":\"N\",\"valor_alertar\":\"N\",\"obligatorio\":\"N\",\"porcentaje\":2.222}," +
                "{\"orden\":4,\"descripcion\":\"1.3 Las instalaciones en el área de proceso se encuentran limpias.\",\"tiporespuesta\":\"VNDA\",\"valorinicial\":0,\"valorfinal\":1,\"valorescala\":1,\"valordefecto\":0,\"alertar\":\"N\",\"valor_alertar\":\"N\",\"obligatorio\":\"N\",\"porcentaje\":2.222}," +
                "{\"orden\":5,\"descripcion\":\"1.4 Las instalaciones en el área de almacenamiento se encuentran limpias.\",\"tiporespuesta\":\"VNDA\",\"valorinicial\":0,\"valorfinal\":1,\"valorescala\":1,\"valordefecto\":0,\"alertar\":\"N\",\"valor_alertar\":\"N\",\"obligatorio\":\"N\",\"porcentaje\":2.222}," +
                "{\"orden\":6,\"descripcion\":\"1.5 El mostrador, mesa mostrador, pc, equipo de stickers, pantalla domicilios, tv se encuentran limpios.\",\"tiporespuesta\":\"VNDA\",\"valorinicial\":0,\"valorfinal\":1,\"valorescala\":1,\"valordefecto\":0,\"alertar\":\"N\",\"valor_alertar\":\"N\",\"obligatorio\":\"N\",\"porcentaje\":2.222}," +
                "{\"orden\":7,\"descripcion\":\"1.6 La fachada y elementos del exterior del punto se encuentran limpios.\",\"tiporespuesta\":\"VNDA\",\"valorinicial\":0,\"valorfinal\":1,\"valorescala\":1,\"valordefecto\":0,\"alertar\":\"N\",\"valor_alertar\":\"N\",\"obligatorio\":\"N\",\"porcentaje\":2.222}," +
                "{\"orden\":8,\"descripcion\":\"1.7 Los servicios sanitarios, lavamanos, orinales se encuentran limpios, sin estancamiento de agua y cuentan con los elementos para la higiene personal (jabón líquido, toallas desechables etc.)\",\"tiporespuesta\":\"VNDA\",\"valorinicial\":0,\"valorfinal\":1,\"valorescala\":1,\"valordefecto\":0,\"alertar\":\"N\",\"valor_alertar\":\"N\",\"obligatorio\":\"N\",\"porcentaje\":2.222}," +
                "{\"orden\":9,\"descripcion\":\"1.8 Las áreas de procesos se encuentran alejadas de focos de contaminación.\",\"tiporespuesta\":\"VNDA\",\"valorinicial\":0,\"valorfinal\":1,\"valorescala\":1,\"valordefecto\":0,\"alertar\":\"N\",\"valor_alertar\":\"N\",\"obligatorio\":\"N\",\"porcentaje\":2.222}," +
                "{\"orden\":10,\"descripcion\":\"1.9 Los equipos se encuentran limpios y sin acumulación de grasas.\",\"tiporespuesta\":\"VNDA\",\"valorinicial\":0,\"valorfinal\":1,\"valorescala\":1,\"valordefecto\":0,\"alertar\":\"N\",\"valor_alertar\":\"N\",\"obligatorio\":\"N\",\"porcentaje\":2.222}," +
                "{\"orden\":11,\"descripcion\":\"1.10 Los utensilios (pinzas, bandejas, parrillas, etc) se encuentran limpios y sin acumulación de grasas.\",\"tiporespuesta\":\"VNDA\",\"valorinicial\":0,\"valorfinal\":1,\"valorescala\":1,\"valordefecto\":0,\"alertar\":\"N\",\"valor_alertar\":\"N\",\"obligatorio\":\"N\",\"porcentaje\":2.222}," +
                "{\"orden\":12,\"descripcion\":\"1.11 Los mesones, mesas se mantienen libres de suciedad y residuos alimenticios.\",\"tiporespuesta\":\"VNDA\",\"valorinicial\":0,\"valorfinal\":1,\"valorescala\":1,\"valordefecto\":0,\"alertar\":\"N\",\"valor_alertar\":\"N\",\"obligatorio\":\"N\",\"porcentaje\":2.222}," +
                "{\"orden\":13,\"descripcion\":\"1.12 La cava, neveras, congeladores se encuentran limpios y ordenados.\",\"tiporespuesta\":\"VNDA\",\"valorinicial\":0,\"valorfinal\":1,\"valorescala\":1,\"valordefecto\":0,\"alertar\":\"N\",\"valor_alertar\":\"N\",\"obligatorio\":\"N\",\"porcentaje\":2.222}," +
                "{\"orden\":14,\"descripcion\":\"1.13 Cajones de domicilios (limpios, publicidad, y numeración)\",\"tiporespuesta\":\"VNDA\",\"valorinicial\":0,\"valorfinal\":1,\"valorescala\":1,\"valordefecto\":0,\"alertar\":\"N\",\"valor_alertar\":\"N\",\"obligatorio\":\"N\",\"porcentaje\":2.222}," +
                "{\"orden\":15,\"descripcion\":\"1.14 Bolsas térmicas limpias y enumeradas\",\"tiporespuesta\":\"VNDA\",\"valorinicial\":0,\"valorfinal\":1,\"valorescala\":1,\"valordefecto\":0,\"alertar\":\"N\",\"valor_alertar\":\"N\",\"obligatorio\":\"N\",\"porcentaje\":2.222}," +
                "{\"orden\":16,\"descripcion\":\"1.15 Los elementos para el lavado del horno se encuentran almacenados de manera adecuada.\",\"tiporespuesta\":\"VNDA\",\"valorinicial\":0,\"valorfinal\":1,\"valorescala\":1,\"valordefecto\":0,\"alertar\":\"N\",\"valor_alertar\":\"N\",\"obligatorio\":\"N\",\"porcentaje\":2.222}," +
                "{\"orden\":17,\"descripcion\":\"1.16 Los elementos para el proceso de limpieza y desinfección (escobas, traperas, recogedor, trapos) se encuentran limpios y almacenados de manera adecuada.\",\"tiporespuesta\":\"VNDA\",\"valorinicial\":0,\"valorfinal\":1,\"valorescala\":1,\"valordefecto\":0,\"alertar\":\"N\",\"valor_alertar\":\"N\",\"obligatorio\":\"N\",\"porcentaje\":2.222}," +
                "{\"orden\":18,\"descripcion\":\"1.17 Los locker se encuentran limpios, organizados y sin alimentos almacenado.\",\"tiporespuesta\":\"VNDA\",\"valorinicial\":0,\"valorfinal\":1,\"valorescala\":1,\"valordefecto\":0,\"alertar\":\"N\",\"valor_alertar\":\"N\",\"obligatorio\":\"N\",\"porcentaje\":2.222}," +
                "{\"orden\":19,\"descripcion\":\"1.18 Se tiene claramente definido los productos utilizados, concentraciones, modo de preparación, empleo y rotación de los mismos.\",\"tiporespuesta\":\"VNDA\",\"valorinicial\":0,\"valorfinal\":1,\"valorescala\":1,\"valordefecto\":0,\"alertar\":\"N\",\"valor_alertar\":\"N\",\"obligatorio\":\"N\",\"porcentaje\":2.222}," +
                "{\"orden\":20,\"descripcion\":\"2. Almacenamiento (30%)\",\"tiporespuesta\":\"SPD\",\"valorinicial\":0,\"valorfinal\":0,\"valorescala\":0,\"valordefecto\":0,\"alertar\":\"N\",\"valor_alertar\":\"N\",\"obligatorio\":\"N\",\"porcentaje\":0.0}," +
                "{\"orden\":21,\"descripcion\":\"2.1 Los productos en bodega están ordenados e identificados.\",\"tiporespuesta\":\"VNDA\",\"valorinicial\":0,\"valorfinal\":1,\"valorescala\":1,\"valordefecto\":0,\"alertar\":\"N\",\"valor_alertar\":\"N\",\"obligatorio\":\"N\",\"porcentaje\":3.333}," +
                "{\"orden\":22,\"descripcion\":\"2.2 El almacenamiento se realiza en condiciones adecuadas de temperatura (0-4°C refrigeración, -10°C-18 °C congelación) y se llevan registros.\",\"tiporespuesta\":\"VNDA\",\"valorinicial\":0,\"valorfinal\":1,\"valorescala\":1,\"valordefecto\":0,\"alertar\":\"N\",\"valor_alertar\":\"N\",\"obligatorio\":\"N\",\"porcentaje\":3.333}," +
                "{\"orden\":23,\"descripcion\":\"2.3 No se evidencia alimentos o materia prima a ras de piso\",\"tiporespuesta\":\"VNDA\",\"valorinicial\":0,\"valorfinal\":1,\"valorescala\":1,\"valordefecto\":0,\"alertar\":\"N\",\"valor_alertar\":\"N\",\"obligatorio\":\"N\",\"porcentaje\":3.333}," +
                "{\"orden\":24,\"descripcion\":\"2.4 Se almacenan los alimentos acordes a las fechas de ingreso, cumpliendo con las PEPS.\",\"tiporespuesta\":\"VNDA\",\"valorinicial\":0,\"valorfinal\":1,\"valorescala\":1,\"valordefecto\":0,\"alertar\":\"N\",\"valor_alertar\":\"N\",\"obligatorio\":\"N\",\"porcentaje\":3.333}," +
                "{\"orden\":25,\"descripcion\":\"2.5 Los productos abiertos están marcados o rotulados y tapados en los cuartos fríos\",\"tiporespuesta\":\"VNDA\",\"valorinicial\":0,\"valorfinal\":1,\"valorescala\":1,\"valordefecto\":0,\"alertar\":\"N\",\"valor_alertar\":\"N\",\"obligatorio\":\"N\",\"porcentaje\":3.333}," +
                "{\"orden\":26,\"descripcion\":\"2.6 Las sustancias químicas se encuentran separadas de los alimentos, ordenadas, rotuladas y tapadas\",\"tiporespuesta\":\"VNDA\",\"valorinicial\":0,\"valorfinal\":1,\"valorescala\":1,\"valordefecto\":0,\"alertar\":\"N\",\"valor_alertar\":\"N\",\"obligatorio\":\"N\",\"porcentaje\":3.333}," +
                "{\"orden\":27,\"descripcion\":\"2.7 No hay riesgo de contaminación cruzada de materia prima y de productos en procesos.\",\"tiporespuesta\":\"VNDA\",\"valorinicial\":0,\"valorfinal\":1,\"valorescala\":1,\"valordefecto\":0,\"alertar\":\"N\",\"valor_alertar\":\"N\",\"obligatorio\":\"N\",\"porcentaje\":3.333}," +
                "{\"orden\":28,\"descripcion\":\"2.8 No se evidencia productos vencidos\",\"tiporespuesta\":\"VNDA\",\"valorinicial\":0,\"valorfinal\":1,\"valorescala\":1,\"valordefecto\":0,\"alertar\":\"N\",\"valor_alertar\":\"N\",\"obligatorio\":\"N\",\"porcentaje\":3.333}," +
                "{\"orden\":29,\"descripcion\":\"2.9 La materia prima se encuentra almacenada en el macklan en los compartimientos correspondientes, se encuentran protegidas y en buen estado.\",\"tiporespuesta\":\"VNDA\",\"valorinicial\":0,\"valorfinal\":1,\"valorescala\":1,\"valordefecto\":0,\"alertar\":\"N\",\"valor_alertar\":\"N\",\"obligatorio\":\"N\",\"porcentaje\":3.333}," +
                "{\"orden\":30,\"descripcion\":\"3. Personal Manipulador (20%)\",\"tiporespuesta\":\"SPD\",\"valorinicial\":0,\"valorfinal\":0,\"valorescala\":0,\"valordefecto\":0,\"alertar\":\"N\",\"valor_alertar\":\"N\",\"obligatorio\":\"N\",\"porcentaje\":0.0}," +
                "{\"orden\":31,\"descripcion\":\"3.1 Los manipuladores de alimentos (artesanos) llevan uniformes adecuados de color claro y limpio, calzado cerrado de material resistente e impermeable.\",\"tiporespuesta\":\"VNDA\",\"valorinicial\":0,\"valorfinal\":1,\"valorescala\":1,\"valordefecto\":0,\"alertar\":\"N\",\"valor_alertar\":\"N\",\"obligatorio\":\"N\",\"porcentaje\":2.857}," +
                "{\"orden\":32,\"descripcion\":\"3.2 Los manipuladores de alimentos cuentan con malla para recubrir el cabello y usan adecuadamente el tapabocas\",\"tiporespuesta\":\"VNDA\",\"valorinicial\":0,\"valorfinal\":1,\"valorescala\":1,\"valordefecto\":0,\"alertar\":\"N\",\"valor_alertar\":\"N\",\"obligatorio\":\"N\",\"porcentaje\":2.857}," +
                "{\"orden\":33,\"descripcion\":\"3.3 Los manipuladores de alimentos (Administrador, SAC y domiciliarios) llevan uniforme en condiciones de limpieza y orden.\",\"tiporespuesta\":\"VNDA\",\"valorinicial\":0,\"valorfinal\":1,\"valorescala\":1,\"valordefecto\":0,\"alertar\":\"N\",\"valor_alertar\":\"N\",\"obligatorio\":\"N\",\"porcentaje\":2.857}," +
                "{\"orden\":34,\"descripcion\":\"3.4 El personal presenta las manos limpias, uñas cortas y sin esmalte. No tiene joyas ni maquillaje. El personal masculino se encuentra afeitado.\",\"tiporespuesta\":\"VNDA\",\"valorinicial\":0,\"valorfinal\":1,\"valorescala\":1,\"valordefecto\":0,\"alertar\":\"N\",\"valor_alertar\":\"N\",\"obligatorio\":\"N\",\"porcentaje\":2.857}," +
                "{\"orden\":35,\"descripcion\":\"3.5 Los empleados que están en contacto directo con los productos, no presentan afecciones en la piel o enfermedades infectocontagiosas.\",\"tiporespuesta\":\"VNDA\",\"valorinicial\":0,\"valorfinal\":1,\"valorescala\":1,\"valordefecto\":0,\"alertar\":\"N\",\"valor_alertar\":\"N\",\"obligatorio\":\"N\",\"porcentaje\":2.857}," +
                "{\"orden\":36,\"descripcion\":\"3.6 Los manipuladores evitan prácticas antihigiénicas tales como comer, fumar, toser, escupir, masticar chicle o rascarse etc.\",\"tiporespuesta\":\"VNDA\",\"valorinicial\":0,\"valorfinal\":1,\"valorescala\":1,\"valordefecto\":0,\"alertar\":\"N\",\"valor_alertar\":\"N\",\"obligatorio\":\"N\",\"porcentaje\":2.857}," +
                "{\"orden\":37,\"descripcion\":\"3.7 Se evidencia el correcto diligenciamiento del formato de manipulación de alimentos.\",\"tiporespuesta\":\"VNDA\",\"valorinicial\":0,\"valorfinal\":1,\"valorescala\":1,\"valordefecto\":0,\"alertar\":\"N\",\"valor_alertar\":\"N\",\"obligatorio\":\"N\",\"porcentaje\":2.857}," +
                "{\"orden\":38,\"descripcion\":\"4. Plan de saneamiento (10%)\",\"tiporespuesta\":\"SPD\",\"valorinicial\":0,\"valorfinal\":0,\"valorescala\":0,\"valordefecto\":0,\"alertar\":\"N\",\"valor_alertar\":\"N\",\"obligatorio\":\"N\",\"porcentaje\":0.0}," +
                "{\"orden\":39,\"descripcion\":\"4.1 Existen registros que indiquen que se realiza inspección periódica de limpieza y desinfección en las diferentes áreas, equipos, utensilios.\",\"tiporespuesta\":\"VNDA\",\"valorinicial\":0,\"valorfinal\":1,\"valorescala\":1,\"valordefecto\":0,\"alertar\":\"N\",\"valor_alertar\":\"N\",\"obligatorio\":\"N\",\"porcentaje\":2.500}," +
                "{\"orden\":40,\"descripcion\":\"4.2 Se realiza el control del cloro y pH y se cuenta con el diligenciamiento adecuado del formato.\",\"tiporespuesta\":\"VNDA\",\"valorinicial\":0,\"valorfinal\":1,\"valorescala\":1,\"valordefecto\":0,\"alertar\":\"N\",\"valor_alertar\":\"N\",\"obligatorio\":\"N\",\"porcentaje\":2.500}," +
                "{\"orden\":41,\"descripcion\":\"4.3 Las canecas designadas para los residuos sólidos, son utilizadas adecuadamente para este fin y permanecen tapadas con sus bolsas correspondientes y se cuenta con el diligenciamiento adecuado del formato.\",\"tiporespuesta\":\"VNDA\",\"valorinicial\":0,\"valorfinal\":1,\"valorescala\":1,\"valordefecto\":0,\"alertar\":\"N\",\"valor_alertar\":\"N\",\"obligatorio\":\"N\",\"porcentaje\":2.500}," +
                "{\"orden\":42,\"descripcion\":\"4.4 No se evidencia presencia de plagas y se cuenta con el diligenciamiento adecuado del formato.\",\"tiporespuesta\":\"VNDA\",\"valorinicial\":0,\"valorfinal\":1,\"valorescala\":1,\"valordefecto\":0,\"alertar\":\"N\",\"valor_alertar\":\"N\",\"obligatorio\":\"N\",\"porcentaje\":2.500}" +
                "]";
    }


}