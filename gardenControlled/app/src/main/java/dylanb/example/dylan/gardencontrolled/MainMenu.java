package dylanb.example.dylan.gardencontrolled;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        final TextView motorInfo=(TextView) findViewById(R.id.txtMotorInfo);
        final ImageButton btnMotorPower=(ImageButton) findViewById(R.id.btnMotorPower);

        getPinStatus(btnMotorPower, motorInfo, 17);

    }
    public void btnPowerClick(View view)  {
        String txtBoxTimeVal   = ((EditText)findViewById(R.id.txtBoxOnFor)).getText().toString();
     //getting buttton for interaction
        int txtBoxTimeValue=0;
        final TextView motorInfo=(TextView) findViewById(R.id.txtMotorInfo);
        final ImageButton btnMotorPower=(ImageButton) findViewById(R.id.btnMotorPower);
    //if button is null just Inverse  power state
        if (txtBoxTimeVal.trim().length() >0) {
            try {
                txtBoxTimeValue = Integer.parseInt(txtBoxTimeVal);
                 if(txtBoxTimeValue > 0) {
                    try {
                        powerGpioPinFor(btnMotorPower, motorInfo, txtBoxTimeValue, 17);
                        final int updateUiAfterSeconds=(txtBoxTimeValue+1)*1000;
                       //run on Ui thread so as Ui update can take place
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        getPinStatus(btnMotorPower, motorInfo, 17);
                                    }
                                }, updateUiAfterSeconds);
//stuff that updates ui

                            }
                        });



                    }
                    catch (JSONException e) {
                        Log.d("app", "Json Error");
                    }
                }

                else if (txtBoxTimeValue ==0){

                    try {
                        inversePinStatus(btnMotorPower,motorInfo,17);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            catch (Exception ex){

            }
        }
        else{
            try {
                inversePinStatus(btnMotorPower,motorInfo,17);
            }
            catch (JSONException e) {
                e.printStackTrace();
                Log.d("ferf","seem null");
            }
        }



    }

    //func used insed other function
    public void changePinInfoStatus(ImageButton btnMotorPower, TextView motorInfo, JSONObject res){
        try {
            if(res.getInt("state")==1){
                motorInfo.setText("on");
                btnMotorPower.setImageResource(R.drawable.onpower);

            }
            else if(res.getInt("state")==0){
                motorInfo.setText("off");
                btnMotorPower.setImageResource(R.drawable.offpower34);

            }

            else{
                motorInfo.setText("do not know ?");
                btnMotorPower.setImageResource(R.drawable.question);
            }
        } catch (JSONException e) {
            motorInfo.setText("Json Err ?");
            btnMotorPower.setImageResource(R.drawable.jsonerror);
        }


    }
    /*
    public void changeGetRequestMotorInfoStatus(ImageButton btnMotorPower,TextView motorInfo,String resp) {

        if (resp.equals("1")) {
            motorInfo.setText("on");
            btnMotorPower.setImageResource(R.drawable.onpower);

        }
        else if (resp.equals("0")) {
            motorInfo.setText("off");
            btnMotorPower.setImageResource(R.drawable.offpower34);

        }
        else{
            motorInfo.setText("do not know ?");
            btnMotorPower.setImageResource(R.drawable.question);
        }
    }

*/



    public void powerGpioPinFor(final ImageButton btnMotorPower, final TextView motorInfo, int timeOn, int pin)throws JSONException{
        //make Ui display wait
        motorInfo.setText("...");
        btnMotorPower.setImageResource(R.drawable.waitingicon);
        //DO api call
        String url = "http://dietpi.zte.com.cn:5000/turnonmotor";

        JSONObject JSONToSent = new JSONObject();
        JSONToSent.put("timeon",timeOn );
        JSONToSent.put("pin",pin );

     //loging
        Log.d("Try connection", url);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST,url,JSONToSent,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        changePinInfoStatus(btnMotorPower, motorInfo, response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        motorInfo.setText("net error");
                        btnMotorPower.setImageResource(R.drawable.networkerror);
                    }
                });


// Access the RequestQueue through your singleton class.
        RaspiApi.getInstance(this).addToRequestQueue(jsObjRequest);
    }

    public void inversePinStatus(final ImageButton btnMotorPower, final TextView motorInfo,int pin)throws JSONException{
     //make Ui display wait
        motorInfo.setText("...");
        btnMotorPower.setImageResource(R.drawable.waitingicon);
     //DO api call
        String url = "http://dietpi.zte.com.cn:5000/switchGpioState";

        JSONObject JSONToSent = new JSONObject();
        JSONToSent.put("pin",pin );
    //loging
        Log.d("Try connection",url);


        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST,url,JSONToSent,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        changePinInfoStatus(btnMotorPower, motorInfo, response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        motorInfo.setText("net error");
                        btnMotorPower.setImageResource(R.drawable.networkerror);
                    }
                });


    // Access the RequestQueue through your singleton class.
        RaspiApi.getInstance(this).addToRequestQueue(jsObjRequest);
    }


//get pin status
    public void getPinStatus(final ImageButton btnMotorPower,final TextView motorInfo,final int pin) {
    //make Ui display wait


        motorInfo.setText("...");
        btnMotorPower.setImageResource(R.drawable.waitingicon);
     //DO api call

        String url = "http://dietpi.zte.com.cn:5000/getpinstate?pin=" +Integer.toString(pin);
     //loging
        Log.d("Try connection",url);
     // Request a string response from the provided URL.
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        changePinInfoStatus(btnMotorPower, motorInfo, response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        motorInfo.setText("net error");
                        btnMotorPower.setImageResource(R.drawable.networkerror);
                    }
                });


     // Access the RequestQueue through your singleton class.
        RaspiApi.getInstance(this).addToRequestQueue(jsObjRequest);

    }


// Access the RequestQueue through your singleton class.




}
