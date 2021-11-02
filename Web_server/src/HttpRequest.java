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
        // 從 socket獲取input跟output的連線
        InputStream is = socket.getInputStream();
        DataOutputStream os = new DataOutputStream(socket.getOutputStream());

        //暫存使用者傳輸的資訊到Buffer
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        //以request line取得Http訊息
        String requestLine = br.readLine();


        //查看request line
        System.out.println();
        System.out.println(requestLine);


        //從request line裡提取filename
        StringTokenizer tokens = new StringTokenizer(requestLine);
        tokens.nextToken();  // skip over the method, which should be "GET"
        String fileName = tokens.nextToken();


        // 在filename前加 "." 表示file在此folder
        fileName = "." + fileName;


        //開啟request的file
        FileInputStream fis = null;
        boolean fileExists = true;
        try {
            fis = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            fileExists = false;
        }


        //建立需回應的資訊
        String statusLine = null;
        String contentTypeLine = null;
        String entityBody = null;


        //檢查file使否存在
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


        //傳輸狀態
        os.writeBytes(statusLine);
        //傳輸內容種類
        os.writeBytes(contentTypeLine);
        //傳輸空行來表示標題的結束
        os.writeBytes(CRLF);


        //傳輸正文
        if (fileExists)	{
            sendBytes(fis, os);
            os.writeBytes(statusLine);
            fis.close();
        } else {
            os.writeBytes(statusLine);
            os.writeBytes(entityBody);
            os.writeBytes(contentTypeLine);
        }


        //獲取header
        String headerLine = null;
        while ((headerLine = br.readLine()).length() != 0) {
            System.out.println(headerLine);
        }


        //將連線跟socket都關閉
        os.close();
        br.close();
        socket.close();
    }


    private static void sendBytes(FileInputStream fis, OutputStream os)
            throws Exception
    {
        //建立1KB的buffer
        byte[] buffer = new byte[1024];
        int bytes = 0;


        //複製request的file到output stream
        while((bytes = fis.read(buffer)) != -1 ) { // -1 代表到file的末端
            os.write(buffer, 0, bytes);  //寫進要傳回給使用者的檔案到output stream
        }
    }


    //獲取回傳的file類型名稱，並回傳給使用者
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
