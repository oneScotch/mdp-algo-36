package com.example.androidgroup36;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ArenaMap extends View implements Serializable {

    private static final String TAG = "Arena Map";
    private String m_Text;
    private static Cell[][] cells;
    //Arena Map is 20 x 20 Grids (200cm x 200cm)
    private static final int mCols = 20, mRows = 20;
    private static float cellSize, hMargin, vMargin;

    //Initial direction of Robot to be North
    private static String robotDirection = "N";
    private static int[] obsCoord = new int[]{-1, -1};
    private static int[] curCoord = new int[]{-1, -1};
    private static int[] oldCoord = new int[]{-1, -1};
    private static ArrayList<int[]> obstacleCoord = new ArrayList<>();

    //Initialize 8 obstacles from getgo
    private static Obstacle [] obstacleList = new Obstacle[8];



    //Initialize Paint
    private static Paint wallPaint = new Paint();
    private static Paint robotPaint = new Paint();
    private static Paint directionPaint = new Paint();
    private static Paint obstaclePaint = new Paint();
    private static Paint unexploredPaint = new Paint();
    private static Paint exploredPaint = new Paint();
    private static Paint gridNumberPaint = new Paint();
    private static Paint obstacleNumberPaint = new Paint();
    private static Paint obstacleNumberPaint2 = new Paint();
    private static Paint obstacleBluePaint = new Paint();
    private static Paint obstacleRedPaint = new Paint();
    private static Paint obstacleYellowPaint = new Paint();
    private static Paint obstacleGreenPaint = new Paint();

    //private static Paint obstacleWhitePaint = new Paint();


    private static Paint emptyPaint = new Paint();
    private static Paint virtualWallPaint = new Paint();

    private static Paint westPaint = new Paint();
    private static Paint eastPaint = new Paint();
    private static Paint southPaint = new Paint();
    private static Paint northPaint = new Paint();
    private static Paint linePaint = new Paint();

    //Set to true when state is true
    private static boolean createCellStatus = false;
    private static boolean setRobotPostition = false;
    private static boolean changedFaceAnnotation = false;
    private static boolean validPosition = false;
    private static boolean canDrawRobot = false;


    private View mapView;
    private Rect r;


    //Long gesture press for setting obstacle face
    private GestureDetectorCompat mGestureDetector;
    private LongPressGestureListener longPressGestureListener;


    //Constructor
    public ArenaMap(Context context) {
        super(context);
        init(null);
    }

    //Constructor
    public ArenaMap(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);

        wallPaint.setColor(Color.WHITE);
        robotPaint.setColor(Color.parseColor("#bada55"));
        directionPaint.setColor(Color.BLACK);
        unexploredPaint.setColor(Color.parseColor("#ccd8d7"));
        exploredPaint.setColor(Color.GRAY);
        emptyPaint.setColor(Color.WHITE);
        virtualWallPaint.setColor(Color.parseColor("#FFA500"));

        obstaclePaint.setColor(Color.BLACK);
        obstaclePaint.setStyle(Paint.Style.FILL);
        obstaclePaint.setStrokeWidth(3f);

        obstacleNumberPaint.setColor(Color.WHITE);
        obstacleNumberPaint.setTextSize(10);
        obstacleNumberPaint.setTypeface(Typeface.DEFAULT_BOLD);
        obstacleNumberPaint.setAntiAlias(true);
        obstacleNumberPaint.setStyle(Paint.Style.FILL);
        obstacleNumberPaint.setTextAlign(Paint.Align.LEFT);

        obstacleNumberPaint2.setColor(Color.WHITE);
        obstacleNumberPaint2.setTextSize(18);
        obstacleNumberPaint2.setTypeface(Typeface.DEFAULT_BOLD);
        obstacleNumberPaint2.setAntiAlias(true);
        obstacleNumberPaint2.setStyle(Paint.Style.FILL);
        obstacleNumberPaint2.setTextAlign(Paint.Align.LEFT);

        obstacleGreenPaint.setColor(Color.parseColor("#00AD30"));
        obstacleGreenPaint.setTextSize(18);
        obstacleGreenPaint.setTypeface(Typeface.DEFAULT_BOLD);
        obstacleGreenPaint.setAntiAlias(true);
        obstacleGreenPaint.setStyle(Paint.Style.FILL);
        obstacleGreenPaint.setTextAlign(Paint.Align.LEFT);

        obstacleBluePaint.setColor(Color.parseColor("#00D2DB"));
        obstacleBluePaint.setTextSize(18);
        obstacleBluePaint.setTypeface(Typeface.DEFAULT_BOLD);
        obstacleBluePaint.setAntiAlias(true);
        obstacleBluePaint.setStyle(Paint.Style.FILL);
        obstacleBluePaint.setTextAlign(Paint.Align.LEFT);

        obstacleRedPaint.setColor(Color.parseColor("#FF0000"));
        obstacleRedPaint.setTextSize(18);
        obstacleRedPaint.setTypeface(Typeface.DEFAULT_BOLD);
        obstacleRedPaint.setAntiAlias(true);
        obstacleRedPaint.setStyle(Paint.Style.FILL);
        obstacleRedPaint.setTextAlign(Paint.Align.LEFT);

        obstacleYellowPaint.setColor(Color.parseColor("#FFD12D"));
        obstacleYellowPaint.setTextSize(18);
        obstacleYellowPaint.setTypeface(Typeface.DEFAULT_BOLD);
        obstacleYellowPaint.setAntiAlias(true);
        obstacleYellowPaint.setStyle(Paint.Style.FILL);
        obstacleYellowPaint.setTextAlign(Paint.Align.LEFT);


        gridNumberPaint.setColor(Color.BLACK);
        gridNumberPaint.setTextSize(15);
        gridNumberPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        westPaint.setColor(Color.GREEN);
        westPaint.setStyle(Paint.Style.FILL);

        eastPaint.setColor(Color.RED);
        eastPaint.setStyle(Paint.Style.FILL);

        northPaint.setColor(Color.YELLOW);
        northPaint.setStyle(Paint.Style.FILL);

        southPaint.setColor(Color.BLUE);
        southPaint.setStyle(Paint.Style.FILL);

        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setColor(Color.YELLOW);
        linePaint.setStrokeWidth(3f);

        mapView = (View) findViewById(R.id.mapView);

        longPressGestureListener = new LongPressGestureListener(this.mapView);
        mGestureDetector = new GestureDetectorCompat(context, longPressGestureListener);



        //Obstacles instantiation
        obstacleList [0] = new Obstacle (580, 12, 580, 12,"0", 0, "None","0");
        obstacleList [1] = new Obstacle(580, 78, 580, 78,"1", 0, "None", "1");
        obstacleList [2] = new Obstacle(580, 144, 580, 144,"2", 0, "None", "2");
        obstacleList [3] = new Obstacle(580, 210, 580, 210,"3", 0, "None", "3");
        obstacleList [4] = new Obstacle(580, 276, 580, 276,"4", 0, "None", "4");
        obstacleList [5] = new Obstacle(580, 342, 580, 342,"5", 0, "None", "5");
        obstacleList [6] = new Obstacle(580, 408, 580, 408,"6", 0, "None", "6");
        obstacleList [7] = new Obstacle(580, 474, 580, 474,"7", 0, "None", "7");

    }

    private void init(@Nullable AttributeSet set){
    }

    //Create individual cell grids
    private void createCells(){
        cells = new Cell[mCols][mRows];
        for (int x = 0; x < mCols; x++) {
            for (int y = 0; y < mRows; y++) {

                cells[x][y] = new Cell(x * cellSize + (cellSize / 30)+12f,
                        y * cellSize + (cellSize / 30),
                        (x + 1) * cellSize - (cellSize / 40)+12f,
                        (y + 1) * cellSize - (cellSize / 60), unexploredPaint);

                float xMiddle = ((((x + 1) * cellSize - (cellSize / 40))-(x * cellSize + (cellSize / 30)))/2);
                float yMiddle =  ((((y + 1) * cellSize - (cellSize / 60))-(y * cellSize + (cellSize / 30)))/2);
                Log.d(TAG, "CreateCell XMid" + xMiddle);
                Log.d(TAG, "CreateCell YMid" + yMiddle);

            }
        }
    }


    //Touch events
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        int coordinates[];
        int x = (int) event.getX();
        int y = (int) event.getY();

        mGestureDetector.onTouchEvent(event);

        //Get touched coordinate
        coordinates = findGridOnTouch(x, y);

        Log.d(TAG, "onTouchEvent: Touched coordinates are " +
                coordinates[0] + " " + coordinates[1]);

        Log.d(TAG, "onTouchEvent: Touched coordinates are " +
                x + " " + y);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //Touch down code
                Log.d(TAG, "onTouchEvent: ACTION_DOWN");
                for (int i = 0; i < obstacleList.length; i++) {
                    if (obstacleList[i].isTouched(x, y) && !obstacleList[i].getPlaceDown()) {
                        Log.d(TAG, "onTouchEvent: this is touched--->" + obstacleList[i]);
                        Log.d(TAG, "onTouchEvent: Coordinates are " +
                                coordinates[0] + " " + coordinates[1]);


                        obstacleList[i].setPlaceDown(true);
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "onTouchEvent: ACTION_MOVE");

                MainActivity.setXPosition(coordinates[0]);
                if(coordinates[1]!=-1)
                {
                    MainActivity.setYPosition(20-coordinates[1]);
                }
                else {
                    MainActivity.setYPosition(0);
                }

                //Touch move code
                for (Obstacle obstacles : obstacleList) {
                    if (obstacles.getPlaceDown()) {
                        Log.d(TAG, "C x: " + x);
                        Log.d(TAG, "C y: " + y);
                        Log.d(TAG, "C First (MOVE): " + coordinates[0]);
                        Log.d(TAG, "C Second (MOVE): " + coordinates[1]);
                        obstacles.setPosition(x, y);
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "onTouchEvent: ACTION_UP");
                //Touch up code
                for (Obstacle obstacles : obstacleList) {
                    if (obstacles.getPlaceDown()) {
                        if (isInArena(coordinates)) {
                            Log.d(TAG, "Grid coordinate x: " + coordinates[0]);
                            Log.d(TAG, "Grid coordinate y: " + coordinates[1]);
                            isInCell(x,y);
                            obstacles.setObsMapCoord(coordinates[0], coordinates[1]);



                            //Sending obstacle message
                        //    BluetoothConnectionService.sendMessage("ADDOBSTACLE," + obstacles.getObsID() + "," + coordinates[0] + "," + inverseCoordinates(coordinates[1] - 1));

                            obstacles.setaObsX(coordinates[0]);
                            Log.d(TAG, "Obstacle Coord x = " + obstacles.getaObsX());

                            if (coordinates[1] == -1) {
                                //When inverse, 0 = 19
                                obstacles.setaObsY(inverseCoordinates(0));
                            } else {
                                obstacles.setaObsY(coordinates[1] - 1);
                            }


                                    obstacles.setLongPress(true);
                                    obstacles.setPlaceDown(true);
                                    obstacles.setPosition(Integer.valueOf(coordinates[0])*28+42,(Integer.valueOf(coordinates[1])-1)*28+62);
                                    Log.d(TAG, "Obstacle " + obstacles.getObsID() + ": " + coordinates[0] + "," + coordinates[1]);

                                    obstacles.setPlaceDown(false);


                            Log.d(TAG, "Obstacle Coord y = " + obstacles.getaObsY());





                        } else {
                            // Out of bounds = go back to starting point
                            obstacles.setObsX(obstacles.getInitCoords()[0]);
                            obstacles.setObsY(obstacles.getInitCoords()[1]);
                            int initX = obstacles.getObsMapCoord()[0];
                            int initY = inverseCoordinates(obstacles.getObsMapCoord()[1] -1);
                            obstacles.setObsMapCoord(-1, -1);

                        }
                    }
                    obstacles.setPlaceDown(false);
                    obstacles.setResizeUp(false);
                    obstacles.setFaceResizeUp(false);
                    invalidate();
                }

                break;
        }

        if (setRobotPostition) {
            if (isInArena(coordinates)) {
                if ((coordinates[0] != 0 && coordinates[0] != 19) && (coordinates[1] != 0 && coordinates[1] != 19)) {
                    //Robot's currenr position is set
                    setCurCoord(coordinates[0], coordinates[1]);

                    invalidate();
                }
            }
        }

        Log.d(TAG, "onTouchEvent: Exiting onTouchEvent");
        //Must be true, else it will only call ACTION_DOWN
        return true;
        //return super.onTouchEvent(event);
    }

//    public int getSetObstacles()
//    {
//        return settedObstacleList.size();
//    }


    public String inputter()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Title");

        // Set up the input
        final EditText input = new EditText(getContext());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        return m_Text;
    }
    //Return details of obstacles in Arena
    public String getObstacles(){
        String obsDetailsString = "\n";
        List<Map<String, Integer>> dictionaryList = new ArrayList<>();
        for (Obstacle obstacles : obstacleList) {

            if (!((obstacles.getaObsX() == 0) && (obstacles.getaObsY() == 0) && (obstacles.getObsFace().equals(" ")))){
                Log.d(TAG, "x = " + obstacles.getaObsX());
                Log.d(TAG, "y = " + obstacles.getaObsY());

//                String ADD = "ADDOBSTACLE," + obstacles.getObsID() + "," + (5+obstacles.getaObsX()*10) + "," + (5+(19-obstacles.getaObsY())*10) + ",";
//                String FACE = "OBSTACLEFACE," + obstacles.getObsID() + "," + obstacles.getObsFaceInt(obstacles.getObsFace()) + "-";

//                String combined = (5+obstacles.getaObsX()*10) + "," + (5+(19-obstacles.getaObsY())*10) + "," + obstacles.getObsFaceInt(obstacles.getObsFace()) + ","+ obstacles.getObsID() + ":";


                // Create and add dictionaries to the list
                Map<String, Integer> dictionary1 = new LinkedHashMap<>();
//                dictionary1.put("x", (5+obstacles.getaObsX()*10));
//                dictionary1.put("y", (5+(19-obstacles.getaObsY())*10));
                dictionary1.put("\"x\"", obstacles.getaObsX());
                dictionary1.put("\"y\"", 20 - obstacles.getaObsY() - 1);
                dictionary1.put("\"direction\"", obstacles.getObsFaceInt(obstacles.getObsFace()));
                dictionary1.put("\"obs_id\"", Integer.valueOf(obstacles.getObsID()));

                dictionaryList.add(dictionary1);



                //                obsDetailsString = ADD.concat(FACE);
//                obsDetailsString = obsDetailsString.concat(combined);


               // obsDetailsString = obsDetailsString.concat(ADD.concat(FACE));

            }
        }

        StringBuilder sb = new StringBuilder(obsDetailsString);

        // Removing the last character
        // of a string
        sb.deleteCharAt(obsDetailsString.length() - 1);
        Log.d(TAG, obsDetailsString);


//        return sb.toString();

        return dictionaryList.toString().replace("=", ":");
    }


    @Override
    protected  void onDraw(Canvas canvas){
        super.onDraw(canvas);


        //Set width and height of the canvas
        int width = getWidth();
        int height = getHeight();

        Log.d(TAG,"Width and Height: " + width + height);


        //Calculate cellsize based on dimensions of the canvas
        if(width/height < mCols/mRows){
            cellSize = width / (mCols + 1);
            Log.d(TAG,"Cell size 1: " + cellSize);
        } else {
            cellSize = height / (mRows + 1);
            Log.d(TAG,"Cell size 2: " + cellSize);
        }

        //Calculate margin size of canvas
        hMargin = ((width - mCols * cellSize) / 2 - 45);
        vMargin = (height - mRows * cellSize) / 2;


        //Create cell
        if(!createCellStatus){
            //Create cell coordinates
            //Log.d(TAG, "onDraw: Creating cells");
            createCells();
            createCellStatus = true;
        }

        //Set Margin
        canvas.translate(hMargin, vMargin);

        drawBorder(canvas);
        drawCell(canvas);
        drawGridNumber(canvas);
        drawRobot(canvas);

        for(Obstacle obstacles : obstacleList) {
            obstacles.drawObj(canvas);
            obstacles.drawObsFace(canvas, obstacles.getTouchCount(), linePaint);
            if(obstacles.found==true)
            {
                if(obstacles.getTargetID()=="3" || obstacles.getTargetID()=="8" || obstacles.getTargetID()=="D" || obstacles.getTargetID()=="S" || obstacles.getTargetID()=="X" || obstacles.getTargetID()=="←")
                {
                    canvas.drawText(obstacles.getTargetID(), obstacles.getObsX() + 9, obstacles.getObsY() + 21, obstacleBluePaint);

                }
                else if(obstacles.getTargetID()=="2" || obstacles.getTargetID()=="7" || obstacles.getTargetID()=="C" || obstacles.getTargetID()=="H" || obstacles.getTargetID()=="W" || obstacles.getTargetID()=="→")
                {
                    canvas.drawText(obstacles.getTargetID(), obstacles.getObsX() + 9, obstacles.getObsY() + 21, obstacleGreenPaint);

                }
                else if(obstacles.getTargetID()=="4" || obstacles.getTargetID()=="9" || obstacles.getTargetID()=="E" || obstacles.getTargetID()=="T" || obstacles.getTargetID()=="Y" || obstacles.getTargetID()=="↓")
                {
                    canvas.drawText(obstacles.getTargetID(), obstacles.getObsX() + 9, obstacles.getObsY() + 21, obstacleRedPaint);

                }
                else if(obstacles.getTargetID()=="5" || obstacles.getTargetID()=="A" || obstacles.getTargetID()=="F" || obstacles.getTargetID()=="U" || obstacles.getTargetID()=="Z" || obstacles.getTargetID()=="⬤")
                {
                    canvas.drawText(obstacles.getTargetID(), obstacles.getObsX() + 9, obstacles.getObsY() + 21, obstacleYellowPaint);

                }
                else
                {
                    canvas.drawText(obstacles.getTargetID(), obstacles.getObsX() + 9, obstacles.getObsY() + 21, obstacleNumberPaint2);
                }
            }
            else
            {
                canvas.drawText(obstacles.getTargetID(), obstacles.getObsX() + 9, obstacles.getObsY() + 21, obstacleNumberPaint);

            }

            invalidate();
        }

    }


    //Draw individual cell
    private void drawCell(Canvas canvas){
        for (int x = 0; x < mCols; x++) {
            for (int y = 0; y < mRows; y++) {
                //Draw cells
                canvas.drawRect(cells[x][y].startX,cells[x][y].startY,cells[x][y].endX,cells[x][y].endY,cells[x][y].paint);
            }
        }
    }

    //Draw border for each cell
    private void drawBorder(Canvas canvas){
        for (int x = 0; x < mCols; x++) {
            for (int y = 0; y < mRows; y++) {
                //Top
                canvas.drawLine(x * cellSize, y * cellSize, (x + 1) * cellSize, y * cellSize, wallPaint);
                //Right
                canvas.drawLine((x + 1) * cellSize, y * cellSize, (x + 1) * cellSize, (y + 1) * cellSize, wallPaint);
                //Left
                canvas.drawLine(x * cellSize, y * cellSize, x * cellSize, (y + 1) * cellSize, wallPaint);
                //Bottom
                canvas.drawLine(x * cellSize, (y + 1) * cellSize, (x + 1) * cellSize, (y + 1) * cellSize, wallPaint);
            }
        }
    }

    //Draw robot on canvas
        public void drawRobot(Canvas canvas) {
        Log.d(TAG,"Drawing Robot");
        int robotCoordinates [] = getCurCoord();
        int x = robotCoordinates[0];
        int y = robotCoordinates[1];
        String direction = getRobotDirection();

        if(x != -1 && y != -1){
            float halfWidth = ((cells[x][y - 1].endX) - (cells[x][y - 1].startX)) / 2;

            //row and col is the middle of the robot
            Log.d(TAG,"drawRobot: Coordinates are= " + x + " , " + inverseCoordinates(y));

            //Draw Robot box
            canvas.drawRect(cells[x][y].startX, cells[x][y].startY, cells[x][y].endX, cells[x][y].endY, robotPaint);
            canvas.drawRect(cells[x][y - 1].startX, cells[x][y - 1].startY, cells[x][y - 1].endX, cells[x][y - 1].endY, robotPaint);
            canvas.drawRect(cells[x + 1][y].startX, cells[x + 1][y].startY, cells[x + 1][y].endX, cells[x + 1][y].endY, robotPaint);
            canvas.drawRect(cells[x - 1][y].startX, cells[x - 1][y].startY, cells[x - 1][y].endX, cells[x - 1][y].endY, robotPaint);
            canvas.drawRect(cells[x + 1][y - 1].startX, cells[x + 1][y - 1].startY, cells[x + 1][y - 1].endX, cells[x + 1][y - 1].endY, robotPaint);
            canvas.drawRect(cells[x - 1][y - 1].startX, cells[x - 1][y - 1].startY, cells[x - 1][y - 1].endX, cells[x - 1][y - 1].endY, robotPaint);
            canvas.drawRect(cells[x][y + 1].startX, cells[x][y + 1].startY, cells[x][y + 1].endX, cells[x][y + 1].endY, robotPaint);
            canvas.drawRect(cells[x + 1][y + 1].startX, cells[x + 1][y + 1].startY, cells[x + 1][y + 1].endX, cells[x + 1][y + 1].endY, robotPaint);
            canvas.drawRect(cells[x - 1][y + 1].startX, cells[x - 1][y + 1].startY, cells[x - 1][y + 1].endX, cells[x - 1][y + 1].endY, robotPaint);

            //Robot direction (Arrow)
            Path path = new Path();
            Log.d(TAG,"Robot direction: " + direction);

            switch (direction){
                case "N":
                    path.moveTo(cells[x][y - 1].startX + halfWidth, cells[x][y - 1].startY); // Top
                    path.lineTo(cells[x][y - 1].startX, cells[x][y - 1].endY); // Bottom left
                    path.lineTo(cells[x][y - 1].endX, cells[x][y - 1].endY); // Bottom right
                    path.lineTo(cells[x][y - 1].startX + halfWidth, cells[x][y - 1].startY); // Back to Top
                    break;
                case "S":
                    path.moveTo(cells[x][y + 1].endX - halfWidth, cells[x][y + 1].endY); // Top
                    path.lineTo(cells[x][y + 1].startX, cells[x][y + 1].startY); // Bottom left
                    path.lineTo(cells[x + 1][y + 1].startX, cells[x +1][y + 1].startY); // Bottom right
                    path.lineTo(cells[x][y + 1].endX - halfWidth, cells[x][y + 1].endY); // Back to Top
                    break;
                case "E":
                    path.moveTo(cells[x+1][y].startX + (2*halfWidth), cells[x][y].startY + halfWidth); // Top
                    path.lineTo(cells[x+1][y].startX, cells[x+1][y].startY); // Bottom left
                    path.lineTo(cells[x+1][y+1].startX, cells[x+1][y+1].startY); // Bottom right
                    path.lineTo(cells[x+1][y].startX + (2*halfWidth) , cells[x][y].startY + halfWidth); // Back to Top
                    break;
                case "W":
                    path.moveTo(cells[x-1][y].startX, cells[x][y].startY + halfWidth); // Top
                    path.lineTo(cells[x][y].startX, cells[x][y].startY); // Bottom left
                    path.lineTo(cells[x][y + 1].startX, cells[x][y  +1].startY); // Bottom right
                    path.lineTo(cells[x-1][y].startX, cells[x][y].startY + halfWidth); // Back to Top
                    break;
            }
            path.close();
            canvas.drawPath(path, directionPaint);

            //After drawing, set drawing to false
            setRobotPostition = false;
            //MainActivity.setRobotDetails(x, inverseCoordinates(y), direction);

            MainActivity.statusMsg.setText("Robot is Online");
        }
    }

    //Label x and y axis of Arena
    private void drawGridNumber(Canvas canvas) {
        //Row
        for (int x = 0; x < 20; x++) {
            if(x >9 && x <20){
                canvas.drawText(Integer.toString(x), cells[x][19].startX + (cellSize / 5), cells[x][19].endY + (cellSize / 1.5f), gridNumberPaint);
            } else {
                canvas.drawText(Integer.toString(x), cells[x][19].startX + (cellSize / 3), cells[x][19].endY + (cellSize / 1.5f), gridNumberPaint);
            }
        }
        //Column
        for (int x = 0; x <20; x++) {
            if(x >9 && x <20){
                canvas.drawText(Integer.toString(19 - x), cells[0][x].startX - (cellSize / 1.5f), cells[0][x].endY - (cellSize / 3.5f), gridNumberPaint);
            } else {
                canvas.drawText(Integer.toString(19 - x), cells[0][x].startX - (cellSize / 1.2f), cells[0][x].endY - (cellSize / 3.5f), gridNumberPaint);
            }
        }
    }

    //Inverting rows
    private int inverseCoordinates(int y){
        return (19 - y);
    }

    public static int getXCoord(){
        return curCoord[0];
    }

    public static int getYCoord(){
        return 19-curCoord[1];
   }




   //Real time update of Arena Map upon receiving Strings
    public void mapUpdate(String message) {
        Log.d(TAG,"mapUpdate: Updating Map!");
        int robotCoordinates [] = getCurCoord();
        String receivedMessage [] = message.split(",");
        String item = receivedMessage[0];
        int x,y;
        String obsID="0", targetID="0";
        String direction="0", movement="0";
        String xpos="0", ypos="0";
        String face ="N";
        int moveAmount=0;


        switch (item){
            case "o":
                Log.d(TAG,"mapUpdate: Move Obstacle!");

                obsID = receivedMessage[1];
                xpos = receivedMessage[2];
                ypos = receivedMessage[3];
         //       face = receivedMessage[4].toUpperCase();
                for(Obstacle obstacles: obstacleList)
                {
                    if(obstacles.getObsID().equals(obsID))
                    {
                        obstacles.setLongPress(true);
                        obstacles.setPlaceDown(true);
                        obstacles.setPosition(Integer.valueOf(xpos)*31+62,(19-Integer.valueOf(ypos))*31+62);
                        obstacles.setObsMapCoord(Integer.valueOf(xpos), Integer.valueOf(ypos));
                        obstacles.setaObsY(Integer.valueOf(xpos)*31+62);
                        obstacles.setaObsY(19-Integer.valueOf(ypos)*31+62);

                        Log.d(TAG, "Obstacle " + obsID + ": " + xpos + "," + ypos);

                        obstacles.setPlaceDown(false);
                      //  obstacles.setLongPress(false);
                        break;
                    }
                }
                break;
            case "TARGET":
                //Update obstacle by displaying image ID
                obsID = receivedMessage[1];
                targetID = receivedMessage[2];
                for(Obstacle obstacles: obstacleList)
                {
                    if(obstacles.getObsID().equals(obsID))
                    {
                        obstacles.found=true;
                    }
                }
                updateTargetText(obsID, targetID);
                break;
            case "ROBOT":
                //Get new robot position
                //String combined = (5+obstacles.getaObsX()*10) + "," + (5+(19-obstacles.getaObsY())*10) + "," + obstacles.getObsFaceInt(obstacles.getObsFace()) + ","+ obstacles.getObsID() + ":";


//                x = (Integer.valueOf(receivedMessage[1]) -5 )/10;
//                y = -1*(((Integer.valueOf(receivedMessage[2])-5)/10)-19);


                x = Integer.valueOf(receivedMessage[1]) + 1;
                y = 19 - Integer.valueOf(receivedMessage[2]);
                direction = receivedMessage[3];

                Log.d(TAG, "New coordinates: " + x + "," + y);
                Log.d(TAG, "Direction " + direction);

                moveRobot(x,y,direction);
                break;
            case "MOVE":
                //Get robot movement
                movement = receivedMessage[1];
                Log.d(TAG, "updateMap: Move " + movement);

                moveRobot(movement);
                break;
            case "f":
                moveAmount=Integer.valueOf(receivedMessage[1])/10;
                for(int i=0;i<moveAmount;i++)
                {
                    moveRobot("w");
                }
                break;
            case "b":
                moveAmount=Integer.valueOf(receivedMessage[1])/10;
                for(int i=0;i<moveAmount;i++)
                {
                    moveRobot("s");
                }
                break;
            case "l":
                //3 box backward, 3 box left
                moveRobot("a");
                moveRobot("w");
                moveRobot("d");
                moveRobot("w");
                moveRobot("a");
                moveRobot("w");
                moveRobot("d");
                moveRobot("w");
//                moveRobot("a");
//                moveRobot("w");
//                moveRobot("d");
//                moveRobot("w");
                moveRobot("a");
                break;
            case "r":
                //3 box backward, 3 box right
                moveRobot("d");
                moveRobot("w");
                moveRobot("a");
                moveRobot("w");
                moveRobot("d");
                moveRobot("w");
                moveRobot("a");
                moveRobot("w");
//                moveRobot("d");
//                moveRobot("w");
//                moveRobot("a");
//                moveRobot("w");
                moveRobot("d");

                break;
            case "L":
                moveRobot("d");
                moveRobot("s");
                moveRobot("a");
                moveRobot("s");
                moveRobot("d");
                moveRobot("s");
                moveRobot("a");
                moveRobot("s");
//                moveRobot("d");
//                moveRobot("s");
//                moveRobot("a");
//                moveRobot("s");
                moveRobot("d");
                break;
            case "R":
                moveRobot("a");
                moveRobot("s");
                moveRobot("d");
                moveRobot("s");
                moveRobot("a");
                moveRobot("s");
                moveRobot("d");
                moveRobot("s");
//                moveRobot("a");
//                moveRobot("s");
//                moveRobot("d");
//                moveRobot("s");
                moveRobot("a");



                break;

            default:
                break;
        }
        invalidate();
    }

    private void updateTargetText(String obsID, String targetID) {
        //Go through list of obstacles
        String ID;
        for (Obstacle obstacles : obstacleList) {
            ID = obstacles.getObsID();
            if(ID.equals(obsID)){
                Log.d(TAG,"obsID: " + obsID);
                Log.d(TAG,"targetID: " + targetID);

                obstacles.setTargetID(imageTranslation(targetID));
            }
        }
        invalidate();
    }

    private String imageTranslation(String targetID)
    {
        switch(targetID)
        {
            case "11":
                return "1";
            case "12":
                return "2";
            case "13":
                return "3";
            case "14":
                return "4";
            case "15":
                return "5";
            case "16":
                return "6";
            case "17":
                return "7";
            case "18":
                return "8";
            case "19":
                return "9";
            case "20":
                return "A";
            case "21":
                return "B";
            case "22":
                return "C";
            case "23":
                return "D";
            case "24":
                return "E";
            case "25":
                return "F";
            case "26":
                return "G";
            case "27":
                return "H";
            case "28":
                return "S";
            case "29":
                return "T";
            case "30":
                return "U";
            case "31":
                return "V";
            case "32":
                return "W";
            case "33":
                return "X";
            case "34":
                return "Y";
            case "35":
                return "Z";
            case "36":
                return "↑";
            case "37":
                return "↓";
            case "38":
                return "→";
            case "39":
                return "←";
            case "40":
                return "⬤";
        }
        return "-";
    }

    //Resetting Arena by resetting everything
    public void robotReset()
    {
        curCoord = new int [] {-1, -1};


        robotDirection = "N";
        createCellStatus = false;
        setRobotPostition = false;
        canDrawRobot = false;
        setStartingPoint(false);
        //MainActivity.setRobotDetails(-1, -1, "N");
        invalidate();
    }
    public void arenaReset(){
        curCoord = new int [] {-1, -1};
        robotDirection = "N";
        createCellStatus = false;
        setRobotPostition = false;
        canDrawRobot = false;
        //settedObstacleList.clear();

        for (Obstacle obstacles : obstacleList){
            obstacles.setObsX(obstacles.getInitCoords()[0]);
            obstacles.setObsY(obstacles.getInitCoords()[1]);
            obstacles.setTargetID(obstacles.getObsID());
            obstacles.setaObsX(0);
            obstacles.setaObsY(0);
            obstacles.found=false;

            obstacles.setTouchCount(0);
            obstacles.setLongPress(false);
        }

        setStartingPoint(false);
        MainActivity.setRobotDetails(-1, -1, "N");
     /*   MainActivity.setXPosition(0);
        MainActivity.setYPosition(0);*/

        invalidate();
    }

    private ArrayList<int[]> getObstacleCoord() {
        return obstacleCoord;
    }

    private boolean isInArena(int touchedCoord []){
        //Check if coordinates is within the stated arena size
        Log.d(TAG,"isInArena: Check if touched coordinates is within the Arena");
        boolean isInArena = false;

        //If in Arena, return true
        if (touchedCoord[0] != -1 && touchedCoord[1] != -1) {
            isInArena = true;
        } else if (touchedCoord [0] != -1 && touchedCoord[1] == -1){
            isInArena = true;
        }

        return isInArena;
    }

    private int[] isInCell(int x, int y){
        //Check if coordinates is within the Cell, set to the nearest position via rounding off
        Log.d(TAG,"isInCell: Check if obstacle coordinates is within the Cell");

        return new int [] {1,2};
    }

    /**
     * Change color of the obstacle to indicate face of the image
     * Black: Default, Green: Left, Red: Right, Yellow: Down, Blue: Front
     * Need to attach count to the object
     *
     * @return*/

    //Find coordinates of cell in arena when touched
    public static int[] findGridOnTouch(float x, float y) {
        int row = -1, cols = -1;
        //FIND COLS OF THE MAZE BASED ON ONTOUCH
        for (int i = 0; i < mCols; i++) {
            if (cells[i][0].endX >= (x - hMargin) && cells[i][0].startX <= (x - hMargin)) {
                cols = i;
                Log.d(TAG, "SDATA startX = " + cells[i][0].startX);
                Log.d(TAG, "SDATA endX = " + cells[i][0].endX);
                Log.d(TAG, "SDATA cols = " + cols);
                Log.d(TAG, "hMargin = " + hMargin);
                Log.d(TAG, "x = " + x);
                Log.d(TAG, "hMargin = " + (x - hMargin));
                break;
            }
        }
        //FIND ROW OF THE MAZE BASED ON ONTOUCH
        for (int j = 0; j < mRows; j++) {
            if (cells[0][j].endY >= (y - vMargin) && cells[0][j].startY <= (y - vMargin)) {
                row = j;
                Log.d(TAG, "SDATA startY = " + cells[0][j].startY);
                Log.d(TAG, "SDATA endY = " + cells[0][j].endY);
                Log.d(TAG, "SDATA row = " + row);
                Log.d(TAG, "hMargin = " + vMargin);
                Log.d(TAG, "y = " + y);
                Log.d(TAG, "hMargin = " + (y - vMargin));
                break;
            }
        }
        return new int[]{cols, row};
    }



    //Get current robot Coordinates that is currently in or out of the arena
    public static int[] getCurCoord(){
        return curCoord;
    }

    //Set the robot direction that it is facing
    public void setRobotDirection(String direction){
        Log.d(TAG,"setRobotDirection");
        MainActivity.txtRobotDirection.setText(direction);
        if(direction.equals("0")||direction.equals("N")){
            robotDirection = "N";
        } else if (direction.equals("90")||direction.equals("E")){
            robotDirection = "E";
        } else if (direction.equals("180")||direction.equals("S")) {
            robotDirection = "S";
        } else if (direction.equals("270")||direction.equals("W")){
            robotDirection = "W";
        }
        Log.d(TAG,robotDirection);
    }

    //Returns the robot direction in the arena
    public String getRobotDirection(){
        return robotDirection;
    }

    //Allow user to set Robot position by setting CanDrawRobot as true
    public void setStartingPoint(boolean status){
        canDrawRobot = true;
        setRobotPostition = status;
    }


    // Change the robot direction given the x and y axis as well as the direction
    public void moveRobot(int x, int y, String direction) {
        Log.d(TAG,"Moving robot");
//        setValidPosition(false);

        String backupDirection = robotDirection;
        int oldCoord[]= this.getCurCoord();

        Log.d(TAG, "onMoveRobot: Old coordinates are " + oldCoord[0] + "," + oldCoord[1]);

        if((oldCoord[0] == -1) && (oldCoord[1] == -1)){
            //store the initial coordinates as old coordinates in an array
            if(((x != 0 && x != 19) && (y != 0 && y != 19))) {
                //Draw the robot if its not drawn.
                setCurCoord(x, y);
                setRobotDirection(direction);
                setStartingPoint(true);

            } else {
                Toast.makeText(getContext(),"Area out of bounds!",Toast.LENGTH_SHORT).show();
            }
        } else {
            setOldRobotCoord(oldCoord[0], oldCoord[1]);
            if ((x != 0 && x != 19) && (y != 0 && y != 19)) {
                //Store new coordinates as current coordinates.
                setCurCoord(x, y);
                setRobotDirection(direction);
            } else {
                Toast.makeText(getContext(),"Area out of bounds!",Toast.LENGTH_SHORT).show();
                setCurCoord(oldCoord[0], oldCoord[1]);
                setRobotDirection(backupDirection);
            }
        }
        invalidate();
    }

    //Command the robot to move according to the different direction and coordinates provided
    public void moveRobot(String movement){
        Log.d(TAG,"Entering moveRobot");
        setValidPosition(false);

        int[] oldCoord = this.getCurCoord();
        String currDirection = getRobotDirection();
        String backupDirection = getRobotDirection();

        int x = oldCoord[0];
        int y = oldCoord[1];

        Log.d(TAG, "onMoveRobot: Current coordinates => " + oldCoord[0] + "," + oldCoord[1]);
        Log.d(TAG,"onMoveRobot: Current Robot direction => " + currDirection);

//        Robot movement depends on the arrow/direction of the robot.
        switch (currDirection) {
            case "N":
                //Ensure that center of the body is within this area
                if((x != 0 && x != 19) && (y != 0 && y != 19)){
                    validPosition = true;
                }
                switch (movement) {
                    case "w": //"forward"
                        if (curCoord[1] != 1) {
                            curCoord[1] -= 1;
                            validPosition = true;
                        } else {
                            setValidPosition(false);
                        }
                        break;
                    case "d": //"right"
                        robotDirection = "E";
                        break;
                    case "s": //"back"
                        if (curCoord[1] != 18) {
                            curCoord[1] += 1;
                            validPosition = true;
                        } else {
                            setValidPosition(false);
                        }
                        break;
                    case "a": //"left"
                        robotDirection = "W";
                        break;
                    default:
                        robotDirection = "error up";
                        break;
                }
                break;
            case "90":
            case "E":
                switch (movement) {
                    case "w":
                        if (curCoord[0] != 18) {
                            curCoord[0] += 1;
                            validPosition = true;
                        } else {
                            setValidPosition(false);
                        }
                        break;
                    case "d":
                        robotDirection = "S";
                        break;
                    case "s":
                        if (curCoord[0] != 1) {
                            curCoord[0] -= 1;
                            validPosition = true;
                        } else {
                            setValidPosition(false);
                        }
                        break;
                    case "a":
                        robotDirection = "N";
                     //   Toast.makeText(getContext(), "Robot: Turning Left", Toast.LENGTH_LONG).show();
                        break;
                    default:
                        robotDirection = "error right";
                }
                break;
            case "180":
            case "S":
                switch (movement) {
                    case "w":
                        if (curCoord[1] != 18) {
                            curCoord[1] += 1;
                            validPosition = true;
                      //      Toast.makeText(getContext(), "Robot: Moving Forward", Toast.LENGTH_LONG).show();
                        } else {
                            setValidPosition(false);
                        }
                        break;
                    case "d":
                        robotDirection = "W";
                        break;
                    case "s":
                        if (curCoord[1] != 1) {
                            curCoord[1] -= 1;
                            validPosition = true;
                        } else {
                            setValidPosition(false);
                        }
                        break;
                    case "a":
                        robotDirection = "E";
                        break;
                    default:
                        robotDirection = "error down";
                }
                break;
            case "270":
            case "W":
                switch (movement) {
                    case "w":
                        if (curCoord[0] != 1) {
                            curCoord[0] -= 1;
                            validPosition = true;
                        } else {
                            setValidPosition(false);
                        }
                        break;
                    case "d":
                        robotDirection = "N";
                        break;
                    case "s":
                        if (curCoord[0] != 18) {
                            curCoord[0] += 1;
                            validPosition = true;
                        } else {
                            setValidPosition(false);
                        }
                        break;
                    case "a":
                        robotDirection = "S";
                        break;
                    default:
                        robotDirection = "error left";
                }
                break;
            default:
                robotDirection = "error moveCurCoord";
                break;
        }

        if (getValidPosition()){
            Log.d(TAG, String.valueOf(getValidPosition()));
            Log.d(TAG,"onMoveRobot: Curr Coord is "+ curCoord[0] + "," + curCoord[1]);
            setCurCoord(curCoord[0], curCoord[1]);
            setOldRobotCoord(x,y);
        } else {
            if (movement.equals("w") || movement.equals("s")){
                robotDirection = backupDirection;
                setCurCoord(oldCoord[0], oldCoord[1]);
            }
            Log.d(TAG, "onMoveRobot: Old coordinates are " + oldCoord[0] + "," + oldCoord[1]);
        }
        MainActivity.txtRobotDirection.setText(robotDirection);
        this.invalidate();
        Log.d(TAG,"Robot has been moved");
    }


    //Set current coordinates of the object
    public void setCurCoord(int col, int row) {
        Log.d(TAG,"Entering setCurCoord");
        curCoord[0] = col;
        curCoord[1] = row;
        MainActivity.txtRobotCoord.setText(Integer.toString(col)+','+Integer.toString(19-row));

        Log.d(TAG, col + "," + row);

        for (int x = col - 1; x <= col + 1; x++)
            for (int y = curCoord[1] - 1; y <= curCoord[1] + 1; y++)
                cells[x][y].setType("robot");
        Log.d(TAG,"Exiting setCurCoord");
    }

    //Set old coordinates robot
    private void setOldRobotCoord(int oldCol, int oldRow) {
        Log.d(TAG,"Entering setOldRobotCoord");
        oldCoord[0] = oldCol;
        oldCoord[1] = oldRow;

        Log.d(TAG, oldCol + "," + oldRow);

        //oldRow = this.inverseCoordinates(oldRow);
        for (int x = oldCoord[0] - 1; x <= oldCoord[0] + 1; x++){
            for (int y = oldCoord[1] - 1; y <= oldCoord[1] + 1; y++){
                cells[x][y].setType("explored");
            }
        }
        Log.d(TAG,"Exiting setOldRobotCoord");
    }

    //Get and Set robot positions
    private int[] getOldRobotCoord() {
        return oldCoord;
    }

    private void setValidPosition(boolean status) {
        validPosition = status;
    }

    private boolean getValidPosition() {
        return validPosition;
    }

    public boolean getCanDrawRobot() {
        return canDrawRobot;
    }


    private class Cell {
        float startX, startY, endX, endY;
        Paint paint;
        String type;

        private Cell(float startX, float startY, float endX, float endY, Paint paint){
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
            this.paint = paint;
        }

        // Set canva type to be drawn
        public void setType(String type) {
            this.type = type;
            switch (type) {
                case "obstacle":
                    this.paint = obstaclePaint;
                    break;
                case "robot":
                    this.paint = robotPaint;
                    break;
                case "unexplored":
                    this.paint = unexploredPaint;
                    break;
                case "explored":
                    this.paint = exploredPaint;
                    break;
                case "arrow":
                    this.paint = directionPaint;
                    break;
                case "id":
                    this.paint = obstacleNumberPaint;
                    break;
                default:
                    Log.d(TAG,"setType default: " + type);
                    break;
            }
        }
    }

    //Long press commands on device
    public class LongPressGestureListener extends GestureDetector.SimpleOnGestureListener {

        public LongPressGestureListener(View arenaMap) {
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            Log.d("TAG","onLongPress: LONG PRESS!");

            int x = (int) e.getX();
            int y = (int) e.getY();

            //Increase counts for annotation
            for (Obstacle obstacles : obstacleList) {
                //Check if it obstacle in touched.
                if (obstacles.isTouched(x, y)) {
                    obstacles.setPlaceDown(false);
                    if(obstacles.getLongPress()){
                        Toast.makeText(getContext(), "Face annotation disabled", Toast.LENGTH_LONG).show();
                        obstacles.setLongPress(false);
                    } else {
                        Toast.makeText(getContext(), "Face annotation enabled", Toast.LENGTH_LONG).show();
                        obstacles.setLongPress(true);
                    }
                }
            }
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }


    //Set the direction of the obstacle face
    public void setObstacleFace(){
         String msg;
        Log.d(TAG,"setObstacleFace");
        for (Obstacle obstacles : obstacleList) {
            //Check if obstacle in touched.
            if(obstacles.getLongPress()){
                if(obstacles.getTouchCount() >= 5){
                    obstacles.resetTouchCount();
                    obstacles.setObsFace(obstacles.getTouchCount());

                } else {
                    obstacles.incrTouchCount();
                    obstacles.setObsFace(obstacles.getTouchCount());
                }
                msg = "FACE,"+ obstacles.getObsID() +","+ obstacles.getObsFace();
         //       BluetoothConnectionService.sendMessage(msg);

            }
        }
        invalidate();
    }

}