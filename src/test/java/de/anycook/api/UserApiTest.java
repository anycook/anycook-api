package de.anycook.api;

import org.glassfish.jersey.test.JerseyTest;

/**
 * @author Jan Graßegger<jan@anycook.de>
 */
public class UserApiTest extends JerseyTest{
    /* TODO
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
    } */
}
