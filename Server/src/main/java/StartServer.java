import Network.Utils.AbstractServer;
import Network.Utils.RPCConcurrentServer;
import Service.Service;
import Services.IServices;
import Services.ServerException;
import org.graalvm.compiler.core.common.type.ArithmeticOpTable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import server.ServicesImplementation;

import java.io.IOException;
import java.util.Properties;

public class StartServer  {
    private static int defaultport=55555;
    public static void main(String[] args){
        /*Properties serverProps=new Properties();
        try{
            serverProps.load(StartServer.class.getResourceAsStream("Server.properties"));
            System.out.println("Server properties set");
            serverProps.list(System.out);
        }catch(IOException e){
            System.out.println("Could not find Server.properties");
            return;
        }
        IServices ServiceImplementation=new ServicesImplementation(getService());
        int ServerPort=defaultport;
        try{
            ServerPort=Integer.parseInt(serverProps.getProperty("server.port"));
        }catch (NumberFormatException e){
            System.err.println("Wrong  Port Number"+e.getMessage());
            System.err.println("Using default port "+defaultport);
        }
        System.out.println("Starting the server on the port "+ServerPort);
        AbstractServer server=new RPCConcurrentServer(ServerPort,ServiceImplementation);
        try {
            server.start();
        } catch (ServerException e) {
            System.err.println("Error starting the server" + e.getMessage());
        }finally {
            try {
                server.stop();
            }catch(ServerException e){
                System.err.println("Error stopping server "+e.getMessage());
            }
        }*/
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring-server.xml");
        AbstractServer server=context.getBean("TCPServer",RPCConcurrentServer.class);
        try{
            server.start();
        }catch(ServerException e){
            System.out.println("Error starting the server");
        }

    }

}
