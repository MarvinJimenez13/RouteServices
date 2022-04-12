package com.udemy.routeservices.Utils.providers;

import android.app.Activity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class AuthProvider {

    private FirebaseAuth firebaseAuth;

    public AuthProvider() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public Task<AuthResult> register(String email, String pass){
        return firebaseAuth.createUserWithEmailAndPassword(email, pass);
    }

    public Task<AuthResult> login(String email, String pass){
        return firebaseAuth.signInWithEmailAndPassword(email, pass);
    }

    //VERIFICACION DE CODIGO POR SMS PARA INICIAR SESION POR NUMERO TELEFONICO
    public Task<AuthResult> signInPhone(String verificationID, String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, code);
        return firebaseAuth.signInWithCredential(credential);
    }

    //ENVIAR SMS DE VERIFICACION
    public void sendCodeVerification(String phone, PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks, Activity activity){
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public String getID(){
        if(firebaseAuth.getCurrentUser() != null)
            return firebaseAuth.getCurrentUser().getUid();
        else return null;
    }

    public boolean existSession(){
        boolean exist = false;
        if(firebaseAuth.getCurrentUser() != null)
            exist = true;

        return exist;
    }

    public void logout(){
        firebaseAuth.signOut();
    }
}
