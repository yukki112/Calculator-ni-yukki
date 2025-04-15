package com.example.calculator;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.material.button.MaterialButton;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private static final char ADDITION = '+';
    private static final char SUBTRACTION = '-';
    private static final char MULTIPLICATION = '*';
    private static final char DIVISION = '/';
    private static final char PERCENT = '%';
    private static final char POWER = '^';
    private static final char SQUARE_ROOT = '√';
    private static final char FACTORIAL = '!';

    private char currentSymbol = '0';
    private boolean isDegreeMode = true;

    private double firstValue = Double.NaN;
    private double secondValue;
    private TextView inputDisplay, outputDisplay;
    private DecimalFormat decimalFormat;

    private MaterialButton button0, button1, button2, button3, button4, button5, button6, button7, button8, button9,
            buttonDot, buttonAdd, buttonSub, buttonMultiply, buttonDivide, buttonPercent, buttonClear,
            buttonOFF, buttonEqual, buttonSin, buttonCos, buttonTan, buttonLog, buttonLn, buttonSqrt,
            buttonPower, buttonFact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        decimalFormat = new DecimalFormat("#.##########");

        inputDisplay = findViewById(R.id.input);
        outputDisplay = findViewById(R.id.output);

        initializeButtons();
        setupNumberButtons();
        setupOperationButtons();
        setupScientificButtons();
        setupOtherButtons();
    }

    private void initializeButtons() {
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

        buttonSin = findViewById(R.id.btnSin);
        buttonCos = findViewById(R.id.btnCos);
        buttonTan = findViewById(R.id.btnTan);
        buttonLog = findViewById(R.id.btnLog);
        buttonLn = findViewById(R.id.btnLn);
        buttonSqrt = findViewById(R.id.btnSqrt);
        buttonPower = findViewById(R.id.btnPower);
        buttonFact = findViewById(R.id.btnFact);
    }

    private void setupNumberButtons() {
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
    }

    private void setNumberButtonListener(MaterialButton button, String value) {
        button.setOnClickListener(view -> {
            if (inputDisplay.getText().toString().equals("0") ||
                    inputDisplay.getText().toString().equals("Error")) {
                inputDisplay.setText(value);
            } else {
                inputDisplay.append(value);
            }
        });
    }

    private void setupOperationButtons() {
        setOperatorButtonListener(buttonAdd, ADDITION, "+");
        setOperatorButtonListener(buttonSub, SUBTRACTION, "-");
        setOperatorButtonListener(buttonMultiply, MULTIPLICATION, "×");
        setOperatorButtonListener(buttonDivide, DIVISION, "÷");
        setOperatorButtonListener(buttonPercent, PERCENT, "%");
        setOperatorButtonListener(buttonPower, POWER, "^");
    }

    private void setOperatorButtonListener(MaterialButton button, char operator, String symbol) {
        button.setOnClickListener(view -> {
            if (inputDisplay.getText().length() > 0) {
                if (Double.isNaN(firstValue)) {
                    firstValue = Double.parseDouble(inputDisplay.getText().toString());
                } else if (currentSymbol != '0') {
                    performCalculation();
                }
                currentSymbol = operator;
                outputDisplay.setText(decimalFormat.format(firstValue) + " " + symbol);
                inputDisplay.setText("");
            }
        });
    }

    private void setupScientificButtons() {
        buttonSin.setOnClickListener(v -> calculateTrigFunction("sin"));
        buttonCos.setOnClickListener(v -> calculateTrigFunction("cos"));
        buttonTan.setOnClickListener(v -> calculateTrigFunction("tan"));
        buttonLog.setOnClickListener(v -> calculateScientificFunction("log", value -> Math.log10(value)));
        buttonLn.setOnClickListener(v -> showMembers()); // Changed to show popup instead of ln function
        buttonSqrt.setOnClickListener(v -> calculateScientificFunction("√", value -> Math.sqrt(value)));
        buttonFact.setOnClickListener(v -> calculateFactorial());
    }

    private void showMembers() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);

        View popupView = LayoutInflater.from(this).inflate(R.layout.popup_newjeans, null);
        dialog.setContentView(popupView);

        Button btnClose = popupView.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.setLayout(
                    (int)(getResources().getDisplayMetrics().widthPixels * 0.8),
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }

    private void calculateTrigFunction(String function) {
        try {
            double value = Double.parseDouble(inputDisplay.getText().toString());
            double radians = isDegreeMode ? Math.toRadians(value) : value;
            double result = 0;

            switch (function) {
                case "sin":
                    result = Math.sin(radians);
                    break;
                case "cos":
                    result = Math.cos(radians);
                    break;
                case "tan":
                    result = Math.tan(radians);
                    if (Math.abs(Math.cos(radians)) < 1E-10) {
                        outputDisplay.setText("Undefined");
                        return;
                    }
                    break;
            }

            inputDisplay.setText(decimalFormat.format(result));
            outputDisplay.setText(function + "(" + value + (isDegreeMode ? "°" : "") + ")");
            firstValue = result;
        } catch (Exception e) {
            outputDisplay.setText("Error");
        }
    }

    private void calculateScientificFunction(String functionName, ScientificFunction function) {
        try {
            double value = Double.parseDouble(inputDisplay.getText().toString());

            if (functionName.equals("√") && value < 0) {
                outputDisplay.setText("Invalid input");
                return;
            }
            if ((functionName.equals("log") || functionName.equals("ln")) && value <= 0) {
                outputDisplay.setText("Invalid input");
                return;
            }

            double result = function.calculate(value);
            inputDisplay.setText(decimalFormat.format(result));
            outputDisplay.setText(functionName + "(" + value + ")");
            firstValue = result;
        } catch (Exception e) {
            outputDisplay.setText("Error");
        }
    }

    private void calculateFactorial() {
        try {
            int value = Integer.parseInt(inputDisplay.getText().toString());
            if (value < 0) {
                outputDisplay.setText("Invalid input");
                return;
            }
            if (value > 20) {
                outputDisplay.setText("Value too large");
                return;
            }

            long result = 1;
            for (int i = 2; i <= value; i++) {
                result *= i;
            }

            inputDisplay.setText(String.valueOf(result));
            outputDisplay.setText(value + "!");
            firstValue = result;
        } catch (Exception e) {
            outputDisplay.setText("Error");
        }
    }

    private void setupOtherButtons() {
        buttonDot.setOnClickListener(view -> {
            if (!inputDisplay.getText().toString().contains(".")) {
                inputDisplay.append(".");
            }
        });

        buttonClear.setOnClickListener(view -> clear());
        buttonOFF.setOnClickListener(view -> finish());
        buttonEqual.setOnClickListener(view -> calculateResult());
    }

    private void clear() {
        inputDisplay.setText("");
        outputDisplay.setText("");
        firstValue = Double.NaN;
        secondValue = 0;
        currentSymbol = '0';
    }

    private void calculateResult() {
        if (inputDisplay.getText().length() > 0) {
            if (Double.isNaN(firstValue)) {
                firstValue = Double.parseDouble(inputDisplay.getText().toString());
                outputDisplay.setText(decimalFormat.format(firstValue));
            } else {
                performCalculation();
            }
            inputDisplay.setText("");
            currentSymbol = '0';
        }
    }

    private void performCalculation() {
        try {
            if (inputDisplay.getText().length() > 0) {
                secondValue = Double.parseDouble(inputDisplay.getText().toString());

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
                            outputDisplay.setText("Cannot divide by zero");
                            firstValue = Double.NaN;
                            return;
                        }
                        break;
                    case PERCENT:
                        firstValue = (firstValue * secondValue) / 100;
                        break;
                    case POWER:
                        firstValue = Math.pow(firstValue, secondValue);
                        break;
                }
                outputDisplay.setText(decimalFormat.format(firstValue));
            }
        } catch (Exception e) {
            outputDisplay.setText("Error");
            firstValue = Double.NaN;
        }
    }

    interface ScientificFunction {
        double calculate(double value);
    }
}