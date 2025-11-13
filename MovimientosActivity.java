package com.example.appingresosegresos;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MovimientosActivity extends AppCompatActivity {

    ListView listView;
    Button btnVolver;
    DBHelper dbHelper;
    ArrayAdapter<String> adapter;
    ArrayList<String> listaMovimientos;
    ArrayList<Integer> listaIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movimientos);

        listView = findViewById(R.id.listViewMovimientos);
        btnVolver = findViewById(R.id.btnVolver);
        dbHelper = new DBHelper(this);

        cargarMovimientos();

        // Botón para volver a la actividad principal
        btnVolver.setOnClickListener(v -> finish());

        // Click largo para eliminar un movimiento
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            int itemId = listaIds.get(position);

            new AlertDialog.Builder(this)
                    .setTitle("Eliminar")
                    .setMessage("¿Deseas eliminar este movimiento?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        dbHelper.eliminarMovimiento(itemId);
                        cargarMovimientos();
                    })
                    .setNegativeButton("No", null)
                    .show();

            return true;
        });
    }

    private void cargarMovimientos() {
        listaMovimientos = new ArrayList<>();
        listaIds = new ArrayList<>();
        Cursor cursor = dbHelper.obtenerMovimientos();

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String concepto = cursor.getString(1);
                String fecha = cursor.getString(2);
                double monto = cursor.getDouble(3);
                listaMovimientos.add(id + ". " + concepto + " - " + fecha + " - $" + monto);
                listaIds.add(id);
            } while (cursor.moveToNext());
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaMovimientos);
        listView.setAdapter(adapter);
        cursor.close();
    }
}
