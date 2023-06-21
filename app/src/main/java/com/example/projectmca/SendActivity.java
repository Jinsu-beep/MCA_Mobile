package com.example.projectmca;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SendActivity extends AppCompatActivity {
    protected Cursor cursor;
    Database database;
    Button btn_dapat;
    EditText token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        database = new Database(this);
        token = findViewById(R.id.token);
        btn_dapat = findViewById(R.id.btn_dapat);
        btn_dapat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData(token.getText().toString());
            }
        });
    }

    void getData(String token) {
        String url2 = "https://mcaapi.000webhostapp.com/api/getData";
        StringRequest respon2 = new StringRequest(Request.Method.POST, url2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject2 = new JSONObject(response);
                    String status2 = jsonObject2.getString("success");
                    Log.d("status",status2);
                    if (status2.equalsIgnoreCase("sukses")) {
                        JSONObject hasil2 = jsonObject2.getJSONObject("pendaftaran");
                        Log.d("data", String.valueOf(hasil2));

                        SQLiteDatabase db = database.getWritableDatabase();
                        db.execSQL("INSERT INTO mahasiswa(nama, kampus) values('" +
                                    hasil2.getString("nama") + "','" +
                                    hasil2.getString("kampus") + "')");

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SendActivity.this);
                        alertDialog.setTitle("SUSKSES");
                        alertDialog.setMessage("Data Berhasil Diambil");
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
            protected Map<String, String> getParams(){
                Map<String, String>form = new HashMap<>();
                form.put("token", token);
                return form;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(respon2);
    }
}