package com.hcrpurdue.jason.hcrhousepoints;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import Models.PointType;
import Utils.Singleton;
import Utils.SingletonInterface;

public class SubmitPoints extends Fragment {
    private Singleton singleton;
    private Context context;
    private AppCompatActivity activity;
    private ProgressBar progressBar;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        activity = (AppCompatActivity) getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        singleton = Singleton.getInstance(getContext());
        progressBar = activity.findViewById(R.id.navigationProgressBar);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        progressBar.setVisibility(View.VISIBLE);
        View view = inflater.inflate(R.layout.submit_points, container, false);
        singleton.getCachedData();
        // Sets the house picture
        try {
            int drawableID = getResources().getIdentifier(singleton.getHouse().toLowerCase(), "drawable", activity.getPackageName());
            ((ImageView) view.findViewById(R.id.houseLogoImageView)).setImageResource(drawableID);
        } catch (Exception e) {
            Toast.makeText(context, "Failed to load house image", Toast.LENGTH_LONG).show();
            Log.e("SubmitPoints", "Error loading house image", e);
        }
        if (singleton.getPointTypeList() != null)
            progressBar.setVisibility(View.GONE);

        Objects.requireNonNull(activity.getSupportActionBar()).setTitle("Submit Points");
        view.findViewById(R.id.submitPointButton).setOnClickListener(v -> submitPoint(view));

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            if (bundle.getBoolean("showSuccess")) {
                animateSuccess(view);
            }
        }
        getPointTypes(view);
        return view;
    }


    private void getPointTypes(View view) {
        try {
            singleton.getPointTypes(new SingletonInterface() {
                public void onPointTypeComplete(List<PointType> data) {
                    List<Map<String, String>> formattedPointTypes = new ArrayList<>();
                    for (PointType type : data) {
                        if (type.getResidentsCanSubmit()) {
                            Map<String, String> map = new HashMap<>();
                            map.put("text", type.getPointDescription());
                            map.put("subText", String.valueOf(type.getPointValue()) + " points");
                            formattedPointTypes.add(map);
                        } else
                            break;
                    }
                    SimpleAdapter adapter = new SimpleAdapter(context, formattedPointTypes, android.R.layout.simple_list_item_2, new String[]{"text", "subText"}, new int[]{android.R.id.text1, android.R.id.text2});
                    adapter.setDropDownViewResource(android.R.layout.simple_list_item_2);
                    ((Spinner) view.findViewById(R.id.pointTypeSpinner)).setAdapter(adapter);
                    if (singleton.getHouse() != null)
                        progressBar.setVisibility(View.GONE);
                }
            }, context);
        } catch (Exception e) {
            Toast.makeText(context, "Failed to load point types", Toast.LENGTH_LONG).show();
            Log.e("SubmitPoints", "Error loading point types", e);
        }
    }

    public void submitPoint(View view) {
        TextView descriptionInput = activity.findViewById(R.id.descriptionInput);
        if (TextUtils.isEmpty(descriptionInput.getText().toString()))
            Toast.makeText(context, "Description is Required", Toast.LENGTH_SHORT).show();
        else {
            progressBar.setVisibility(View.VISIBLE);
            singleton.submitPoints(((EditText) view.findViewById(R.id.descriptionInput)).getText().toString(),
                    singleton.getPointTypeList().get(((Spinner) view.findViewById(R.id.pointTypeSpinner)).getSelectedItemPosition()),
                    new SingletonInterface() {
                        @Override
                        public void onSuccess() {
                            descriptionInput.setText("");
                            progressBar.setVisibility(View.GONE);
                            animateSuccess(view);
                        }
                    });
        }
    }

    private void animateSuccess(View view) {
        LinearLayout successLayout = view.findViewById(R.id.success_layout);
        AlphaAnimation showAnim = new AlphaAnimation(0.0f, 1.0f);
        showAnim.setDuration(1000);
        showAnim.setRepeatCount(2);
        showAnim.setRepeatMode(Animation.REVERSE);
        showAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                successLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                successLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        successLayout.startAnimation(showAnim);
    }
}
