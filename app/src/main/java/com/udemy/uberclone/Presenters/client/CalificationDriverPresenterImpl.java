package com.udemy.uberclone.Presenters.client;

import com.udemy.uberclone.Interactors.client.CalificationDriverInteractorImpl;
import com.udemy.uberclone.Interfaces.client.calification_driver.CalificationDriverInteractor;
import com.udemy.uberclone.Interfaces.client.calification_driver.CalificationDriverPresenter;
import com.udemy.uberclone.Interfaces.client.calification_driver.CalificationDriverView;

public class CalificationDriverPresenterImpl implements CalificationDriverPresenter {

    private CalificationDriverView calificationDriverView;
    private CalificationDriverInteractor calificationDriverInteractor;

    public CalificationDriverPresenterImpl(CalificationDriverView calificationDriverView){
        this.calificationDriverView = calificationDriverView;
        calificationDriverInteractor = new CalificationDriverInteractorImpl(this);
    }

    @Override
    public void getClientBooking(String idClient) {
        calificationDriverInteractor.getClientBooking(idClient);
    }

    @Override
    public void setRoute(String origin, String destination) {
        if(calificationDriverView != null)
            calificationDriverView.setRoute(origin, destination);
    }

    @Override
    public void calificar(float calification) {
        calificationDriverInteractor.calificar(calification);
    }

    @Override
    public void showError(String error) {
        if(calificationDriverView != null)
            calificationDriverView.showError(error);
    }

    @Override
    public void successCalification(String mensaje) {
        if(calificationDriverView != null)
            calificationDriverView.successCalification(mensaje);
    }

}
