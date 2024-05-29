import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import org.json.JSONObject;
import java.util.Scanner;

public class Network {
    Socket client;
    String server;
    int port;
    public String p;

    public Network() {
        this.server = "localhost";
        this.port = 5555;
        try {
            this.client = new Socket(server, port);
            this.p = connect();
            System.out.println("Verbindung erfolgreich");
            System.out.println("Player: " + this.p);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String connect() {
        return send("\"get\"").substring(0, 1);
    }

    public String send(String data) {
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter writer = new PrintWriter(client.getOutputStream(), true);

            //String json = new JSONObject().put("request", "get").toString();

            writer.println(data);
            writer.flush();

            char[] buffer = new char[2048]; // Adjust the buffer size as needed
            int bytesRead = reader.read(buffer); // Read data into the buffer
            String result = "";
            if (bytesRead != -1) {
                result = new String(buffer, 0, bytesRead); // Convert the read characters to a String
                //System.out.println("Received:");
                //System.out.println(result);
            } else {
                System.out.println("End of stream reached.");
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        Network network = new Network();
        System.out.println(network.send("\"get\""));

        Scanner in = new Scanner(System.in);
        String line = "";
        line = in.nextLine();
        System.out.println("line read");

        //System.out.println(network.send("\"E2-F2\""));
        System.out.println(network.send("\"E7-F7\""));

        line = in.nextLine();
        System.out.println("line read");

        System.out.println(network.send("\"get\""));

//        System.out.println(network.send(json));
//        System.out.println(json);
//        String response = network.send(json);
//        System.out.println(response);
    }
}
