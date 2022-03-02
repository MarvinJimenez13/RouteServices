package com.udemy.uberclone.Interfaces.driver.register;

import android.app.Activity;

public interface RegisterDriverPresenter {

    void showProgress();

    void hideProgress();

    void showError(String error);

    void registrar(String nombre, String email, String pass, String marca,
                   String placa, String telefono, Activity activity);

    void successRegister();

}
