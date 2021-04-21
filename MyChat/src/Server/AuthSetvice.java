package Server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuthSetvice {

    private static Connection connection;

    public static void connect(){

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:chat-db.db");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

    }


//      Добавление нового пользователя в БД.
    public static boolean addUser(String log, String pass, String nick){

        PreparedStatement statement = null;

        try {

            String query = "INSERT INTO users (login, password, nickname) VALUES (?, ?, ?);";
            statement = connection.prepareStatement(query);

            statement.setString(1, log);
            statement.setInt(2, pass.hashCode());
            statement.setString(3, nick);
            statement.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            statementClose(statement);
        }
        return false;
    }

//    Обновление blacklist.
    public static List<String> blacklistIni(String nickname){

        List<String> historyList = new ArrayList<>();
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {

            statement = connection.prepareStatement(
                    "SELECT * FROM blacklist WHERE nickname = ?;"
            );

            statement.setString(1,nickname);

            rs = statement.executeQuery();

            while (rs.next()){
                historyList.add(rs.getString("userBlackList"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            statementClose(statement);
            resultSetClose(rs);
        }

        return historyList;
    }


//    Добавление пользователя в blacklist БД.
    public static int addUserBlackList(String nickname, String usersBlackList){

        PreparedStatement statement = null;

        try {

            String query = "INSERT INTO blacklist (nickname, userBlackList) VALUES (?, ?);";
            statement = connection.prepareStatement(query);

            statement.setString(1, nickname);
            statement.setString(2, usersBlackList);

            return statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            statementClose(statement);
        }
        return 0;
    }

    //    Удаление пользователя из blacklist БД.
    public static void delUserBlackList(String nickname, String usersBlackList){

        PreparedStatement statement = null;

        try {

            String query = "DELETE from blacklist WHERE nickname = ? AND userBlackList = ?;";
            statement = connection.prepareStatement(query);

            statement.setString(1, nickname);
            statement.setString(2, usersBlackList);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            statementClose(statement);
        }

    }

//    Проверка на уникальность "nickname" и "login" при регистрации нового пользователя
    public static boolean checkReg(String strfrom, String strVal){

        PreparedStatement statement = null;
        ResultSet rs = null;

        try {

            statement = connection.prepareStatement(
                    "SELECT * FROM users WHERE " + strfrom + " = ?;"
            );


            statement.setString(1,strVal);

            rs = statement.executeQuery();

            if (rs.next()) {
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            statementClose(statement);
            resultSetClose(rs);
        }

        return false;

    }

//     Записть сообщения в историю БД.
    public static void historyMsgAdd(String nickname, String msg){

        PreparedStatement statement = null;

        try {

            String query = "INSERT INTO historyMSG (nickname, history) VALUES (?, ?);";
            statement = connection.prepareStatement(query);

            statement.setString(1, nickname);
            statement.setString(2, msg);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            statementClose(statement);
        }
    }

//    Запрос истории сообщений.
    public static List<String> historyMsg(String nickname){

        List<String> historyList = new ArrayList<>();
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {

            statement = connection.prepareStatement(
                    "SELECT * FROM historyMSG WHERE nickname = ?;"
            );

            statement.setString(1,nickname);

           rs = statement.executeQuery();

           while (rs.next()){
               historyList.add(rs.getString("history"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            statementClose(statement);
            resultSetClose(rs);
        }

        return historyList;
    }

////    Авторизация пользователя через БД (при ошибке авторизации возвращает null).
//    public static String getNicknameByLoginAndPassword(String login, String password){
//
//        PreparedStatement statement = null;
//        ResultSet rs = null;
//
//        try {
//
//            statement = connection.prepareStatement(
//                    "SELECT * FROM users WHERE login = ?;"
//            );
//
//            statement.setString(1,login);
//
//            rs = statement.executeQuery();
//
//            if (rs.next()){
//
//                int passHash = password.hashCode();
//
//                String nick = rs.getString("nickname");
//                int dbHash = rs.getInt("password");
//
//                if (passHash == dbHash){
//                    return nick;
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            statementClose(statement);
//            resultSetClose(rs);
//        }
//
//        return null;
//    }

    public static void disconnect(){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void statementClose(Statement statement){
        try {
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void resultSetClose(ResultSet resultSet){
        try {
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
