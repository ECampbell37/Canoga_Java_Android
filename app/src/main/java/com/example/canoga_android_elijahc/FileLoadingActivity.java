/************************************************************
 * Name:    Elijah Campbell‑Ihim
 * Project: Canoga Java/Android
 * Class:   CMPS-366 Organization of Programming Languages
 * Date:    April 2025
 * File:    FileLoadingActivity.java
 ************************************************************/

package com.example.canoga_android_elijahc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;


/**
 * Activity that displays a list of saved game files and allows
 * the user to select one to resume, or go back to the main menu.
 */
public class FileLoadingActivity extends AppCompatActivity {

    // ──────────────────────────────────────────────────────────────
    // Protected/Private Methods
    // ──────────────────────────────────────────────────────────────


    /**
     * Sets up the UI, copies default asset files if needed,
     * populates the file list, and attaches click handlers.
     *
     * <p><strong>Pseudocode:</strong>
     * <ol>
     *   <li>Call super.onCreate and set the content view.</li>
     *   <li>Initialize the ListView reference.</li>
     *   <li>Copy default .txt files from assets to internal storage.</li>
     *   <li>Load the list of .txt files into fileList.</li>
     *   <li>Create and set an ArrayAdapter for the ListView.</li>
     *   <li>Attach an OnItemClickListener to launch a game with the selected file.</li>
     *   <li>Attach a click listener to the Back button to finish the activity.</li>
     * </ol>
     *
     * @param savedInstanceState standard Android state bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_loading);

        // Bind ListView from layout
        listView = findViewById(R.id.listViewFiles);

        // Ensure the default ser1-ser5 files exist in internal storage
        copyDefaultFilesFromAssets();

        // Load files from internal storage into the list
        loadFileList();

        // Create an adapter to show filenames in the ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fileList);
        listView.setAdapter(adapter);


        // Handle clicks on any filename
        listView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            String filename = fileList.get(position);
            launchGameActivityWithFile(filename);
        });

        // Back button to return to the previous menu
        findViewById(R.id.btnBackFileLoading).setOnClickListener(v -> finish());
    }



    /**
     * Copies default serialization files (ser1.txt … ser5.txt)
     * from assets to internal storage if they do not already exist.
     *
     * <p><strong>Pseudocode:</strong>
     * <ol>
     *   <li>For each filename in the default list:</li>
     *     <ol type="a">
     *       <li>Check if file exists in getFilesDir().</li>
     *       <li>If not, open asset InputStream and FileOutputStream.</li>
     *       <li>Read/write in 1KB chunks until done.</li>
     *       <li>Close streams safely.</li>
     *     </ol>
     * </ol>
     */
    private void copyDefaultFilesFromAssets() {
        String[] defaultFiles = {"ser1.txt", "ser2.txt", "ser3.txt", "ser4.txt", "ser5.txt"};

        for (String filename : defaultFiles) {
            File destFile = new File(getFilesDir(), filename);

            // Only copy if it doesn’t already exist
            if (!destFile.exists()) {
                try (InputStream in = getAssets().open(filename);
                     OutputStream out = new FileOutputStream(destFile)) {

                    // Read+write in 1KB chunks
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) > 0) {
                        out.write(buffer, 0, length);
                    }
                } catch (IOException e) {
                    // Log failure to copy
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * Scans the app’s internal storage directory for .txt files
     * and populates fileList with their names.
     *
     * <p><strong>Pseudocode:</strong>
     * <ol>
     *   <li>Initialize fileList as new ArrayList.</li>
     *   <li>List all files in getFilesDir().</li>
     *   <li>If filename ends with ".txt", add to fileList.</li>
     * </ol>
     */
    private void loadFileList() {
        fileList = new ArrayList<>();
        File dir = getFilesDir();
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(".txt")) {
                    fileList.add(file.getName());
                }
            }
        }
    }


    /**
     * Starts GameActivity, passing the selected filename as an extra.
     * @param filename the .txt file to load
     */
    private void launchGameActivityWithFile(String filename) {
        Intent intent = new Intent(FileLoadingActivity.this, GameActivity.class);
        intent.putExtra("loadFile", filename);
        startActivity(intent);
    }


    // ──────────────────────────────────────────────────────────────
    // Private Members
    // ──────────────────────────────────────────────────────────────

    // UI component for listing files
    private ListView listView;

    // Data source for the ListView
    private ArrayList<String> fileList;
}
