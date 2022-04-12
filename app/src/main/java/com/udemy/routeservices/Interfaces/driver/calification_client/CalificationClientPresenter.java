package com.udemy.routeservices.Interfaces.driver.calification_client;

public interface CalificationClientPresenter {

    void getClientBooking(String idClientBooking);

    void calificar(float calification);

    void showProgress();

    void hideProgress();

    void showError(String error);

    void calificationSuccess();

    void setInfoRoute(String origin, String destination);

}
