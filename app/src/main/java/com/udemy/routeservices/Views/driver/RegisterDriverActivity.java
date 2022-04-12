package com.udemy.routeservices.Views.driver;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.hbb20.CountryCodePicker;
import com.udemy.routeservices.Interfaces.driver.register.RegisterDriverPresenter;
import com.udemy.routeservices.Interfaces.driver.register.RegisterDriverView;
import com.udemy.routeservices.Presenters.driver.RegisterDriverPresenterImpl;
import com.udemy.routeservices.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class RegisterDriverActivity extends AppCompatActivity implements RegisterDriverView {

    private Unbinder mUnbinder;
    private ProgressDialog progressDialog;
    private RegisterDriverPresenter registerDriverPresenter;

    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
    @BindView(R.id.tietNombre)
    TextInputEditText etNombre;
    @BindView(R.id.tietCorreo)
    TextInputEditText etCorreo;
    @BindView(R.id.tietPass)
    TextInputEditText etPass;
    @BindView(R.id.tietMarca)
    TextInputEditText etMarca;
    @BindView(R.id.tietPlaca)
    TextInputEditText etPlaca;
    @BindView(R.id.codePicker)
    CountryCodePicker countryCodePicker;
    @BindView(R.id.tietTel)
    TextInputEditText etTelefono;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_driver);
        mUnbinder = ButterKnife.bind(this);

        registerDriverPresenter = new RegisterDriverPresenterImpl(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @OnClick(R.id.btnRegistro)
    public void onClick(){
        final String nombre = etNombre.getText().toString();
        final String email = etCorreo.getText().toString().trim();
        String pass = etPass.getText().toString().trim();
        String marca = etMarca.getText().toString();
        String placa = etPlaca.getText().toString().trim();
        String telefono = countryCodePicker.getSelectedCountryCodeWithPlus() + etTelefono.getText().toString().trim();

        registerDriverPresenter.registrar(nombre, email, pass, marca, placa, telefono, RegisterDriverActivity.this);
    }

    @Override
    public void showProgress() {
        progressDialog = ProgressDialog.show(this, "Registrando usuario", "Espere por favor.");
    }

    @Override
    public void hideProgress() {
        progressDialog.dismiss();
    }

    @Override
    public void showError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void successRegister() {
        Toast.makeText(this, "Registro de Conductor exitoso.", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MapDriverActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

}