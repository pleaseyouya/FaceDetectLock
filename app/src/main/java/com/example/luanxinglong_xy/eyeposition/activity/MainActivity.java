package com.example.luanxinglong_xy.eyeposition.activity;

import android.app.Activity;  
import android.content.Intent;  
import android.os.Bundle;  
import android.util.Log;  
import android.view.Window;  
import android.view.WindowManager;

import com.example.luanxinglong_xy.eyeposition.R;

public class MainActivity extends Activity {
    /** Called when the activity is first created. */  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.main);
        Log.i("pingbao","pingbao");  
        Intent mService = new Intent(MainActivity.this,ZyScreenService.class);
        mService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
        startService(mService);  
          
          
    }  
} 