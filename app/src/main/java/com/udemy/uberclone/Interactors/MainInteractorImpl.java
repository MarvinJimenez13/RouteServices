package com.udemy.uberclone.Interactors;

import android.content.Context;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.udemy.uberclone.Interfaces.main.MainInteractor;
import com.udemy.uberclone.Interfaces.main.MainPresenter;
import com.udemy.uberclone.Views.client.MapClientActivity;
import com.udemy.uberclone.Views.driver.MapDriverActivity;
import com.udemy.uberclone.Utils.preferences.SharedPreferencesUber;

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
