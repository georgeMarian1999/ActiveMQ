package server;

import Models.DTOBJCursa;
import Notification.Notification;
import Notification.NotificationType;
import Services.INotificationService;
import org.springframework.jms.core.JmsOperations;

public class NotificationServiceImpl implements INotificationService {

    private JmsOperations jmsOperations;

    public NotificationServiceImpl(JmsOperations operations){
        this.jmsOperations=operations;
    }

    @Override
    public void newSubmit(DTOBJCursa[] curse) {
        System.out.println("New submit is sending to active mq...");
        Notification notification=new Notification(NotificationType.NEW_SUBMIT,curse);
        jmsOperations.convertAndSend(notification);
        System.out.println("New submit sent to the activemq!");
    }
}
