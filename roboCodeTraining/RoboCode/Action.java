package roboCodeTraining.RoboCode;

import java.util.LinkedList;

public class Action<T> extends LinkedList<T> {

    public int action;
    public double[] encodedAction;

    public Action(int action){
        this.action = action;
        switch(action){
            case 0:
                this.encodedAction = new double[]{-1.0, -1.0, -1.0, -1.0, 1.0};
                break;
            case 1:
                this.encodedAction = new double[]{-1.0, -1.0, -1.0, 1.0, -1.0};
                break;
            case 2:
                this.encodedAction = new double[]{-1.0, -1.0, 1.0, -1.0, -1.0};
                break;
            case 3:
                this.encodedAction = new double[]{-1.0, 1.0, -1.0, -1.0, -1.0};
                break;
            case 4:
                this.encodedAction = new double[]{1.0, -1.0, -1.0, -1.0, -1.0};
                break;
            default:
                this.encodedAction = new double[]{-1.0, -1.0, -1.0, -1.0, -1.0};
                break;
        }


    }

    public double[] getEncoded(){
        double[] encoded = new double[5];
        for (int i =0 ; i<5; i++){
            encoded[i] = encodedAction[i];
        }
        return encoded;
    }
}
