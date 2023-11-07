package com.example.appoperaciones;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.appoperaciones.databinding.ActivityLogin2Binding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.OAuthCredential;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.gson.Gson;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {



    private SignInButton signInButton;
    private FirebaseAuth mAuth;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 123;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        signInButton = findViewById(R.id.loginGoogle);
        sharedPref = this.getSharedPreferences("SESIONES", Context.MODE_PRIVATE);
        editor = sharedPref.edit();


        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });


    }


    private void signIn(){
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            home();
        }
    }


    // Lanzar el flujo de inicio de sesión


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                System.out.println(e.getMessage());
                Toast.makeText(getApplicationContext(),"No es posible el acceso  por medio de google" , Toast.LENGTH_LONG).show();
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        String email = account.getEmail();
        db.collection("Usuario").document(email).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String rol = documentSnapshot.getString("rol");
                            editor.putString("rol",rol);
                            editor.apply();
                          //  FirebaseAuth auth = FirebaseAuth.getInstance();
                            mAuth.signInWithCredential(GoogleAuthProvider.getCredential(account.getIdToken(), null)).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Inicio de sesión en Firebase exitoso

                                        home();
                                        // Hacer algo con el usuario autenticado
                                    } else {
                                        // Error en el inicio de sesión en Firebase
                                        Toast.makeText(getApplicationContext(),"Error en el inicio de sesión en Firebase" , Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            // El documento del usuario no existe
                            logout();
                            Toast.makeText(getApplicationContext(),"Este usuario no tiene autorización." , Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error al obtener los datos del usuario
                        logout();
                        Toast.makeText(getApplicationContext(),"Ocurrió un error al obtener los datos." , Toast.LENGTH_LONG).show();
                    }
                });



    }

/**
    private void firebaseAuth() {
        String email_text = email.getText().toString();
        String password_text = contraseña.getText().toString();

        if(!email_text.isEmpty() && !password_text.isEmpty()){
            CollectionReference usuariosRef = db.collection("Usuario");
            Query query = usuariosRef.whereEqualTo("email", email_text);
            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Verifica si se encontraron documentos
                    if (!task.getResult().isEmpty()) {
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(email_text, password_text)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // El inicio de sesión con correo y contraseña fue exitos
                                            home();
                                            // Realizar las operaciones correspondientes para el usuario autenticado
                                        } else {
                                            // El inicio de sesión con correo y contraseña falló
                                            Toast.makeText(getApplicationContext(),"Inicio de sesión fallido." , Toast.LENGTH_LONG).show();
                                        }

                                    }
                                });
                    } else {
                        if(mAuth.getCurrentUser() != null){
                            mAuth.signOut();
                        }
                        Toast.makeText(getApplicationContext(),"Este usuario no tiene autorización." , Toast.LENGTH_LONG).show();
                        // El correo electrónico no coincide o no se encontró el documento "Usuario"
                        // Aquí puedes mostrar un mensaje de error o realizar otras acciones
                    }
                } else {
                    if(mAuth.getCurrentUser() != null){
                        mAuth.signOut();
                    }
                    Toast.makeText(getApplicationContext(),"Ocurrió un error al obtener los datos." , Toast.LENGTH_LONG).show();
                    // Ocurrió un error al obtener los documentos
                    // Aquí puedes manejar el error apropiadamente
                }
            });

        }else{
            Toast.makeText(getApplicationContext(),"Ingrese los datos solicitados" , Toast.LENGTH_LONG).show();
        }

    }

 **/


         private  void home(){
             Intent intent = new Intent(this, ReportesOperacionActivity.class);
             startActivity(intent);
             finish();



         }

    private void logout(){

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if(mAuth.getCurrentUser() != null){
            mAuth.signOut();
        }

        if(account != null){
            googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(Task<Void> task) {
                    if(!task.isSuccessful()){
                        Toast.makeText(getApplicationContext(),"No se pudo cerrar la sesion en google" , Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        editor.putString("rol","");
        editor.apply();

    }

}