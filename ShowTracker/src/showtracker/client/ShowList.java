package showtracker.client;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import showtracker.Helper;
import showtracker.Show;
import showtracker.User;

public class ShowList extends JPanel {
    private ClientController cc;
    private JPanel panelShowList = new JPanel();
    private ArrayList<JButton> btnArrayList = new ArrayList<>();
    private JScrollPane scrollPanel = new JScrollPane();
    private MyDocumentListener myDocumentListener = new MyDocumentListener(); // Sökrutan för söka bland sparade serier
    private JLabel lblSearchSeries = new JLabel("Search your series: ");
    private JPanel pnlSearch = new JPanel(new GridLayout(1,2));
    private int x = 0;

    public ShowList(ClientController cc) {
        this.cc = cc;

    }

    public void draw() {
        Collections.sort(cc.getUser().getShows(), new Helper.NameComparator());
        drawShowList(cc.getUser().getShows());

        myDocumentListener.setToolTipText("Search your shows");

        setLayout(new BorderLayout());

        pnlSearch.add(lblSearchSeries);
        pnlSearch.add(myDocumentListener);

        add(pnlSearch, BorderLayout.NORTH);

        add(scrollPanel, BorderLayout.CENTER);
        scrollPanel.getVerticalScrollBar().setUnitIncrement(16);

    }

    public void redraw() {
        scrollPanel.getVerticalScrollBar().updateUI();
        myDocumentListener.updateUI();
        panelShowList.updateUI();
        pnlSearch.updateUI();
        lblSearchSeries.updateUI();

    }

    void drawShowList(ArrayList<Show> shows) {
        GridBagConstraints gbc = new GridBagConstraints();
        panelShowList.setLayout(new GridBagLayout());
        gbc.fill = GridBagConstraints.HORIZONTAL;

        panelShowList.removeAll();
        if (shows.size() > 0) {
            for (Show s : shows) {
                JPanel middlePanel = new JPanel(new FlowLayout());
                JPanel southPanel = new JPanel(new GridLayout(3, 1));

                JButton btnInfo = new JButton("Mark watched episodes");
                JButton btnUpdate = new JButton("Check for new episodes");
                JButton btnRemove = new JButton("Remove show from list");

                middlePanel.add(new JLabel("<html><body><p style=\"width: 200px; text-align: center;\">" + s.getName() + "</p></body></html>"));

                southPanel.add(btnInfo);
                southPanel.add(btnUpdate);
                southPanel.add(btnRemove);

                JPanel mainPanel = new JPanel(new BorderLayout());


                mainPanel.add(middlePanel, BorderLayout.CENTER);
                mainPanel.add(southPanel, BorderLayout.SOUTH);

                btnInfo.addActionListener(new ActionListener() {
                    private int counter = x;
                    private Show tempShow = s;

                    public void actionPerformed(ActionEvent e) {
                        cc.setPanel("Info", tempShow);

                    }
                });

                btnUpdate.addActionListener(e -> cc.getUser().updateShow(cc.updateShow(s)));

                btnRemove.addActionListener(new ActionListener() {
                    private Show show = s;

                    public void actionPerformed(ActionEvent e) {

                        // value 0 för YES, 1 NO, 2 CANCEL
                        int removalChioce = JOptionPane.showConfirmDialog(null, "Are you sure you want to remove " + s.getName() + "?");

                        if (removalChioce == 0) {
                            cc.getUser().removeShow(show.getImdbId());
                            drawShowList(cc.getUser().getShows());
                        }

                    }
                });

                gbc.gridx = 0;
                gbc.weightx = 1;

                panelShowList.add(mainPanel, gbc);

            }
            JPanel panel = new JPanel();
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.weighty = 1;
            panelShowList.add(panel, gbc);

        } else {
            panelShowList.add(new JLabel("   Your list of shows is empty!   "));

        }
        scrollPanel.setViewportView(panelShowList);
        scrollPanel.setLayout(new ScrollPaneLayout());
        panelShowList.revalidate();

    }

    private class MyDocumentListener extends JTextField implements DocumentListener {

        public MyDocumentListener() {
            javax.swing.text.Document doc = this.getDocument();
            doc.addDocumentListener(this);

        }

        public void changedUpdate(DocumentEvent e) {
            searchShow();
        }

        public void insertUpdate(DocumentEvent e) {
            searchShow();
        }

        public void removeUpdate(DocumentEvent e) {
            searchShow();
        }

        public void searchShow() {
            ArrayList<Show> searchShows = new ArrayList<>();
            for (Show testshow : cc.getUser().getShows()) {
                if (testshow.getName().toLowerCase().contains(getText().toLowerCase()))
                    searchShows.add(testshow);
            }
            panelShowList.removeAll();
            btnArrayList.clear();
            drawShowList(searchShows);
        }
    }
}
