package com.udemy.routeservices.Interfaces.main;

import android.content.Context;

public interface MainInteractor {

    void getCurrentUser(Context context);

    void guardarUsuario(Context context, String tipo);

}
