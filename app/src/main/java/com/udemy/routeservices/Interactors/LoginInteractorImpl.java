package com.udemy.routeservices.Interactors;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.udemy.routeservices.Interfaces.login.LoginInteractor;
import com.udemy.routeservices.Interfaces.login.LoginPresenter;
import com.udemy.routeservices.Models.ClientData;
import com.udemy.routeservices.Models.DriverData;
import com.udemy.routeservices.Utils.preferences.SharedPreferencesUber;
import com.udemy.routeservices.Utils.providers.AuthProvider;
import com.udemy.routeservices.Utils.providers.ClientProvider;
import com.udemy.routeservices.Utils.providers.DriverProvider;
import com.udemy.routeservices.Views.client.MapClientActivity;
import com.udemy.routeservices.Views.driver.MapDriverActivity;

public class LoginInteractorImpl implements LoginInteractor {

    private LoginPresenter loginPresenter;
    private AuthProvider authProvider;
    private ClientProvider clientProvider;
    private DriverProvider driverProvider;

    public LoginInteractorImpl(LoginPresenter loginPresenter){
        this.loginPresenter = loginPresenter;
        authProvider = new AuthProvider();
        clientProvider = new ClientProvider();
        driverProvider = new DriverProvider();
    }

    @Override
    public void login(Context context, String email, String pass) {
        if(!email.isEmpty() && !pass.isEmpty()){
            if(pass.length() >= 6){
                loginPresenter.showProgress();
                //VALIDAR DEPENDIENDO EL PERFIL
                if(SharedPreferencesUber.getInstance(context).getTipoUsuario().equals("CLIENTE")){
                    clientProvider.emailExist().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean exist = false;
                            for(DataSnapshot data: snapshot.getChildren()){
                                ClientData clientData = data.getValue(ClientData.class);
                                if(clientData.getEmail().equals(email))
                                    exist = true;
                            }

                            if(exist){
                                authProvider.login(email.trim(), pass.trim()).addOnCompleteListener(task -> {
                                    loginPresenter.hideProgress();
                                    if(task.isSuccessful()){
                                        context.startActivity(new Intent(context, MapClientActivity.class));
                                        loginPresenter.finishActivity();
                                    }else
                                        Toast.makeText(context, "La contrasena o el correo son incorrectos.", Toast.LENGTH_LONG).show();
                                });
                            }else{
                                loginPresenter.hideProgress();
                                Toast.makeText(context, "Credenciales no regitradas en Clients.", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            loginPresenter.hideProgress();
                        }
                    });
                }else if(SharedPreferencesUber.getInstance(context).getTipoUsuario().equals("CONDUCTOR")){
                    driverProvider.emailExist().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean exist = false;
                            for(DataSnapshot data: snapshot.getChildren()){
                                DriverData driverData = data.getValue(DriverData.class);
                                if(driverData.getEmail().equals(email))
                                    exist = true;
                            }

                            if(exist){
                                authProvider.login(email.trim(), pass.trim()).addOnCompleteListener(task -> {
                                    loginPresenter.hideProgress();
                                    if(task.isSuccessful()){
                                        context.startActivity(new Intent(context, MapDriverActivity.class));
                                        loginPresenter.finishActivity();
                                    }else
                                        Toast.makeText(context, "La contrasena o el correo son incorrectos.", Toast.LENGTH_LONG).show();
                                });
                            }else{
                                loginPresenter.hideProgress();
                                Toast.makeText(context, "Credenciales no regitradas en Drivers.", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            loginPresenter.hideProgress();
                        }
                    });
                }
            }else
                Toast.makeText(context, "La contrasena debe ser mayor a 6 caracteres.", Toast.LENGTH_LONG).show();
        }

    }

}
