package com.example.accelerometer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;


public class SimulationView extends View implements SensorEventListener {

private SensorManager sensorManager;
private Display mDisplay;
private Bitmap mField;
private Bitmap mBasket;
private Bitmap mBitmap;
private static final int BALL_SIZE = 180;
private static final int BASKET_SIZE = 80;
private float mXOrigin = 0;
private float mYOrigin = 0;
private float mHorizontalBound = 0;
private float mVerticalBound = 0;
private float mSensorX;
private float mSensorY;
private float mSensorZ;
private long mSensorTimeStamp;
Particle mBall = new Particle();

public SimulationView(Context context) {
    super(context);
    Bitmap ball = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
    mBitmap = Bitmap.createScaledBitmap(ball, BALL_SIZE, BALL_SIZE, true);
    Bitmap basket = BitmapFactory.decodeResource(getResources(), R.drawable.basket);
    mBasket = Bitmap.createScaledBitmap(basket, BASKET_SIZE, BASKET_SIZE, true);
    BitmapFactory.Options opts = new BitmapFactory.Options();
    opts.inPreferredConfig = Bitmap.Config.RGB_565;
    mField = BitmapFactory.decodeResource(getResources(), R.drawable.field, opts);
    WindowManager mWindowManager = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
    mDisplay = mWindowManager.getDefaultDisplay();
    sensorManager = (SensorManager)getContext().getSystemService(Context.SENSOR_SERVICE);
    startSimulation();
}

    @Override
    public void onSensorChanged(SensorEvent event) {
    if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) return;
    switch (mDisplay.getRotation()) {
        case Surface.ROTATION_0:
            mSensorX = event.values[0];
            mSensorY = event.values[1];
            break;
        case Surface.ROTATION_90:
            mSensorX = -event.values[1];
            mSensorY = event.values[0];
            break;
        case Surface.ROTATION_180:
            mSensorX = -event.values[0];
            mSensorY = -event.values[1];
            break;
        case Surface.ROTATION_270:
            mSensorX = event.values[1];
            mSensorY = -event.values[0];
            break;
        }
        mSensorZ = event.values[2];
        mSensorTimeStamp = event.timestamp;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void startSimulation(){
    sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), sensorManager.SENSOR_DELAY_UI);
    }

    public void stopSimulation(){
    sensorManager.unregisterListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    canvas.drawBitmap(mField, 0, 0, null);
    canvas.drawBitmap(mBasket, mXOrigin - BASKET_SIZE / 2, mYOrigin - BASKET_SIZE / 2, null);
    mBall.updatePosition(mSensorX, mSensorY, mSensorZ, mSensorTimeStamp);
    mBall.resolveCollisionWithBounds(mHorizontalBound, mVerticalBound);
    canvas.drawBitmap(mBitmap,(mXOrigin - BALL_SIZE / 2) + mBall.mPosX, (mYOrigin - BALL_SIZE / 2) - mBall.mPosY, null);
    invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w,h,oldw,oldh);
        mXOrigin = w * 0.5f;
        mYOrigin = h * 0.5f;
        mHorizontalBound = (w - BALL_SIZE) * 0.5f;
        mVerticalBound = (h - BALL_SIZE) * 0.5f;
    }

}