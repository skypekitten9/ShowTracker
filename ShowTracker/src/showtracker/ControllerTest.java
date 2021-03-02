package showtracker;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import showtracker.server.Controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @Created 02/03/2021
 * @Project ShowTracker
 * @auther Mewk
 */
public class ControllerTest {
    @Test
    //Testas 2 ggr, med ett giltigt och ogiltigt användarnamn.
    //Nedanstående test gick ej genom.
    public void testSignUpUserExist() {
        Controller controller = new Controller();
        String[] userInfo = { "Marri", "Marianne31." };

        assertEquals(new Envelope("Username already taken", "signin"), controller.signUp(userInfo));

    }

    @Test
    public void testSignUpUserDoNotExist() {
        Controller controller = new Controller();
        String[] userInfo = { "Hanna", "Hanna25." };

        assertEquals(new Envelope("Profile saved", "confirmation"), controller.signUp(userInfo));

    }



}
