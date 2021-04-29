public class MyCharArr {

//    Объект принимает char[].

    private final Object mon = new Object();

    private char[] chars;

    private volatile char currentLetter;


    public MyCharArr(char[] chars){
        this.chars = chars;
        this.currentLetter = chars[0];
    }


//    Количество Threads = char[].length.

    public void printThreads(int numberPrint) {

        for (int i = 0; i < chars.length; i++) {

            final int NUM_T = i;

            new Thread(() -> {

                synchronized (mon) {

                    try {

                        for (int j = 0; j < numberPrint; j++) {

                            while (currentLetter != chars[NUM_T]) {
                                mon.wait();
                            }

                            System.out.print(chars[NUM_T]);

                            if (NUM_T == chars.length - 1) {
                                currentLetter = chars[0];
                            } else {
                                currentLetter = chars[NUM_T + 1];
                            }

                            mon.notifyAll();

                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }).start();

        }

    }

    public static void main (String ... agrs){

        char[] c = {'A','B','C'};

        MyCharArr myCharArr = new MyCharArr(c);

        myCharArr.printThreads(5);

    }
}
