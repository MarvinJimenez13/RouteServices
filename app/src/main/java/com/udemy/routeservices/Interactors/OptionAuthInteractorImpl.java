package com.udemy.routeservices.Interactors;

import android.content.Context;
import android.content.Intent;
import com.udemy.routeservices.Interfaces.option_auth.OptionAuthInteractor;
import com.udemy.routeservices.Views.LoginActivity;
import com.udemy.routeservices.Views.client.RegisterActivity;
import com.udemy.routeservices.Views.driver.RegisterDriverActivity;
import com.udemy.routeservices.Utils.preferences.SharedPreferencesUber;

public class OptionAuthInteractorImpl implements OptionAuthInteractor {

    @Override
    public void tengoCuenta(Context context) {
        context.startActivity(new Intent(context, LoginActivity.class));
    }

    @Override
    public void registrarme(Context context) {
        if(SharedPreferencesUber.getInstance(context).getTipoUsuario().equals("CLIENTE"))
            context.startActivity(new Intent(context, RegisterActivity.class));
        else if(SharedPreferencesUber.getInstance(context).getTipoUsuario().equals("CONDUCTOR"))
            context.startActivity(new Intent(context, RegisterDriverActivity.class));
    }

}
