package br.com.bossini.usjt_ccp3an_bua_weather_forecast;

import android.content.Context;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView weatherRecyclerView;
    private WeatherAdapter adapter;
    private List <Weather> previsoes;
    private RequestQueue requestQueue;
    private EditText locationEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        locationEditText = findViewById(R.id.locationEditText);
        requestQueue = Volley.newRequestQueue(this);
        weatherRecyclerView =
                findViewById(R.id.weatherRecyclerView);
        previsoes =
                new ArrayList<>();
        adapter = new WeatherAdapter(this, previsoes);
        LinearLayoutManager llm =
                new LinearLayoutManager(this);
        weatherRecyclerView.setAdapter(adapter);
        weatherRecyclerView.setLayoutManager(llm);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                String cidade = locationEditText.getText().toString();
                obtemPrevisoes(cidade);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void obtemPrevisoes (String cidade){
        String url = getString(
                R.string.web_service_url,
                cidade,
                getString(R.string.api_key)
        );
        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                (resultado) -> {
                    //processar o json
                    previsoes.clear();
                    try {
                        JSONArray list = resultado.getJSONArray("list");
                        for (int i = 0; i < list.length(); i++){
                            JSONObject iesimo = list.getJSONObject(i);
                            long dt = iesimo.getLong("dt");
                            JSONObject main = iesimo.getJSONObject("main");
                            double temp_min =
                                    main.getDouble("temp_min");
                            double temp_max =
                                    main.getDouble("temp_max");
                            double humidity =
                                    main.getDouble("humidity");
                            JSONArray weather = iesimo.getJSONArray("weather");
                            String description =
                                    weather.getJSONObject(0).
                                            getString("description");
                            String icon =
                                    weather.getJSONObject(0).getString("icon");
                            Weather w =
                                    new Weather(dt, temp_min,
                                            temp_max, humidity, description, icon);
                            previsoes.add(w);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                (excecao) -> {
                    Toast.makeText(
                            this,
                            getString(R.string.connect_error),
                            Toast.LENGTH_SHORT
                    ).show();
                    excecao.printStackTrace();
                }
        );
        requestQueue.add(req);
    }
}




class WeatherViewHolder extends RecyclerView.ViewHolder{
    public ImageView conditionImageView;
    public TextView dayTextView;
    public TextView lowTextView;
    public TextView highTextView;
    public TextView humidityTextView;

    public WeatherViewHolder (View raiz){
        super (raiz);
        this.conditionImageView =
                raiz.findViewById(R.id.conditionImageView);
        this.dayTextView =
                raiz.findViewById(R.id.dayTextView);
        this.lowTextView =
                raiz.findViewById(R.id.lowTextView);
        this.highTextView =
                raiz.findViewById(R.id.highTextView);
        this.humidityTextView =
                raiz.findViewById(R.id.humidityTextView);
    }
}

class WeatherAdapter
        extends RecyclerView.Adapter <WeatherViewHolder>{

    private Context context;
    private List<Weather> previsoes;

    public WeatherAdapter(Context context, List<Weather> previsoes) {
        this.context = context;
        this.previsoes = previsoes;
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =
                LayoutInflater.from(context);
        View raiz = inflater.inflate(
                R.layout.list_item,
                parent,
                false
        );
        return new WeatherViewHolder(raiz);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
        Weather w = previsoes.get(position);
        holder.lowTextView.setText(
            context.getString(
                    R.string.low_temp,
                    w.minTemp
            )
        );
        holder.highTextView.setText(
            context.getString(
                R.string.high_temp,
                w.maxTemp
            )
        );
        holder.humidityTextView.setText(
            context.getString(
                    R.string.humidity,
                    w.humidity
            )
        );
        holder.dayTextView.setText(
                context.getString(
                        R.string.day_description,
                        w.dayOfWeek,
                        w.description
                )
        );
        Glide.with(context).load(w.iconURL).into(holder.conditionImageView);
    }

    @Override
    public int getItemCount() {
        return previsoes.size();
    }
}
