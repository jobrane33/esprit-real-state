package com.example.app;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class UsserProfileActivit extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usser_profile);

        // Load fragment only once
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new AccountFragment())
                    .commit();
        }
    }
}
