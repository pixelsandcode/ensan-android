package ir.app.ensan.common;

import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Created by k.monem on 2/27/2017.
 */

public class FireBaseManager {

    private static FireBaseManager instance;

    public static FireBaseManager getInstance() {

        if (instance == null) {
            instance = new FireBaseManager();
        }
        return instance;
    }

    public void init(){
        subscribeDefaultTopic();
    }

    private void subscribeDefaultTopic(){
        FirebaseMessaging.getInstance().subscribeToTopic("Default");
    }

    public void sendMessagingToken(String messagingToken){
        //// TODO: 2/27/2017 send notification id to server
    }
}
