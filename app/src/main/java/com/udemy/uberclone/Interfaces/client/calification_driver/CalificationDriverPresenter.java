package com.udemy.uberclone.Interfaces.client.calification_driver;

public interface CalificationDriverPresenter {

    void getClientBooking(String idClient);

    void setRoute(String origin, String destination);

    void calificar(float calification);

    void showError(String error);

    void successCalification(String mensaje);

}
