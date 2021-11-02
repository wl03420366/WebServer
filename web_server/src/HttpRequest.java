import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;

final public class HttpRequest implements Runnable
{
    final static String CRLF = "\r\n";
    Socket socket;

    public HttpRequest(Socket socket) throws Exception
    {
        this.socket = socket;
    }

    //接收到HttpRequest的後續反應
    @Override
    public void run()
    {
        try
        {
            processRequest();
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }

    private void processRequest() throws Exception
    {
        // 從 socket 取得 input 和 output 的連線
        InputStream is = socket.getInputStream();
        DataOutputStream os = new DataOutputStream(socket.getOutputStream());

        // 設定 input filters
        //暫存使用者傳輸的資訊到Buffer
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        // 從 request line 取得 Http 訊息
        String requestLine = br.readLine();


        // 顯示 request line
        System.out.println();
        System.out.println(requestLine);

        // 從 request line 中抽出檔案名稱
        StringTokenizer tokens = new StringTokenizer(requestLine);
        tokens.nextToken();  // skip over the method, which should be "GET"
        String fileName = tokens.nextToken();

        // 在檔案名稱加個 "." 代表檔案在目前的資料夾
        fileName = "." + fileName;

        // 打開 request 的檔案
        FileInputStream fis = null;
        boolean fileExists = true;
        try {
            fis = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            fileExists = false;
        }

        // 建構要回應的訊息
        String statusLine = null;
        String contentTypeLine = null;
        String entityBody = null;
        // 檢查檔案是否存在
        if (fileExists) {
            statusLine = "HTTP/1.0 200 OK" + CRLF;
            contentTypeLine = "Content-type: " +
                    contentType(fileName) + CRLF;
        } else {
            statusLine = "HTTP/1.0 404 Not Found" + CRLF;
            contentTypeLine = "Content-type: " + contentType(fileName) + CRLF;
            entityBody = "<HTML>" +
                    "<HEAD><TITLE>Not Found</TITLE></HEAD>" +
                    "<BODY>Not Found</BODY></HTML>";
        }

        // 傳送狀態
        os.writeBytes(statusLine);

        // 傳送內容種類
        os.writeBytes(contentTypeLine);

        // 發送空行表示標題的結束
        os.writeBytes(CRLF);

        // 發送正文
        if (fileExists)	{
            sendBytes(fis, os);
            os.writeBytes(statusLine);
            fis.close();
        } else {
            os.writeBytes(statusLine);
            os.writeBytes(entityBody);
            os.writeBytes(contentTypeLine);
        }

        // 取得 header 並顯示
        String headerLine = null;
        while ((headerLine = br.readLine()).length() != 0) {
            System.out.println(headerLine);
        }

        // 關閉連線和 socket
        os.close();
        br.close();
        socket.close();
    }

    private static void sendBytes(FileInputStream fis, OutputStream os)
            throws Exception
    {
        // 預留 1K 的 buffer
        byte[] buffer = new byte[1024];
        int bytes = 0;

        // 複製 request 的檔案到 output stream
        while((bytes = fis.read(buffer)) != -1 ) { // -1 代表到檔案的尾巴
            os.write(buffer, 0, bytes);  //寫進要傳回給使用者的檔案到output stream
        }
    }

    private static String contentType(String fileName)
    {
        if(fileName.endsWith(".htm") || fileName.endsWith(".html")) {
            return "text/html";
        }
        if(fileName.endsWith(".gif")){
            return "image/gif";
        }
        if(fileName.endsWith(".jpeg") || fileName.endsWith(".jpg")){
            return "image/jpeg";
        }
        return "application.octet-stream";
    }
}
