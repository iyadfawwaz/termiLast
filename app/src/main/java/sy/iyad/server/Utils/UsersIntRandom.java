package sy.iyad.server.Utils;


import java.util.Random;


public class UsersIntRandom {

    protected int rand;

    public int detectRand(int length) {

        double pow = Math.pow(10.0d, length - 1);
        double nextInt =  new Random().nextInt(((int) Math.pow(10.0d, length - 1)) * 9);
        int i = (int) (pow + nextInt);
        this.rand = i;
        return i;
    }
}
