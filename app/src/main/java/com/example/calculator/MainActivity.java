package com.example.calculator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import java.text.DecimalFormat;
import java.util.Random;

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
    private boolean isDarkMode = true;
    private boolean isFrozen = false;

    private double firstValue = Double.NaN;
    private double secondValue;
    private TextView inputDisplay, outputDisplay;
    private DecimalFormat decimalFormat;

    private MaterialButton button0, button1, button2, button3, button4, button5, button6, button7, button8, button9,
            buttonDot, buttonAdd, buttonSub, buttonMultiply, buttonDivide, buttonPercent, buttonClear,
            buttonOFF, buttonEqual, buttonSin, buttonCos, buttonTan, buttonLog, buttonLn, buttonSqrt,
            buttonPower, buttonFact;

    private Button btnDarkMode, btnLightMode;

    private int zeroClickCount = 0;
    private long lastZeroClickTime = 0;
    private static final int CLICKS_NEEDED = 5;
    private static final long CLICK_TIMEOUT = 2000;

    // For freeze mode
    private StringBuilder freezeCodeInput = new StringBuilder();
    private static final String FREEZE_CODE = "9999";
    private static final long FREEZE_DURATION = 10000; // 10 seconds in milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        decimalFormat = new DecimalFormat("#.##########");

        inputDisplay = findViewById(R.id.input);
        outputDisplay = findViewById(R.id.output);

        btnDarkMode = findViewById(R.id.btnDarkMode);
        btnLightMode = findViewById(R.id.btnLightMode);

        setDarkMode();

        btnDarkMode.setOnClickListener(v -> setDarkMode());
        btnLightMode.setOnClickListener(v -> setLightMode());

        initializeButtons();
        setupNumberButtons();
        setupOperationButtons();
        setupScientificButtons();
        setupOtherButtons();
    }

    // Add this method to handle freeze mode
    private void checkForFreezeCode(String input) {
        if (isFrozen) return;

        freezeCodeInput.append(input);

        // If the input is longer than the freeze code, reset
        if (freezeCodeInput.length() > FREEZE_CODE.length()) {
            freezeCodeInput.setLength(0);
            freezeCodeInput.append(input);
        }

        // Check if the input matches the freeze code
        if (FREEZE_CODE.equals(freezeCodeInput.toString())) {
            activateFreezeMode();
            freezeCodeInput.setLength(0);
        }
    }

    // Add this method to activate freeze mode
    private void activateFreezeMode() {
        isFrozen = true;
        Toast.makeText(this, "🧊 Frozen. You calculated too hard.", Toast.LENGTH_SHORT).show();

        // Disable all buttons
        setButtonsEnabled(false);

        // Set up handler to unfreeze after delay
        new Handler().postDelayed(() -> {
            isFrozen = false;
            setButtonsEnabled(true);
            Toast.makeText(this, "Calculator unfrozen!", Toast.LENGTH_SHORT).show();
        }, FREEZE_DURATION);
    }

    // Add this method to enable/disable all buttons
    private void setButtonsEnabled(boolean enabled) {
        MaterialButton[] allButtons = {
                button0, button1, button2, button3, button4, button5, button6, button7, button8, button9,
                buttonDot, buttonAdd, buttonSub, buttonMultiply, buttonDivide, buttonPercent, buttonClear,
                buttonOFF, buttonEqual, buttonSin, buttonCos, buttonTan, buttonLog, buttonLn, buttonSqrt,
                buttonPower, buttonFact
        };

        for (MaterialButton button : allButtons) {
            if (button != null) {
                button.setEnabled(enabled);
            }
        }

        btnDarkMode.setEnabled(enabled);
        btnLightMode.setEnabled(enabled);
    }

    private void setDarkMode() {
        if (isFrozen) return;

        isDarkMode = true;

        inputDisplay.setTextColor(ContextCompat.getColor(this, R.color.white));
        outputDisplay.setTextColor(ContextCompat.getColor(this, R.color.white));

        btnDarkMode.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.dark_button_active)));
        btnLightMode.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.light_button_inactive)));

        btnDarkMode.setTextColor(ContextCompat.getColor(this, R.color.white));
        btnLightMode.setTextColor(ContextCompat.getColor(this, R.color.black));

        updateCalculatorButtonsTheme();
    }

    private void setLightMode() {
        if (isFrozen) return;

        isDarkMode = false;

        inputDisplay.setTextColor(ContextCompat.getColor(this, R.color.white));
        outputDisplay.setTextColor(ContextCompat.getColor(this, R.color.white));

        btnDarkMode.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.dark_button_inactive)));
        btnLightMode.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.light_button_active)));

        btnDarkMode.setTextColor(ContextCompat.getColor(this, R.color.white));
        btnLightMode.setTextColor(ContextCompat.getColor(this, R.color.black));

        updateCalculatorButtonsTheme();
    }

    private void updateCalculatorButtonsTheme() {
        int whiteTextColor = ContextCompat.getColor(this, R.color.white);
        int blackTextColor = ContextCompat.getColor(this, R.color.black);
        int navyColor = ContextCompat.getColor(this, R.color.navy);
        int grayColor = ContextCompat.getColor(this, R.color.gray);
        int lightBlackColor = ContextCompat.getColor(this, R.color.dark_number_bg);
        int whiteBgColor = ContextCompat.getColor(this, R.color.light_number_bg);
        int defaultTextColor = isDarkMode ? whiteTextColor : blackTextColor;
        int numberBgColor = isDarkMode ? lightBlackColor : whiteBgColor;

        MaterialButton[] allButtons = {
                button0, button1, button2, button3, button4, button5, button6, button7, button8, button9,
                buttonDot, buttonAdd, buttonSub, buttonMultiply, buttonDivide, buttonPercent, buttonClear,
                buttonOFF, buttonEqual, buttonSin, buttonCos, buttonTan, buttonLog, buttonLn, buttonSqrt,
                buttonPower, buttonFact
        };

        for (MaterialButton button : allButtons) {
            if (button != null) {
                button.setTextColor(whiteTextColor);

                boolean isNumberOrDot = button == button0 || button == button1 || button == button2 ||
                        button == button3 || button == button4 || button == button5 ||
                        button == button6 || button == button7 || button == button8 ||
                        button == button9 || button == buttonDot;

                if (isNumberOrDot) {
                    button.setBackgroundTintList(ColorStateList.valueOf(numberBgColor));
                    button.setTextColor(defaultTextColor);
                } else {
                    int bgColor = isDarkMode ? grayColor : navyColor;
                    button.setBackgroundTintList(ColorStateList.valueOf(bgColor));
                    button.setTextColor(whiteTextColor);
                }
            }
        }
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
            if (isFrozen) return;

            checkForFreezeCode(value);

            if (inputDisplay.getText().toString().equals("0") ||
                    inputDisplay.getText().toString().equals("Error")) {
                inputDisplay.setText(value);
            } else {
                inputDisplay.append(value);
            }

            if (button == button0) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastZeroClickTime > CLICK_TIMEOUT) {
                    zeroClickCount = 1;
                } else {
                    zeroClickCount++;
                    if (zeroClickCount >= CLICKS_NEEDED) {
                        showWhackAMemberGame();
                        zeroClickCount = 0;
                    }
                }
                lastZeroClickTime = currentTime;
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
            if (isFrozen) return;

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
        buttonSin.setOnClickListener(v -> {
            if (isFrozen) return;
            calculateTrigFunction("sin");
        });
        buttonCos.setOnClickListener(v -> {
            if (isFrozen) return;
            calculateTrigFunction("cos");
        });
        buttonTan.setOnClickListener(v -> {
            if (isFrozen) return;
            calculateTrigFunction("tan");
        });
        buttonLog.setOnClickListener(v -> {
            if (isFrozen) return;
            calculateScientificFunction("log", value -> Math.log10(value));
        });
        buttonLn.setOnClickListener(v -> {
            if (isFrozen) return;
            showMembers();
        });
        buttonSqrt.setOnClickListener(v -> {
            if (isFrozen) return;
            calculateScientificFunction("√", value -> Math.sqrt(value));
        });
        buttonFact.setOnClickListener(v -> {
            if (isFrozen) return;
            calculateFactorial();
        });
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

    private void showWhackAMemberGame() {
        Dialog gameDialog = new Dialog(this);
        gameDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        gameDialog.setCancelable(true);
        gameDialog.setContentView(R.layout.game_whack_member);

        TextView scoreText = gameDialog.findViewById(R.id.scoreText);
        TextView timeText = gameDialog.findViewById(R.id.timeText);
        FrameLayout gameContainer = gameDialog.findViewById(R.id.gameContainer);
        Button btnCloseGame = gameDialog.findViewById(R.id.btnCloseGame);

        int[] memberImages = {
                R.drawable.bantilan,
                R.drawable.baturi,
                R.drawable.carranza,
                R.drawable.condes,
                R.drawable.delantar,
                R.drawable.pusa,
                R.drawable.viray
        };

        int[] score = {0};
        int[] timeLeft = {30};

        Handler handler = new Handler();
        Runnable gameRunnable = new Runnable() {
            @Override
            public void run() {
                FrameLayout circleContainer = new FrameLayout(MainActivity.this);
                FrameLayout.LayoutParams containerParams = new FrameLayout.LayoutParams(100, 100);
                circleContainer.setLayoutParams(containerParams);
                circleContainer.setBackgroundResource(R.drawable.circle);

                ImageView member = new ImageView(MainActivity.this);
                member.setLayoutParams(new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT));
                member.setScaleType(ImageView.ScaleType.CENTER_CROP);

                Random random = new Random();
                int randomImageIndex = random.nextInt(memberImages.length);
                member.setImageResource(memberImages[randomImageIndex]);

                circleContainer.addView(member);

                int maxX = gameContainer.getWidth() - 100;
                int maxY = gameContainer.getHeight() - 100;
                if (maxX > 0 && maxY > 0) {
                    circleContainer.setX(random.nextInt(maxX));
                    circleContainer.setY(random.nextInt(maxY));
                }

                circleContainer.setOnClickListener(v -> {
                    gameContainer.removeView(v);
                    score[0]++;
                    scoreText.setText("Score: " + score[0]);
                });

                gameContainer.addView(circleContainer);

                new Handler().postDelayed(() -> {
                    if (circleContainer.getParent() != null) {
                        gameContainer.removeView(circleContainer);
                    }
                }, 1000);

                if (timeLeft[0] > 0) {
                    handler.postDelayed(this, 800);
                }
            }
        };

        Runnable timerRunnable = new Runnable() {
            @Override
            public void run() {
                timeLeft[0]--;
                timeText.setText("Time: " + timeLeft[0] + "s");

                if (timeLeft[0] > 0) {
                    handler.postDelayed(this, 1000);
                } else {
                    handler.removeCallbacks(gameRunnable);
                    timeText.setText("Game Over! Final Score: " + score[0]);
                }
            }
        };

        btnCloseGame.setOnClickListener(v -> {
            handler.removeCallbacksAndMessages(null);
            gameDialog.dismiss();
        });

        handler.post(gameRunnable);
        handler.post(timerRunnable);

        gameDialog.show();

        Window window = gameDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.setLayout(
                    (int)(getResources().getDisplayMetrics().widthPixels * 0.9),
                    LinearLayout.LayoutParams.WRAP_CONTENT
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
            if (isFrozen) return;
            if (!inputDisplay.getText().toString().contains(".")) {
                inputDisplay.append(".");
            }
        });

        buttonClear.setOnClickListener(view -> {
            if (isFrozen) return;
            clear();
        });

        buttonOFF.setOnClickListener(view -> {
            if (isFrozen) return;
            finish();
        });

        buttonEqual.setOnClickListener(view -> {
            if (isFrozen) return;
            calculateResult();
        });
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