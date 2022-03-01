import java.io.*;
import java.net.Socket;
import java.sql.*;
import java.util.Scanner;
abstract class States{
    static States Start, Login, Register, Logout, Echo, Current;
    void enter() throws SQLException, IOException {}
    void update(){}
}

public class client extends States{

    private static final String USER_CREATE = "INSERT INTO user_info (username, password) VALUES (?, ?)";
    private static final String USER_LOGIN = "SELECT * FROM user_info WHERE username = ? AND password = ?";
    private static final String GET_USER_BY_USERNAME = "SELECT * FROM user_info WHERE username = ?";

    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private String clientName;

    public client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.clientName = username;
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());

        }
        catch (Exception e) {
            closeEverything(socket, objectOutputStream, objectInputStream);
        }
    }

    public void closeEverything(Socket socket, ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream) {
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

    private synchronized User isUserExisted(User user) throws SQLException {
        User existedUser = null;
        try (DBConnection dbHelper = DBConnection.getDBHelper();
             Connection connection = dbHelper.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_USER_BY_USERNAME)) {
                 preparedStatement.setString(1, user.getUsername());
                 ResultSet resultSet = preparedStatement.executeQuery();
                 if (resultSet.next()) {
                     existedUser = new User();
                     existedUser.setUsername(resultSet.getString("username"));
                 }
                 return existedUser;
        }

    }

    private synchronized Boolean createUser(User user) throws SQLException {
        boolean rowUpdated = false;
        try (DBConnection dbHelper = DBConnection.getDBHelper();
             Connection connection = dbHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(USER_CREATE)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            rowUpdated = statement.executeUpdate() > 0;

            return rowUpdated;
        }
    }

    private void send(User user) throws IOException {
        objectOutputStream.writeObject(user);
        objectOutputStream.flush();
    }


    class Start extends States{
        void enter(){
            System.out.println("Select menu.");
        }
        void update(){
            while(true) {
                System.out.println("1. Login");
                System.out.println("2. Register");
                System.out.println("3. Echo");
                System.out.println("4. Exit");
                Scanner sc = new Scanner(System.in);
                String input = sc.nextLine();
                switch (input) {
                    case "1":
                        Current = Login;
                        return;
                    case "2":
                        Current = Register;
                        return;
                    case "3":
                        Current = Echo;
                        return;
                    case "4":
                        System.exit(0);
                    default:
                        System.out.println("Invalid input.");
                }
            }
        }
    }
    class Register extends States {
        void enter() throws IOException {
            System.out.println("Now in state Register");
            Scanner sc = new Scanner(System.in);
            System.out.println("Enter username: ");
            String username = sc.nextLine();
            System.out.println("Enter password: ");
            String password = sc.nextLine();
            User registerUser = new User();
            send(registerUser);
        }

        void update() {
            while (true) {
                System.out.println("This is Register system!");
                System.out.println("Choose the next action");
                System.out.println("1. Login");
                System.out.println("2. Continue Register more user");
                System.out.println("3. Exit");


                Scanner sc = new Scanner(System.in);
                String input = sc.nextLine();
                switch (input) {
                    case "1":
                        Current = Login;
                        return;
                    case "2":
                        Current = Register;
                        return;
                    case "3":
                        System.exit(0);
                    default:
                        System.out.println("Invalid input.");
                }
            }
        }
    }
    class Login extends States{
        void enter() throws IOException{
            System.out.println("Now in state Login");
            Scanner sc = new Scanner(System.in);
            System.out.println("Enter username: ");
            String username = sc.nextLine();
            System.out.println("Enter password: ");
            String password = sc.nextLine();
            User loginUser = new User();
            send(loginUser);
        }
        void update(){
            while(true) {
                System.out.println("This is login system!");
                System.out.println("Choose the next action");
                System.out.println("1. Logout");
                System.out.println("2. Echo");
                System.out.println("3. Exit");
                Scanner sc = new Scanner(System.in);
                String input = sc.nextLine();
                switch (input) {
                    case "1":
                        Current = Logout;
                        return;
                    case "2":
                        Current = Echo;
                        return;
                    //System.exit(0);
                    case "3":
                        System.exit(0);
                    default:
                        System.out.println("Invalid input.");
                }
            }
        }
    }
    class Echo extends States{
        void enter(){
            System.out.println("Echo State");
        }
        void update(){
            while(true) {
                System.out.println("This is Echo!");
                System.out.println("Choose the next action");
                System.out.println("1. Exit");
                System.out.println("2. Back to main menu");

                Scanner sc = new Scanner(System.in);
                String input = sc.nextLine();
                switch (input) {
                    case "1":
                        System.exit(0);
                    case "2":
                        Current = Start;
                        return;
                    default:
                        System.out.println("Invalid input.");
                }
            }
        }
    }
    class Logout extends States{
        void enter(){
            System.out.println("Now in state Logout");
        }
        void update(){
            while(true) {
                System.out.println("Logout successful.");
                System.out.println("Choose the next action");
                System.out.println("1. Login");
                System.out.println("2. Register user");
                System.out.println("3. Exit");

                Scanner sc = new Scanner(System.in);
                String input = sc.nextLine();
                switch (input) {
                    case "1":
                        Current = Login;
                        return;
                    case "2":
                        Current = Register;
                        return;
                    case "3":
                        System.exit(0);
                    default:
                        System.out.println("Invalid input.");
                }
            }
        }
    }


    public void sendMsg(){
        try {
            objectOutputStream.writeUTF(clientName);
            objectOutputStream.flush();

            States.Start = new Start();
            States.Login = new Login();
            States.Register = new Register();
            States.Logout = new Logout();
            States.Echo = new Echo();
            States.Current = States.Start;
            while (socket.isConnected()) {

                States.Current.enter();
                States.Current.update();

            }

        } catch (IOException e) {
            closeEverything(socket, objectOutputStream, objectInputStream);
        } catch (SQLException e) {
            closeEverything(socket, objectOutputStream, objectInputStream);
        }
    }
    public void listenForMsg(){
        new Thread (new Runnable() {
            @Override
            public void run() {
                String msgFromServer;
                while (socket.isConnected()) {
                    try {
                        msgFromServer = objectInputStream.readUTF();
                        System.out.println(msgFromServer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username: ");
        String username = scanner.nextLine();


        //String returnName = BufferedReader.readLine();

        Socket socket = new Socket("localhost", 5001);
        client client = new client(socket, username);


            client.listenForMsg();
            client.sendMsg();



    }
}
