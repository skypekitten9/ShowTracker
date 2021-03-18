package test;

import org.junit.Test;
import showtracker.Envelope;
import showtracker.User;
import showtracker.server.Controller;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;


/**
 * @Created 02/03/2021
 * @Project ShowTracker
 * @auther Mewk, Robert
 */


public class ControllerTest {

    @Test
    //Avser att en användarnamn redan är upptaget
    public void testSignUpUserExist() {
        Controller controller = new Controller();
        Envelope envelope;
        String[] userInfo = { "Hanna", "Handboll1" };
        envelope = controller.signUp(userInfo);
        assertEquals(("Username already taken"), envelope.getContent());
    }

    @Test
    //Avser att en användarnamn inte är upptaget
    public void testSignUpUserDoNotExist() {
        Controller controller = new Controller();
        Envelope envelope;
        String[] userInfo = { "TESTUSER12", "TestUser1" };
        envelope = controller.signUp(userInfo);
        assertEquals(("User registered"), envelope.getContent());
    }

    @Test
    //Kontrollerar typen av Envelopet
    public void signUpEnvelope() {
        Controller controller = new Controller();
        Envelope envelope;
        String[] userInfo = { "Marianne", "Handboll1" };
        envelope = controller.signUp(userInfo);
        assertEquals(("signin"), envelope.getType());
    }

    @Test
    //Kontrollerar typen av Envelopet
    public void test2() {
        Controller controller = new Controller();
        Envelope envelope;
        String[] userInfo = { "Marianne", "Handboll1" };
        envelope = controller.signUp(userInfo);
        assertEquals(("signin"), envelope.getType());
    }

    //SIGNINTEST

    @Test public void signInTestSuccess(){
        Controller controller = new Controller();
        String username = "Username";
        String[] userinfo = {username, "Password123"};
        Envelope e = controller.loginUser(userinfo);
        User content = (User) e.getContent();
        assertEquals(content.getUserName(),username);
    }
    @Test public void signInTestFailNoUsername(){
        Controller controller = new Controller();
        String username = "UsernameDab";
        String[] userinfo = {username, "Password123"};
        Envelope e = controller.loginUser(userinfo);
        User content = (User) e.getContent();
        assertNull(content);
    }

    @Test public void signInTestFailWrongPassword(){
        Controller controller = new Controller();
        String username = "Username";
        String[] userinfo = {username, "Password1234"};
        Envelope e = controller.loginUser(userinfo);
        User content = (User) e.getContent();
        assertNull(content);
    }

    @Test public void signInBlank(){
        Controller controller = new Controller();
        String username = "";
        String[] userinfo = {username, ""};
        Envelope e = controller.loginUser(userinfo);
        User content = (User) e.getContent();
        assertNull(content);
    }

    //USERNAMEAVAILABILITY

    @Test public void usernameNotAvailable(){
        Controller controller = new Controller();
        String username = "Username";
        Envelope e = controller.isUsernameAvailable(username);
        boolean b = (boolean) e.getContent();
        assertFalse(b);
    }
    @Test public void usernameAvailable(){
        Controller controller = new Controller();
        String username = "Test563";
        Envelope e = controller.isUsernameAvailable(username);
        boolean b = (boolean) e.getContent();
        assertTrue(b);
    }

}

