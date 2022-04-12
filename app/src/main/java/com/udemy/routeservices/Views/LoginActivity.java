package com.udemy.routeservices.Views;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.os.Bundle;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.udemy.routeservices.Interfaces.login.LoginPresenter;
import com.udemy.routeservices.Interfaces.login.LoginView;
import com.udemy.routeservices.Presenters.LoginPresenterImpl;
import com.udemy.routeservices.R;
import com.udemy.routeservices.Utils.includes.MyToolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class LoginActivity extends AppCompatActivity implements LoginView {

    private Unbinder mUnbinder;
    private ProgressDialog progressDialog;
    private LoginPresenter loginPresenter;

    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
    @BindView(R.id.tietCorreo)
    TextInputEditText etCorreo;
    @BindView(R.id.tietPass)
    TextInputEditText etPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mUnbinder = ButterKnife.bind(this);

        MyToolbar.show(this, "Login", true, toolbar);
        loginPresenter = new LoginPresenterImpl(LoginActivity.this);
    }

    @OnClick(R.id.btnEntrar)
    public void onClick(){
        String email = etCorreo.getText().toString().trim();
        String pass = etPass.getText().toString().trim();
        loginPresenter.login(LoginActivity.this, email, pass);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    @Override
    public void showProgress() {
        progressDialog = ProgressDialog.show(this, "Iniciando Sesion", "Espere...");
    }

    @Override
    public void hideProgress() {
        progressDialog.dismiss();
    }

    @Override
    public void finishActivity() {
        finish();
    }

}