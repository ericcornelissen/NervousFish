package com.nervousfish.nervousfish;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

/**
 * Demo Login activity class
 */
@SuppressWarnings("PMD")
public class LoginActivity extends Activity {

    private final String DUMMY_PASS = "12345";

    private EditText mPassword;
    private View mError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);

        mPassword = (EditText) findViewById(R.id.password);

        View submitButton = findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mPassword.getError() == null) {
                    validateAccount();
                }
            }
        });
    }

    private void validateAccount() {
        if (mError == null) {
            mError = findViewById(R.id.error);
        }

        // TODO: have an error when empty, don't accept it
        boolean skipPassword = mPassword.getText().toString().isEmpty();
        if (skipPassword) {
            mError.setVisibility(View.GONE);
            Intent k = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(k);
        } else {
            boolean wrongPassword = !mPassword.getText().toString().equals(DUMMY_PASS);
            if (wrongPassword) {
                mError.setVisibility(View.VISIBLE);
            } else {
                mError.setVisibility(View.GONE);
                Intent k = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(k);
            }
        }
    }
}
