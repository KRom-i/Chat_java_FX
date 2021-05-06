package Server;

import javafx.application.Application;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class ServerChat {
    private Vector<ClientHandler> users;
    private final Logger LOGGER = LogManager.getLogger(Application.class);

    public ServerChat() {

        users = new Vector<>();
        ServerSocket serverSocket = null;
        Socket socket = null;

        try {
            AuthSetvice.connect();
            serverSocket = new ServerSocket(6000);
            LOGGER.info(String.format("Server [%s] start", serverSocket.getLocalSocketAddress()));

            while (true){
                socket = serverSocket.accept();
                LOGGER.info(String.format("Client [%s] try to connect", socket.getInetAddress()));
             new ClientHandler(this, socket);

            }

        } catch (IOException e){
            LOGGER.error("Error! Server start", e);
        } finally {
            try {
                socket.close();
                LOGGER.info("Socket close");
            } catch (IOException e) {
                LOGGER.error("Error! Socket close",e);
            }

            try {
                serverSocket.close();
                LOGGER.info("Server socket close");
            } catch (IOException e) {
                LOGGER.error("Error! Server socket close",e);
            }

            AuthSetvice.disconnect();
            LOGGER.info(String.format("Server [%s] close", serverSocket.getLocalSocketAddress()));
        }
    }

    public void subscribe(ClientHandler client){
        users.add(client);
        broadcastContactsList();
        String log = ("User ["+ client.getNickname()+ "] connected");
        LOGGER.info(log);
    }

    public void unsubscribe(ClientHandler client){
        users.remove(client);
        broadcastContactsList();
        String log = ("User [" + client.getNickname() + "] disconnected");
        LOGGER.info(log);
    }

    //    Отправку сообщения всем пользователям.
    public void broadcastMSG(ClientHandler from, String str){

        FileHistoryMSG.writeFileHistoryMSG(str, false);

        for (ClientHandler c: users) {

//            Проверка на наличие nickname (получателя/отправителя) в blacklist (отправителя/получателя).
            if (!c.checkBlackList(from.getNickname())
                    && !from.checkBlackList(c.getNickname())){
                c.sendMSG(str);
            }
        }

        LOGGER.info(String.format("User [%s] out broadcast MSG", from.getNickname()));
    }

//      Отправка приватных сообщений.
    public void privateMSG(String nickOut, String nickIn, String str){

        boolean writeHistory = false;
        String msg = String.format("%s send for %s msg: %s",nickOut,nickIn,str);

        for (ClientHandler c: users) {
//            Сообщение отправляется только двум пользователямю.
            if (c.getNickname().equals(nickOut) || c.getNickname().equals(nickIn)) {

//                Проверка наличия nickname отправителя в blacklist получателя.
                if (!c.checkBlackList(nickOut)){

                    c.sendMSG(msg);

                    if (!writeHistory){
                        FileHistoryMSG.writeFileHistoryMSG( msg, true);
                        writeHistory = true;
                    }
                }
            }
        }
        LOGGER.info(String.format("User [%s] out private MSG", nickOut));
    }

    public boolean checkNick(String nick){
        for (ClientHandler c: users
        ) {
            if(nick.equals(c.getNickname())){
                return true;
            }
        }
        return false;
    }


//    Отправка списка онлайн пользователей.
    public void broadcastContactsList() {

        StringBuilder strSB = new StringBuilder();
        strSB.append("/clientlist ");

        for (ClientHandler c: users
        ) {
            strSB.append(c.getNickname() + " ");
        }

        String outCmd = strSB.toString();

        for (ClientHandler c: users) {
                c.sendMSG(outCmd);
            }
    }


    public void loggerInfo(String strInfoLoggerr){
        LOGGER.info(strInfoLoggerr);
    }

    public void loggerError(Exception e){
        LOGGER.error(e);
    }
}