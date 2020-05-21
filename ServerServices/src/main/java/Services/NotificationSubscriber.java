package Services;

import Notification.Notification;

public interface NotificationSubscriber {
    void notificationReceived(Notification notification);
}
