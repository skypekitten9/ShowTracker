package showtracker.client;

import java.util.ArrayList;

import javax.swing.*;

import showtracker.Helper;
import showtracker.Show;
import showtracker.User;

public class DatabasStub {
	private ArrayList <Show> shows = new ArrayList <Show>();

	/*public DatabasStub() {
		fyllShows();
	}

	public void fyllShows() {
		Show s1 =new Show ("show1");
		Show s2 = new Show ("show2");
		Show s3 = new Show ("show3");
		Show s4 = new Show ("show4");
		Show s5 = new Show ("show5");
		Show s6 = new Show ("show6");
		Show s7 = new Show ("show6");
		Show s8 = new Show ("show6");
		Show s9 = new Show ("show6");
		Show s11 = new Show ("show6");
		Show s23= new Show ("show6");
		Show s324= new Show ("show6");
		Show s342= new Show ("show6");
		Show s6342 = new Show ("show6");
		Show s6432 = new Show ("show6");
		Show s634322 = new Show ("show6");
		Show s64322 = new Show ("show6");

		shows.add(s1);
		shows.add(s2);
		shows.add(s3);
		shows.add(s4);
		shows.add(s5);
		shows.add(s6);
		shows.add(s324);
		shows.add(s342);
		shows.add(s6342);
		shows.add(s6432);
		shows.add(s634322);
		shows.add(s64322);
		shows.add(s7);
		shows.add(s8);
		shows.add(s9);
		shows.add(s11);
		shows.add(s23);
		shows.add(new Show("e"));
		shows.add(new Show("3"));
		shows.add(new Show("34"));
		shows.add(new Show("32"));
		shows.add(new Show("334"));
		shows.add(new Show("314"));

	}

	public ArrayList<Show> getShowsAL() {
		return shows;
	}*/

	public boolean containsShow(Show show) {
		return shows.contains(show);
	}

	public static JPanel buttonPanel (JButton b1, JButton b2,JButton b3,JButton b4,JButton b5) {
		JPanel jp = new JPanel();

		return null;
	}

	public static ArrayList<Show> getShows() {
		ArrayList<Show> shows = new ArrayList<>();
		shows.add(new Show("Game of Thrones"));
		shows.add(new Show("Game of Vikings"));
		shows.add(new Show("Prison break"));
		shows.add(new Show("Breaking bad"));
		shows.add(new Show("Vikings"));
		shows.add(new Show("Musti"));
		shows.add(new Show("1"));
		shows.add(new Show("2"));
		shows.add(new Show("3"));
		shows.add(new Show("4"));
		shows.add(new Show("5"));
		shows.add(new Show("6"));

		return shows;
	}

	public static ArrayList<Show> getShowsFromFile() {
		ArrayList<Show> shows = new ArrayList<>();
		Show s = (Show) Helper.readFromFile("files/venture_bros.obj");
		shows.add(s);
		return shows;
	}

	public static ArrayList<User> getUsers() {
		ArrayList<User> userAL = new ArrayList<User>();
		/*User user1 = new User("namn1", "losenord1", "email1", new ImageIcon("images/defaultPicture.jpg"));
		User user2 = new User("namn2", "losenord2", "email2", new ImageIcon("images/defaultPicture.jpg"));
		User user3 = new User("namn3", "losenord3", "email3", new ImageIcon("images/defaultPicture.jpg"));*
		userAL.add(user1);
		userAL.add(user2);
		userAL.add(user3);*/
		return userAL;
	}
}
