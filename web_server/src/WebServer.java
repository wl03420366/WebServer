import java.io.* ;
import java.net.* ;

public final class WebServer {
    public static void main(String[] argv) throws Exception {
        // 設定 port
        int port = 1999;
        ServerSocket serverSocket = null;
        Socket clientSocket = null;

        // 打開 socket
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println(e);
        }

        // 使用無限迴圈處理來自 Http 的請求
        while(true) {
            // 等待 TCP 請求連接
            //檢查socket有沒有接收到請求
            try {
                if (serverSocket != null) {
                    clientSocket = serverSocket.accept();
                }
                System.out.println("Client Connection accepted");

                // 創建 HttpRequest 物件
                HttpRequest request = new HttpRequest(clientSocket);

                // 建新的線程來處理請求
                Thread thread = new Thread(request);

                // 線程開始
                thread.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
