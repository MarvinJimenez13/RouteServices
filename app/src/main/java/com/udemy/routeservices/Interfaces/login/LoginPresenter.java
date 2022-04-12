package com.udemy.routeservices.Interfaces.login;

import android.content.Context;

public interface LoginPresenter {

    void login(Context context, String email, String pass);

    void showProgress();

    void hideProgress();

    void finishActivity();

}
