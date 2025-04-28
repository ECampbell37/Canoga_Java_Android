/************************************************************
 * Name:    Elijah Campbell‑Ihim
 * Project: Canoga Java/Android
 * Class:   CMPS-366 Organization of Programming Languages
 * Date:    April 2025
 * File:    MainMenuActivity.java
 ************************************************************/

package com.example.canoga_android_elijahc;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;


/**
 * The launch screen of the app, offering options to start
 * a new game, load an existing one, or exit the application.
 */
public class MainMenuActivity extends AppCompatActivity {

    /**
     * Initializes the activity, wiring up button callbacks.
     *
     * <p><strong>Pseudocode:</strong>
     * <ol>
     *   <li>Call super.onCreate.</li>
     *   <li>Set the layout to activity_main_menu.</li>
     *   <li>Attach click listeners:
     *     <ul>
     *       <li>Start Game → BoardSizeActivity</li>
     *       <li>Load Game  → FileLoadingActivity</li>
     *       <li>Exit       → finishAffinity()</li>
     *     </ul>
     *   </li>
     * </ol>
     *
     * @param savedInstanceState Android state bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        findViewById(R.id.btnStartGame).setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, BoardSizeActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btnLoadGame).setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, FileLoadingActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btnExit).setOnClickListener(v -> finishAffinity());
    }
}
