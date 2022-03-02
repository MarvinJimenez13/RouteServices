package com.udemy.uberclone.Views;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.appbar.MaterialToolbar;
import com.udemy.uberclone.Interfaces.option_auth.OptionAuthPresenter;
import com.udemy.uberclone.Interfaces.option_auth.OptionAuthView;
import com.udemy.uberclone.Presenters.OptionAuthPresenterImpl;
import com.udemy.uberclone.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class OptionAuthActivity extends AppCompatActivity implements OptionAuthView {

    private Unbinder mUnbinder;
    private OptionAuthPresenter optionAuthPresenter;

    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option_auth);
        mUnbinder = ButterKnife.bind(this);

        optionAuthPresenter = new OptionAuthPresenterImpl(OptionAuthActivity.this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Seleccionar opción");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void tengoCuenta(){
        optionAuthPresenter.tengoCuenta(OptionAuthActivity.this);
    }

    @Override
    public void registrarme(){
        optionAuthPresenter.registrarme(OptionAuthActivity.this);
    }

    @OnClick({R.id.btnCuenta, R.id.btnRegistrarme})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btnCuenta:
                tengoCuenta();
                break;
            case R.id.btnRegistrarme:
                registrarme();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }
}