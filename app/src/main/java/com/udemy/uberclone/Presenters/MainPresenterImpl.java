package com.udemy.uberclone.Presenters;

import android.content.Context;
import com.udemy.uberclone.Interactors.MainInteractorImpl;
import com.udemy.uberclone.Interfaces.main.MainInteractor;
import com.udemy.uberclone.Interfaces.main.MainPresenter;
import com.udemy.uberclone.Interfaces.main.MainView;

public class MainPresenterImpl implements MainPresenter {

    private MainView mainView;
    private MainInteractor mainInteractor;

    public MainPresenterImpl(MainView mainView){
        this.mainView = mainView;
        mainInteractor = new MainInteractorImpl(this);
    }

    @Override
        public void soyCliente(Context context) {
        if(mainView != null){
            mainInteractor.guardarUsuario(context,"CLIENTE");
            mainView.auth();
        }
    }

    @Override
    public void soyConductor(Context context) {
        if(mainView != null){
            mainInteractor.guardarUsuario(context, "CONDUCTOR");
            mainView.auth();
        }
    }

    @Override
    public void getCurrentUser(Context context) {
        if(mainView != null)
            mainInteractor.getCurrentUser(context);
    }

    @Override
    public void finishActivity() {
        if(mainView != null)
            mainView.finishActivity();
    }

}
