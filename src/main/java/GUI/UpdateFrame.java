package GUI;

import DAO.AbstractDAO;
import DAO.CustomerDAO;
import DAO.ProductDAO;

import javax.swing.*;
import java.awt.*;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class UpdateFrame<T> extends JFrame {

    public UpdateFrame(JFrame prev, Class<T> t)
    {
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

        Field[] fields = t.getDeclaredFields();
        JLabel[] labels = new JLabel[fields.length];
        JTextField[] textFields = new JTextField[fields.length];

        for(int i = 0; i < fields.length; i++)
        {
            labels[i] = new JLabel(fields[i].getName().toUpperCase() + ":");
            labels[i].setFont(new Font(Font.SERIF, Font.PLAIN, 25));
            panel.add(labels[i]);
            labels[i].setBounds(40, 100 + 30 * i, 200, 30);

            textFields[i] = new JTextField();
            textFields[i].setFont(new Font(Font.SERIF, Font.PLAIN, 25));
            panel.add(textFields[i]);
            textFields[i].setBounds(200, 100 + i * 30, 300, 30);
        }

        AbstractDAO dao;

        if(t.getSimpleName().equals("Customer"))
            dao = new CustomerDAO();
        else
            dao = new ProductDAO();

        JButton go = new JButton("Update");
        go.setFont(new Font(Font.SERIF, Font.PLAIN, 25));
        panel.add(go);
        go.setBounds(40, 100 + fields.length * 30, 150, 30);
        go.addActionListener(e ->{
            T obj = (T)dao.findById(Integer.parseInt(textFields[0].getText()));
            Class<T> type = dao.getType();
            PropertyDescriptor propertyDescriptor = null;

            try {
                    for(int i = 1; i < fields.length; i++) {
                        String s = textFields[i].getText();
                        propertyDescriptor = new PropertyDescriptor(fields[i].getName(), type);
                        switch (fields[i].getType().getSimpleName()) {
                            case "String" -> propertyDescriptor.getWriteMethod().invoke(obj, s);
                            case "int" -> propertyDescriptor.getWriteMethod().invoke(obj, Integer.parseInt(s));
                        }
                    }
                dao.update(obj);
                } catch (IntrospectionException | InvocationTargetException | IllegalAccessException ex) {
                    throw new RuntimeException(ex);
                }
            this.dispose();
            prev.setVisible(true);
        });
    }
}
