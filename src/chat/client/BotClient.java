package chat.client;

import chat.ConsoleHelper;
import chat.client.Client;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BotClient extends Client {

    public static void main(String[] args) {
        BotClient botClient = new BotClient();
        botClient.run();
    }
    public class BotSocketThread extends chat.client.Client.SocketThread {
        @Override
        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            BotClient.this.sendTextMessage("Привет чатику. Я бот. Понимаю команды: дата, день, месяц, год, время, час, минуты, секунды.");
            super.clientMainLoop();
        }

        @Override
        protected void processIncomingMessage(String message) {
            ConsoleHelper.writeMessage(message);
            
            String[] data = message.split(": ");
            if (data.length != 2) return;
            
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat formatter = null;
            switch (data[1]){
                case ("дата"):
                  //  Calendar calendar = Calendar.getInstance();
                    formatter = new SimpleDateFormat("d.MM.yyyy");
                   // BotClient.this.sendTextMessage("Информация для " + data[0] + ":" + formatter.format(calendar.getTime()));
                    break;
                case ("день"):
                    formatter = new SimpleDateFormat("d");
                   // BotClient.this.sendTextMessage("Информация для " + data[0] + ":" + formatter.format(calendar.getTime()));
                    break;
                case ("месяц"):
                    formatter = new SimpleDateFormat("MMMM");
                 //   BotClient.this.sendTextMessage("Информация для " + data[0] + ":" + formatter.format(calendar.getTime()));
                    break;
                case ("год"):
                    formatter = new SimpleDateFormat("yyyy");
                  //  BotClient.this.sendTextMessage("Информация для " + data[0] + ":" + formatter.format(calendar.getTime()));
                    break;
                case ("время"):
                    formatter = new SimpleDateFormat("H:mm:ss");
                    BotClient.this.sendTextMessage("Информация для " + data[0] + ":" + formatter.format(calendar.getTime()));
                    break;
                case ("час"):
                    formatter = new SimpleDateFormat("H");
                  //  BotClient.this.sendTextMessage("Информация для " + data[0] + ":" + formatter.format(calendar.getTime()));
                    break;
                case ("минуты"):
                    formatter = new SimpleDateFormat("m");
                   // BotClient.this.sendTextMessage("Информация для " + data[0] + ":" + formatter.format(calendar.getTime()));
                    break;
                case ("секунды"):
                    formatter = new SimpleDateFormat("s");
                  //  BotClient.this.sendTextMessage("Информация для " + data[0] + ":" + formatter.format(calendar.getTime()));
                    break;
            }
            if(formatter != null)
                BotClient.this.sendTextMessage("Информация для " + data[0] + ": " + formatter.format(calendar.getTime()));
        }
    }

    @Override
    protected chat.client.Client.SocketThread getSocketThread() {
        return new BotSocketThread();
    }

    @Override
    protected boolean shouldSendTextFromConsole() {
        return false;
    }

    @Override
    protected String getUserName() {
        String newName = "date_bot_" + (int) (Math.random()*100);
        return newName;
    }
}
