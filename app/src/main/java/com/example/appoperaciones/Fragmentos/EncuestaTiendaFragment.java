package com.example.appoperaciones.Fragmentos;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appoperaciones.Clases.Tienda;
import com.example.appoperaciones.EncuestaTiendaActivity;
import com.example.appoperaciones.GraficaPlatActivity;
import com.example.appoperaciones.R;
import com.example.appoperaciones.ReportesOperacionActivity;
import com.example.appoperaciones.Servicios.CRUDTiempoPedido;
import com.example.appoperaciones.Servicios.ObtenerEncuestaDetalleTiendasAPP;
import com.example.appoperaciones.Servicios.ObtenerEncuestaTiendasAPP;
import com.example.appoperaciones.Servicios.ObtenerHorariosAdministradoresDia;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
    private Button realizarEncuenta ;
    private String encabezado="";


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
        realizarEncuenta=  view.findViewById(R.id.realizar_encuesta);
        obtenerTiendas();
        Bundle args = getArguments();
        if (args != null) {
            String data = args.getString("param1");
            try {
                JSONObject jsonObject = new JSONObject(data);
                encabezado = jsonObject.getString("Encabezado");
                descripcion.setText(jsonObject.getString("descripcion"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

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
                int idtienda = item.getId();
                if(idtienda != 0){
                     ObtenerEncuestaDetalleTiendas(idtienda);
                }else{
                    Toast.makeText(getContext(),"Debes seleccionar uhna de las tiendas",Toast.LENGTH_LONG).show();
                }
            }
        });
        return view;
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


    public void ObtenerEncuestaDetalleTiendas(int idtienda){
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
                if (!result.contains("Error en servicio")) {
                    Intent intent= new Intent(getContext(), EncuestaTiendaActivity.class);
                    intent.putExtra("datos",result);
                    intent.putExtra("encabezado",encabezado);
                    intent.putExtra("idtienda",String.valueOf(idtienda));
                    System.out.println(idtienda);
                    startActivity(intent);
                }else{
                    Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
                }
            }
        });
        service.execute();

    }


}