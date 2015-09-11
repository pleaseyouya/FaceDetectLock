package com.example.luanxinglong_xy.eyeposition.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.media.MediaPlayer;
import android.net.Uri;

import com.example.luanxinglong_xy.eyeposition.R;
import com.example.luanxinglong_xy.eyeposition.activity.CameraActivity;

import java.io.IOException;


public class MyDialog {

	public static void dialog(Context mContext, int resId, String content,
                              final DialogEvent event) {
		Builder builder = new Builder(mContext);
		builder.setMessage(content);
		builder.setPositiveButton("确定", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
                event.onConfirm();
				}
		}).setTitle("提示").setIcon(R.mipmap.ic_launcher).show();
	}

    public static void resultDialog(Context mContext, int resId, String content) {
        Builder builder = new Builder(mContext);
        builder.setMessage(content);
        builder.setPositiveButton("确定", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setTitle("提示").setIcon(resId).show();
    }

    public static void dialog(final Context mContext, String content, int mp3, int resId,
                              final boolean success) {
        final MediaPlayer   player  =   new MediaPlayer();

        //String exPath = Environment.getExternalStorageDirectory().getAbsolutePath();

        //String  path   = exPath + "/test.mp3";// "/sdcard/test.mp3";

        Uri uri = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + mp3);
        try {
            player.setDataSource(mContext, uri);
            //player.setDataSource(R.raw.test);//path);
            player.prepare();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        player.start();

        Builder builder = new Builder(mContext);
        builder.setMessage(content);
        builder.setPositiveButton("确定", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                player.stop();
                dialog.dismiss();
                if (success) {
                    Activity activity = (Activity)mContext;
                    activity.finish();
                    return ;
                }
//                MyDialog.dialog(mContext, R.mipmap.ic_launcher, "人眼识别?", (CameraActivity)mContext);
                ((CameraActivity)mContext).onConfirm();
            }
        }).setTitle("提示").setIcon(resId).show();
    }

}
