package showtracker;

import org.junit.Test;
import showtracker.server.Controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @Created 02/03/2021
 * @Project ShowTracker
 * @auther Mewk
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
        String[] userInfo = { "Marianne", "Handboll1" };
        envelope = controller.signUp(userInfo);
        assertEquals(("User registered"), envelope.getContent());
    }


    @Test
    //Kontrollerar typen av Envelopet
    public void test1() {
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

}

