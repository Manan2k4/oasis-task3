package com.oasis.calculator;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.Locale;

public class MainActivity extends Activity implements View.OnClickListener {

    private TextView txtFormulaDisplay, txtMainDisplay;
    private String currentInput = "";
    private double firstNumber = Double.NaN;
    private char currentOperator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtFormulaDisplay = findViewById(R.id.txtFormulaDisplay);
        txtMainDisplay = findViewById(R.id.txtMainDisplay);

        // Intention Mapping Array IDs
        int[] buttonIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
                R.id.btnDot, R.id.btnC, R.id.btnBackspace, R.id.btnPercent,
                R.id.btnAdd, R.id.btnSubtract, R.id.btnMultiply, R.id.btnDivide, R.id.btnEquals
        };

        for (int id : buttonIds) {
            findViewById(id).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        // 1. Clear Operation
        if (id == R.id.btnC) {
            currentInput = "";
            firstNumber = Double.NaN;
            txtFormulaDisplay.setText("");
            txtMainDisplay.setText("0");
        }
        // 2. Backspace Operation
        else if (id == R.id.btnBackspace) {
            if (currentInput.length() > 0) {
                currentInput = currentInput.substring(0, currentInput.length() - 1);
                txtMainDisplay.setText(currentInput.isEmpty() ? "0" : currentInput);
            }
        }
        // 3. Percentage Modifier Operation
        else if (id == R.id.btnPercent) {
            if (!currentInput.isEmpty()) {
                double val = Double.parseDouble(currentInput) / 100.0;
                currentInput = String.valueOf(val);
                txtMainDisplay.setText(currentInput);
            }
        }
        // 4. Decimal Dot Addition Guardrail
        else if (id == R.id.btnDot) {
            if (!currentInput.contains(".")) {
                currentInput += currentInput.isEmpty() ? "0." : ".";
                txtMainDisplay.setText(currentInput);
            }
        }
        // 5. Math Operator Ingestion Triggers
        else if (id == R.id.btnAdd || id == R.id.btnSubtract || id == R.id.btnMultiply || id == R.id.btnDivide) {
            Button btn = (Button) v;
            if (!currentInput.isEmpty()) {
                computeValue();
                currentOperator = btn.getText().charAt(0);
                firstNumber = Double.parseDouble(txtMainDisplay.getText().toString());
                txtFormulaDisplay.setText(String.format(Locale.US, "%s %c", formatNumber(firstNumber), currentOperator));
                currentInput = "";
            } else if (!Double.isNaN(firstNumber)) {
                currentOperator = btn.getText().charAt(0);
                txtFormulaDisplay.setText(String.format(Locale.US, "%s %c", formatNumber(firstNumber), currentOperator));
            }
        }
        // 6. Execution Trigger (= Key)
        else if (id == R.id.btnEquals) {
            if (!currentInput.isEmpty() && !Double.isNaN(firstNumber)) {
                computeValue();
                txtFormulaDisplay.setText("");
                firstNumber = Double.NaN;
            }
        }
        // 7. Numerical Keystroke Ingestion Defaults
        else {
            Button btn = (Button) v;
            currentInput += btn.getText().toString();
            txtMainDisplay.setText(currentInput);
        }
    }

    private void computeValue() {
        if (!Double.isNaN(firstNumber) && !currentInput.isEmpty()) {
            double secondNumber = Double.parseDouble(currentInput);
            double outputValue = 0.0;

            switch (currentOperator) {
                case '+': outputValue = firstNumber + secondNumber; break;
                case '-': outputValue = firstNumber - secondNumber; break;
                case '×': outputValue = firstNumber * secondNumber; break;
                case '÷':
                    if (secondNumber == 0) {
                        txtMainDisplay.setText("Error");
                        currentInput = "";
                        firstNumber = Double.NaN;
                        return;
                    }
                    outputValue = firstNumber / secondNumber;
                    break;
            }
            txtMainDisplay.setText(formatNumber(outputValue));
            currentInput = formatNumber(outputValue);
        }
    }

    // Helper formatting engine to remove trailing decimal zeroes cleanly (.00)
    private String formatNumber(double num) {
        if (num == (long) num) {
            return String.format(Locale.US, "%d", (long) num);
        } else {
            return String.format(Locale.US, "%.4f", num).replaceAll("0*$", "").replaceAll("\\.$", "");
        }
    }
}