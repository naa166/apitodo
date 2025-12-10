package com.unimus.apitodo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

    public TodoAdapter(List<Todo> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_todo, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(list.get(position).getTitle());

        // EDIT
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

        // HAPUS dengan bubble modern
        holder.btnDelete.setOnClickListener(v -> {
            View dialogView = LayoutInflater.from(v.getContext())
                    .inflate(R.layout.dialog_confirm_delete, null, false);

            TextView tvMessage = dialogView.findViewById(R.id.tvMessage);
            Button btnCancel = dialogView.findViewById(R.id.btnCancel);
            Button btnOk = dialogView.findViewById(R.id.btnOk);

            tvMessage.setText("Tugas \"" + list.get(position).getTitle() + "\" akan dihapus.");

            AlertDialog dialog = new AlertDialog.Builder(v.getContext())
                    .setView(dialogView)
                    .setCancelable(true)
                    .create();

            btnCancel.setOnClickListener(view -> dialog.dismiss());

            btnOk.setOnClickListener(view -> {
                ApiClient.getService().deleteTodo(list.get(position).getId())
                        .enqueue(new Callback<BasicResponse>() {
                            @Override
                            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                                list.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, list.size());
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(Call<BasicResponse> call, Throwable t) {
                                dialog.dismiss();
                            }
                        });
            });

            dialog.show();
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
