package chat;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketAddress;

public class Connection implements Closeable {
    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;

    public Connection(Socket socket) {
        this.socket = socket;
        try {
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
        }
        catch (IOException e){
            throw new RuntimeException();
        }
    }

    public void send(Message message)throws IOException{
        synchronized (out) {
            this.out.writeObject(message);
        }
    }

    public Message receive() throws IOException{
        try {
            synchronized (in){
             return (Message) in.readObject();
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public SocketAddress getRemoteSocketAddress(){
        return this.socket.getRemoteSocketAddress();
    }

    public void close() throws IOException{
        this.socket.close();
        this.out.close();
        this.in.close();
    }
}
