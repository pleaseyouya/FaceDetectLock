package com.example.luanxinglong_xy.eyeposition.ui;

import com.example.luanxinglong_xy.eyeposition.R;
import com.example.luanxinglong_xy.eyeposition.activity.CameraActivity;
import com.example.luanxinglong_xy.eyeposition.camera.CameraInterface;
import com.example.luanxinglong_xy.eyeposition.util.Timer;
import com.example.luanxinglong_xy.eyeposition.util.Util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Face;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class FaceView extends ImageView {
	private static final String TAG = "YanZi";
	private Context mContext;
	private Paint mLinePaint;
	private Face[] mFaces;
	private Matrix mMatrix = new Matrix();
	private RectF mRect = new RectF();
	private Drawable mFaceIndicator = null;

    private CameraActivity cameraActivity;
    private Timer timer;
    private boolean detected = false;

    private FileOutputStream stream;
    private String pathName="/mnt/sdcard/test_";

	public FaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initPaint();
		mContext = context;
		mFaceIndicator = getResources().getDrawable(R.drawable.ic_face_find_2);
	}

    public void createBufferedWriter() {
        File file = new File(pathName + System.currentTimeMillis());
        try {
            if( !file.exists()) {
                file.createNewFile();
            }
            stream = new FileOutputStream(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setCameraActivity(CameraActivity cameraActivity) {
        this.cameraActivity = cameraActivity;
    }

    public void setDetected(boolean detected) {
        this.detected = detected;
    }

    public boolean isDetected() {
        return detected;
    }

	public void setFaces(Face[] faces){
		this.mFaces = faces;
		invalidate();
	}
	public void clearFaces(){
		mFaces = null;
//        try {
//            stream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        invalidate();
	}

    private void writeFileToSD(String s) {
        String sdStatus = Environment.getExternalStorageState();
        if(!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
            Log.d("TestFile", "SD card is not avaiable/writeable right now.");
            return;
        }
        try {
            String pathName="/sdcard/test/";
            String fileName="file.txt";
            File path = new File(pathName);
            File file = new File(pathName + fileName);
            if( !path.exists()) {
                Log.d("TestFile", "Create the path:" + pathName);
                path.mkdir();
            }
            if( !file.exists()) {
                Log.d("TestFile", "Create the file:" + fileName);
                file.createNewFile();
            }
            FileOutputStream stream = new FileOutputStream(file, true);

            byte[] buf = s.getBytes();
            stream.write(buf);

            stream.close();

        } catch(Exception e) {
            Log.e("TestFile", "Error on writeFilToSD.");
            e.printStackTrace();
        }
    }

    @Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
        Log.e("onDraw", "1");
		if(mFaces == null || mFaces.length < 1){
            Log.e("onDraw", "2");
			return;
		}

		boolean isMirror = false;
		int Id = CameraInterface.getInstance().getCameraId();
		if(Id == CameraInfo.CAMERA_FACING_BACK){
			isMirror = false; //后置Camera无需mirror
		}else if(Id == CameraInfo.CAMERA_FACING_FRONT){
			isMirror = true;  //前置Camera需要mirror
		}
		Util.prepareMatrix(mMatrix, isMirror, 90, getWidth(), getHeight());
		canvas.save();
		mMatrix.postRotate(0); //Matrix.postRotate默认是顺时针
		canvas.rotate(-0);   //Canvas.rotate()默认是逆时针

		for(int i = 0; i< mFaces.length; i++){
            Log.e("onDraw", "3");
			mRect.set(mFaces[i].rect);
			Point Leye = mFaces[i].leftEye;
			Point Reye = mFaces[i].rightEye;
			float centerX = (Leye.x + Reye.x)/2;
			float centerY = (Leye.y + Reye.y)/2;
//			float left = mRect.left;
//			float right = mRect.right;
//			float top = mRect.top;
//			float bottom = mRect.bottom;
//			float centerX = (left + right)/2;
//			float centerY = (top + bottom)/2;

//            if (!isTimerStop() && isDetected()) {
//                Log.e("onDraw", "4");
                cameraActivity.addPoint(500-centerY,
                        1000 + centerX);
//            } else {
//                Log.e("onDraw", "5");
//                clearFaces();
//                cameraActivity.stopGoogleFaceDetect();
//                break;
//            }
//            Log.e("onDraw", "6");
			Log.e("位置",(500-centerY) +", "+ (1000 + centerX));
//            try {
//                String str = (200-centerY) +", "+ (1000 + centerX) + "\n";
//                stream.write(str.getBytes());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            writeFileToSD((200-centerY) +", "+ (1000 + centerX));
            mMatrix.mapRect(mRect);
            mFaceIndicator.setBounds(Math.round(mRect.left), Math.round(mRect.top),
                    Math.round(mRect.right), Math.round(mRect.bottom));
            mFaceIndicator.draw(canvas);
//			canvas.drawRect(mRect, mLinePaint);
		}
		canvas.restore();
		super.onDraw(canvas);

        postInvalidateDelayed(1);

	}

	private void initPaint(){
		mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//		int color = Color.rgb(0, 150, 255);
		int color = Color.rgb(98, 212, 68);
//		mLinePaint.setColor(Color.RED);
		mLinePaint.setColor(color);
		mLinePaint.setStyle(Style.STROKE);
		mLinePaint.setStrokeWidth(5f);
		mLinePaint.setAlpha(180);
	}
}
