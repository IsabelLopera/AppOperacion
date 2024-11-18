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
           System.out.println(datos);
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


}