package com.unimus.apitodo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    EditText inputTodo;
    Button btnAdd;
    RecyclerView rvTodo;
    TodoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputTodo = findViewById(R.id.inputTodo);
        btnAdd = findViewById(R.id.btnAdd);
        rvTodo = findViewById(R.id.rvTodo);
        rvTodo.setLayoutManager(new LinearLayoutManager(this));

        loadTodo();
        btnAdd.setOnClickListener(v->addTodo());

    }

    private void addTodo() {
        String title = inputTodo.getText().toString();

        // validasi sederhana, tidak mengubah alur asli
        if (TextUtils.isEmpty(title.trim())) {
            Toast.makeText(this, "Tulis dulu tugasnya", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiClient.getService().addTodo(title).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                inputTodo.setText("");
                loadTodo();
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {

            }
        });
    }

    private void loadTodo() {
        ApiClient.getService().getTodo().enqueue(new Callback<TodoResponse>() {
            @Override
            public void onResponse(Call<TodoResponse> call, Response<TodoResponse> r) {
                // tambahkan cek null supaya aman, tapi struktur if tetap sama
                if (r.body() != null && r.body().status){
                    adapter = new TodoAdapter(r.body().getData());
                    rvTodo.setAdapter(adapter);

                }
            }

            @Override
            public void onFailure(Call<TodoResponse> call, Throwable t) {

            }
        });
    }
}
