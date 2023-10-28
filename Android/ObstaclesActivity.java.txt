package com.example.androidgroup36;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class ObstaclesActivity extends View {
    private static final String TAG = "ObstaclesActivity";
    private DraggableObject [] draggableObjectArray = new DraggableObject[3];
    public ObstaclesActivity(Context context) {
        super(context);
    }
    //Constructor
    public ObstaclesActivity(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        draggableObjectArray[0] = new DraggableObject(30, 20);
        draggableObjectArray[1] = new DraggableObject(30, 150);
        draggableObjectArray[2] = new DraggableObject(30, 300);
    }
    //Constructor
    public ObstaclesActivity(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //Arena map being drawn
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for(DraggableObject draggableObject : draggableObjectArray) {
            draggableObject.drawObj(canvas);
        }
    }


    // Touch events : ACTION_DOWN , ACTION_MOVE, ACTION_UP
    @Override
    public boolean onTouchEvent(MotionEvent event){
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //Touch down code
                Log.d(TAG, "onTouchEvent: ACTION_DOWN");
                for(int i = 0; i < draggableObjectArray.length; i++){
                    Log.d(TAG,"onTouchEvent: " + draggableObjectArray[i].toString());
                    if(draggableObjectArray[i].isTouched(event.getX(), event.getY()) &&
                            !draggableObjectArray[i].getActionDown()){
                        Log.d(TAG, "onTouchEvent: this is touched--->" + draggableObjectArray[i]);
                        draggableObjectArray[i].setActionDown(true);
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "onTouchEvent: ACTION_MOVE");
                //Touch move code
                for(DraggableObject draggableObject : draggableObjectArray) {
                    if (draggableObject.getActionDown()) {
                        draggableObject.setPosition(event.getX(), event.getY());
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "onTouchEvent: ACTION_UP");
                for(DraggableObject draggableObject : draggableObjectArray) {
                    if(draggableObject.getActionDown()){
                        draggableObject.setActionDown(false);
                    }
                }
                break;
        }
        return true;
    }

    // Draggable objects with x and y axis
    public class DraggableObject {
        private float x, y;
        private boolean actionDown = false;
        public DraggableObject(int x, int y) {
            this.x = x;
            this.y = y;
        }
        public void setActionDown(boolean status){
            //When touched down
            actionDown = status;
        }
        public boolean getActionDown(){
            return actionDown;
        }
        public void setPosition(float x, float y){
            this.x = x-50;
            this.y = y-50;
        }

        //Draw objects
        public void drawObj(Canvas canvas){
            Paint myPaint = new Paint();
            myPaint.setColor(Color.BLACK);
            myPaint.setStrokeWidth(10);
            canvas.drawRect(x,y,x+100,y+100,myPaint);
            invalidate();
        }

        //return touch status of that particular coordinates
        public boolean isTouched(float x, float y){
            boolean xIsInside = x > this.x && x < this.x + 100;
            boolean yIsInside = y > this.y && x < this.y - 100;
            return xIsInside && yIsInside;
        }

    }

}


