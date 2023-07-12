import com.google.gson.Gson;
import com.itextpdf.io.IOException;
import java.io.*;
import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws Exception {
        BooleanSearchEngine engine = new BooleanSearchEngine (new File("pdfs"));
        System.out.println(engine.search("бизнес"));

        try (ServerSocket serverSocket = new ServerSocket(8989);) {
            while (true) {
                try (
                        Socket socket = serverSocket.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter out = new PrintWriter(socket.getOutputStream());
                ) {
                    String word = in.readLine().toLowerCase();
                    var listPageEntry = engine.search(word);
                    Gson gson = new Gson();
                    out.println(gson.toJson(listPageEntry));
                }
            }
        } catch (IOException e) {
            System.out.println("Не могу стартовать сервер");
            e.printStackTrace();
        }
    }
}