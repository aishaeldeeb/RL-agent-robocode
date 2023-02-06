package roboCodeTraining.NN;

import roboCodeTraining.LUT.LookUpTable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.Arrays;


public class Main{
    static int numInputs = 10;
    static int numOutput = 1;
    static int numHidden = 10;
    static double momentum = 0.9;
    public static final int numX = 6;
    public static final int numY = 6;
    public static final int numEnemyDis = 4;
    //    public static final int numHeadingCos = 4;
//    public static final int numHeadingSin = 4;
//    public static final int numBearingCos = 4;
//    public static final int numBearingSin = 4;
    public static final int numEnergy = 4;

    public static final int numActions = 5;

    public static LookUpTable lut = new LookUpTable();
    public static double[][] ActionStateQ = new double[numEnemyDis*numEnergy*numEnergy*numX*numY*numActions][numInputs+1];

    public static double[][] ActionState = new double[numEnemyDis*numEnergy*numEnergy*numX*numY*numActions][numInputs];

    public static double[] Q = new double[numEnemyDis*numEnergy*numEnergy*numX*numY*numActions];


    public static double[] inputArray = new double[numInputs];
    static boolean isBipolar=true;


    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        boolean flag = true;
        while (flag) {

            NeuralNetwork nn = new NeuralNetwork(numInputs, numHidden, numOutput, momentum);


            ActionStateQ = lut.getQStatesActions(("C:\\robocode\\robots\\roboCodeTraining\\MainRobotV4.data\\LUTData.dat"));


            for (int i = 0; i < ActionStateQ.length; i++){
                for (int j = 0; j < ActionState[0].length; j++){
                    ActionState[i][j] = ActionStateQ[i][j];
                }
                Q[i] = ActionStateQ[i][ActionState[0].length]; //last cell
            }

            System.out.println(Arrays.stream(Q).max());
            System.out.println(Arrays.stream(Q).min());

//            List<Double> xorOutput;

            for (int i = 0; i < ActionStateQ.length; i++){

            }

            nn.fit(ActionState, Q);
//            for (double d[] : XOR_INPUT) {
//                xorOutput = nn.predict(d);
//                System.out.println(xorOutput.toString());
//            }

            int i =0;
        }


    }
}