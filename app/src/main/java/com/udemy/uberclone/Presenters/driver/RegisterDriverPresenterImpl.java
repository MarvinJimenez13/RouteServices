package com.udemy.uberclone.Presenters.driver;

import android.app.Activity;

import com.udemy.uberclone.Interactors.driver.RegisterDriverInteractorImpl;
import com.udemy.uberclone.Interfaces.driver.register.RegisterDriverInteractor;
import com.udemy.uberclone.Interfaces.driver.register.RegisterDriverPresenter;
import com.udemy.uberclone.Interfaces.driver.register.RegisterDriverView;

public class RegisterDriverPresenterImpl implements RegisterDriverPresenter {

    private RegisterDriverView registerDriverView;
    private RegisterDriverInteractor registerDriverInteractor;

    public RegisterDriverPresenterImpl(RegisterDriverView registerDriverView){
        this.registerDriverView = registerDriverView;
        registerDriverInteractor = new RegisterDriverInteractorImpl(this);
    }

    @Override
    public void showProgress() {
        if(registerDriverView != null)
            registerDriverView.showProgress();
    }

    @Override
    public void hideProgress() {
        if(registerDriverView != null)
            registerDriverView.hideProgress();
    }

    @Override
    public void showError(String error) {
        if(registerDriverView != null)
            registerDriverView.showError(error);
    }

    @Override
    public void registrar(String nombre, String email, String pass, String marca, String placa, String telefono, Activity activity) {
        registerDriverInteractor.registrar(nombre, email, pass, marca, placa, telefono, activity);
    }

    @Override
    public void successRegister() {
        if(registerDriverView != null)
            registerDriverView.successRegister();
    }

}
