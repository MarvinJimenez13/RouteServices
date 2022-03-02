package com.udemy.uberclone.Interactors.client;

import android.content.Context;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.udemy.uberclone.Interfaces.client.register.RegisterInteractor;
import com.udemy.uberclone.Interfaces.client.register.RegisterPresenter;
import com.udemy.uberclone.Models.Client;
import com.udemy.uberclone.Utils.providers.AuthProvider;
import com.udemy.uberclone.Utils.providers.ClientProvider;

public class RegisterInteractorImpl implements RegisterInteractor{

    private RegisterPresenter registerPresenter;
    private AuthProvider authProvider;
    private ClientProvider clientProvider;

    public RegisterInteractorImpl(RegisterPresenter registerPresenter){
        this.registerPresenter = registerPresenter;
        authProvider = new AuthProvider();
        clientProvider = new ClientProvider();
    }

    @Override
    public void register(String email, String pass, String nombre, Context context) {
        registerPresenter.showProgress();
        authProvider.register(email.trim(), pass).addOnCompleteListener(listener ->{
            if(listener.isSuccessful()){
                String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                saveUser(id, nombre, email, context);
            } else
                Toast.makeText(context, "Error en el REGISTRO.", Toast.LENGTH_LONG).show();
            registerPresenter.hideProgress();
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
