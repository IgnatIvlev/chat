package chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        ConsoleHelper.writeMessage("Введите порт сервера:");
        int port = ConsoleHelper.readInt();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            ConsoleHelper.writeMessage("Чат сервер запущен.");
            while (true) {
                // Ожидаем входящее соединение и запускаем отдельный поток при его принятии
                Socket socket = serverSocket.accept();
                new Handler(socket).start();
            }
        } catch (Exception e) {
            ConsoleHelper.writeMessage("Произошла ошибка при запуске или работе сервера.");
        }
    }

    private static class Handler extends Thread {
        private Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            ConsoleHelper.writeMessage("Установленно соединение с удаленным адресом " + socket.getRemoteSocketAddress());
            String nameUser = null;
            
            try (Connection connection = new Connection(socket)) {
                nameUser = serverHandshake(connection);
                sendBroadcastMessage(new Message(MessageType.USER_ADDED, nameUser));
                notifyUsers(connection, nameUser);
                serverMainLoop(connection, nameUser);
            } catch (IOException | ClassNotFoundException e) {
                ConsoleHelper.writeMessage("Ошибка при обмене данными с " + socket.getRemoteSocketAddress());
            }
            if(nameUser != null){
                connectionMap.remove(nameUser);
                sendBroadcastMessage(new Message(MessageType.USER_REMOVED, nameUser));
            }
            ConsoleHelper.writeMessage("Соединение с " + socket.getRemoteSocketAddress() + " закрыто.");
        }

        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException{
            boolean result = false;
            String userName = null;

            while(!result){
                ConsoleHelper.writeMessage("Пожалуйста введите имя: ");
                connection.send(new Message(MessageType.NAME_REQUEST));
                Message message = connection.receive();
                userName = message.getData();
                MessageType type = message.getType();

                if(type != MessageType.USER_NAME)
                    continue;

                if(userName.isEmpty())
                    continue;

                if(connectionMap.containsKey(userName))
                    continue;



                connectionMap.put(userName, connection);
                ConsoleHelper.writeMessage("Ваше имя принято!");
                connection.send(new Message(MessageType.NAME_ACCEPTED));
                result = true;

            }

           return userName;
        }

        private void notifyUsers(Connection connection, String userName) throws IOException{
            for(Map.Entry<String, Connection> entry:connectionMap.entrySet()){
                String name = entry.getKey();
                if(!name.equals(userName)){
                    connection.send(new Message(MessageType.USER_ADDED, name));
                }
            }
        }

        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException{
            while (true) {
                Message message = connection.receive();
                if (message.getType() == MessageType.TEXT) {
                    String newText = userName + ": " + message.getData();
                    Message sendMessage = new Message(message.getType(), newText);
                    sendBroadcastMessage(sendMessage);
                }
                else
                    ConsoleHelper.writeMessage("Некорректный тип сообщения!");
            }

        }
    }

    public static void sendBroadcastMessage(Message message){
        for(Map.Entry<String, Connection> entry:connectionMap.entrySet()){
            try {
                entry.getValue().send(message);
            } catch (IOException e) {
                ConsoleHelper.writeMessage("Не смогли отправить сообщение пользователю:" + entry.getValue().getRemoteSocketAddress());
            }
        }
    }
}
