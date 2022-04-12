package com.udemy.routeservices.Interfaces.client.calification_driver;

public interface CalificationDriverView {

    void setRoute(String origin, String destination);

    void showError(String error);

    void successCalification(String mensaje);

}
