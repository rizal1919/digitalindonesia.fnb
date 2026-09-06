package com.digitalindonesia.fnb;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.NumberFormat;
import java.util.Locale;

public class HoldSuccessActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hold_success);

        double total = getIntent().getDoubleExtra("EXTRA_TOTAL", 0);
        String trx = getIntent().getStringExtra("EXTRA_TRX");

        TextView tvTrxNumber = findViewById(R.id.tvTrxNumber);
        TextView tvTotal = findViewById(R.id.tvTotal);

        if (trx != null) tvTrxNumber.setText(trx);
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        format.setMaximumFractionDigits(0);
        tvTotal.setText(format.format(total).replace("Rp", "Rp "));

        findViewById(R.id.btnTransaksiBaru).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        findViewById(R.id.btnKeHome).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }
}