package com.udemy.uberclone.Interactors;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.udemy.uberclone.Interfaces.login.LoginInteractor;
import com.udemy.uberclone.Interfaces.login.LoginPresenter;
import com.udemy.uberclone.Utils.preferences.SharedPreferencesUber;
import com.udemy.uberclone.Utils.providers.AuthProvider;
import com.udemy.uberclone.Views.client.MapClientActivity;
import com.udemy.uberclone.Views.driver.MapDriverActivity;

public class LoginInteractorImpl implements LoginInteractor {

    private LoginPresenter loginPresenter;
    private AuthProvider authProvider;

    public LoginInteractorImpl(LoginPresenter loginPresenter){
        this.loginPresenter = loginPresenter;
        authProvider = new AuthProvider();
    }

    @Override
    public void login(Context context, String email, String pass) {
        if(!email.isEmpty() && !pass.isEmpty()){
            if(pass.length() >= 6){
                loginPresenter.showProgress();
                authProvider.login(email.trim(), pass.trim()).addOnCompleteListener(task -> {
                    loginPresenter.hideProgress();
                    if(task.isSuccessful()){
                        if(SharedPreferencesUber.getInstance(context).getTipoUsuario().equals("CLIENTE"))
                            context.startActivity(new Intent(context, MapClientActivity.class));
                        else if(SharedPreferencesUber.getInstance(context).getTipoUsuario().equals("CONDUCTOR"))
                            context.startActivity(new Intent(context, MapDriverActivity.class));
                        loginPresenter.finishActivity();
                    }else
                        Toast.makeText(context, "La contrasena o el correo son incorrectos.", Toast.LENGTH_LONG).show();
                });
            }else
                Toast.makeText(context, "La contrasena debe ser mayor a 6 caracteres.", Toast.LENGTH_LONG).show();
        }
    }

}