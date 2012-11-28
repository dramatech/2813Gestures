package org.dramatech.atq.gesture;

import SimpleOpenNI.SimpleOpenNI;
import netP5.NetAddress;
import org.dramatech.atq.graphics.PFrame;
import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;

/* --------------------------------------------------------------------------
 * SimpleOpenNI User Test
 * --------------------------------------------------------------------------
 * Processing Wrapper for the OpenNI/Kinect library
 * http://code.google.com/p/simple-openni
 * --------------------------------------------------------------------------
 * prog:  Max Rheiner / Interaction Design / zhdk / http://iad.zhdk.ch/
 * date:  02/16/2011 (m/d/y)
 * ----------------------------------------------------------------------------
 */
public class GestureTest extends PApplet {
    OscP5               oscP5;
    NetAddress          myRemoteLocation;
    SimpleOpenNI        context;
    boolean             autoCalib = true;
    GestureController   controller;
    int                 checkGestures = 0;
    Gesture             currGesture;

    PFrame gestureInfoFrame;

    public void setup()
    {
        context = new SimpleOpenNI(this, SimpleOpenNI.RUN_MODE_MULTI_THREADED);

        // Enable depthMap generation
        if(!context.enableDepth())
        {
            println("Can't open the depthMap, maybe the camera is not connected!");
            exit();
            return;
        }

        // Enable skeleton generation for all joints
        context.enableUser(SimpleOpenNI.SKEL_PROFILE_ALL);

        background(200, 0, 0);

        stroke(0, 0, 255);
        strokeWeight(3);
        smooth();


        gestureInfoFrame = new PFrame("Current Gesture");
        PFont font;
        font = loadFont("Serif-30.vlw");
        gestureInfoFrame.s.textFont(font);
        controller = new GestureController();
        GestureInfo.init();

        // Start oscP5, telling it to listen for incoming messages at port 5001
        oscP5 = new OscP5(this, 50001);

        // Set the remote location to be the localhost on port 5001
        // TODO: Load this from config file?
        myRemoteLocation = new NetAddress("127.0.0.1", 57131);

        size(context.depthWidth(), context.depthHeight());
    }

    public void draw()
    {
        // Update the camera
        context.update();

        // Draw depthImageMap
        image(context.depthImage(),0,0);

        // Draw center of mass
        // TODO: Don't really need this
        PVector pos = new PVector();
        pushStyle();
        strokeWeight(15);
        for (int userId = 1; userId <= 10; userId++)
        {
            if(context.isTrackingSkeleton(userId)){
                context.getCoM(userId, pos);
                PVector displayPos = new PVector();
                context.convertRealWorldToProjective(pos, displayPos);
                stroke(0, 255, 0);
                point(displayPos.x, displayPos.y);
            }
        }
        popStyle();

        // Draw the skeleton if it's available
        for(int i = 0; i < 10; i++){
            if(context.isTrackingSkeleton(i)){
                fill(0, 0, 255);
                drawSkeleton(i);

                if(checkGestures == 3){
                    checkGestures = 0;
                    PVector[] joints = new PVector[GestureInfo.JOINTS_LENGTH];

                    PVector jointPos = new PVector();
                    context.getJointPositionSkeleton(i,SimpleOpenNI.SKEL_LEFT_ELBOW,jointPos);
                    joints[GestureInfo.LEFT_ELBOW] = jointPos;

                    PVector jointPos1 = new PVector();
                    context.getJointPositionSkeleton(i,SimpleOpenNI.SKEL_LEFT_HAND,jointPos1);
                    joints[GestureInfo.LEFT_HAND] = jointPos1;

                    PVector jointPos2 = new PVector();
                    context.getJointPositionSkeleton(i,SimpleOpenNI.SKEL_LEFT_SHOULDER,jointPos2);
                    joints[GestureInfo.LEFT_SHOULDER] = jointPos2;

                    PVector jointPos3 = new PVector();
                    context.getJointPositionSkeleton(i,SimpleOpenNI.SKEL_TORSO,jointPos3);
                    joints[GestureInfo.TORSO] = jointPos3;

                    PVector jointPos4 = new PVector();
                    context.getJointPositionSkeleton(i,SimpleOpenNI.SKEL_RIGHT_ELBOW,jointPos4);
                    joints[GestureInfo.RIGHT_ELBOW] = jointPos4;

                    PVector jointPos5 = new PVector();
                    context.getJointPositionSkeleton(i,SimpleOpenNI.SKEL_RIGHT_HAND,jointPos5);
                    joints[GestureInfo.RIGHT_HAND] = jointPos5;

                    PVector jointPos6 = new PVector();
                    context.getJointPositionSkeleton(i,SimpleOpenNI.SKEL_RIGHT_SHOULDER,jointPos6);
                    joints[GestureInfo.RIGHT_SHOULDER] = jointPos6;

                    PVector jointPos7 = new PVector();
                    context.getJointPositionSkeleton(i,SimpleOpenNI.SKEL_HEAD,jointPos7);
                    joints[GestureInfo.HEAD] = jointPos7;

                    PVector jointPos8 = new PVector();
                    context.getJointPositionSkeleton(i,SimpleOpenNI.SKEL_NECK,jointPos8);
                    joints[GestureInfo.NECK] = jointPos8;

                    PVector jointPos9 = new PVector();
                    context.getJointPositionSkeleton(i,SimpleOpenNI.SKEL_LEFT_KNEE,jointPos9);
                    joints[GestureInfo.LEFT_KNEE] = jointPos9;

                    PVector jointPos10 = new PVector();
                    context.getJointPositionSkeleton(i,SimpleOpenNI.SKEL_RIGHT_KNEE,jointPos10);
                    joints[GestureInfo.RIGHT_KNEE] = jointPos10;


                    Gesture response = controller.updateGestures(joints);

                    if(response != null){
                        currGesture = response;
                    }
                } else {
                    checkGestures++;
                }
            }

        }
        gestureInfoFrame.s.fill(0);
        gestureInfoFrame.s.rect(0, 0, gestureInfoFrame.w, gestureInfoFrame.h);
        if(currGesture != null) {
            if(currGesture.confidence > 0.1f) {
                //Erase previous
                if(currGesture.confidence > 0) {
                    gestureInfoFrame.s.fill(255, 255, 255);
                    gestureInfoFrame.s.text(currGesture.name + ". Con: " + currGesture.confidence + ". Dur: "
                            + currGesture.duration + ". Tempo: " + currGesture.tempo, 0, 50);
                }
            }
        }
    }

    // Draw the skeleton with the selected joints
    public void drawSkeleton(int userId)
    {
        // to get the 3d joint data
        /*
        PVector jointPos = new PVector();
        context.getJointPositionSkeleton(userId,SimpleOpenNI.SKEL_NECK,jointPos);
        println(jointPos);
        */

        context.drawLimb(userId, SimpleOpenNI.SKEL_HEAD, SimpleOpenNI.SKEL_NECK);

        context.drawLimb(userId, SimpleOpenNI.SKEL_NECK, SimpleOpenNI.SKEL_LEFT_SHOULDER);
        context.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_SHOULDER, SimpleOpenNI.SKEL_LEFT_ELBOW);
        context.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_ELBOW, SimpleOpenNI.SKEL_LEFT_HAND);

        context.drawLimb(userId, SimpleOpenNI.SKEL_NECK, SimpleOpenNI.SKEL_RIGHT_SHOULDER);
        context.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_SHOULDER, SimpleOpenNI.SKEL_RIGHT_ELBOW);
        context.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_ELBOW, SimpleOpenNI.SKEL_RIGHT_HAND);

        context.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_SHOULDER, SimpleOpenNI.SKEL_TORSO);
        context.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_SHOULDER, SimpleOpenNI.SKEL_TORSO);

        context.drawLimb(userId, SimpleOpenNI.SKEL_TORSO, SimpleOpenNI.SKEL_LEFT_HIP);
        context.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_HIP, SimpleOpenNI.SKEL_LEFT_KNEE);
        context.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_KNEE, SimpleOpenNI.SKEL_LEFT_FOOT);

        context.drawLimb(userId, SimpleOpenNI.SKEL_TORSO, SimpleOpenNI.SKEL_RIGHT_HIP);
        context.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_HIP, SimpleOpenNI.SKEL_RIGHT_KNEE);
        context.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_KNEE, SimpleOpenNI.SKEL_RIGHT_FOOT);
    }


    public void sendJointPosition(int userId) {
        PVector centerOfMass = new PVector();

        //////////////////
        // HEAD AND TORSO
        //////////////////

        // Get the joint position of the right hand
        context.getCoM(userId, centerOfMass);

        // Create an OSC message
        OscMessage gestureMessage = new OscMessage("/" + currGesture.name);


        // Send joint position of all axises by OSC
        gestureMessage.add(centerOfMass.x);
        gestureMessage.add(centerOfMass.y);
        gestureMessage.add(centerOfMass.z);


        gestureMessage.add(currGesture.confidence);
        gestureMessage.add(currGesture.duration);
        gestureMessage.add(currGesture.tempo);

        oscP5.send(gestureMessage, myRemoteLocation);
    }

// SimpleOpenNI events

    public void onNewUser(int userId) {
        println("onNewUser - userId: " + userId);
        println("  start pose detection");

        if(autoCalib) {
            context.requestCalibrationSkeleton(userId, true);
        } else {
            context.startPoseDetection("Psi", userId);
        }
    }

    public void onLostUser(int userId) {
        println("onLostUser - userId: " + userId);
    }

    public void onStartCalibration(int userId) {
        println("onStartCalibration - userId: " + userId);
    }

    public void onEndCalibration(int userId, boolean successfull) {
        println("onEndCalibration - userId: " + userId + ", successfull: " + successfull);

        if (successfull) {
            println("  User calibrated !!!");
            context.startTrackingSkeleton(userId);
        } else {
            println("  Failed to calibrate user !!!");
            println("  Start pose detection");
            context.startPoseDetection("Psi", userId);
        }
    }

    public void onStartPose(String pose, int userId) {
        println("onStartPose - userId: " + userId + ", pose: " + pose);
        println(" stop pose detection");

        context.stopPoseDetection(userId);
        context.requestCalibrationSkeleton(userId, true);

    }

    public void onEndPose(String pose, int userId) {
        println("onEndPose - userId: " + userId + ", pose: " + pose);
    }


    static public void main(String args[]) {
        PApplet.main(new String[] { "--bgcolor=#FFFFFF", "GestureTest" });
    }
}
