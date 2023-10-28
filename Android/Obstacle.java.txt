package com.example.androidgroup36;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

public class Obstacle {

    //Initialise obstacles variables
    int x, y;
    int initX, initY;
    int xArena = -1, yArena = -1;
    boolean found=false;
    int offset;
    float obsOffset1 = 3f;
    int touchCount = 0;
    int obsOffset2 = 28;
    int aObsX, aObsY;
    boolean placeDown = false;
    boolean longPress = false;
    String obsFace = "None";
    String obsID, targetID;
    String color;

    //Obstacle constructors
    public Obstacle(int x, int y, int initX, int initY, String obsID, int touchCount, String obsFace, String targetID){
        this.x = x;
        this.y = y;
        this.touchCount = touchCount;
        this.obsFace = obsFace;
        this.obsID = obsID;
        this.targetID = targetID;
        this.initX = initX;
        this.initY = initY;
        this.color = "white";
    }

    //Return X coordinates
    public int getObsX(){
        return  x;
    }

    //Return Y coordinates
    public int getObsY(){
        return  y;
    }

    //Return obstacle x value
    public int getaObsX(){
        return  aObsX;
    }

    //Return obstacle y value
    public int getaObsY(){
        return  aObsY;
    }

    //Set obstacle values
    public void setaObsX(int aObsX){
        this.aObsX = aObsX;
    }

    public void setaObsY(int aObsY){
        this.aObsY = aObsY;
    }

    public int [] getInitCoords () {
        System.out.println(initX);
        System.out.println(initY);
        return new int [] {initX,initY};}

    public String getObsFace(){
        return obsFace;
    }

    //Return obstacle face values
    public int getObsFaceInt(String c)
    {
        switch(c)
        {
            case"N":
                return 90;
            case"E":
                return 0;
            case "W":
                return 180;
            case"S":
                return -90;
            default:
                return 0;
        }
    }

    public String getObsID(){
        return obsID;
    }

    public int getTouchCount(){
        return touchCount;
    }

    public int incrTouchCount(){
        touchCount++;
        return touchCount;
    }

    public void setObsX(int x){
        this.x = x;
    }

    public void setObsY(int y){
        this.y = y;
    }

    // Set the obstacle face
    public String setObsFace(int touchCount){
        switch (touchCount){
            case 1:
                obsFace = "N";
                break;
            case 2:
                obsFace = "E";
                break;
            case 3:
                obsFace = "S";
                break;
            case 4:
                obsFace = "W";
                break;
            default:
                obsFace = " ";
        }
        this.obsFace = obsFace;
        return obsFace;
    }

    //Draw the obstacle obstacle face
    public void drawObsFace(Canvas canvas, int touchCount, Paint paint){
        switch (touchCount){
            case 1:
                obsFace = "N";
                canvas.drawLine(x, y + obsOffset1, x + offset, y + obsOffset1, paint);
                break;

            case 2:
                obsFace = "E";
                canvas.drawLine(x + obsOffset2, y , x + obsOffset2, y + offset, paint);
                break;
            case 3:
                obsFace = "S";
                canvas.drawLine(x, y + obsOffset2, x + offset, y + obsOffset2, paint);
                break;
            case 4:
                obsFace = "W";
                canvas.drawLine(x + obsOffset1, y , x + obsOffset1, y + offset, paint);
                break;
            default:
                obsFace = " ";
        }
    }

    //Set the status of placing down object
    public void setPlaceDown(boolean status){
        //When touched down
        this.placeDown = status;
    }

    //Retrieve the boolean for placing down object
    public boolean getPlaceDown(){
        return placeDown;
    }

    //Set the status for long presses
    public void setLongPress(boolean status){
        //When touched down
        this.longPress = status;
    }

    // Retrieve long press statuses
    public boolean getLongPress(){
        return longPress;
    }

    //New position of draggable object being set
    public void setPosition(int x, int y){
        this.x = x - 30;
        this.y = y - 63;
    }

    // Coordinates of Draggable object return if being touched
    public boolean isTouched(int x, int y){
//        Log.d("isTouched", x + ", " + y);
//        Log.d("isTouched", this.x + ", " + this.y);

        boolean xIsInside = x > this.x && x < this.x + 100;
        boolean yIsInside = y > this.y && y < this.y + 100;

//        Log.d("isTouched", xIsInside + ", " + yIsInside);

        return xIsInside && yIsInside;
    }

    // Set the obstacle x and y coordinates
    public void setObsMapCoord (int xArena, int yArena){
        this.xArena = xArena;
        this.yArena = yArena;
    }

    public int[] getObsMapCoord (){
        return new int[]{xArena, yArena};
    }

    //Reset the touch counts
    public int resetTouchCount() {
        touchCount = 1;
        return touchCount;
    }

    //Set the number of touch counts
    public void setTouchCount(int touchCount) {
        this.touchCount = touchCount;
    }


    //Set the target ID
    public String setTargetID(String targetID) {
        this.targetID = targetID;
        return targetID;
    }

    //Retrieve targetID
    public String getTargetID() {
        return targetID;
    }

    //Draw objects onto canva in the arena map
    public void drawObj(Canvas canvas){
        Log.d("Obstacle", "Drawing Object");
        Paint obstaclePaint = new Paint();
        obstaclePaint.setColor(Color.BLACK);
        obstaclePaint.setStyle(Paint.Style.FILL);
        obstaclePaint.setStrokeWidth(3f);

        Log.d("Drawing Object:", "Coordinates: " + x + "," + y);

        canvas.drawRect(x,y,x+offset,y+offset, obstaclePaint);
    }

    //Basic resize function
    public void setResizeUp(boolean status){
        if(status == true){
            offset = 70;
        } else {
            offset = 28;
        }

    }

    public void setFaceResizeUp(boolean status){
        if(status == true){
            offset = 100;
        } else {
            offset = 28;
        }

    }


}
