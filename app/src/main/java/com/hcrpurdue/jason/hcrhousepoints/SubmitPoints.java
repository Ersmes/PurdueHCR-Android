package com.hcrpurdue.jason.hcrhousepoints;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Models.PointType;
import Utils.Singleton;
import Utils.SingletonInterface;

public class SubmitPoints extends AppCompatActivity {
    private Singleton singleton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_points);
        singleton = Singleton.getInstance();
        getPointTypes();
        try {
            ((ImageView)findViewById(R.id.houseLogoImageView)).setImageResource(Drawable.class.getDeclaredField(singleton.getHouse().toLowerCase()).getInt(Drawable.class.getDeclaredField(singleton.getHouse().toLowerCase())));
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Failed to load house image, please have your RHP tell Jason", Toast.LENGTH_LONG).show();
        }
    }


    private void getPointTypes() {
        singleton.getPointTypes(new SingletonInterface() {
            @Override
            public void onPointTypeComplete(List<PointType> data) {
                List<Map<String, String>> formattedPointTypes = new ArrayList<>();
                for(PointType type : data)
                {
                    Map<String, String> map = new HashMap<>();
                    map.put("text", type.getPointDescription());
                    map.put("subText", String.valueOf(type.getPointValue()) + " points");
                    formattedPointTypes.add(map);
                }
                SimpleAdapter adapter = new SimpleAdapter(SubmitPoints.this, formattedPointTypes, android.R.layout.simple_list_item_2, new String[] {"text", "subText"}, new int[] {android.R.id.text1, android.R.id.text2});
                adapter.setDropDownViewResource(android.R.layout.simple_list_item_2);
                ((Spinner)findViewById(R.id.pointTypeSpinner)).setAdapter(adapter);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
