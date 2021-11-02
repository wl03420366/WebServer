import java.io.* ;
import java.net.* ;

public final class WebServer {
    public static void main(String[] argv) throws Exception {
        //設定1000以上的port值
        int port = 1999;
        ServerSocket serverSocket = null;
        Socket clientSocket = null;


        //開啟socket
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println(e);
        }


        //以無限while handle從Http來的請求
        while(true) {

            //等待TCP request連接
            //查看socket是否接收到request


            try {
                if (serverSocket != null) {
                    clientSocket = serverSocket.accept();
                }
                System.out.println("Client Connection accepted");


                //建立HttpRequest
                HttpRequest request = new HttpRequest(clientSocket);


                //建立線程來處理request
                Thread thread = new Thread(request);
                thread.start();


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
