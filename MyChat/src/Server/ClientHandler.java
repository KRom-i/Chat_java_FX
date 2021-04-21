package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class ClientHandler {

    private ServerChat server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String nickname;
    private List<String> blacklist;
    private boolean checkAuth;
    private boolean isExit;
    private boolean registration;
    private List<String> history;

    public ClientHandler(ServerChat server, Socket socket) {

        try {

            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.blacklist = new LinkedList<>();

            new Thread(() ->{

                isExit = false;
                checkAuth = false;

                try {
                    while (true){

                        String str = in.readUTF();

//                      Обработка запроса на авторизацию пользователя.
                        if(str.startsWith("/auth ")) {

                            String[] tokens = str.split(" ");
                            String nick = AuthSetvice.getNicknameByLoginAndPassword(
                                    tokens[1], tokens[2]);

                            if (nick != null && !server.checkNick(nick)) {

                                checkAuth = true;
                                sendMSG("/auth-OK " + nick);

//                              Отправка истории сообщений авториз. пользователю.
                                history = AuthSetvice.historyMsg(nick);
                                if (history.size() > 0){
                                    for (int i = 0; i < history.size(); i++) {
                                        sendMSG(history.get(i));
                                    }
                                }

                                setNickname(nick);

//                              Обновление blacklist авториз. пользователя.
                                server.subscribe(ClientHandler.this);
                                blacklist = AuthSetvice.blacklistIni(getNickname());
                                break;

                            } else {
                                sendMSG("Wrong Login/password");
                            }

//                      Обработка запроса на регистрацию нового пользователя.
                        } else if (str.startsWith("/signup")) {

                            registration = true;

                                String[] tokensReg = str.split(" ");

                                if (AuthSetvice.checkReg("login", tokensReg[1])){

                                    sendMSG("login failed");

                                } else if (AuthSetvice.checkReg("nickname", tokensReg[3])){

                                    sendMSG("nickname failed");

                                } else if (AuthSetvice.addUser(tokensReg[1], tokensReg[2], tokensReg[3])) {

                                    sendMSG("Successful registration");
                                    System.out.printf("Successful registration log [%s], pass [%s], nick [%s]",
                                            tokensReg[1], tokensReg[2].hashCode(), tokensReg[3]);

                                } else {

                                    sendMSG("Registration failed");
                                    System.out.printf("Registration failed log [%s], pass [%s], nick [%s]",
                                            tokensReg[1], tokensReg[2].hashCode(), tokensReg[3]);

                                }

                            }

//                      Комманда disconnect.
                        if(str.equals("/end")){

                            out.writeUTF("/serverClosed");
                            System.out.printf("Client [%s] disconnected \n", socket.getInetAddress());
                            isExit = true;
                            break;

                        }


                    }

                    if (!isExit) {

                        while (true) {

                            String str = in.readUTF();

//                          Обработка комманд от пользователя.
                            if (str.startsWith("/") || str.startsWith("@")) {

//                               Комманда disconnect.
                                if (str.equals("/end")) {
                                    out.writeUTF("/serverClosed");
                                    System.out.printf("Client [%s] disconnected \n", socket.getInetAddress());
                                    break;

//                              Обработка приватных сообщений.
                                } else if (str.startsWith("@")) {

                                    String[] tokensMSG = str.split(" ", 2);
                                    String nickOut = tokensMSG[0].substring(1, tokensMSG[0].length());

//                                    Проверка наличия nickname получателя в blackList отправителя.
                                    if (!checkBlackList(nickOut)) {

//                                    Проверка наличия nickname получателя в списке онлайн пользователей.
                                        if (server.checkNick(nickOut)) {
                                            server.privateMSG(getNickname(), nickOut, tokensMSG[1]);

                                        } else {
                                            sendMSG("Error! Nick [" + nickOut + "] not found");
                                        }
                                    } else {
                                        sendMSG("Nick [" + nickOut + "] you blacklist");
                                    }

//                              Изменение blacklist.
                                } else if (str.startsWith("/blacklist")) {

                                    String[] nickBlackList = str.split(" ");
                                    String nickBlack = nickBlackList[1];

//                                    Проверка на совпадение c ClientHandler.nickname.
                                    if (!nickBlack.equalsIgnoreCase(getNickname())) {

//                                        Проверяем существует ли данный nickname.
                                        if (AuthSetvice.checkReg("nickname", nickBlack)) {

                                            if (!blacklist.contains(nickBlack)) {

//                                                Добавление в blacklist.
                                                if (AuthSetvice.addUserBlackList(getNickname(), nickBlack) == 1){
                                                    blacklist.add(nickBlack);
                                                    sendMSG("You added [" + nickBlack + "] to blacklist");
                                                } else {
                                                    sendMSG("Error! Added to blacklist");
                                                }

                                            } else {

//                                            При наличии данного nickname в blacklist (уладяем и
//                                            обновляем список).
                                                    AuthSetvice.delUserBlackList(getNickname(), nickBlack);
                                                    sendMSG("You have removed a [" + nickBlack + "] " +
                                                            "from the blacklist");
                                                    blacklist = AuthSetvice.blacklistIni(getNickname());

                                                 }
                                        } else {
                                            sendMSG("Error! user [" + nickBlack + "] does not exist");
                                        }
                                    } else {
                                        sendMSG("Error! Unable to add your nickname to the blacklist");
                                    }

//                              Актуализация списка пользователей онлайн.
                                } else if (str.startsWith("/clientlist")) {

                                    server.broadcastContactsList();
                                }

//                          Отправка сообщения всем пользователям.
                            } else {
                                server.broadcastMSG(this, nickname + ": " + str);
                            }
                        }
                    }
                } catch (IOException e){
                    e.printStackTrace();
                } finally {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                server.unsubscribe(this);

            }).start();

//            Thread на закрытие соединения по таймауту стартует при попытке авторизации.
//            При регистрации логика следущая (connect -> out -> in -> disconnect).
            if (!registration) {
                new Thread(() -> {
                    try {
                        Thread.sleep(120000);
                        if (!checkAuth) {
                            try {
                                out.writeUTF("/endTimeAuth");
                                System.out.printf("Client [%s] disconnected (auth=false)", socket.getInetAddress());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void setNickname(String nick) {
        this.nickname = nick;
    }

    public String getNickname() {
        return nickname;
    }

    public void sendMSG (String str){
     try {
         out.writeUTF(str);
     } catch (IOException e) {
         e.printStackTrace();
     }
 }

    public boolean checkBlackList(String nickname) {
        return blacklist.contains(nickname);
    }
}
