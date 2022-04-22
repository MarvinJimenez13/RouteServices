package com.udemy.routeservices.Utils.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.udemy.routeservices.Models.Client;

import java.util.HashMap;
import java.util.Map;

public class ClientProvider {

    private DatabaseReference mDatabase;

    public ClientProvider(){
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Clients");
    }

    public Task<Void> create(Client client){
        Map<String, Object> map = new HashMap<>();
        map.put("name", client.getName());
        map.put("email", client.getEmail());
        return mDatabase.child(client.getId()).setValue(map);
    }

    public DatabaseReference emailExist(){
        return mDatabase;
    }

    public DatabaseReference getCLient(String idClient){
        return mDatabase.child(idClient);
    }

}
