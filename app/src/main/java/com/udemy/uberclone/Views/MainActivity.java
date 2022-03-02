package com.udemy.uberclone.Views;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.udemy.uberclone.Interfaces.main.MainPresenter;
import com.udemy.uberclone.Interfaces.main.MainView;
import com.udemy.uberclone.Presenters.MainPresenterImpl;
import com.udemy.uberclone.R;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity implements MainView {

    private Unbinder mUnbinder;
    private MainPresenter mainPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //SPLASH SCREEN
        setTheme(R.style.Theme_CloneUber);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUnbinder = ButterKnife.bind(this);
        mainPresenter = new MainPresenterImpl(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mainPresenter.getCurrentUser(this);
    }

    @Override
    public void auth(){
        startActivity(new Intent(this, OptionAuthActivity.class));
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @OnClick({R.id.btnCliente, R.id.btnConductor})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btnCliente:
                mainPresenter.soyCliente(this);
                break;
            case R.id.btnConductor:
                mainPresenter.soyConductor(this);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }
}