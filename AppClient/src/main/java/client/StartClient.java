package client;

import Network.AMSProtocol.AMSServicesProxy;
import Network.RPCProtocol.ClientServicesProxy;
import Services.IServices;
import Services.IServicesAMS;
import Services.NotificationReceiver;
import client.GUI.LoginController;
import client.GUI.MainController;
import client.GUI.NotificationReceiverImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class StartClient extends Application {
    private Stage primaryStage;

    private static int defaultChatPort = 55555;
    private static String defaultServer = "localhost";


    public void start(Stage primaryStage) throws Exception {
        /*System.out.println("In start");
        Properties clientProps = new Properties();
        try {
            clientProps.load(StartClient.class.getResourceAsStream("/Client.properties"));
            System.out.println("Client properties set. ");
            clientProps.list(System.out);
        } catch (IOException e) {
            System.err.println("Cannot find Client.properties " + e);
            return;
        }
        String serverIP = clientProps.getProperty("server.host", defaultServer);
        int serverPort = defaultChatPort;

        try {
            serverPort = Integer.parseInt(clientProps.getProperty("server.port"));
        } catch (NumberFormatException ex) {
            System.err.println("Wrong port number " + ex.getMessage());
            System.out.println("Using default port: " + defaultChatPort);
        }
        System.out.println("Using server IP " + serverIP);
        System.out.println("Using server port " + serverPort);*/

        //IServices server = new ClientServicesProxy(serverIP, serverPort);

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring-client.xml");
        IServicesAMS server=context.getBean("Services", AMSServicesProxy.class);
        NotificationReceiver receiver=context.getBean("notificationReceiver", NotificationReceiverImpl.class);
        //MainController mainCtrl=context.getBean("MainView",MainController.class);
        FXMLLoader loader = new FXMLLoader(
                getClass().getClassLoader().getResource("LoginView.fxml"));
        Parent root=loader.load();


        LoginController ctrl =
                loader.<LoginController>getController();
        FXMLLoader cloader=new FXMLLoader(getClass().getClassLoader().getResource("MainView.fxml"));
        Parent croot=cloader.load();




       MainController mainCtrl = cloader.<MainController>getController();
        mainCtrl.setServer(server);
        mainCtrl.setReceiver(receiver);

        ctrl.SetMainCtrl(mainCtrl);
        ctrl.SetParent(croot);

        primaryStage.setTitle("Login");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
