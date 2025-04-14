package com.example.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private static final char ADDITION = '+';
    private static final char SUBTRACTION = '-';
    private static final char MULTIPLICATION = '*';
    private static final char DIVISION = '/';
    private static final char PERCENT = '%';

    private char currentSymbol;

    private double firstValue = Double.NaN;
    private double secondValue;
    private TextView inputDisplay, outputDisplay;
    private DecimalFormat decimalFormat;
    private MaterialButton button0, button1, button2, button3, button4, button5, button6, button7, button8, button9,
            buttonDot, buttonAdd, buttonSub, buttonMultiply, buttonDivide, buttonPercent, buttonClear, buttonOFF, buttonEqual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        decimalFormat = new DecimalFormat("#.##########");

        inputDisplay = findViewById(R.id.input);
        outputDisplay = findViewById(R.id.output);

        button0 = findViewById(R.id.btn0);
        button1 = findViewById(R.id.btn1);
        button2 = findViewById(R.id.btn2);
        button3 = findViewById(R.id.btn3);
        button4 = findViewById(R.id.btn4);
        button5 = findViewById(R.id.btn5);
        button6 = findViewById(R.id.btn6);
        button7 = findViewById(R.id.btn7);
        button8 = findViewById(R.id.btn8);
        button9 = findViewById(R.id.btn9);

        buttonAdd = findViewById(R.id.add);
        buttonSub = findViewById(R.id.subtract);
        buttonMultiply = findViewById(R.id.multiply);
        buttonDivide = findViewById(R.id.division);
        buttonDot = findViewById(R.id.btnPoint);
        buttonPercent = findViewById(R.id.percent);
        buttonClear = findViewById(R.id.clear);
        buttonOFF = findViewById(R.id.off);
        buttonEqual = findViewById(R.id.equal);

        // Number buttons
        setNumberButtonListener(button0, "0");
        setNumberButtonListener(button1, "1");
        setNumberButtonListener(button2, "2");
        setNumberButtonListener(button3, "3");
        setNumberButtonListener(button4, "4");
        setNumberButtonListener(button5, "5");
        setNumberButtonListener(button6, "6");
        setNumberButtonListener(button7, "7");
        setNumberButtonListener(button8, "8");
        setNumberButtonListener(button9, "9");

        // Operator buttons
        setOperatorButtonListener(buttonAdd, ADDITION, "+");
        setOperatorButtonListener(buttonSub, SUBTRACTION, "-");
        setOperatorButtonListener(buttonMultiply, MULTIPLICATION, "x");
        setOperatorButtonListener(buttonDivide, DIVISION, "/");
        setOperatorButtonListener(buttonPercent, PERCENT, "%");

        // Other buttons
        buttonDot.setOnClickListener(view -> inputDisplay.append("."));
        buttonClear.setOnClickListener(view -> clear());
        buttonOFF.setOnClickListener(view -> finish());
        buttonEqual.setOnClickListener(view -> calculateResult());
    }

    private void setNumberButtonListener(MaterialButton button, String value) {
        button.setOnClickListener(view -> inputDisplay.append(value));
    }

    private void setOperatorButtonListener(MaterialButton button, char operator, String symbol) {
        button.setOnClickListener(view -> {
            performCalculation();
            currentSymbol = operator;
            outputDisplay.setText(decimalFormat.format(firstValue) + symbol);
            inputDisplay.setText("");
        });
    }

    private void clear() {
        if (inputDisplay.getText().length() > 0) {
            inputDisplay.setText(inputDisplay.getText().subSequence(0, inputDisplay.length() - 1));
        } else {
            firstValue = Double.NaN;
            secondValue = 0;
            inputDisplay.setText("");
            outputDisplay.setText("");
        }
    }

    private void calculateResult() {
        performCalculation();
        outputDisplay.setText(decimalFormat.format(firstValue));
        firstValue = Double.NaN;
        currentSymbol = '0'; // Reset operator after calculation
    }

    private void performCalculation() {
        if (!Double.isNaN(firstValue)) {
            secondValue = Double.parseDouble(inputDisplay.getText().toString());
            inputDisplay.setText("");
            switch (currentSymbol) {
                case ADDITION:
                    firstValue += secondValue;
                    break;
                case SUBTRACTION:
                    firstValue -= secondValue;
                    break;
                case MULTIPLICATION:
                    firstValue *= secondValue;
                    break;
                case DIVISION:
                    if (secondValue != 0) {
                        firstValue /= secondValue;
                    } else {
                        outputDisplay.setText("Error");
                        firstValue = Double.NaN;
                    }
                    break;
                case PERCENT:
                    firstValue = (firstValue * secondValue) / 100;
                    break;
            }
        } else {
            try {
                firstValue = Double.parseDouble(inputDisplay.getText().toString());
            } catch (Exception e) {
                outputDisplay.setText("Error");
            }
        }
    }
}
