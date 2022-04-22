package com.udemy.routeservices.Interactors.driver;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.udemy.routeservices.Interfaces.driver.register.RegisterDriverInteractor;
import com.udemy.routeservices.Interfaces.driver.register.RegisterDriverPresenter;
import com.udemy.routeservices.Models.Driver;
import com.udemy.routeservices.Models.DriverData;
import com.udemy.routeservices.Utils.providers.AuthProvider;
import com.udemy.routeservices.Utils.providers.DriverProvider;

public class RegisterDriverInteractorImpl implements RegisterDriverInteractor {

    private RegisterDriverPresenter registerDriverPresenter;
    private AuthProvider authProvider;
    private DriverProvider driverProvider;

    public RegisterDriverInteractorImpl(RegisterDriverPresenter registerDriverPresenter){
        this.registerDriverPresenter = registerDriverPresenter;
        authProvider = new AuthProvider();
        driverProvider = new DriverProvider();
    }

    @Override
    public void registrar(String nombre, String email, String pass, String marca, String placa, String telefono, Activity activity) {
        if(!nombre.isEmpty() && !email.isEmpty() && !pass.isEmpty() && !telefono.isEmpty()){
            if(pass.length() >= 6){
                registerDriverPresenter.showProgress();
                //VALIDAR SI EXISTE ESTE CORREO EN EL NODO DE Drivers
                driverProvider.emailExist().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean exist = false;
                        for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                            DriverData driverData = dataSnapshot.getValue(DriverData.class);
                            if(driverData.getEmail().equals(email))
                                exist = true;
                        }
                        if(!exist){//REGISTRAMOS
                            authProvider.register(email.trim(), pass).addOnCompleteListener(listener ->{
                                if(listener.isSuccessful()){
                                    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    saveUser(id, nombre, email, marca, placa, telefono);
                                } else
                                    registerDriverPresenter.showError("Error en el REGISTRO.");
                                registerDriverPresenter.hideProgress();
                            });
                        }else{
                            registerDriverPresenter.hideProgress();
                            registerDriverPresenter.showError("Correo ya registrado en Drivers.");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }else
                registerDriverPresenter.showError("El pass debe tener 6 o mas carcateres.");
        }else
            registerDriverPresenter.showError("Ingresa todos los campos.");
    }

    private void saveUser(String id, String nombre, String email, String marca, String placa, String telefono){
        /*if(SharedPreferencesUber.getInstance(this).getTipoUsuario().equals("CONDUCTOR")){
            TODO EL push es para crear un id unico como en SQL, pero qui usaremos el id creado al registrar en Authentiation
            databaseReference.child("Users").child("Conductores").push().setValue(user).addOnCompleteListener(listener ->{
                if(listener.isSuccessful())
                    Toast.makeText(this, "Registro de Conductor exitoso.", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Error en el registro del Conductor.", Toast.LENGTH_SHORT).show();
            });
        }else if(SharedPreferencesUber.getInstance(this).getTipoUsuario().equals("CLIENTE")){*/
        Log.d("TEL", telefono);
        Driver driver = new Driver(id, nombre, email, marca, placa, telefono);
        driverProvider.create(driver).addOnCompleteListener(listener ->{
            if(listener.isSuccessful())
                registerDriverPresenter.successRegister();
            else
                registerDriverPresenter.showError("Error en el registro del Conductor." + listener.getException().getMessage());
            registerDriverPresenter.hideProgress();
        });
        //}
    }

}
