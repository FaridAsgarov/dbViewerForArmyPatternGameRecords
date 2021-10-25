package view;

import Entity.BattleInfoEntity;
import org.hibernate.HibernateException;
import org.hibernate.Metamodel;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import javax.persistence.metamodel.EntityType;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

public class DatabaseGUI extends JFrame {
    private final int frameHeight = 1000;
    private final int frameWidth = 900;
    private final String startingHtmlTag = "<html>";
    private final String startingLabelHTMLText = startingHtmlTag + "Database Game Records:" + "<br>";
    private final String noSuchIdErrorTitle = "Error: Such an Id does not exist in the database";
    private final String noSuchIdErrorMessage = "Such an Id does not exist, please enter a number Id which exists in the database";

    private SessionFactory ourSessionFactory = buildSessionFactory();
    JLabel log = new JLabel();
    ArrayList<Integer> idNums = new ArrayList<Integer>();

    public DatabaseGUI(){
        this.setSize(frameWidth,frameHeight);

        final JMenuBar menuBar = new JMenuBar();
        JMenu viewDb = new JMenu("Show database info");
        JMenu writeToDb = new JMenu("Write to database");
        JMenu clear = new JMenu("Clear the log");
        JMenu delete = new JMenu("Delete from the database");

        JMenuItem viewBattleInfo = new JMenuItem("Show squad battle game records from database");
        JMenuItem writeNewGameInfoToDb = new JMenuItem("Write a new game record to the database");
        JMenuItem clearTheLog = new JMenuItem("Remove all the text from the log");
        JMenuItem deleteById = new JMenuItem("Delete a record from the database based on its Id");

        log.setBounds(0,60,500,400);
        log.setHorizontalAlignment(SwingConstants.CENTER);
        log.setVerticalAlignment(SwingConstants.TOP);
        log.setOpaque(true);

        JScrollPane dbLog = new JScrollPane(log);
        dbLog.setBounds(0,60,500,400);
        dbLog.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        dbLog.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        viewBattleInfo.addActionListener(e -> showDbInTableForm());

        writeNewGameInfoToDb.addActionListener(e -> {
            writeToDbTable(Objects.requireNonNull(JOptionPane.showInputDialog(log, "Enter the name for the battle game mode:")),
                    Objects.requireNonNull(JOptionPane.showInputDialog(log, "Enter the name for the winner:")));

            JOptionPane.showMessageDialog(log,"The new game record is successfully entered into the database","Success",JOptionPane.INFORMATION_MESSAGE);
            restartTheLog();
        });

        clearTheLog.addActionListener(e -> {
          deleteTheLog();
        });

        deleteById.addActionListener(e -> {
            try{
                deleteFromTableById(
                           Integer.parseInt(Objects.requireNonNull(JOptionPane.showInputDialog(log, "Enter a number ID of the record which you want to delete:"))));
                restartTheLog();
            } catch (Exception ignored){
                JOptionPane.showMessageDialog(log, noSuchIdErrorMessage, noSuchIdErrorTitle, JOptionPane.ERROR_MESSAGE);
            }

            });

        this.setJMenuBar(menuBar);
        menuBar.setVisible(true);
        menuBar.setOpaque(true);
        this.add(menuBar);
        menuBar.add(viewDb);
        menuBar.add(writeToDb);
        menuBar.add(clear);
        menuBar.add(delete);
        viewDb.add(viewBattleInfo);
        writeToDb.add(writeNewGameInfoToDb);
        clear.add(clearTheLog);
        delete.add(deleteById);
        this.add(dbLog);

        log.setLayout(new GridLayout(0,1));
        setVisible(true);


        showDbInTableForm();

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    private SessionFactory buildSessionFactory() {
        final SessionFactory ourSessionFactory;
        try {
            Configuration configuration = new Configuration();
            configuration.configure();

            ourSessionFactory = configuration.buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
        return ourSessionFactory;
    }

    private Session getSession() throws HibernateException {
        return ourSessionFactory.openSession();
    }


    private void showDbTable(JLabel component) {
        final Session session = getSession();
        try {
            final Metamodel metamodel = session.getSessionFactory().getMetamodel();
            for (EntityType<?> entityType : metamodel.getEntities()) {
                final String entityName = entityType.getName();
                final Query query = session.createQuery("from " + entityName);

                component.setText(startingLabelHTMLText);

                for (Object o : query.list()) {
                    BattleInfoEntity a = (BattleInfoEntity) o;
                    component.setText(component.getText() + a.getBattleId() + "." + a.getBattleMode() + ", " +
                            a.getBattleWinner() + ", date: " + a.getSubmissionDate() + "<br/>");
                }
            }
        } finally {
            session.close();
        }
    }

    private void showDbInTableForm() {
        final Session session = getSession();
        try {
            final Metamodel metamodel = session.getSessionFactory().getMetamodel();

            JLabel pre_log = new JLabel();
            pre_log.setBounds(0,59,900,59);
            log.add(pre_log);

            JLabel header = new JLabel();
            header.setLayout(new GridLayout(1,3));
            header.add(new Button("Battle Id:"));
            header.add(new Button("Battle Mode:"));
            header.add(new Button("Battle Winner:"));
            header.add(new Button("Date of the game:"));
            log.add(header);

            for (EntityType<?> entityType : metamodel.getEntities()) {
                final String entityName = entityType.getName();
                final Query query = session.createQuery("from " + entityName);

                for (Object o : query.list()) {
                    BattleInfoEntity a = (BattleInfoEntity) o;
                    JLabel b = new JLabel();
                    b.setLayout(new GridLayout(1,3));
                    b.add(new Button(String.valueOf(a.getBattleId())));
                    idNums.add((a.getBattleId()));
                    b.add(new Button(a.getBattleMode()));
                    b.add(new Button(a.getBattleWinner()));
                    b.add(new Button(String.valueOf(a.getSubmissionDate())));
                    log.add(b);
                }
            }
        } finally {
            if (this.getHeight()!=450){
                this.setSize(800, 450);
            } else {
                this.setSize(800, 455);
            }
            session.close();
        }
    }

    private void writeToDbTable(String battle_mode, String battle_winner) {
        final Session session = getSession();
        try {
            session.beginTransaction();
            BattleInfoEntity bf = new BattleInfoEntity();
            bf.setBattleMode(battle_mode);
            bf.setBattleWinner(battle_winner);
            bf.setSubmissionDate(new java.sql.Date(System.currentTimeMillis()));

            session.save(bf);

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    private void deleteTheLog() {
        for (Component component: log.getComponents()){
            log.remove(component);
        }
    }

    private void restartTheLog() {
       deleteTheLog();
       showDbInTableForm();
    }

    private void deleteFromTableById(int id){
        final Session session = getSession();
        try {
            final Metamodel metamodel = session.getSessionFactory().getMetamodel();
            session.beginTransaction();

            boolean idExistsInDB = false;

            for(int i = 0; i < idNums.size(); i++) {
                if (id == idNums.get(i)) {

                    int query = session.createNativeQuery("delete from battle_info where battle_id = :battle_id")
                            .setParameter("battle_id", id)
                            .executeUpdate();
                    System.out.println(id);
                    JOptionPane.showMessageDialog(log, "The record number " + id + " should now be deleted from the database", "Success", JOptionPane.INFORMATION_MESSAGE);
                    idExistsInDB = true;
                    break;
                }
            }
            if(idExistsInDB == false){
                JOptionPane.showMessageDialog(log, noSuchIdErrorMessage, noSuchIdErrorTitle, JOptionPane.ERROR_MESSAGE);
            }
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            session.close();
        }
        }

}