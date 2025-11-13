package com.example.appingresosegresos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnIrConceptos, btnIrRegistro, btnIrBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnIrConceptos = findViewById(R.id.btnIrConceptos);
        btnIrRegistro = findViewById(R.id.btnIrRegistro);
        btnIrBalance = findViewById(R.id.btnIrBalance);

        btnIrConceptos.setOnClickListener(v -> startActivity(new Intent(this, ConceptosActivity.class)));
        btnIrRegistro.setOnClickListener(v -> startActivity(new Intent(this, RegistroActivity.class)));
        btnIrBalance.setOnClickListener(v -> startActivity(new Intent(this, BalanceActivity.class)));
    }
}
