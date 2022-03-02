package com.udemy.uberclone.Views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.udemy.uberclone.Models.HistoryBooking;
import com.udemy.uberclone.R;
import com.udemy.uberclone.Utils.providers.DriverProvider;

public class HistoryBookingClientAdapter extends FirebaseRecyclerAdapter<HistoryBooking, HistoryBookingClientAdapter.ViewHolder> {

    private DriverProvider driverProvider;
    private Context context;

    public HistoryBookingClientAdapter(@NonNull FirebaseRecyclerOptions<HistoryBooking> options, Context context) {
        super(options);
        driverProvider = new DriverProvider();
        this.context = context;
    }

    //VALORES QUE IRAN EN LAS VISTAS DE EL CARD
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull HistoryBooking model) {
        driverProvider.getDriver(model.getIdDriver()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                    holder.tvName.setText(snapshot.child("name").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.tvOrigin.setText(model.getOrigin());
        holder.tvDestination.setText(model.getDestination());
        holder.tvCalification.setText(String.valueOf(model.getCalificationClient()));
        holder.view.setOnClickListener(view -> {
            Toast.makeText(context, model.getIdHistoryBooking(), Toast.LENGTH_LONG).show();
        });
    }

    //DONDE INSTANCIAMOS EL LAYOUT A UTILIZAR
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_history_booking, parent, false);
       return new ViewHolder(view);
    }

    //CLASE DONDE INSTANCIAMOS LOS ID DE LA VISTA CARD
    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvName, tvOrigin, tvDestination, tvCalification;
        private View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            tvName = itemView.findViewById(R.id.tvNombre);
            tvOrigin = itemView.findViewById(R.id.tvOrigen);
            tvDestination = itemView.findViewById(R.id.tvDestino);
            tvCalification = itemView.findViewById(R.id.tvCalificacion);
        }

    }

}
