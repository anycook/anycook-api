package de.anycook.api;

import de.anycook.user.User;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.GenericType;
import java.util.List;

/**
 * @author Jan Graßegger<jan@anycook.de>
 */
public class UserApiTest extends JerseyTest{
    @Override
    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        return new ResourceConfig(UserApi.class);
    }

    @Test
    public void testGetAll() {
        final List<User> userList = target("user/1").request().get(new GenericType<List<User>>() {});
        Assert.assertNotEquals("should not be 0", userList.size(), 0);
    }
}
