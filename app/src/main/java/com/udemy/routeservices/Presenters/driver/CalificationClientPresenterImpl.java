package com.udemy.routeservices.Presenters.driver;

import com.udemy.routeservices.Interactors.driver.CalificationClientInteractorImpl;
import com.udemy.routeservices.Interfaces.driver.calification_client.CalificationClientInteractor;
import com.udemy.routeservices.Interfaces.driver.calification_client.CalificationClientPresenter;
import com.udemy.routeservices.Interfaces.driver.calification_client.CalificationClientView;

public class CalificationClientPresenterImpl implements CalificationClientPresenter {

    private CalificationClientView calificationClientView;
    private CalificationClientInteractor calificationClientInteractor;

    public CalificationClientPresenterImpl(CalificationClientView calificationClientView){
        this.calificationClientView = calificationClientView;
        calificationClientInteractor = new CalificationClientInteractorImpl(this);
    }

    @Override
    public void getClientBooking(String idClientBooking) {
        calificationClientInteractor.getClientBooking(idClientBooking);
    }

    @Override
    public void calificar(float calification) {
        calificationClientInteractor.calificar(calification);
    }

    @Override
    public void showProgress() {
        if(calificationClientView != null)
            calificationClientView.showProgress();
    }

    @Override
    public void hideProgress() {
        if(calificationClientView != null)
            calificationClientView.hideProgress();
    }

    @Override
    public void showError(String error) {
        if(calificationClientView != null)
            calificationClientView.showError(error);
    }

    @Override
    public void calificationSuccess() {
        if(calificationClientView != null)
            calificationClientView.calificationSuccess();
    }

    @Override
    public void setInfoRoute(String origin, String destination) {
        if(calificationClientView != null)
            calificationClientView.setInfoRoute(origin, destination);
    }

}
