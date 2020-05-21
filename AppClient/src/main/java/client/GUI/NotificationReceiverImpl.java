package client.GUI;

import Notification.Notification;
import Services.NotificationReceiver;
import Services.NotificationSubscriber;
import org.springframework.jms.core.JmsOperations;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class NotificationReceiverImpl implements NotificationReceiver {
    private JmsOperations jmsOperations;
    private boolean running;
    public NotificationReceiverImpl(JmsOperations operations) {
        jmsOperations=operations;
    }
    ExecutorService service;
    NotificationSubscriber subscriber;
    @Override
    public void start(NotificationSubscriber notificationSubscriber) {
        System.out.println("Starting notification receiver ...");
        running=true;
        this.subscriber=notificationSubscriber;
        service = Executors.newSingleThreadExecutor();
        service.submit(()->{run();});
    }
    private void run(){
        while(running){
            System.out.println("Trying to receive notification....");
            Notification notif=(Notification)jmsOperations.receiveAndConvert();
            System.out.println("Received Notification... "+notif);
            subscriber.notificationReceived(notif);
        }
    }

    @Override
    public void stop() {
        running=false;
        try {
            service.awaitTermination(100, TimeUnit.MILLISECONDS);
            service.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Stopped notification receiver");
    }
}
