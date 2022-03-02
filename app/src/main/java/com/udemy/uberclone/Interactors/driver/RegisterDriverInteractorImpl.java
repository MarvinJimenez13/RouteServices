package com.udemy.uberclone.Interactors.driver;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.udemy.uberclone.Interfaces.driver.register.RegisterDriverInteractor;
import com.udemy.uberclone.Interfaces.driver.register.RegisterDriverPresenter;
import com.udemy.uberclone.Models.Driver;
import com.udemy.uberclone.Utils.providers.AuthProvider;
import com.udemy.uberclone.Utils.providers.DriverProvider;

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
                authProvider.register(email.trim(), pass).addOnCompleteListener(listener ->{
                    if(listener.isSuccessful()){
                        //ENVIAR CODIGO SMS DE VERIFICACION
                        PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                //La autenticacion fue exitosa.
                                //EL USUARIO INSERTO EL CODIGO CORRECTAMENTE
                                //EN CASO DE QUE NUESTRO MOVIL DETECTO AUTOMATICAMENTE EL CODIGO

                                String code = phoneAuthCredential.getSmsCode();
                                if(code != null){
                                    registerDriverPresenter.showError("Codigo detectado:" + code);
                                    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    saveUser(id, nombre, email, marca, placa, telefono);
                                }
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                //CUANDO EL ENVIO DEL SMS FALLA
                                registerDriverPresenter.showError("Error: " + e.getMessage());
                            }

                            //CODIGO DE VERIFICACION SE ENVIA A TRAVES DE MENSAJE DE TEXTO SMS
                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                registerDriverPresenter.showError("Codigo enviado");
                            }
                        };

                        authProvider.sendCodeVerification(telefono, callbacks, activity);
                    } else
                        registerDriverPresenter.showError("Error en el REGISTRO.");
                    registerDriverPresenter.hideProgress();
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
