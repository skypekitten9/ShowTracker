package showtracker;

import javax.swing.*;
import java.io.*;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

public class Helper {
	public static final DecimalFormat df = new DecimalFormat("0.#");
	
    public static void decompressGzip(String input) {
        File fiInput = new File(input);
        String stOutput = fiInput.getName().substring(0, fiInput.getName().lastIndexOf("."));
        File fiOutput = new File(stOutput);
        try (GZIPInputStream stInput = new GZIPInputStream(new FileInputStream(fiInput))){
            try (FileOutputStream fos = new FileOutputStream(fiOutput)){
                byte[] buffer = new byte[1024];
                int len;
                while((len = stInput.read(buffer)) != -1){
                    fos.write(buffer, 0, len);
                }
            }
        } catch (Exception e) {
            System.out.println("DatabaseReader.decompressGzip: " + e);
        }
    }

    public static void writeToFile(Object o, String file) {
        File fiWrite = new File(file);
        try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fiWrite)))) {
            oos.writeObject(o);
        } catch (Exception e) {
            System.out.println("Helper.writeToFile: " + e);
        }
    }

    public static Object readFromFile(String file) {
        File fiRead= new File(file);
        Object o = null;
        try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(fiRead)))) {
            o = ois.readObject();
        } catch (Exception e) {
            System.out.println("Helper.readFromFile: " + e);
        }
        return o;
    }

    public static String decodeUnicode(String input) {
        if (input != null) {
            Pattern pattern = Pattern.compile("\\\\u.{4}");
            Matcher matcher = pattern.matcher(input);
            while (matcher.find()) {
                String s = Character.toString((char) matcher.group().getBytes()[0]);
                input.replaceAll(matcher.group(), s);
            }
        }
        return input;
    }

    public static boolean checkUsernameValidity(String username) {
        String pattern = "[\\\\/:*?\"<>|%]";
        Pattern p = Pattern.compile(pattern);
        Matcher match = p.matcher(username);
        return (!(match.find() || (username.equals(""))));
    }

    public static boolean checkEmailValidity(String email) {
        String pattern = "[a-z0-9]+@[a-z0-9]+\\.[a-z]{1,3}";
        Pattern p = Pattern.compile(pattern);
        Matcher match = p.matcher(email.toLowerCase());
        return (!(email.equals("")) && match.find());
    }

    public static boolean checkPasswordValidity(String password) {
        Pattern p1 = Pattern.compile("[a-z]");
        Pattern p2 = Pattern.compile("[A-Z]");
        Pattern p3 = Pattern.compile("[0-9]");
        Matcher match1 = p1.matcher(password);
        Matcher match2 = p2.matcher(password);
        Matcher match3 = p3.matcher(password);

        return (password.length() > 7 && match1.find() && match2.find() && match3.find());
    }

    public static void message(String message) {
        JOptionPane.showMessageDialog(null, message);
    }
    
    public static void errorMessage(String message) {
    	JOptionPane.showMessageDialog(null, message, "No connection" ,JOptionPane.ERROR_MESSAGE);
    }
    

    public static class LastWatchedComparator implements Comparator<Show> {

        @Override
        public int compare(Show s1, Show s2) {
            return s2.getLastWatched().compareTo(s1.getLastWatched()) ;
        }
    }

    public static class NameComparator implements Comparator<Show> {

        @Override
        public int compare(Show s1, Show s2) {
            return s1.getName().compareTo(s2.getName()) ;
        }
    }
}