package com.example.weatherapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private ImageView button, weatherImage;
    private ConstraintLayout parent;
    private ArrayList<Weather> weathers = new ArrayList<>();
    private ImageView image;
    private TextInputEditText editText;
    private TextView resultCurrTemp, resultMinTemp, resultMaxTemp, resultWeather, resultDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.btn);
        image = findViewById(R.id.image);
        editText = findViewById(R.id.city);
        resultCurrTemp = findViewById(R.id.resultCurrTemp);
        resultMinTemp = findViewById(R.id.resultMinTemp);
        resultMaxTemp = findViewById(R.id.resultMaxTemp);
        resultWeather = findViewById(R.id.resultCondition);
        resultDesc = findViewById(R.id.Desc);
        parent = findViewById(R.id.displayLayout);
        weatherImage = findViewById(R.id.weatherImage);

        weathers.add(new Weather("Drizzle", "https://images.unsplash.com/photo-1524693788736-5e6f1716beb3?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=500&q=60"));
        weathers.add(new Weather("Rain", "https://images.unsplash.com/photo-1493503979513-c7463d52a528?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=500&q=60"));
        weathers.add(new Weather("Snow", "https://images.unsplash.com/photo-1514632595-4944383f2737?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=500&q=60"));
        weathers.add(new Weather("Mist", "https://images.unsplash.com/photo-1485236715568-ddc5ee6ca227?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=800&q=60"));
        weathers.add(new Weather("Smoke", "https://images.unsplash.com/photo-1510596713412-56030de252c8?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=500&q=60"));
        weathers.add(new Weather("Dust", "https://images.unsplash.com/photo-1520632587893-f4e855502ca3?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=500&q=60"));
        weathers.add(new Weather("Fog", "https://images.unsplash.com/photo-1510596713412-56030de252c8?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=800&q=60"));
        weathers.add(new Weather("Sand", "https://images.unsplash.com/photo-1520632587893-f4e855502ca3?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=500&q=60"));
        weathers.add(new Weather("Ash", "https://images.unsplash.com/photo-1510596713412-56030de252c8?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=800&q=60"));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(editText.getWindowToken(),0);
                parent.setAlpha(0);
                resultCurrTemp.setText("");
                resultMinTemp.setText("");
                resultMaxTemp.setText("");
                resultWeather.setText("");
                resultDesc.setText("");
                weatherImage.setImageDrawable(null);
                String input = editText.getText().toString();
                if (!(input.isEmpty())) {
                    String formatted = "";
                    try {
                        formatted = input.substring(0, 1).toUpperCase() + input.substring(1);
                        formatted = URLEncoder.encode(formatted,"UTF-8");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    JsonLoader jsonLoader = new JsonLoader();

                    try {
                        jsonLoader.execute("https://api.openweathermap.org/data/2.5/weather?q=" + formatted + "&APPID=f178f2a2aac6f85f1241053f90a2d4f8");
                    } catch (Exception e) {
                        errorSnack();
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public String tempC(String message) {
        float result = (Float.parseFloat(message) - 273.15f);
        return (Math.round(result) + "°");
    }

    private class JsonLoader extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            Log.d("TAG", "doInBackground: ");
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream is = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(is);
                int data = reader.read();
                while (data != -1) {
                    char c = (char) data;
                    result.append(c);
                    data = reader.read();
                }
                return result.toString();
            } catch (Exception e) {
                e.printStackTrace();
                errorSnack();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            String message="";

            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray weather = jsonObject.getJSONArray("weather");
                JSONObject main = jsonObject.getJSONObject("main");
                for (int i = 0; i < weather.length(); i++) {
                    JSONObject jsonWeather = weather.getJSONObject(i);
                    message = jsonWeather.getString("main");
                    if (!(message.equals(""))) {
                        int flag = 0;
                        for (Weather tempWeather : weathers) {
                            if (message.equals(tempWeather.getWeatherName())) {
                                Glide.with(MainActivity.this).asBitmap().load(tempWeather.getUrl()).into(image);
                                flag = 1;
                                break;
                            }
                        }
                        if (flag == 0) {
                            Glide.with(MainActivity.this).asBitmap().load("https://images.unsplash.com/photo-1530908295418-a12e326966ba?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=500&q=60").into(image);
                        }
                        resultWeather.setText(message);
                    }
                    message = "";
                    message = jsonWeather.getString("description");
                    if (!(message.equals(""))) {
                        resultDesc.setText(message);
                    }
                    message = "";
                    message = main.getString("temp");
                    if (!(message.equals(""))) {
                        resultCurrTemp.setText(tempC(message));
                    }
                    message = "";
                    message = main.getString("temp_min");
                    if (!(message.equals(""))) {
                        resultMinTemp.setText(tempC(message));
                    }
                    message = "";
                    message = main.getString("temp_max");
                    if (!(message.equals(""))) {
                        resultMaxTemp.setText(tempC(message));
                    }
                    message = "";
                    message = jsonWeather.getString("icon");
                    if (!(message.equals(""))) {
                        Glide.with(MainActivity.this).asBitmap().load("https://openweathermap.org/img/wn/" + message + "@2x.png").into(weatherImage);
                    }
                    parent.animate().alpha(1).setDuration(3000);
                }

            } catch (Exception e) {
                errorSnack();
                e.printStackTrace();
            }
        }
    }

    private void errorSnack() {
        final Snackbar snackbar = Snackbar.make(parent,"We were unable to process that request", BaseTransientBottomBar.LENGTH_LONG);
        snackbar.setAction("CLOSE", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

}