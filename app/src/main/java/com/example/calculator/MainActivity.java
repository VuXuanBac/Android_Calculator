package com.example.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Stack;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tracer;
    private TextView current;
    private boolean is_equal_click;
    private StringBuilder string_tracer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tracer = findViewById(R.id.textTrace);
        current = findViewById(R.id.textCurrent);
        is_equal_click = false;
        string_tracer = new StringBuilder();
        setListenerForViews();
    }

    private void setListenerForViews() {
        setListener(R.id.buttonPlus);
        setListener(R.id.buttonMinus);
        setListener(R.id.buttonMultiply);
        setListener(R.id.buttonDivide);
        setListener(R.id.buttonClearAll);
        setListener(R.id.buttonClearEntry);
        setListener(R.id.buttonBackSpace);
        setListener(R.id.button0);
        setListener(R.id.button1);
        setListener(R.id.button2);
        setListener(R.id.button3);
        setListener(R.id.button4);
        setListener(R.id.button5);
        setListener(R.id.button6);
        setListener(R.id.button7);
        setListener(R.id.button8);
        setListener(R.id.button9);
        setListener(R.id.buttonEqual);
        setListener(R.id.buttonDot);
    }

    private void setListener(int id){
        View view = findViewById(id);
        view.setOnClickListener(this);
    }

    private void numberButtonOnClick(Button button){
        int number = 0;
        try {
            number = Integer.parseInt(button.getText().toString());
        }
        catch (Exception e){
            return;
        }
        if(current.length() == 1 && current.getText().charAt(0) == '0')
            current.setText(button.getText());
        else {
            if(is_equal_click){
                current.setText("");
            }
            current.append(button.getText());
        }
    }

    private void operatorButtonOnClick(Button button){
        int tracerlen = string_tracer.length();
        if(current.length() > 0){
            string_tracer.append(current.getText());
            current.setText("");
            string_tracer.append(button.getText());
        }
        else if(tracerlen > 0){
            string_tracer.setCharAt(tracerlen - 1, button.getText().charAt(0));
        }

        tracer.setText(string_tracer);
    }

    private void equalButtonOnClick(){
        if(current.length() > 0){
            string_tracer.append(current.getText());
            tracer.setText(string_tracer);
        }
        else{
            string_tracer.deleteCharAt(string_tracer.length() - 1);
            tracer.setText(string_tracer);
        }
        current.setText(evaluate(string_tracer.toString()));
        is_equal_click = true;
    }

    private void backspaceButtonClick(){
        int length = current.length();
        if(length > 1)
            current.setText(current.getText().subSequence(0, length - 1));
        else if(length == 1)
            current.setText("0");
    }

    private void clearAllButtonOnClick(){
        current.setText("");
        string_tracer.setLength(0);
        tracer.setText(string_tracer);
    }

    private void clearEntryButtonOnClick(){
        current.setText("0");
    }

    private void dotButtonOnClick(){
        is_equal_click = false;
        if(current.getText().toString().indexOf(".") < 0) {
            if(current.length() == 0)
                current.append("0");
            current.append(".");
        }
    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(is_equal_click){
            string_tracer.setLength(0);
            tracer.setText(string_tracer);
        }
        switch(id){
            case R.id.buttonPlus:
            case R.id.buttonMinus:
            case R.id.buttonMultiply:
            case R.id.buttonDivide:
                operatorButtonOnClick((Button)view);
                break;
            case R.id.buttonBackSpace:
                backspaceButtonClick();
                break;
            case R.id.buttonClearEntry:
                clearEntryButtonOnClick();
                break;
            case R.id.buttonClearAll:
                clearAllButtonOnClick();
                break;
            case R.id.buttonDot:
                dotButtonOnClick();
                break;
            case R.id.buttonEqual:
                equalButtonOnClick();
                break;
            default:
                numberButtonOnClick((Button)view);
        }
        if(id != R.id.buttonEqual)
            is_equal_click = false;
    }

    // Calculate a 2 operands expression
    // Throw exception if divide by zero or operator not support
    private float calculate(float f, float s, char operator) throws ArithmeticException{
        switch (operator){
            case '+':
                return f + s;
            case '-':
                return f - s;
            case '*':
                return f * s;
            case '/':
                if(s != 0)
                    return f / s;
                throw new ArithmeticException("Divide by zero");
            default:
                throw new ArithmeticException("Not support operator " + operator);
        }
    }

    private boolean isNumeric(char ch){
        return ch == '.' || (ch >= '0' && ch <= '9');
    }

    private boolean isOperator(char ch){
        return (ch == '+' || ch == '-' || ch == '*' || ch == '/');
    }

    // Evaluate a postfix expression
    private float evaluatePostfix(ArrayList<String> expression) throws Exception {
        Stack<Float> stack = new Stack<Float>();
        for(String str: expression){
            if(isOperator(str.charAt(0))){
                try {
                    float f = stack.pop();
                    float s = stack.pop();
                    stack.push(calculate(s, f, str.charAt(0)));
                }catch (Exception e){
                    throw e;
                }
            }
            else{
                stack.push(Float.parseFloat(str));
            }
        }
        return stack.pop();
    }

    // The operator <top> should be pop from stack or not.
    // Use in convertToPostfix algorithm
    private boolean isPopFromStack(char top, char ch) {
        if(getPriority(top) < getPriority(ch))
            return false;
        else if(top == ch && top == '^') // right order
            return false;
        return true;
    }

    // Get operator priority
    private int getPriority(char operator){
        if(operator == '+' || operator == '-')
            return 1;
        else if(operator == '*' || operator == '/')
            return 2;
        return 0;
    }

    // Convert infix expression to postfix expression
    private ArrayList<String> convertToPostfix(String expression){
        ArrayList<String> postfix = new ArrayList<String>();
        int len = expression.length();
        Stack<Character> operators = new Stack<Character>();
        char ch;
        for(int i = 0; i < len; ++i){
            ch = expression.charAt(i);
            if(isOperator(ch)){
                while(!operators.empty()){
                    if(isPopFromStack(operators.peek(), ch))
                        postfix.add(operators.pop().toString());
                    else
                        break;
                }
                operators.push(ch);
            }
            else{
                int start = i;
                while(isNumeric(expression.charAt(i))){
                    ++i;
                    if(i >= len)
                        break;
                }
                if(i == start)
                    throw new ArithmeticException("Invalid Expression");
                postfix.add(expression.substring(start, i));
                i--;
            }
        }
        while(!operators.empty())
            postfix.add(operators.pop().toString());
        return postfix;
    }

    // Evaluate an infix expression
    // return result if success and message if fail
    private String evaluate(String expression){
        try{
            return Float.toString(evaluatePostfix(convertToPostfix(expression)));
        }catch(Exception e){
            return e.getMessage();
        }
    }
}