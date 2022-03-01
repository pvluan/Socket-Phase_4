import java.io.*;
import java.net.Socket;
import java.nio.Buffer;
import java.util.ArrayList;

public class clientHandler implements Runnable {
    public static ArrayList<clientHandler> clients = new ArrayList<>();
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private String clientName;

    public clientHandler(Socket socket) {
        this.socket = socket;
        try {
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());
            this.clientName = objectInputStream.readUTF(); // read client name
            clients.add(this);
            System.out.println("Connected from " + clientName);
            greetingMsg("[SERVER] Hi " + clientName + "Iam NASA's server");//
            broadCastMsg("[SERVER] Welcome " + clientName + "to our server");
            //broadCastMsg ("Server " + clientName + " has joined the chat room");
        } catch (Exception e) {
            closeEveryThing(socket, objectOutputStream, objectInputStream);
        }
    }
    @Override
    public void run() {
        User user = new User();

        while (socket.isConnected()) {
            try {
                user = (User) objectInputStream.readObject();
                objectOutputStream.writeUTF("OK"+ user.getUsername());
                //broadCastMsg(msgFromClient);
            }catch (Exception e) {
                closeEveryThing(socket, objectOutputStream, objectInputStream);
                break;
            }
        }

    }

    public void greetingMsg(String msgToSend) {
        try {
            if (clientName.equals(clientName)) {
                objectOutputStream.writeUTF(msgToSend);
                objectOutputStream.flush();
            }    } catch (IOException e) {
            closeEveryThing(socket, objectOutputStream, objectInputStream);
        }
    }
    public void broadCastMsg(String msgToSend) {
        for (clientHandler client : clients) {
            try {
                if (!client.clientName.equals(clientName)) {
                    client.objectOutputStream.writeUTF(msgToSend);
                    client.objectOutputStream.flush();
                }
            } catch (IOException e) {
                closeEveryThing(client.socket, client.objectOutputStream, client.objectInputStream);
            }

        }
    }
    public void removeClientHandler(){
        clients.remove(this);
        broadCastMsg("Server " + clientName + " has left the chat room");

    }


    public void closeEveryThing(Socket socket, ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream) {
        removeClientHandler();
        try {
            if (objectOutputStream != null) {
                objectOutputStream.close();
            }
            if (objectInputStream != null) {
                objectInputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}