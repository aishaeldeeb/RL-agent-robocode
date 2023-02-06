package roboCodeTraining.NN;


import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class NeuralNetwork {

    static Matrix weights_ih, weights_ho, bias_h, bias_o,weightChange_ih,weightChange_ho;
    static double l_rate = 0.001;

    double momentumTerm;
    static boolean isBipolar = true;

    public NeuralNetwork(int i, int h, int o, double mTerm) {
        weights_ih = new Matrix(h, i);
        weights_ho = new Matrix(o, h,0);
        weightChange_ih= new Matrix(h, i,0);
        this.momentumTerm = mTerm;
        weightChange_ho=new Matrix(o, h);
        bias_h = new Matrix(h, 1, true);
        bias_o = new Matrix(o, 1, true);
    }

    public double predict(double[] X) {
        Matrix input = Matrix.fromArray(X);
        Matrix hidden = Matrix.multiply(weights_ih, input);
        hidden.add(bias_h);

        if (isBipolar) {
            hidden.customSigmoid();
        } else {
            hidden.sigmoid();
        }


        Matrix output = Matrix.multiply(weights_ho, hidden);
        output.add(bias_o);

        if (isBipolar) {
            output.customSigmoid();
        } else {
            output.sigmoid();
        }


        return output.toDouble();
    }

    public double calculateRMSError(List<Double> actualoutputs, double[] expectedoutputs) {
        double error = 0.0;
        for (int i = 0; i < expectedoutputs.length; i++) {
            error += Math.pow(expectedoutputs[i] - actualoutputs.get(i), 2.0);
        }
        error = Math.sqrt((error)/expectedoutputs.length) * 100;
        return error;
    }
    public double calculateTotalError(List<Double> actualoutputs, double[] expectedoutputs) {
        double error = 0.0;
        for (int i = 0; i < expectedoutputs.length; i++) {
            error += Math.pow(expectedoutputs[i] - actualoutputs.get(i), 2.0);
        }
        error = error * 0.5;
        return error;
    }

    public void fit(double[][] X, double[] Y) {
        PrintStream write = null;


        int epoch = 1;
        ArrayList<double[]> errorVsEpoch = new ArrayList<>();
        double totalError = -0.001;
        double RMSError;
        List<Double> actualoutputs = new ArrayList<>();
        List<Double> expectedoutputs;
//        expectedoutputs = Arrays.asList(-1.0,1.0,1.0,-1.0);
//        if (Main.isBipolar) {
//            expectedoutputs = Arrays.asList(-1.0,1.0,1.0,-1.0);
//        } else {
//            expectedoutputs = Arrays.asList(0.0, 1.0, 1.0, 0.0);
//        }

        do {
            for (int i = 0; i < X.length; i++) {
                this.train(X[i], new double[]{Y[i]});
                double x = predict(X[i]);
                actualoutputs.add(x);

            }
//            double[] actualoutputsArr = new double[actualoutputs.size()];
//            actualoutputsArr = actualoutputs.toArray();
//            totalError = calculateTotalError(actualoutputs, Y);
            RMSError = calculateRMSError(actualoutputs, Y);

//            System.out.println("Total Error at Epoch no: " + epoch + "= " + totalError);
            System.out.println(epoch + "," + RMSError);

            errorVsEpoch.add(new double[]{RMSError, epoch});

            epoch++;
            actualoutputs.clear();



            try {
                write = new PrintStream(new BufferedOutputStream(new FileOutputStream("data1.csv", true)));

                write.println(epoch + "," + RMSError);
                if (write.checkError())
                    System.out.println("Could not save the data!");
                write.close();

            } catch (IOException e) {
            }






        } while (RMSError > 0.05);

        try {
            if (write != null)
                write.flush();
            write.close();
        } catch (Exception e) {

        }

    }

    public void train(double[] X, double[] Y) {


        Matrix input = Matrix.fromArray(X);
        Matrix hidden = Matrix.multiply(weights_ih, input);
        hidden.add(bias_h);

        if (isBipolar) {
            hidden.customSigmoid();
        }else {
            hidden.sigmoid();
        }


        Matrix output = Matrix.multiply(weights_ho, hidden);
        output.add(bias_o);


        if (isBipolar) {
            output.customSigmoid();
        }else{
            output.sigmoid();
        }


        Matrix target = Matrix.fromArray(Y);

        Matrix error = Matrix.subtract(target, output);
        Matrix gradient;

        if (isBipolar) {
            gradient = output.dCustomsigmoid();
        }else{
            gradient = output.dsigmoid();
        }

        gradient.multiply(error);
        gradient.multiply(l_rate);

        Matrix hidden_T = Matrix.transpose(hidden);
        Matrix who_delta = Matrix.multiply(gradient, hidden_T);
        weightChange_ho.multiply(momentumTerm);
        weights_ho.add(who_delta);
        weights_ho.add(weightChange_ho);

        weightChange_ho.add(who_delta);

        bias_o.add(gradient);

        Matrix who_T = Matrix.transpose(weights_ho);
        Matrix hidden_errors = Matrix.multiply(who_T, error);
        Matrix h_gradient;

        if (isBipolar) {
            h_gradient = hidden.dCustomsigmoid();
        }else{
            h_gradient = hidden.dsigmoid();
        }

        h_gradient.multiply(hidden_errors);
        h_gradient.multiply(l_rate);

        Matrix i_T = Matrix.transpose(input);
        Matrix wih_delta = Matrix.multiply(h_gradient, i_T);
        weightChange_ih.multiply(momentumTerm);
        weights_ih.add(wih_delta);
        weights_ih.add(weightChange_ih);

        weightChange_ih.add(wih_delta);
        weights_ih.add(wih_delta);
        bias_h.add(h_gradient);

    }


}