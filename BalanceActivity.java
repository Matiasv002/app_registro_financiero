package com.example.appingresosegresos;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import java.util.List;

public class BalanceActivity extends AppCompatActivity {

    TextView txtIngresos, txtEgresos, txtBalance, txtIVA, txtGravados;
    Button btnDetalles, btnVolver;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);

        txtIngresos = findViewById(R.id.txtIngresos);
        txtEgresos = findViewById(R.id.txtEgresos);
        txtBalance = findViewById(R.id.txtBalance);
        txtIVA = findViewById(R.id.txtIVA);
        txtGravados = findViewById(R.id.txtGravados);
        btnDetalles = findViewById(R.id.btnDetalles);
        btnVolver = findViewById(R.id.btnVolver);

        dbHelper = new DBHelper(this);

        actualizarBalance();

        btnDetalles.setOnClickListener(v -> {
            List<String> movimientos = dbHelper.obtenerDetallesBalance();
            StringBuilder detalles = new StringBuilder();
            for (String m : movimientos) {
                detalles.append(m).append("\n");
            }

            new AlertDialog.Builder(this)
                    .setTitle("Detalles de Movimientos")
                    .setMessage(detalles.toString())
                    .setPositiveButton("OK", null)
                    .show();
        });

        btnVolver.setOnClickListener(v -> finish());
    }

    private void actualizarBalance() {
        double ingresos = dbHelper.obtenerTotalPorTipo("Ingreso");
        double egresos = dbHelper.obtenerTotalPorTipo("Egreso");
        double balance = ingresos - egresos;
        double totalIVA = dbHelper.obtenerTotalIVA();
        int totalGravados = dbHelper.contarMovimientosGravados();

        txtIngresos.setText("Ingresos: $" + ingresos);
        txtEgresos.setText("Egresos: $" + egresos);
        txtBalance.setText("Balance: $" + balance);
        txtIVA.setText("Total IVA: $" + totalIVA);
        txtGravados.setText("Movimientos Gravados: " + totalGravados);
    }
}
