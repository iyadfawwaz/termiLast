package sy.iyad.server.Utils;


import java.util.Random;


public class UsersIntRandom {

    protected int rand;

    public int ditectRand(int length) {

        double pow = Math.pow(10.0d, (double) (length - 1));
        double nextInt =  new Random().nextInt(((int) Math.pow(10.0d, (double) (length - 1))) * 9);
        int i = (int) (pow + nextInt);
        this.rand = i;
        return i;
    }
}
