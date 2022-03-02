package com.udemy.uberclone.Models;

import java.util.Map;

/*
*
* Modelo de Notificaciones
*
* */
public class FCMBody {

    private String to, priority;
    private Map<String, String> data;
    private String ttl;//Time to live para asegurar que la notificacion se envie lo mas pronto posible.

    public FCMBody(String to, String priority, Map<String, String> data, String ttl) {
        this.to = to;
        this.priority = priority;
        this.data = data;
        this.ttl = ttl;
    }

}
