package roboCodeTraining.RoboCode;

import roboCodeTraining.NN.NeuralNetwork;
import robocode.*;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class BeastRobot extends AdvancedRobot {

    //stats
    public static int rounds;
    public static int totalRounds;
    public static final int avgRounds = 100; //number of round to average the win rate


    public static int numWins;
    public static double winRate;

    public static boolean intermediateRewards = true;
    public static double rewards;
    public static final double positiveReward = 0.02;
    public static final double negativeReward = -0.02;
    public static final double positiveIntermediateReward = 0.01;
    public static final double negativeIntermediateReward = -0.01;
    public static boolean onPolicy = false;

    //my robot actions
    public static final int aheadIncrement = 100;
    public static final int backIncrement = 100;
    public static final int turnIncrement = 90; //degrees
    public static final int left = 0;
    public static final int right = 1;
    public static final int ahead = 2;
    public static final int back = 3;
    public static final int shoot = 4;

    public static String enemyName;
    public static double enemyDistance;
    public static double enemyBearing;
    public static double enemyBearingDegrees;

    //new states

    public static double myEnergy;
    public static  double xPos;
    public static  double yPos;
    public static double heading;
    public static double enemyEnergy;

    public static int leftCount;
    public static int rightCount;
    public static int aheadCount;
    public static int backCount;
    public static int fireCount;
    public static double leftRate;
    public static double rightRate;
    public static double aheadRate;
    public static double backRate;
    public static double fireRate;
    public static double totaleRewrd;
    public static boolean first = true;
    public static int  actionIndex;

    public static int counter;


    public static boolean scan;


    public static double epsilon;


    public static State currentState;
    public static State previousState;
    public static Action currentAction;
    public static Action previousAction;


    static int numInputs = 10;
    static int numOutput = 1;
    static int numHidden = 10;
    static double momentum = 0.9;

    public static final int numActions = 5;
    public static NeuralNetwork nn = new NeuralNetwork(numInputs, numHidden, numOutput, momentum);
    public static double updatedQ;

    public static final double gamma = 0.4;
    public static final double learnRate = 0.9;

    public static List<State> stateLst;
    public static List<Action> actionLst;

    public static List<Double> Qlst;
    public int replayMemSize = 50;

    public static ReplayMemory replay;
    public static ReplayMemory replayAction;

    public static Object[] replayStateArr;
    public static Object[] replayActionArr;

//    public static Object[] experience;
    public static double prevReward;
    public static Experience experience;
    public void run() {


        scan = true;

        if (totalRounds >= 15000){
            epsilon = 0.0;
        }

        else if(totalRounds < 10000 && totalRounds > 15000 ){
            epsilon = 0.6;
        }
        else{
            epsilon = 0.9;
        }

        while (true) {

            if (rounds == avgRounds) {
                //update epsilon value
                System.out.println("New Epsilon: " + epsilon + "\n");
                try {
                    updateLogs();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            if (scan) {
                rewards = 0.0;
                turnRadarLeft(90);

            }


            else {

                counter++;



                //update look up table
                if (first) {
                    counter = 0;
                    first = false;
                    stateLst = new ArrayList<>();
                    actionLst = new ArrayList<>();
                    Qlst = new ArrayList<>();
                    replay = new ReplayMemory<>(replayMemSize);
                    replayAction = new ReplayMemory<>(replayMemSize);

                    replayStateArr = new State[replayMemSize];
                    replayActionArr = new Action[replayMemSize];
//                    experience = new Object[4];


                }



                switch (currentAction.action) {

                    case left: {

                        leftCount++;
                        setTurnLeft(turnIncrement);
                        setAhead(aheadIncrement);
                        execute();
                        break;

                    }
                    case right: {
                        rightCount++;
                        setTurnRight(turnIncrement);
                        setAhead(aheadIncrement);
                        execute();
                        break;

                    }
                    case ahead: {
                        aheadCount++;
                        setAhead(aheadIncrement);
                        execute();
                        break;

                    }
                    case back: {
                        backCount++;
                        setBack(backIncrement);
                        execute();
                        break;

                    }
                    case shoot: {
                        fireCount++;
                        double turn = getHeading() - getGunHeading() + enemyBearingDegrees;
                        if (turn == 360.0 | turn == -360.0) {
                            turn = 0.0;
                        }
                        turnGunRight(turn);
                        fire(3);



                        execute();
                        break;
                    }

                }


                //backstep
                double[] prevStateVector = previousState.getStateVector();
                double[] prevEncodedAction = previousAction.getEncoded();
                double[] prevStateAction = concatenate(prevStateVector, prevEncodedAction);


                updatedQ = backStep(onPolicy, rewards, currentState, currentAction, prevStateAction);

                nn.train(prevStateAction, new double[]{updatedQ});





                if (replay.sizeOf() == replayMemSize){
//                    replayStateArr = replay.sample(replayMemSize);
//                    replayActionArr = replayAction.sample(replayMemSize);
                    Object[] experiences = replay.sample(replayMemSize);
                    for (int i = 0; i < replayMemSize; i++){
//                        Object[] exp = (Object[]) experiences[i];
//                        Action futureAction = bestAction((State)exp[3]);
//                        updatedQ = backStep(onPolicy, rewards, (State) replayStateArr[i], currentAction, currentStateAction);

//                        if (i == 0) {
//                            double[] currentStateVector = currentState.getStateVector();
//                            double[] currentEncodedAction = currentAction.getEncoded();
//                            double[] currentStateAction = concatenate(currentStateVector, currentEncodedAction);
//                            updatedQ = backStep(onPolicy, rewards, (State) replayStateArr[i], currentAction, currentStateAction);
//                            nn.train(prevStateAction, new double[]{updatedQ});
//                        }
//                        else {
//
//                            double[] currentStateVector = ((State) replayStateArr[i-1]).getStateVector();
//                            double[] currentEncodedAction = ((Action) replayActionArr[i-1]).getEncoded();
//                            double[] currentStateAction = concatenate(currentStateVector, currentEncodedAction);
//                            updatedQ = backStep(onPolicy, rewards, (State) replayStateArr[i], (Action) replayActionArr[i-1], currentStateAction);
//                            nn.train(prevStateAction, new double[]{updatedQ});
//                        }
                    }
                }

//                replay.add( currentState);
//                replayAction.add(currentAction);
                //backpropagation old Q and new Q associated with...
                scan = true;
            }


        }
    }

    public double backStep(boolean onPolicy, double rewards, State currentState, Action currentAction, double[] prevStateAction){
        double prevQValue = nn.predict(prevStateAction);
        double QCurrent = 0.0;
        if (onPolicy) { //on policy
            double[] stateVector = currentState.getStateVector();
            double[] encodedAction = currentAction.getEncoded();
            double[] currentStateAction = concatenate(stateVector, encodedAction);
            double Q = nn.predict(currentStateAction);
            QCurrent = prevQValue + (learnRate * (rewards + (gamma * Q)) - prevQValue);
//            System.out.println("current Q: " + QCurrent + "\n");

        }

        else { //off policy
            double max = MaxQ(currentState);
            QCurrent = prevQValue + (learnRate *  (rewards + (gamma * max)) - prevQValue);


        }
        return QCurrent;
    }

    public double MaxQ(State currentState) {
        double Qmax = Double.NEGATIVE_INFINITY;
        double[] stateVector = currentState.getStateVector();

        //find state vector
        double[] Qs = new double[5];
        for (int i = 0; i < 5; i++){
            Action action = new Action(i);
            double[] encodedAction = action.getEncoded();
            double[] stateAction = concatenate(stateVector, encodedAction);
            Qs[i] = nn.predict(stateAction);
        }

        for (int i = 0; i < numActions; i++) {


            if (Qs[i]> Qmax) {
                Qmax = Qs[i];

            }
        }
        return Qmax;

    }



    public Action getAction(double epsilon, State currentState){

        double rand =  Math.random();
        if (rand < epsilon) {
            Random randomNum = new Random();
            int random = randomNum.nextInt( numActions);
            return new Action(random);
        }
        else {
            return bestAction(currentState);
        }
    }

    public Action bestAction(State currentState){

        int best = 0;
        double Qmax = Double.NEGATIVE_INFINITY;
        double[] stateVector = currentState.getStateVector();

        //find state vector
        double[] Qs = new double[5];
        for (int i = 0; i < 5; i++){
            Action action = new Action(i);
            double[] encodedAction = action.getEncoded();
            double[] stateAction = concatenate(stateVector, encodedAction);
            Qs[i] = nn.predict(stateAction);
        }

        for (int i = 0; i < numActions; i++) {


            if (Qs[i]> Qmax) {
                Qmax = Qs[i];
                best = i;

            }
        }
        return new Action(best);

    }

    public double[] concatenate(double[] a, double[] b) {
        int aLen = a.length;
        int bLen = b.length;

        @SuppressWarnings("unchecked")
        double[] c = (double[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }


    public void onScannedRobot(ScannedRobotEvent e) {

        enemyBearing = e.getBearingRadians();
        enemyBearingDegrees = e.getBearing();
        enemyDistance = e.getDistance(); //relative distance from enemy
        heading = getHeading();
        enemyName = e.getName();
        enemyEnergy = e.getEnergy();

        xPos = getX();
        yPos = getY();
        myEnergy = getEnergy();



        previousState = currentState;
        previousAction = currentAction;


        prevReward = rewards;
        currentState = new State(enemyDistance/1000.0, xPos/1000.0, yPos/1000.0, myEnergy/100.0, enemyEnergy/100.0);
        //pick action index based on state index
        currentAction = getAction(epsilon, currentState);
        experience = new Experience(previousState, previousAction, rewards, currentState );
        replay.add(experience);

        scan = false;

    }




    public static double normalRelativeAngleDegrees(double angle) {
        return (angle %= 360) >= 0 ? (angle < 180) ? angle : angle - 360 : (angle >= -180) ? angle : angle + 360;
    }

    //execute when winning
    public void onWin(WinEvent event) {
        rewards = positiveReward;
        rounds += 1;
        totalRounds += 1;
        numWins += 1;

        totaleRewrd += rewards;

        double[] prevStateVector = previousState.getStateVector();
        double[] prevEncodedAction = previousAction.getEncoded();
        double[] prevStateAction = concatenate(prevStateVector, prevEncodedAction);

        updatedQ = backStep(onPolicy, rewards, currentState, currentAction, prevStateAction);


        nn.train(prevStateAction, new double[]{updatedQ});

    }

    //execute when losing
    public void onDeath(DeathEvent event) {
        rewards = negativeReward;
        rounds += 1;
        totalRounds += 1;

        double[] prevStateVector = previousState.getStateVector();
        double[] prevEncodedAction = previousAction.getEncoded();
        double[] prevStateAction = concatenate(prevStateVector, prevEncodedAction);

        updatedQ = backStep(onPolicy, rewards, currentState, currentAction, prevStateAction);

        nn.train(prevStateAction, new double[]{updatedQ});

        totaleRewrd += rewards;



    }

    public void updateLogs() throws IOException {
        winRate = 100 * (double) (numWins) / avgRounds;
        leftRate = 100 * (double) (leftCount) / counter;
        rightRate = 100 * (double) (rightCount) / counter;
        aheadRate = 100 * (double) (aheadCount) / counter;
        backRate = 100 * (double) (backCount) / counter;
        fireRate = 100 * (double) (fireCount) / counter;


        System.out.println("Win Rate: " + winRate + "\nnumber of wins: " + numWins + "\nrounds: " + rounds);
        saveWinningRate();

        //reset
        numWins = 0;
        rounds = 0;
        leftCount = 0;
        rightCount = 0;
        aheadCount = 0;
        backCount = 0;
        fireCount = 0;
        counter = 0;
        rewards = 0.0;
    }

    public void saveWinningRate(){
        PrintStream file2 = null;
        try {
            file2 = new PrintStream(new RobocodeFileOutputStream(getDataFile("stats.csv").getAbsolutePath(), true));
            file2.println(totaleRewrd + "," + totalRounds + "," + winRate + "," + epsilon + "," + numWins + "," + rewards + "," + leftRate+ "," + rightRate+ ","+ aheadRate+ ","+ backRate + "," + fireRate);
//            file2.println( String.format("%1.1f %d %1.1f \n", winRate, totalRounds, lut.exploreRate));


            if (file2.checkError())
                System.out.println("can't save win rate");
            file2.close();
        } catch (IOException e) {

        } finally {
            try {
                if (file2 != null)
                    file2.close();
            } catch (Exception e) {

            }
        }
    }

    public void saveNumRounds(){
        PrintStream file3 = null;
        try {
            file3 = new PrintStream(new RobocodeFileOutputStream(getDataFile("number_of_rounds.dat").getAbsolutePath(), true));
            file3.println(totalRounds);

            if (file3.checkError())
                System.out.println("can't save rounds");
            file3.close();
        } catch (IOException e) {

        } finally {
            try {
                if (file3 != null)
                    file3.close();
            } catch (Exception e) {

            }
        }
    }


    //intermediate rewards update
    public void onBulletMissed(BulletMissedEvent e) {
        if (intermediateRewards) {
            rewards = negativeIntermediateReward;

        }
    }

    public void onRobotDeath(RobotDeathEvent e) {
        if (e.getName() == enemyName)
            enemyDistance = 2000; //reset
    }

    public void onHitWall(HitWallEvent e) {

        if (intermediateRewards) {
            rewards = (negativeIntermediateReward - 1);
        }

    }
    public void onBulletHit(BulletHitEvent e) {
        if (intermediateRewards) {
            rewards = positiveIntermediateReward;

        }
    }

    public void onHitByBullet(HitByBulletEvent e) {

        if (intermediateRewards) {
            rewards = negativeIntermediateReward;
        }

    }





}
