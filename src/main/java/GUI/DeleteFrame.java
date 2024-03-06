package GUI;

import DAO.AbstractDAO;
import DAO.CustomerDAO;
import DAO.ProductDAO;

import javax.swing.*;
import java.awt.*;

public class DeleteFrame<T> extends JFrame {

    public DeleteFrame(JFrame prev, Class<T> t)
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

        JLabel id = new JLabel("Id:");
        id.setFont(new Font(Font.SERIF, Font.PLAIN, 25));
        panel.add(id);
        id.setBounds(40, 130, 200, 30);

        JTextField idTF = new JTextField();
        idTF.setFont(new Font(Font.SERIF, Font.PLAIN, 25));
        panel.add(idTF);
        idTF.setBounds(120, 130, 300, 30);

        AbstractDAO dao;

        if(t.getSimpleName().equals("Customer"))
            dao = new CustomerDAO();
        else
            dao = new ProductDAO();

        JButton go = new JButton("Delete");
        go.setFont(new Font(Font.SERIF, Font.PLAIN, 25));
        panel.add(go);
        go.setBounds(430, 130, 150, 30);
        go.addActionListener(e ->{
            T obj = (T)dao.findById(Integer.parseInt(idTF.getText()));
            dao.delete(obj);
            this.dispose();
            prev.setVisible(true);
        });
    }
}
