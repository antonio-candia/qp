import java.io.*;

public class qpServer {
    public static void main(String[] args) throws IOException {
        new qpServerThread().start();
    }
}
