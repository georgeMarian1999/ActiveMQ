package Services;

public interface NotificationReceiver {
    void start(NotificationSubscriber notificationSubscriber);
    void stop();
}
