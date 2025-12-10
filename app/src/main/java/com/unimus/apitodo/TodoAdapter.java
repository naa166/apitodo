package com.unimus.apitodo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder> {

    List<Todo> list;
    ImageView btnDelete;

    public TodoAdapter(List<Todo> list) {
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_todo, parent, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.title.setText(list.get(position).getTitle());

        holder.itemView.setOnClickListener(v -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext());
            dialog.setTitle("Edit Tugas");

            final EditText input = new EditText(v.getContext());
            input.setText(list.get(position).getTitle());
            dialog.setView(input);

            dialog.setPositiveButton("Update", (d, w) -> {
                String newTitle = input.getText().toString();

                ApiClient.getService().updateTodo(list.get(position).getId(), newTitle)
                        .enqueue(new Callback<BasicResponse>() {
                            @Override
                            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                                list.get(position).setTitle(newTitle);
                                notifyItemChanged(position);
                            }

                            @Override
                            public void onFailure(Call<BasicResponse> call, Throwable t) {}
                        });
            });

            dialog.setNegativeButton("Batal", null);
            dialog.show();
        });


        holder.btnDelete.setOnClickListener(v -> {
            ApiClient.getService().deleteTodo(list.get(position).getId())
                    .enqueue(new Callback<BasicResponse>() {
                        @Override
                        public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                            list.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, list.size());
                        }

                        @Override
                        public void onFailure(Call<BasicResponse> call, Throwable t) {

                        }
                    });
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.txtTitle);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}

