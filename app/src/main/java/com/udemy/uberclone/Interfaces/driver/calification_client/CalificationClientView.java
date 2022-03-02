package com.udemy.uberclone.Interfaces.driver.calification_client;

public interface CalificationClientView {

    void showProgress();

    void hideProgress();

    void showError(String error);

    void calificationSuccess();

    void setInfoRoute(String origin, String destination);

}
