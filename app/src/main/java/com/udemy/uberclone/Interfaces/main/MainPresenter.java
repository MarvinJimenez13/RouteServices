package com.udemy.uberclone.Interfaces.main;

import android.content.Context;

public interface MainPresenter {

    void soyCliente(Context context);

    void soyConductor(Context context);

    void getCurrentUser(Context context);

    void finishActivity();

}
