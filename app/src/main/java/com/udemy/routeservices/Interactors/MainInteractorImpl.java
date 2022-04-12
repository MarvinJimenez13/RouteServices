package com.udemy.routeservices.Interactors;

import android.content.Context;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.udemy.routeservices.Interfaces.main.MainInteractor;
import com.udemy.routeservices.Interfaces.main.MainPresenter;
import com.udemy.routeservices.Views.client.MapClientActivity;
import com.udemy.routeservices.Views.driver.MapDriverActivity;
import com.udemy.routeservices.Utils.preferences.SharedPreferencesUber;

public class MainInteractorImpl implements MainInteractor {

    private MainPresenter mainPresenter;

    public MainInteractorImpl(MainPresenter mainPresenter){
        this.mainPresenter = mainPresenter;
    }

    @Override
    public void getCurrentUser(Context context) {
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            if(SharedPreferencesUber.getInstance(context).getTipoUsuario().equals("CLIENTE"))
                context.startActivity(new Intent(context, MapClientActivity.class));
            else if(SharedPreferencesUber.getInstance(context).getTipoUsuario().equals("CONDUCTOR"))
                context.startActivity(new Intent(context, MapDriverActivity.class));

            mainPresenter.finishActivity();
        }
    }

    @Override
    public void guardarUsuario(Context context, String tipo) {
        SharedPreferencesUber.getInstance(context).guardarTipoUsuario(tipo);
    }

}
