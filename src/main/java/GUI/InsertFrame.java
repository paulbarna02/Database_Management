package GUI;

import DAO.AbstractDAO;
import DAO.CustomerDAO;
import DAO.OrdersDAO;
import DAO.ProductDAO;

import javax.swing.*;
import java.awt.*;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class InsertFrame<T> extends JFrame{

    public InsertFrame(JFrame prev, Class<T> t)
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

        for(int i = 1; i < fields.length; i++)
        {
            labels[i - 1] = new JLabel(fields[i].getName().toUpperCase() + ":");
            labels[i - 1].setFont(new Font(Font.SERIF, Font.PLAIN, 25));
            panel.add(labels[i - 1]);
            labels[i - 1].setBounds(40, 100 + 30 * i, 200, 30);

            textFields[i - 1] = new JTextField();
            textFields[i - 1].setFont(new Font(Font.SERIF, Font.PLAIN, 25));
            panel.add(textFields[i - 1]);
            textFields[i - 1].setBounds(200, 100 + i * 30, 300, 30);
        }

        AbstractDAO dao;

        if(t.getSimpleName().equals("Customer"))
            dao = new CustomerDAO();
        else
            if(t.getSimpleName().equals("Product"))
                dao = new ProductDAO();
            else
                dao = new OrdersDAO();

        JButton go = new JButton("Insert");
        go.setFont(new Font(Font.SERIF, Font.PLAIN, 25));
        panel.add(go);
        go.setBounds(40, 100 + fields.length * 30, 150, 30);
        go.addActionListener(e ->{
            Class<T> type = dao.getType();

            PropertyDescriptor propertyDescriptor = null;

            Constructor[] ctors = type.getDeclaredConstructors();
            Constructor ctor = null;
            for (int i = 0; i < ctors.length; i++) {
                ctor = ctors[i];
                if (ctor.getGenericParameterTypes().length == 0)
                    break;
            }

            try {
                ctor.setAccessible(true);
                T instance = (T)ctor.newInstance();
                for(int i = 1; i < fields.length; i++)
                {
                    String s = textFields[i - 1].getText();
                    propertyDescriptor = new PropertyDescriptor(fields[i].getName(), type);
                    switch (fields[i].getType().getSimpleName()){
                        case "String" -> propertyDescriptor.getWriteMethod().invoke(instance, s);
                        case "int" -> propertyDescriptor.getWriteMethod().invoke(instance, Integer.parseInt(s));
                    }

                }
                dao.insert(instance);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     IntrospectionException ex) {
                throw new RuntimeException(ex);
            }
            this.dispose();
            prev.setVisible(true);
        });
    }
}
