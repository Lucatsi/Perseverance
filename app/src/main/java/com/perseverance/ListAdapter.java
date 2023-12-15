package com.perseverance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    private List<ItemTarea> mdata;
    private LayoutInflater mInflate;
    private Context context;


    public  ListAdapter(List<ItemTarea> itemList, Context context){
        this.mInflate = LayoutInflater.from(context);
        this.context = context;
        this.mdata = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflate.inflate(R.layout.item_tarea, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ItemTarea item = mdata.get(position);
        holder.bindData(item);
        holder.borrarDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Eliminar el elemento en esa posición
                mdata.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
            }
        });
        holder.editarDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.nombreTarea.setEnabled(true);
                holder.nombreTarea.setFocusableInTouchMode(true);
                holder.nombreTarea.requestFocus();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }
    public void setItems(List<ItemTarea> items){ mdata = items;}

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView nombreTarea, fechaTarea;
        Button editarDatos, borrarDatos;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreTarea = itemView.findViewById(R.id.nombreTarea);
            fechaTarea = itemView.findViewById(R.id.fechaTarea);
            editarDatos = itemView.findViewById(R.id.editarDatos);
            borrarDatos = itemView.findViewById(R.id.borraDatos);

        }

        void bindData(final ItemTarea item){
            nombreTarea.setText(item.getNombre());
            fechaTarea.setText(item.getFecha());
            editarDatos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Haz clic en Editar", Toast.LENGTH_SHORT).show();
                }
            });

            // Ejemplo de cómo agregar un clic al botón borrarDatos
            borrarDatos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Haz clic en Borrar", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }


}