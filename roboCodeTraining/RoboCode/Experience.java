package roboCodeTraining.RoboCode;


import java.util.LinkedList;

public class Experience<T> extends LinkedList<T> {

    public State prevState;
    public Action prevAction;
    public double currentReward;
    public State currentSate;



    public Experience(State prevSate, Action prevAction, double currentReward, State currentSate){
        this.prevState = prevSate;
        this.prevAction = prevAction;
        this.currentReward = currentReward;
        this.currentSate = currentSate;


    }


}
