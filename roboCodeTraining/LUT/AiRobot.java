package roboCodeTraining.LUT;


import robocode.*;


import java.io.IOException;
import java.io.PrintStream;

public class AiRobot extends AdvancedRobot {

    public static LookUpTable lut = new LookUpTable();

    //stats
    public static int rounds;
    public static int totalRounds;
    public static final int avgRounds = 100; //number of round to average the win rate


    public static int numWins;
    public static double winRate;

    public static boolean intermediateRewards = true;
    public static double rewards;
    public static final double positiveReward = 5;
    public static final double negativeReward = -5;
    public static final double positiveIntermediateReward = 0.5;
    public static final double negativeIntermediateReward = -0.5;
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
    public static double distance2Enemy;
    public static double distance2Center;

    public static int quantizedMyEnergy;
    public static int quantizedEnemyEnergy;
    public static int quantizedDistance2Enemy;
    public static int quantizedDistance2Center;



    public static int quantizedEnemyDistance;
    public static int quantizedXPos;
    public static int quantizedYPos;
    public static int quantizedEnemyBearingCos;
    public static int quantizedEnemyBearingSin;
    public static int quantizedHeadingCos;
    public static int quantizedHeadingSin;

    public static double gunTurnAmt;


    public static int count;

    public static int leftCount;
    public static int rightCount;
    public static int aheadCount;
    public static int backCount;
    public static int fireCount;

    public static int track1;
    public static int track2;

    public static double leftRate;
    public static double rightRate;

    public static double aheadRate;

    public static double backRate;

    public static double fireRate;

    private static byte moveDirection = 1;

    public static double totaleRewrd;
    public static boolean first = true;
    public static int  actionIndex;
    public static int  dist = 50;
    public static double oldEnemyHeading;
    public static int counter;
    public static int interReward;


    public static int terminalReward;

    public static boolean scan;


    public static double epsilon;
    public static double enemyHeading;
    public static double  enemyVelocity;
    public void run() {


        scan = true;


//        loadLUT();

        if (totalRounds == 0){

//            rewards = 0.0;
            loadLUT();

        }


        //initilize epsilon to 0.9

//        lut.initEpsilon(totalRounds);



//        // divorce radar movement from gun movement
//        setAdjustRadarForGunTurn(true);
//        // divorce gun movement from tank movement
//        setAdjustGunForRobotTurn(true);
//        // initial scan
//        setTurnRadarRight(360);

        double bulletPower = Math.min(3.0,getEnergy());

        if (totalRounds >= 3500){
            epsilon = 0.0;
        }
        else if(totalRounds < 3500 && totalRounds > 2000 ){
            epsilon = 0.7;
        }
        else{
            epsilon = 0.9;
        }

        while (true) {


//            if (counter > 5){
//                rewards = 0.0;
//                counter = 0;
//            }

//            turnGunRight(5);

//            rewards = 0.0;


//            xPos = getX();
//            yPos = getY();
//            myEnergy = getEnergy();

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

                quantizedEnemyDistance = lut.quantizeEnemyDistance(enemyDistance);
//            System.out.println("Enemy Distanc: " + quantizedEnemyDistance);

//            quantizedEnemyBearingCos = lut.quantizeHeading(Math.cos(enemyBearingDegrees));
//            quantizedEnemyBearingSin = lut.quantizeHeading(Math.sin(enemyBearingDegrees));
//
//
//            quantizedHeadingCos = lut.quantizeHeading(Math.cos(heading));
//            quantizedHeadingSin = lut.quantizeHeading(Math.sin(heading));

//                track1 = quantizedEnemyEnergy;
//                track2 = quantizedMyEnergy;

                quantizedMyEnergy = lut.quantizeEnergy(myEnergy);
                quantizedEnemyEnergy = lut.quantizeEnergy(enemyEnergy);
                quantizedXPos = lut.quantizeXPos(xPos);
                quantizedYPos = lut.quantizeXPos(yPos);

                //pick action index based on state index
                actionIndex = lut.getAction(epsilon, quantizedEnemyDistance, quantizedXPos, quantizedYPos, quantizedMyEnergy, quantizedEnemyEnergy);
                //update look up table
                if (first) {
                    counter = 0;
                    lut.updateState(quantizedEnemyDistance, quantizedXPos, quantizedYPos, quantizedMyEnergy, quantizedEnemyEnergy, actionIndex);
                    first = false;

                }


                // calculate firepower based on distance
                double firePower = Math.min(500 / enemyDistance, 3);


                switch (actionIndex) {

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


//                        double myX = getX();
//                        double myY = getY();
//                        double absoluteBearing = getHeadingRadians() + enemyBearing;
//                        double enemyX = getX() + enemyDistance * Math.sin(absoluteBearing);
//                        double enemyY = getY() +enemyDistance * Math.cos(absoluteBearing);
//
//                        double enemyHeadingChange = enemyHeading - oldEnemyHeading;
//
//                        oldEnemyHeading = enemyHeading;
//
//                        double deltaTime = 0;
//                        double battleFieldHeight = getBattleFieldHeight(),
//                                battleFieldWidth = getBattleFieldWidth();
//                        double predictedX = enemyX, predictedY = enemyY;
//                        while((++deltaTime) * (20.0 - 3.0 * bulletPower) < Point2D.Double.distance(myX, myY, predictedX, predictedY)){
//                            predictedX += Math.sin(enemyHeading) * enemyVelocity;
//                            predictedY += Math.cos(enemyHeading) * enemyVelocity;
//                            enemyHeading += enemyHeadingChange;
//                            if(	predictedX < 18.0
//                                    || predictedY < 18.0
//                                    || predictedX > battleFieldWidth - 18.0
//                                    || predictedY > battleFieldHeight - 18.0){
//
//                                predictedX = Math.min(Math.max(18.0, predictedX),
//                                        battleFieldWidth - 18.0);
//                                predictedY = Math.min(Math.max(18.0, predictedY),
//                                        battleFieldHeight - 18.0);
//                                break;
//                            }
//                        }
//                        double theta = Utils.normalAbsoluteAngle(Math.atan2(
//                                predictedX - getX(), predictedY - getY()));
//
//                        setTurnRadarRightRadians(Utils.normalRelativeAngle(
//                                absoluteBearing - getRadarHeadingRadians()));
//                        setTurnGunRightRadians(Utils.normalRelativeAngle(
//                                theta - getGunHeadingRadians()));
//                        fire(3);
                        execute();
                        break;
                    }


                    // if the gun is cool and we're pointed at the target, shoot!
//            if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 10)
//                setFire(firePower);

//            if (rewards != 0){
//                rewards = 0.0;
//            }
//                execute();

                }
                lut.updateLUT(onPolicy, rewards, quantizedEnemyDistance, quantizedXPos, quantizedYPos, quantizedMyEnergy, quantizedEnemyEnergy, actionIndex);

                scan = true;
            }


        }
    }



    // normalizes a bearing to between +180 and -180
    double normalizeBearing(double angle) {
        while (angle > 180) angle -= 360;
        while (angle < -180) angle += 360;
        return angle;
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
        double enemyHeading = e.getHeadingRadians();
        double enemyHeadingChange = enemyHeading - oldEnemyHeading;
        double enemyVelocity = e.getVelocity();


        scan = false;
//        //  calculate gun turn toward enemy
//        double turn = getHeading() - getGunHeading() + e.getBearing();
//        // normalize the turn to take the shortest path there
//        setTurnGunRight(normalizeBearing(turn));
//        setTurnRadarLeftRadians(getRadarTurnRemainingRadians());

//        setAhead(100 * avoidWalls());

//

//        scan();



//        if (e.getDistance() < 50 && getEnergy() > 50) {
//            fire(3);
//        } // otherwise, fire 1.
//        else {
//            fire(1);
//        }
        // Call scan again, before we turn the gun



//
//        // Calculate exact location of the robot
//        double absoluteBearing = getHeading() + e.getBearing();
//        double bearingFromGun = normalRelativeAngleDegrees(absoluteBearing - getGunHeading());
//
//        // If it's close enough, fire!
//        if (Math.abs(bearingFromGun) <= 3) {
//            turnGunRight(bearingFromGun);
//            // We check gun heat here, because calling fire()
//            // uses a turn, which could cause us to lose track
//            // of the other robot.
//            if (getGunHeat() == 0) {
//                fire(Math.min(3 - Math.abs(bearingFromGun), getEnergy() - .1));
//            }
//        } // otherwise just set the gun to turn.
//        // Note:  This will have no effect until we call scan()
//        else {
//            turnGunRight(bearingFromGun);
//        }
        // Generates another scan event if we see a robot.
        // We only need to call this if the gun (and therefore radar)
        // are not turning.  Otherwise, scan is called automatically.
//        if (bearingFromGun == 0) {
//            scan();
//        }
    }

    private double avoidWalls() {

        double wall_avoid_distance = 60;
        double fieldHeight = getBattleFieldHeight();
        double fieldWidth = getBattleFieldWidth();
        double centerX = (fieldWidth / 2);
        double centerY = (fieldHeight / 2);
        double currentHeading = getHeading();
        double x = getX();
        double y = getY();
        if (x < wall_avoid_distance || x > fieldWidth -
                wall_avoid_distance) {
            moveDirection *= -1;
        }
        if (y < wall_avoid_distance || y > fieldHeight -
                wall_avoid_distance) {
            moveDirection *= -1;
        }
        return moveDirection;
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

//        if (rounds == avgRounds) {
//            //update epsilon value every 100 rounds
//            lut.initEpsilon(totalRounds);
//            System.out.println("New Epsilon: " + lut.exploreRate + "\n");
//            try {
//                updateLogs();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
        logLUT();
        totaleRewrd += rewards;



    }

    //execute when losing
    public void onDeath(DeathEvent event) {
        rewards = negativeReward;
        rounds += 1;
        totalRounds += 1;

//        if (rounds == avgRounds) {
//            lut.initEpsilon(totalRounds);
//            System.out.println("Updating Stats");
//            try {
//                updateLogs();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
        lut.updateLUT(onPolicy, rewards, quantizedEnemyDistance, quantizedXPos, quantizedYPos, quantizedMyEnergy, quantizedEnemyEnergy, actionIndex);

        logLUT();

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
//        saveNumWin();
        saveWinningRate();
//        saveNumRounds();

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

    public void saveNumWin(){
        PrintStream file1 = null;
        try {
            file1 = new PrintStream(new RobocodeFileOutputStream(getDataFile("number_of_wins.dat").getAbsolutePath(), true));

            file1.println(lut.exploreRate);


            if (file1.checkError())
                System.out.println("can't save number of wins");
            file1.close();
        } catch (IOException e) {

        } finally {
            try {
                if (file1 != null)
                    file1.close();
            } catch (Exception e) {

            }
        }
    }
    public void saveWinningRate(){
        PrintStream file2 = null;
        try {
            file2 = new PrintStream(new RobocodeFileOutputStream(getDataFile("stats.dat").getAbsolutePath(), true));
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



    public void loadLUT() {
        try {
            lut.load((getDataFile("LUTData.dat")));
        } catch (Exception e) {
            out.println(e);
        }
    }

    public void logLUT() {
        try {
            lut.save(getDataFile("LUTData.dat"));
        } catch (Exception e) {
            out.println(e);
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
//        if (e.getName() == enemyName) {
//            if (intermediateRewards) {
//                rewards = positiveIntermediateReward;
//
//            }


//        }

        if (intermediateRewards) {
            rewards = positiveIntermediateReward;

        }
    }

    public void onHitByBullet(HitByBulletEvent e) {
        // Only print if he's not already our target.
//        if (enemyName != null && !enemyName.equals(e.getName())) {
//            out.println("Tracking " + e.getName() + " due to collision");
//        }
//        // Set the target
//        enemyName = e.getName();
//        // Back up a bit.
        // Note:  We won't get scan events while we're doing this!
        // An AdvancedRobot might use setBack(); execute();
//        gunTurnAmt = normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading()));
//        turnGunRight(gunTurnAmt);
////        fire(3);
//        back(50);

//        turnRight(normalRelativeAngleDegrees(90 - (getHeading() - e.getHeading())));
//
//        ahead(dist);
//        dist *= -1;
//        scan();

//        if (e.getName() == enemyName)
//            if (intermediateRewards) {
//                rewards = negativeIntermediateReward;
//            }

        if (intermediateRewards) {
            rewards = negativeIntermediateReward;
        }

    }





}
