# WebServer
學號：M1026157  
姓名：陳正隆  
作業內容：用戶端對WebServer發送HttpRequest，WebServer收到請求後回傳訊息給用戶端。  
編譯方式：以IntelliJ撰寫JAVA程式，並且進行編譯。  
執行方式：以Intellij執行撰寫完成的程式，執行後，對WebServer發送HttpRequest請求，
發送的請求會先暫存在buffer接著傳輸到WebServer，預防傳輸過程塞車的狀況，WebServer收到訊息後回傳資訊給用戶端，
回傳過程中會先檢查request的檔案是否存在，然後存入input stream來避免塞車的情況，接著將request的檔案寫進output stream傳輸給用戶端。
