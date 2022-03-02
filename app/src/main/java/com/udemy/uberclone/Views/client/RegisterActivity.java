package com.udemy.uberclone.Views.client;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.udemy.uberclone.Interfaces.client.register.RegisterPresenter;
import com.udemy.uberclone.Interfaces.client.register.RegisterView;
import com.udemy.uberclone.Presenters.client.RegisterPresenterImpl;
import com.udemy.uberclone.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class RegisterActivity extends AppCompatActivity implements RegisterView {

    private Unbinder mUnbinder;
    private ProgressDialog progressDialog;
    private RegisterPresenter registerPresenter;

    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
    @BindView(R.id.tietNombre)
    TextInputEditText etNombre;
    @BindView(R.id.tietCorreo)
    TextInputEditText etCorreo;
    @BindView(R.id.tietPass)
    TextInputEditText etPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mUnbinder = ButterKnife.bind(this);

        registerPresenter = new RegisterPresenterImpl(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @OnClick(R.id.btnRegistro)
    public void onClick(){
        final String nombre = etNombre.getText().toString();
        final String email = etCorreo.getText().toString().trim();
        String pass = etPass.getText().toString().trim();

        if(!nombre.isEmpty() && !email.isEmpty() && !pass.isEmpty()){
            if(pass.length() >= 6)
                registerPresenter.register(email.trim(), pass, nombre, this);
            else
                Toast.makeText(this, "El pass debe tener 6 o mas carcateres.", Toast.LENGTH_LONG).show();
        }else
            Toast.makeText(this, "Ingresa todos los campos.", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
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
    public void toMapClient() {
        startActivity(new Intent(this, MapClientActivity.class));
        finish();
    }

}