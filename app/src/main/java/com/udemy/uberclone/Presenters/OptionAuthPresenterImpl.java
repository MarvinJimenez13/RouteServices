package com.udemy.uberclone.Presenters;

import android.content.Context;

import com.udemy.uberclone.Interactors.OptionAuthInteractorImpl;
import com.udemy.uberclone.Interfaces.option_auth.OptionAuthInteractor;
import com.udemy.uberclone.Interfaces.option_auth.OptionAuthPresenter;
import com.udemy.uberclone.Interfaces.option_auth.OptionAuthView;

public class OptionAuthPresenterImpl implements OptionAuthPresenter {

    private OptionAuthView optionAuthView;
    private OptionAuthInteractor optionAuthInteractor;

    public OptionAuthPresenterImpl(OptionAuthView optionAuthView){
        this.optionAuthView = optionAuthView;
        optionAuthInteractor = new OptionAuthInteractorImpl();
    }

    @Override
    public void tengoCuenta(Context context) {
        optionAuthInteractor.tengoCuenta(context);
    }

    @Override
    public void registrarme(Context context) {
        optionAuthInteractor.registrarme(context);
    }
}
