package org.dramatech.atq.gesture;

import processing.core.PVector;

public class Sadness extends Gesture{
    //And therefore right leg



    public Sadness(){
        super();
        super.name="Sadness";
        prevJoints = new PVector[GestureInfo.JOINTS_LENGTH];
    }

    //Checks is birth is happening,
    //In this case hand is below elbow which is below shoulder at first
    public boolean birthChecker(){
        if(state==-1){

            //If leftHand is below leftElbow which is below leftShoulder (or right)
            // println("Ever got hands together?: "+GestureInfo.gesturePieces[GestureInfo.HANDS_TOGETHER]);
            if(GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_DOWN] && GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_DOWN]
                    && GestureInfo.gesturePieces[GestureInfo.LEFT_ARM_VERTICAL] && GestureInfo.gesturePieces[GestureInfo.RIGHT_ARM_VERTICAL]){

                //birth is totes happening
                //print("Got the starter");
                state=0;
                confidence=0.0f;
                return true;
            }
        }
        else if(state==0){
            //If hand is higher than it was previously
            if(prevJoints[GestureInfo.LEFT_HAND]==null){

            }

            if((GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_RISING]) &&
                    (GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_RISING])&&
                    GestureInfo.gesturePieces[GestureInfo.HANDS_TOGETHER] &&
                    !GestureInfo.gesturePieces[GestureInfo.HANDS_ABOVE_HEAD]){
                //println("Got one of them rising");
                duration++;
                PVector diffHand = PVector.sub(joints[GestureInfo.LEFT_HAND], prevJoints[GestureInfo.LEFT_HAND]);
                tempo+=diffHand.mag();
                PVector diffHandR = PVector.sub(joints[GestureInfo.RIGHT_HAND],prevJoints[GestureInfo.RIGHT_HAND]);

                //Average of two vectors
                tempo+=(diffHand.mag()+diffHandR.mag())/2;

                return true;
            }
        }

        return false;
        //return true;
    }


    public boolean lifeChecker(){
        //print("Getting to life of sadness");
        //If hand isn't going up anymore, that's a prerequisite
        if(state==0 || state==1){
            //If is moving back and forth
            if((GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_RISING]) &&
                    (GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_RISING])&&
                    GestureInfo.gesturePieces[GestureInfo.HANDS_TOGETHER] &&
                    GestureInfo.gesturePieces[GestureInfo.HANDS_ABOVE_HEAD]){

                duration++;
                PVector handDiff = new PVector();

                //Average
                handDiff = PVector.sub(joints[GestureInfo.LEFT_HAND],prevJoints[GestureInfo.LEFT_HAND]);
                PVector handDiffR = PVector.sub(joints[GestureInfo.RIGHT_HAND],prevJoints[GestureInfo.RIGHT_HAND]);
                tempo+=(handDiff.mag()+handDiffR.mag());
                return true;
            }
        }




        return false;
    }


    public boolean deathChecker(){
        //print("Getting to death of sadness");
        if(state==1 || state==2){

            if(GestureInfo.gesturePieces[GestureInfo.HANDS_ABOVE_HEAD] && GestureInfo.gesturePieces[GestureInfo.LEFT_HAND_STILL]
                    && GestureInfo.gesturePieces[GestureInfo.RIGHT_HAND_STILL]){

                PVector diffHand = PVector.sub(joints[GestureInfo.LEFT_HAND],prevJoints[GestureInfo.LEFT_HAND]);
                PVector diffHandR = PVector.sub(joints[GestureInfo.RIGHT_HAND],prevJoints[GestureInfo.RIGHT_HAND]);

                //Average
                tempo+=(diffHand.mag()+diffHandR.mag())/2;
                duration++;
                return true;
            }

        }




        return false;

    }
}
