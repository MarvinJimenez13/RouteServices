package com.udemy.routeservices.Interfaces.client.register;

import android.content.Context;

public interface RegisterPresenter {

    void register(String email, String pass, String nombre, Context context);

    void showProgress();

    void hideProgress();

    void toMapClient();

}
