package com.udemy.uberclone.Presenters;

import android.content.Context;
import com.udemy.uberclone.Interactors.LoginInteractorImpl;
import com.udemy.uberclone.Interfaces.login.LoginInteractor;
import com.udemy.uberclone.Interfaces.login.LoginPresenter;
import com.udemy.uberclone.Interfaces.login.LoginView;

public class LoginPresenterImpl implements LoginPresenter {

    private LoginView loginView;
    private LoginInteractor loginInteractor;

    public LoginPresenterImpl(LoginView loginView){
        this.loginView = loginView;
        loginInteractor = new LoginInteractorImpl(this);
    }

    @Override
    public void login(Context context, String email, String pass) {
        loginInteractor.login(context, email, pass);
    }

    @Override
    public void showProgress() {
        if(loginView != null)
            loginView.showProgress();
    }

    @Override
    public void hideProgress() {
        if(loginView != null)
            loginView.hideProgress();
    }

    @Override
    public void finishActivity() {
        if(loginView != null)
            loginView.finishActivity();
    }

}
