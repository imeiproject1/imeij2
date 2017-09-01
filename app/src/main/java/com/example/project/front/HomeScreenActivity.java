package com.example.project.front;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

public class HomeScreenActivity extends AppCompatActivity implements View.OnClickListener,NavigationView.OnNavigationItemSelectedListener {

    private Button scanButton;
    private TextView format, content;
    EditText IMEIEntered;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen);
        IMEIEntered = (EditText)findViewById(R.id.etIMEI);

        scanButton = (Button) findViewById(R.id.scanButton);
        format = (TextView) findViewById(R.id.formatText);
        content = (TextView) findViewById(R.id.contentText);

        scanButton.setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.my_imei) {
            // Handle the camera action
            Intent intent = new Intent(this, myimei.class);
            startActivity(intent);

        } else if (id == R.id.history) {
            Intent intent = new Intent(this, history.class);
            startActivity(intent);

        } else if (id == R.id.about) {
            Intent intent = new Intent(this, about.class);
            startActivity(intent);

        }



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();
            format.setText("FORMAT: " + scanFormat);
            content.setText("CONTENT: " + scanContent);
        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scanButton: IntentIntegrator scanIntegrator = new IntentIntegrator(this);
                scanIntegrator.initiateScan();
        }
    }
    public void OnButtonClick(View view) throws ExecutionException, InterruptedException {
        String imei_number = IMEIEntered.getText().toString();
        String tac_number = imei_number.substring(0,8);
        String password = "";
        String type = "login";
        BackgroundWorker backgroundWorker = new BackgroundWorker(this,HomeScreenActivity.this);
        backgroundWorker.execute(type,imei_number,tac_number,password);
    }
    public class BackgroundWorker extends AsyncTask<String,Void,String> {
        Context context;
        AlertDialog alertDialog;
        private Activity activity;
        BackgroundWorker (Context ctx,Activity activity) {
            context = ctx;
            this.activity = activity;
        }
        @Override
        protected String doInBackground(String... params) {
            String retval = "";
            String type = params[0];
            String login_url = "http://192.168.110.167/IMEIjasoos/login.php";
            if(type.equals("login")) {
                try {

                    String imei_code = params[1];
                    String tac_code = params[2];
                    //String password = params[2];
                    URL url = new URL(login_url);
                    Log.d("mydebug","Working till here");
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    //Log.d("mydebug",Integer.toString(httpURLConnection.getResponseCode()));
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    String post_data = URLEncoder.encode("imei_n","UTF-8")+"="+URLEncoder.encode(imei_code,"UTF-8")+"&"+URLEncoder.encode("tac_n","UTF-8")+"="+URLEncoder.encode(tac_code,"UTF-8");
                    bufferedWriter.write(post_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                    String result="";
                    String line="";
                    while((line = bufferedReader.readLine())!= null) {
                        result += line;
                    }
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    if(result.equals("IMEI is valid")){
                        int sumval=0;
                        String even_nu="";
                        for(int i = 0; i<14;i++){
                            //Log.d("mydebug", String.valueOf(imei_code.charAt(i)));
                            if(i%2==0)
                                sumval += Character.getNumericValue(imei_code.charAt(i));
                            else {
                                int temp = Character.getNumericValue(imei_code.charAt(i)) * 2;
                                even_nu += temp;
                            }
                        }
                        Log.d("mydebug",Integer.toString(sumval));
                        Log.d("mydebug",even_nu);
                        for(int i = 0; i<even_nu.length(); i++) {
                            sumval += (Character.getNumericValue(even_nu.charAt(i)));
                        }
                        Log.d("mydebug",Integer.toString(sumval));
                        int t2 = sumval%10;
                        if(t2>0)
                            t2=10-t2;
                        //  Log.d("mydebug",Integer.toString(t2));

                        //Log.d("mydebug",Integer.toString(Character.getNumericValue(imei_code.charAt(14))));

                        if(t2==Character.getNumericValue(imei_code.charAt(14)))
                            result = "IMEI is Valid";
                        else
                            result = "IMEI is Invalid";




                    }

                    return result;

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return retval;
        }

        @Override
        protected void onPreExecute() {
            alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle("IMEI Status Info");
        }

        @Override
        protected void onPostExecute(String result) {


            alertDialog.setMessage(result);
            alertDialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

}
