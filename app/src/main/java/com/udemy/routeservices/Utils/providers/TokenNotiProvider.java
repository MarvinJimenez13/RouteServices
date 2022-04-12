package com.udemy.routeservices.Utils.providers;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.udemy.routeservices.Models.Token;

public class TokenNotiProvider {

    DatabaseReference databaseReference;

    public TokenNotiProvider(){
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Tokens");
    }

    public void create(String idUser){
        if(idUser == null) return;
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            Token token = new Token();
            token.setToken(task.getResult());
            databaseReference.child(idUser).setValue(token);
        });
    }

    public DatabaseReference getToken(String idUser){
        return databaseReference.child(idUser);
    }

}
