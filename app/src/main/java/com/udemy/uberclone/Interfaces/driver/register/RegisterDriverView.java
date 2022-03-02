package com.udemy.uberclone.Interfaces.driver.register;

public interface RegisterDriverView {

    void showProgress();

    void hideProgress();

    void showError(String error);

    void successRegister();

}
