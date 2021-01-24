package server_side;

public class boot_server {
    public static void main(String[] args) {
        Server s=new MySerialServer(); // initialize
        CacheManager cacheManager=new FileCacheManager();
        MyClientHandler clientHandler=new MyClientHandler(cacheManager);
        s.open(2030,new ClientHandlerPath(clientHandler));
    }
}
