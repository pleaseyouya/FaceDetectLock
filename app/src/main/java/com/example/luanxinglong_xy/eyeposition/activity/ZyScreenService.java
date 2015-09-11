package com.example.luanxinglong_xy.eyeposition.activity;
import android.app.KeyguardManager;  
import android.app.Service;  
import android.app.KeyguardManager.KeyguardLock;  
import android.content.BroadcastReceiver;  
import android.content.Context;  
import android.content.Intent;  
import android.content.IntentFilter;  
import android.os.IBinder;  
import android.util.Log;  
public class ZyScreenService extends Service {  
    KeyguardManager mKeyguardManager = null;  
    private KeyguardLock mKeyguardLock = null;  
    @Override  
    public IBinder onBind(Intent arg0) {  
        // TODO Auto-generated method stub  
        return null;  
    }  
    @Override  
      public void onCreate()  
      {  
        // TODO Auto-generated method stub  
            
        super.onCreate();  
      }  
     @Override  
      public void onStart(Intent intent, int startId)  
      {  
        // TODO Auto-generated method stub  
         Log.i("in Service","in Service");  
            mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);  
            mKeyguardLock = mKeyguardManager.newKeyguardLock("");  
            mKeyguardLock.disableKeyguard();  
            Log.i("in Service1","in Service1");  
            BroadcastReceiver mMasterResetReciever = new BroadcastReceiver() {  
                public void onReceive(Context context, Intent intent) {  
                    try {  
                        Intent i = new Intent();  
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
                        i.setClass(context, CameraActivity.class);
                        context.startActivity(i);  
                        // finish();  
                        Log.i("BroadcastReceiver","BroadcastReceiver");  
                    } catch (Exception e) {  
                        Log.i("Output:", e.toString());  
                    }  
                }  
            };  
            /*BroadcastReceiver mMasterResetReciever= new BroadcastReceiver() {
            	public void onReceive(Context context, Intent intent){ 
            	try{
            	Intent i = new Intent();
            	i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            	i.setClass(context, ZyScreenSaver.class);
            	context.startActivity(i);
            	//finish();

            	}catch(Exception e){
            	Log.i("Output:", e.toString());
            	} 
            	}
            	};*/
            registerReceiver(mMasterResetReciever, new IntentFilter(  
                    Intent.ACTION_SCREEN_OFF));  
      }  
      
}  