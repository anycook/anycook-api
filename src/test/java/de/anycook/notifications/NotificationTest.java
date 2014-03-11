package de.anycook.notifications;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class NotificationTest {

    @Test
    public void testParse() {
        Map<String, String> data = new HashMap<>();
        data.put("user", "username");
        data.put("tag", "tagname");


        //assertEquals("username", Notification.parse("%user", data));
        //assertEquals("tagname", Notification.parse("%tag", data));
        //assertEquals("Hallo username, das ist der Tag: tagname", Notification.parse("Hallo %user, das ist der Tag: %tag", data));
    }

}
