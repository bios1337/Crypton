package com.example.bios.crypton;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.bios.crypton.widget.CoinWidgetProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {

    private EditText et_email, et_password;
    private FirebaseAuth auth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(SignInActivity.this, BottomNavigationActivity.class));
            CoinWidgetProvider.sendRefreshBroadcast(getApplicationContext());
            finish();
        }

        progressBar = findViewById(R.id.progressBar);
        et_email = findViewById(R.id.email);
        et_password = findViewById(R.id.password);
    }

    public void performSignIn(View view) {
        String email = et_email.getText().toString();
        final String password = et_password.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), getString(R.string.empty_email), Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), getString(R.string.empty_password), Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        //authenticate user
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        progressBar.setVisibility(View.GONE);
                        if (!task.isSuccessful()) {
                            // there was an error
                            if (password.length() < 6) {
                                et_password.setError(getString(R.string.minimum_password));
                            } else {
                                Toast.makeText(SignInActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Intent intent = new Intent(SignInActivity.this, BottomNavigationActivity.class);
                            startActivity(intent);
                            CoinWidgetProvider.sendRefreshBroadcast(getApplicationContext());
                            finish();
                        }
                    }


                });
    }

    public void performResetPassword(View view) {
        startActivity(new Intent(SignInActivity.this, ResetPasswordActivity.class));
    }

    public void openSignUpActivity(View view) {
        startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
    }
}
