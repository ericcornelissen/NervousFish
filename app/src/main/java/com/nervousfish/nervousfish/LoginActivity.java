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

        // TODO: have a real password check
        boolean wrongPassword = !mPassword.getText().toString().isEmpty();
        if (wrongPassword) {
            mError.setVisibility(View.VISIBLE);
        } else {
            mError.setVisibility(View.GONE);
            Intent k = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(k);
        }
    }
}
