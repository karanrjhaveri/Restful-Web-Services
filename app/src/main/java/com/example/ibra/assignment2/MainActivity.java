package com.example.ibra.assignment2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    Button html, xml, json;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        html = (Button)findViewById(R.id.HTMLBtn);
        xml = (Button)findViewById(R.id.XMLBtn);
        json = (Button)findViewById(R.id.JSONBtn);

        searchByHTML();
        searchByXML();
        searchByJSON();
    }

    private void searchByHTML() {
        html.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, HTML.class);
                startActivity(i);
            }
        });
    }

    private void searchByXML() {
        xml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, XML.class);
                startActivity(i);
            }
        });
    }

    private void searchByJSON() {
        json.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, JSON.class);
                startActivity(i);
            }
        });
    }
}
