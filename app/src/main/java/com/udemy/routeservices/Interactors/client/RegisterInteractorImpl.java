package com.udemy.routeservices.Interactors.client;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.udemy.routeservices.Interfaces.client.register.RegisterInteractor;
import com.udemy.routeservices.Interfaces.client.register.RegisterPresenter;
import com.udemy.routeservices.Models.Client;
import com.udemy.routeservices.Models.ClientData;
import com.udemy.routeservices.Models.Driver;
import com.udemy.routeservices.Utils.preferences.SharedPreferencesUber;
import com.udemy.routeservices.Utils.providers.AuthProvider;
import com.udemy.routeservices.Utils.providers.ClientProvider;
import com.udemy.routeservices.Utils.providers.DriverProvider;

public class RegisterInteractorImpl implements RegisterInteractor{

    private RegisterPresenter registerPresenter;
    private AuthProvider authProvider;
    private ClientProvider clientProvider;
    private DriverProvider driverProvider;

    public RegisterInteractorImpl(RegisterPresenter registerPresenter){
        this.registerPresenter = registerPresenter;
        authProvider = new AuthProvider();
        clientProvider = new ClientProvider();
        driverProvider = new DriverProvider();
    }

    @Override
    public void register(String email, String pass, String nombre, Context context) {
        registerPresenter.showProgress();
        //VALIDAR SI EXISTE ESTE CORREO EN EL NODO DE Clients
        clientProvider.emailExist().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean exist = false;
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    ClientData clientData = dataSnapshot.getValue(ClientData.class);
                    if(clientData.getEmail().equals(email))
                        exist = true;
                }
                if(!exist){//REGISTRAMOS
                    authProvider.register(email.trim(), pass).addOnCompleteListener(listener ->{
                        if(listener.isSuccessful()){
                            String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            saveUser(id, nombre, email, context);
                        } else
                            Toast.makeText(context, "Error en el REGISTRO.", Toast.LENGTH_LONG).show();
                        registerPresenter.hideProgress();
                    });
                }else{
                    registerPresenter.hideProgress();
                    Toast.makeText(context, "Correo ya registrado en Clientes.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void saveUser(String id, String nombre, String email, Context context){
        /*if(SharedPreferencesUber.getInstance(this).getTipoUsuario().equals("CONDUCTOR")){
             TODO EL push es para crear un id unico como en SQL, pero qui usaremos el id creado al registrar en Authentiation
            databaseReference.child("Users").child("Conductores").push().setValue(user).addOnCompleteListener(listener ->{
                if(listener.isSuccessful())
                    Toast.makeText(this, "Registro de Conductor exitoso.", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Error en el registro del Conductor.", Toast.LENGTH_SHORT).show();
            });
        }else if(SharedPreferencesUber.getInstance(this).getTipoUsuario().equals("CLIENTE")){*/
        Client client = new Client(id, nombre, email);
        clientProvider.create(client).addOnCompleteListener(listener ->{
            if(listener.isSuccessful()){
                Toast.makeText(context, "Registro de Cliente exitoso.", Toast.LENGTH_SHORT).show();
                registerPresenter.toMapClient();
            }else
                Toast.makeText(context, "Error en el registro del Cliente.", Toast.LENGTH_SHORT).show();
            registerPresenter.hideProgress();
        });
        //}

    }
}
