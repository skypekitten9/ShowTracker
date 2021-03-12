package test;

import org.json.simple.JSONObject;
import org.junit.Test;
import showtracker.Show;
import showtracker.server.DatabaseReader;

import javax.xml.crypto.Data;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertFalse;

/**
 * Test for the DatabaseReaderTest
 * @Author Robert
 */
public class DatabaseReaderTest {
    @Test
    public void searchShowResultNotNull(){
        DatabaseReader reader = new DatabaseReader();
        String [][] res = reader.searchShows("Vikings");
        assertNotNull(res);
    }

    @Test
    public void searchShowNoResult(){
        DatabaseReader reader = new DatabaseReader();
        String [][] res = reader.searchShows("xdasxasrxase");
        assertNull(res);
    }
    @Test
    public void searchShowResultNotEmpty(){
        DatabaseReader reader = new DatabaseReader();
        String [][] res = reader.searchShows("Vikings");
        assertFalse(res.length == 0);
    }

    @Test
    public void searchShowIDCorrectId(){
        DatabaseReader reader = new DatabaseReader();
        JSONObject object = reader.searchShowID("tt2306299");
        assertNotNull(object);
    }
    @Test
    public void searchShowIDWrongId(){
        DatabaseReader reader = new DatabaseReader();
        JSONObject object = reader.searchShowID("xd");
        assertNotNull(object);
    }
    @Test public void generateShow(){
        DatabaseReader reader = new DatabaseReader();
        String[] arr = new String[]{"Vikings", "tt2306299"};
        Show show = reader.generateShow(arr);
        assertFalse(show.getEpisodes().size() == 0);
    }

}
