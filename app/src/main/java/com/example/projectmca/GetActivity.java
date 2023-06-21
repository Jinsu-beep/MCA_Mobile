package com.example.projectmca;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class GetActivity extends AppCompatActivity {
    protected Cursor cursor;
    Database database;
    Button btn_kirim;
    EditText nama, kampus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get);
        database = new Database(this);
        nama = findViewById(R.id.nama);
        kampus = findViewById(R.id.kampus);
        btn_kirim = findViewById(R.id.btn_kirim);

        SQLiteDatabase db = database.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM mahasiswa WHERE nama = '" +
                getIntent().getStringExtra("nama")+"'",null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0){
            cursor.moveToPosition(0);
            nama.setText(cursor.getString(0).toString());
            kampus.setText(cursor.getString(1).toString());
        }

        btn_kirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kirim_data(nama.getText().toString(), kampus.getText().toString());
            }
        });
    }

    void kirim_data(String nama, String kampus)
    {
        String url1 = "https://mcaapi.000webhostapp.com/api/insertData";
        StringRequest respon1 = new StringRequest(Request.Method.POST, url1, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("success");
                    Log.d("status",status);
                    if (status.equalsIgnoreCase("sukses")) {
                        JSONObject hasil = jsonObject.getJSONObject("pendaftaran");
                        Log.d("data", String.valueOf(hasil));
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(GetActivity.this);
                        alertDialog.setTitle("SUSKSES");
                        alertDialog.setMessage("Kode Verifiksai = " + hasil.getString("kode_verifikasi"));
                        alertDialog.setPositiveButton("OKE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MainActivity.ma.RefreshList();
                                finish();
                            }
                        });
                        AlertDialog alert = alertDialog.create();
                        alert.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }){
            protected Map<String, String>getParams(){
                Map<String, String>form = new HashMap<>();
                form.put("nama", nama);
                form.put("kampus", kampus);
                return form;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(respon1);
    }
}