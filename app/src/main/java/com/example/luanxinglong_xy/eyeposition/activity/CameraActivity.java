package com.example.luanxinglong_xy.eyeposition.activity;

import com.example.luanxinglong_xy.eyeposition.R;
import com.example.luanxinglong_xy.eyeposition.camera.CameraInterface;
import com.example.luanxinglong_xy.eyeposition.camera.CameraInterface.CamOpenOverCallback;
import com.example.luanxinglong_xy.eyeposition.camera.preview.CameraSurfaceView;
import com.example.luanxinglong_xy.eyeposition.mode.GoogleFaceDetect;
import com.example.luanxinglong_xy.eyeposition.ui.DialogEvent;
import com.example.luanxinglong_xy.eyeposition.ui.FaceView;
import com.example.luanxinglong_xy.eyeposition.ui.MyDialog;
import com.example.luanxinglong_xy.eyeposition.util.Analyze;
import com.example.luanxinglong_xy.eyeposition.util.DisplayUtil;
import com.example.luanxinglong_xy.eyeposition.util.EventUtil;
import com.example.luanxinglong_xy.eyeposition.util.Timer;
import com.example.luanxinglong_xy.eyeposition.util.cluster.Point;
import com.example.luanxinglong_xy.eyeposition.util.cluster.PointList;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Face;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.Menu;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Vector;


public class CameraActivity extends Activity implements DialogEvent{

    private PowerManager.WakeLock mWakeLock;

	private static final String TAG = "yanzi";
	CameraSurfaceView surfaceView = null;
//	ImageButton shutterBtn;
//	ImageButton switchBtn;
	FaceView faceView;
	float previewRate = -1f;
	private MainHandler mMainHandler = null;
	GoogleFaceDetect googleFaceDetect = null;

    Vector<Float> xList;
    Vector<Float> yList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_camera);

        PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);
        mWakeLock = pm.newWakeLock(
                PowerManager.ACQUIRE_CAUSES_WAKEUP |
                        PowerManager.SCREEN_DIM_WAKE_LOCK |
                        PowerManager.ON_AFTER_RELEASE,"SimpleTimer");

		initUI();
//		initViewParams();
		mMainHandler = new MainHandler();
		googleFaceDetect = new GoogleFaceDetect(getApplicationContext(), mMainHandler);


//		shutterBtn.setOnClickListener(new BtnListeners());
//		switchBtn.setOnClickListener(new BtnListeners());

        // 新建定时器

        xList = new Vector<Float>();
        yList = new Vector<Float>();
        MyDialog.dialog(this, R.mipmap.ic_launcher, "人眼识别?", this);
//		mMainHandler.sendEmptyMessageDelayed(EventUtil.CAMERA_HAS_STARTED_PREVIEW, 1500);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.camera, menu);
		return true;
	}

	private void initUI(){
		surfaceView = (CameraSurfaceView)findViewById(R.id.camera_surfaceview);
//		shutterBtn = (ImageButton)findViewById(R.id.btn_shutter);
//		switchBtn = (ImageButton)findViewById(R.id.btn_switch);
		faceView = (FaceView)findViewById(R.id.face_view);
        faceView.setCameraActivity(this);
	}
//	private void initViewParams(){
//		LayoutParams params = surfaceView.getLayoutParams();
//		Point p = DisplayUtil.getScreenMetrics(this);
//		params.width = p.x;
//		params.height = p.y;
//		previewRate = DisplayUtil.getScreenRate(this); //默认全屏的比例预览
//		surfaceView.setLayoutParams(params);
//	}

	private class BtnListeners implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
				case R.id.btn_shutter:
					takePicture();
					break;
				case R.id.btn_switch:
					switchCamera();
					break;
				default:break;
			}
		}

	}

    private boolean detectedStarted;

    public boolean isDetectedStarted() {
        return detectedStarted;
    }

    public void setDetectedStarted(boolean detectedStarted) {
        this.detectedStarted = detectedStarted;
    }

	private  class MainHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what){
				case EventUtil.UPDATE_FACE_RECT:
//                    if (faceView.isTimerStop()) {
//                        faceView.setDetected(false);
//                        setDetectedStarted(false);
//                        return ;
//                    } else if (!isDetectedStarted()){
//                        setDetectedStarted(true);
//                        faceView.setDetected(true);
//                    }
					Face[] faces = (Face[]) msg.obj;
//                    if (faces != null) {
                        faceView.setFaces(faces);
//                    }
					break;
				case EventUtil.CAMERA_HAS_STARTED_PREVIEW:
					startGoogleFaceDetect();
					break;
                case EventUtil.STOP_DETECT:
                    clearFaces();
                    stopGoogleFaceDetect();
                    break;
			}
			super.handleMessage(msg);
		}
	}

	private void takePicture(){
		CameraInterface.getInstance().doTakePicture();
		mMainHandler.sendEmptyMessageDelayed(EventUtil.CAMERA_HAS_STARTED_PREVIEW, 1500);
	}
	private void switchCamera(){
		stopGoogleFaceDetect();
		int newId = (CameraInterface.getInstance().getCameraId() + 1)%2;
		CameraInterface.getInstance().doStopCamera();
		CameraInterface.getInstance().doOpenCamera(null, newId);
		CameraInterface.getInstance().doStartPreview(surfaceView.getSurfaceHolder(), previewRate);
		mMainHandler.sendEmptyMessageDelayed(EventUtil.CAMERA_HAS_STARTED_PREVIEW, 1500);
//		startGoogleFaceDetect();

	}
	public void startGoogleFaceDetect(){
		Camera.Parameters params = CameraInterface.getInstance().getCameraParams();
		if(params.getMaxNumDetectedFaces() > 0){
			if(faceView != null){
				faceView.clearFaces();
				faceView.setVisibility(View.VISIBLE);
			}
			CameraInterface.getInstance().getCameraDevice().setFaceDetectionListener(googleFaceDetect);
			CameraInterface.getInstance().getCameraDevice().startFaceDetection();
		}
	}
	public void stopGoogleFaceDetect(){
		Camera.Parameters params = CameraInterface.getInstance().getCameraParams();
		if(params.getMaxNumDetectedFaces() > 0){
			CameraInterface.getInstance().getCameraDevice().setFaceDetectionListener(null);
			CameraInterface.getInstance().getCameraDevice().stopFaceDetection();
//			faceView.clearFaces();
            analyze();
		}
	}

    @Override
    public void onConfirm() {
        clearPoints();

//        mMainHandler.sendEmptyMessageDelayed(EventUtil.CAMERA_HAS_STARTED_PREVIEW, 5);
        mMainHandler.sendEmptyMessage(EventUtil.CAMERA_HAS_STARTED_PREVIEW);
        mMainHandler.sendEmptyMessageDelayed(EventUtil.STOP_DETECT, 7000);
//        faceView.createBufferedWriter();
//        startGoogleFaceDetect();
//        faceView.startTimer();
//        Timer timer = new Timer(7000, this);
//        timer.execute();
    }

    public void clearPoints() {
        xList.clear();
        yList.clear();
    }

    public void addPoint(float x, float y) {
        xList.add(x);
        yList.add(y);
    }

    public void analyze() {
//        Log.e("位置", "-----------------------------1");

        writeFileToSD();

        PointList pointList = new PointList(xList, yList);

        boolean[] result = {false, false, false};
        int[] e = {5, 8, 10};
        int[] minpt = {10, 10, 15};
        boolean success = false;
        for (int i = 0; i < 3; ++i) {
            pointList.setE_Minpt(e[i], minpt[i]);
            result[i] = pointList.analyse();
            success = (success || result[i]);
        }

        long start = System.currentTimeMillis();
        boolean matchSuccess = pointList.analyse();
        long cost = System.currentTimeMillis()-start;

        Vector<Point> rawPointList = pointList.getPointList();
        Vector<List> clusterList = pointList.getClusterResult();
        Vector<Point> centerList = pointList.getCenterList();
        Vector<Integer> moveRoute = pointList.getMoveRoute();

        Log.e("Analyze Result", "原始点列表规模为："+rawPointList.size());
        Log.e("Analyze Result","聚类结果");
        int count = 0;
        int newSum = 0;
        for (List<Point> list : clusterList) {
            Log.e("Analyze Result","簇"+count+"包含 "+list.size()+" 个点");
            newSum += list.size();
            count ++;
        }
        Log.e("Analyze Result","聚类结果的点数为："+newSum);
        Log.e("Analyze Result","各个簇的中心点");
        for (Point point : centerList) {
            Log.e("Analyze Result",point.toString());
        }
        Log.e("Analyze Result","各个簇中心点所处区域的序号");
        Log.e("Analyze Result",moveRoute+"");
        String resultStr = matchSuccess?"成功":"失败";
        Log.e("Analyze Result",resultStr);



        if (success) {
//            Log.e("位置", "-----------------------------2");
            // todo 跳转到指定页面
//            Toast.makeText(this, "识别成功", Toast.LENGTH_SHORT).show();
//            MyDialog.resultDialog(this, R.mipmap.ic_launcher, "识别成功");
            MyDialog.dialog(this, "识别成功", R.raw.success, R.mipmap.win, true);

        } else {
//            Log.e("位置", "-----------------------------3");
//            MyDialog.dialog(this, R.mipmap.ic_launcher, "人眼识别?", this);
            MyDialog.dialog(this, "识别失败", R.raw.lose, R.mipmap.not_success, false);
        }
    }

    private void writeFileToSD() {
        String sdStatus = Environment.getExternalStorageState();
        if(!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
            Log.d("TestFile", "SD card is not avaiable/writeable right now.");
            return;
        }
        try {
            String pathName="/sdcard/test/";
            String fileName="file";
            File path = new File(pathName);
            File file = new File(pathName + fileName + "_" + System.currentTimeMillis() + ".txt");
            if( !path.exists()) {
                Log.d("TestFile", "Create the path:" + pathName);
                path.mkdir();
            }
            if( !file.exists()) {
                Log.d("TestFile", "Create the file:" + fileName);
                file.createNewFile();
            }
            FileOutputStream stream = new FileOutputStream(file, true);

            for (int i = 0; i < xList.size(); ++i) {
                String s = xList.get(i) + ", " + yList.get(i) + "\n";
                stream.write(s.getBytes());
            }
            stream.close();

        } catch(Exception e) {
            Log.e("TestFile", "Error on writeFilToSD.");
            e.printStackTrace();
        }
    }

    public void clearFaces() {
        faceView.clearFaces();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        mWakeLock.release();
    }
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mWakeLock.acquire();
    }
}
