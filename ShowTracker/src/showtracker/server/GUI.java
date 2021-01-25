package showtracker.server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URI;

import static javax.swing.JOptionPane.OK_CANCEL_OPTION;
import static javax.swing.JOptionPane.PLAIN_MESSAGE;

/**
 * @author Filip Spånberg
 * Hanterar det grafiska interfacet för servern
 */
public class GUI {
    private Controller controller;
    private JPanel pnMain = new JPanel();
    private JLabel lbActiveThreads = new JLabel("Active threads: 0");
    private JComboBox cbThreadNumber;
    private JButton bnStart = new JButton("Start server");
    private JButton bnStop = new JButton("Stop server");
    private JButton bnPassword = new JButton("Change password");
    private JFrame frame = new JFrame("ShowTracker server");

    public GUI(Controller controller) {
        this.controller = controller;
        //SpinnerModel smThreadNumber = new SpinnerNumberModel(1, 1, 10, 1);
        //int[] arThreadNumber = {1,2,3,4,5,6,7,8,9,10};
        String[] arThreadNumber = {"1","2","3","4","5","6","7","8","9","10"};
        cbThreadNumber = new JComboBox(arThreadNumber);

        pnMain.setLayout(new GridLayout(3,2));
    }

    private void startConnection(int threads) {
        controller.startConnection(threads);
        bnStart.setEnabled(false);
        bnStop.setEnabled(true);
    }

    private void stopConnection() {
        controller.stopConnection();
        bnStart.setEnabled(true);
        bnStop.setEnabled(false);
    }

    public void setActiveThreads(int i) {
        lbActiveThreads.setText("Active threads: " + i);
    }

    public void authenticateTheTVDB() {
        String token = controller.authenticateTheTVDB();
        //String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1NTQ4MDcxMTIsImlkIjoiU2hvd1RyYWNrZXIiLCJvcmlnX2lhdCI6MTU1NDcyMDcxMiwidXNlcmlkIjo1MjQzMDIsInVzZXJuYW1lIjoiZmlsaXAuc3BhbmJlcmdxcnMifQ.dGVukYqnBUzOT9VQs3gUjFAwappax_6PxPXJKbvHhkOoiZO3Wl4EdJy7jjF909vJWiNZxi0_4w6NXdiydbVGsiAjCgxPtLC7NvLaBUC7XmesH9bBWZZowY3XspDspNa9rIXtm3mVrTPZX7VpBrXl2fJdN0ujo1Ey3zkAak859VebDVy5aM8gN_PWGNLqo1_8nQUSXzsP5C6QE6-MGpB8P01tB3Uz-Y2itD2FOjnfwlu2eUHAQ9W0H0pFJ2lwZGm16jZE6FvJV3yNAfjxBZYLRHJA9db4SvIzFohW1lQkGN9YhGLYYulqdGnY0sFCdQVjS8VsPJegaom2eMoUcrdg_Q";
        JTextArea textArea = new JTextArea(8, 50);
        textArea.setText(token);
        textArea.setLineWrap(true);
        JLabel label = new JLabel("Gå in på https://api.thetvdb.com/swagger#/ och klistra in följande token:");
        label.setPreferredSize(new Dimension(100, 50));
        JButton button = new JButton("Gå till browser");
        button.addActionListener(e -> openBrowser());
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(label, BorderLayout.NORTH);
        panel.add(button, BorderLayout.SOUTH);
        panel.add(textArea, BorderLayout.CENTER);
        JOptionPane.showMessageDialog(null, panel);
    }

    private static void openBrowser() {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI("https://api.thetvdb.com/swagger#/"));
            } catch (Exception e) {
                System.out.println("GUI: " + e);
            }
        }
    }

    public void testPane() {
        JTextField tfOldPass = new JTextField();
        JTextField tfNewPass = new JTextField();
        JPanel pnPass = new JPanel();
        pnPass.setLayout(new GridLayout(2, 1));
        pnPass.add(tfOldPass);
        pnPass.add(tfNewPass);
        int res = JOptionPane.showConfirmDialog(null, pnPass,
                "Enter your old and new password", JOptionPane.OK_CANCEL_OPTION, PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            System.out.println("Old: " + tfOldPass.getText());
            System.out.println("New: " + tfNewPass.getText());
        }
    }

    public void start() {
        bnStart.addActionListener(e -> startConnection(Integer.parseInt((String) cbThreadNumber.getSelectedItem())));
        pnMain.add(bnStart);
        pnMain.add(cbThreadNumber);
        bnStop.addActionListener(e -> stopConnection());
        pnMain.add(bnStop);
        pnMain.add(lbActiveThreads);
        bnStop.setEnabled(false);
        JButton bnAuthenticate = new JButton("Authenticate");
        bnAuthenticate.addActionListener(e -> authenticateTheTVDB());
        pnMain.add(bnAuthenticate);
        bnPassword.addActionListener(e -> testPane());
        pnMain.add(bnPassword);
        frame.add(pnMain);
        frame.setVisible(true);
        frame.pack();
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("GUI exiting...");
                controller.stopConnection();
                System.out.println("GUI exited.");
                System.exit(0);
            }
        });
    }
}
