package Server;

import java.io.*;

public class FileHistoryMSG {

    private static final int NUM_STR = 100;
    private static final char CONTROL_CHAR = '@';
    private static final char END_CHAR = '\n';
    private static final String FILE_NAME = "History_MSG_Chat.txt";

//    Метод записывает сообщение в файл.
    public static void writeFileHistoryMSG(String str, boolean privateMSG){

        File file = new File(FILE_NAME);

        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (privateMSG){
            str = CONTROL_CHAR + str;
        }

        try (BufferedWriter bufferedWriter = new BufferedWriter(
                new FileWriter(FILE_NAME, true)
        )){

            bufferedWriter.write(str);
            bufferedWriter.newLine();


        } catch (Exception e){
            e.printStackTrace();
        }
    }

//    Метод возвращает историю сообщений после успешной авторизации.
    public static String readFileHistoryMSG(String nickName){

        File file = new File(FILE_NAME);

        try (RandomAccessFile raf = new RandomAccessFile(file, "r")){

            long lgsFile = file.length() -1;
            int appStr = 0;


            StringBuilder sbStr = new StringBuilder();
            StringBuilder history = new StringBuilder();

            for (long i = lgsFile; i >= 0; i--){

                raf.seek(i);
                char c = (char) raf.read();

                if (c == END_CHAR){

                    if (sbStr.length() > 0){

                        char cBuilder = sbStr.charAt(sbStr.length() - 1);

                        if (cBuilder != CONTROL_CHAR){
//                            Добавляет сообщения общего чата.

                            history.append(sbStr);
                            appStr++;

                        } else if (cBuilder == CONTROL_CHAR){
//                            Добавляет сообщения приватного чата.

                            String[] strings = sbStr.reverse().deleteCharAt(0).toString().split(" ");

                            if (strings.length > 4){
                                if (strings[0].equals(nickName) || strings[3].equals(nickName)){
                                    history.append(sbStr.reverse());
                                    appStr++;
                                }
                            }

                        }

                    }

                    sbStr.setLength(0);
                }

                if (appStr == NUM_STR){
                    break;
                }

                sbStr.append(c);
            }

            return history.reverse().toString();

        }catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }
}
