package com.udemy.routeservices.Presenters.client;

import android.content.Context;
import com.udemy.routeservices.Interactors.client.RegisterInteractorImpl;
import com.udemy.routeservices.Interfaces.client.register.RegisterInteractor;
import com.udemy.routeservices.Interfaces.client.register.RegisterPresenter;
import com.udemy.routeservices.Interfaces.client.register.RegisterView;

public class RegisterPresenterImpl implements RegisterPresenter {

    private RegisterView registerView;
    private RegisterInteractor registerInteractor;

    public RegisterPresenterImpl(RegisterView registerView){
        this.registerView = registerView;
        registerInteractor = new RegisterInteractorImpl(this);
    }

    @Override
    public void register(String email, String pass, String nombre, Context context) {
        registerInteractor.register(email, pass, nombre, context);
    }

    @Override
    public void showProgress() {
        if(registerView != null)
            registerView.showProgress();
    }

    @Override
    public void hideProgress() {
        if(registerView != null)
            registerView.hideProgress();
    }

    @Override
    public void toMapClient() {
        if(registerView != null)
            registerView.toMapClient();
    }

}
