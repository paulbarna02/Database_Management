package GUI;

import DAO.AbstractDAO;
import DAO.CustomerDAO;
import DAO.OrdersDAO;
import DAO.ProductDAO;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.sql.SQLOutput;
import java.util.Arrays;
import java.util.Vector;

public class FindFrame<T> extends JFrame {

    private AbstractDAO dao;

    public FindFrame(JFrame prev, Class<T> t) {
        this.setTitle("Database Application");
        this.setBounds(300, 150, 900, 600);
        this.setResizable(false);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);

        JPanel panel = new JPanel(null);
        this.add(panel);

        JButton back = new JButton("Back");
        back.setFont(new Font(Font.SERIF, Font.PLAIN, 30));
        panel.add(back);
        back.setBounds(760, 15, 100, 60);
        back.addActionListener(c -> {
            this.dispose();
            prev.setVisible(true);
        });

        JLabel id = new JLabel("Id:");
        id.setFont(new Font(Font.SERIF, Font.PLAIN, 25));
        panel.add(id);
        id.setBounds(40, 30, 50, 30);

        JTextField idTF = new JTextField();
        idTF.setFont(new Font(Font.SERIF, Font.PLAIN, 25));
        panel.add(idTF);
        idTF.setBounds(100, 30, 100, 30);

        Field[] fields = t.getDeclaredFields();
        String[] cols = new String[fields.length];


        if(t.getSimpleName().equals("Customer"))
            dao = new CustomerDAO();
        else if(t.getSimpleName().equals("Product"))
            dao = new ProductDAO();
        else if(t.getSimpleName().equals("Orders"))
            dao = new OrdersDAO();

        String[][] s = new String[dao.findAll().size()][];

        for (int i = 0; i < dao.findAll().size(); i++){
            s[i] = dao.fieldsToString(dao.findAll().get(i));
        }

        for(int i = 0; i < fields.length; i++) {
            cols[i] = new String(fields[i].getName());
        }

        JTable table = new JTable(s, cols);
        JScrollPane jScrollPane = new JScrollPane(table);
        panel.add(jScrollPane);
        jScrollPane.setBounds(0, 100, 900, 600);

        JButton go = new JButton("Find");
        go.setFont(new Font(Font.SERIF, Font.PLAIN, 25));
        panel.add(go);
        go.setBounds(210, 30, 80, 30);
        go.addActionListener(c ->{
            if(idTF.getText().equals("")) {
                JOptionPane.showMessageDialog(this, "No input", "Error", JOptionPane.ERROR_MESSAGE);
            }
            String[][] found = new String[1][];
            found[0] = dao.fieldsToString(dao.findById(Integer.parseInt(idTF.getText())));

            JTable newTable = new JTable(found, cols);
            JScrollPane newScrollPane = new JScrollPane(newTable);
            panel.add(newScrollPane);
            newScrollPane.setBounds(0, 100, 900, 600);
        });
    }
}
