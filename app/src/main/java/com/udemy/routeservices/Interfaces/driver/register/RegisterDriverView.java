package com.udemy.routeservices.Interfaces.driver.register;

public interface RegisterDriverView {

    void showProgress();

    void hideProgress();

    void showError(String error);

    void successRegister();

}
