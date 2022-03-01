import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class server {
    private ServerSocket serverSocket;

    public server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }
    //
    public void startServer(){
        try {
            while(!serverSocket.isClosed()){

                Socket socket = serverSocket.accept();
                //System.out.println("New connection");
                clientHandler clientHandler = new clientHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void closeServer(){
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(5001);
        server server = new server(serverSocket);
        server.startServer();
    }
}