package GUI;

import DAO.BillDAO;
import DAO.CustomerDAO;
import DAO.OrdersDAO;
import DAO.ProductDAO;
import model.Bill;
import model.Customer;
import model.Orders;
import model.Product;

import javax.swing.*;
import java.awt.*;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class GUI {
    private final JFrame frame;
    private JPanel panel;

    public GUI()
    {
        frame = new JFrame("Database Application");
        frame.setBounds(300, 150, 900, 600);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

        panel = new JPanel(null);
        frame.add(panel);

        JLabel title = new JLabel("Database Management Application");
        title.setFont(new Font(Font.SERIF, Font.PLAIN, 40));
        panel.add(title);
        title.setBounds(170, 70, 600, 60);

        JButton customer = new JButton("Customers");
        customer.setFont(new Font(Font.SERIF, Font.PLAIN, 30));
        panel.add(customer);
        customer.setBounds(75, 270, 200, 60);
        customer.addActionListener(e ->{
            JFrame tableFrame = new JFrame("Database Application");
            tableFrame.setBounds(300, 150, 900, 600);
            tableFrame.setResizable(false);
            tableFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            tableFrame.setVisible(true);
            frame.setVisible(false);

            JPanel newPanel = new JPanel(null);
            tableFrame.add(newPanel);

            JLabel newTitle = new JLabel("Customers");
            newTitle.setFont(new Font(Font.SERIF, Font.PLAIN, 40));
            newPanel.add(newTitle);
            newTitle.setBounds(350, 70, 600, 60);

            JButton back = new JButton("Back");
            back.setFont(new Font(Font.SERIF, Font.PLAIN, 30));
            newPanel.add(back);
            back.setBounds(750, 450, 100, 60);
            back.addActionListener(c ->{
                tableFrame.dispose();
                frame.setVisible(true);
            });

            JButton insert = new JButton("Insert");
            insert.setFont(new Font(Font.SERIF, Font.PLAIN, 30));
            newPanel.add(insert);
            insert.setBounds(75, 220, 200, 60);
            insert.addActionListener(c ->{
                tableFrame.setVisible(false);
                InsertFrame<Customer> insertFrame = new InsertFrame<Customer>(tableFrame, Customer.class);
            });

            JButton update = new JButton("Update");
            update.setFont(new Font(Font.SERIF, Font.PLAIN, 30));
            newPanel.add(update);
            update.setBounds(350, 220, 200, 60);
            update.addActionListener(c ->{
                tableFrame.setVisible(false);
                UpdateFrame<Customer> updateFrame = new UpdateFrame<Customer>(tableFrame, Customer.class);
            });

            JButton delete = new JButton("Delete");
            delete.setFont(new Font(Font.SERIF, Font.PLAIN, 30));
            newPanel.add(delete);
            delete.setBounds(625, 220, 200, 60);
            delete.addActionListener(c ->{
                tableFrame.setVisible(false);
                DeleteFrame<Customer> deleteFrame = new DeleteFrame<Customer>(tableFrame, Customer.class);
            });

            JButton find = new JButton("Find");
            find.setFont(new Font(Font.SERIF, Font.PLAIN, 30));
            newPanel.add(find);
            find.setBounds(350, 340, 200, 60);
            find.addActionListener(c ->{
                tableFrame.setVisible(false);
                FindFrame<Customer> findFrame = new FindFrame<Customer>(tableFrame, Customer.class);
            });
        });

        JButton order = new JButton("Orders");
        order.setFont(new Font(Font.SERIF, Font.PLAIN, 30));
        panel.add(order);
        order.setBounds(350, 270, 200, 60);
        order.addActionListener(e ->{
            JFrame tableFrame = new JFrame("Database Application");
            tableFrame.setBounds(300, 150, 900, 600);
            tableFrame.setResizable(false);
            tableFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            tableFrame.setVisible(true);
            frame.setVisible(false);

            JPanel newPanel = new JPanel(null);
            tableFrame.add(newPanel);

            JLabel newTitle = new JLabel("Orders");
            newTitle.setFont(new Font(Font.SERIF, Font.PLAIN, 40));
            newPanel.add(newTitle);
            newTitle.setBounds(350, 0, 600, 60);

            JButton back = new JButton("Back");
            back.setFont(new Font(Font.SERIF, Font.PLAIN, 30));
            newPanel.add(back);
            back.setBounds(750, 20, 100, 60);
            back.addActionListener(c ->{
                tableFrame.dispose();
                frame.setVisible(true);
            });

            JLabel customerId = new JLabel("Customer ID");
            customerId.setFont(new Font(Font.SERIF, Font.PLAIN, 30));
            newPanel.add(customerId);
            customerId.setBounds(100, 140, 300, 30);

            CustomerDAO daoCustomers = new CustomerDAO();

            JComboBox<Integer> customerComboBox = new JComboBox<Integer>(daoCustomers.getIds());
            customerComboBox.setFont(new Font(Font.SERIF, Font.PLAIN, 30));
            newPanel.add(customerComboBox);
            customerComboBox.setBounds(100, 180, 100, 30);

            JLabel productId = new JLabel("Product ID");
            productId.setFont(new Font(Font.SERIF, Font.PLAIN, 30));
            newPanel.add(productId);
            productId.setBounds(500, 140, 300, 30);

            ProductDAO daoProduct = new ProductDAO();

            JComboBox<Integer> productComboBox = new JComboBox<Integer>(daoProduct.getIds());
            productComboBox.setFont(new Font(Font.SERIF, Font.PLAIN, 30));
            newPanel.add(productComboBox);
            productComboBox.setBounds(500, 180, 100, 30);

            JLabel quantity = new JLabel("Quantity");
            quantity.setFont(new Font(Font.SERIF, Font.PLAIN, 30));
            newPanel.add(quantity);
            quantity.setBounds(100, 220, 200, 35);

            JTextField quantityTF = new JTextField();
            quantityTF.setFont(new Font(Font.SERIF, Font.PLAIN, 30));
            newPanel.add(quantityTF);
            quantityTF.setBounds(100, 270, 200, 30);

            JButton go = new JButton("Create");
            go.setFont(new Font(Font.SERIF, Font.PLAIN, 30));
            newPanel.add(go);
            go.setBounds(500, 270, 200, 30);
            go.addActionListener(c ->{
                OrdersDAO dao = new OrdersDAO();
                Class<Orders> type = dao.getType();
                BillDAO bill = new BillDAO();

                PropertyDescriptor propertyDescriptor = null;

                Constructor[] ctors = type.getDeclaredConstructors();
                Constructor ctor = null;
                Constructor billCtor = null;
                for (int i = 0; i < ctors.length; i++) {
                    ctor = ctors[i];
                    if (ctor.getGenericParameterTypes().length == 0)
                        break;
                }

                Field[] fields = Orders.class.getDeclaredFields();
                int product_id, customer_id, total_price, amount;

                try {
                        ctor.setAccessible(true);
                        Orders instance = (Orders) ctor.newInstance();
                        ProductDAO productDAO = new ProductDAO();
                        Integer s = customerComboBox.getItemAt(customerComboBox.getSelectedIndex());
                        customer_id = s;
                        propertyDescriptor = new PropertyDescriptor("id_customer", type);
                        propertyDescriptor.getWriteMethod().invoke(instance, s);
                        s = productComboBox.getItemAt(productComboBox.getSelectedIndex());
                        product_id = s;
                        propertyDescriptor = new PropertyDescriptor("id_product", type);
                        propertyDescriptor.getWriteMethod().invoke(instance, s);
                        PropertyDescriptor propertyDescriptor1 = new PropertyDescriptor("price", productDAO.getType());
                        int price = (int)propertyDescriptor1.getReadMethod().invoke(productDAO.findById((int)propertyDescriptor.getReadMethod().invoke(instance)));
                        s = Integer.parseInt(quantityTF.getText());
                        amount = s;
                        propertyDescriptor = new PropertyDescriptor("amount", type);
                        propertyDescriptor.getWriteMethod().invoke(instance, s);
                        propertyDescriptor = new PropertyDescriptor("total_price", type);
                        propertyDescriptor.getWriteMethod().invoke(instance, price * s);
                        total_price = price * s;
                        if(daoProduct.checkAmount(productComboBox.getItemAt(productComboBox.getSelectedIndex()), s)) {
                            dao.insert(instance);
                            bill.insert(new Bill(customer_id, product_id, total_price, amount));
                            daoProduct.decrementStock(productComboBox.getItemAt(productComboBox.getSelectedIndex()), s);
                            JOptionPane.showMessageDialog(tableFrame, "Order successful", "Success", JOptionPane.INFORMATION_MESSAGE);
                        }
                        else{
                            JOptionPane.showMessageDialog(tableFrame, "Not enough stock", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                         IntrospectionException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException(ex);
                }
            });

            JButton find = new JButton("Orders Table");
            find.setFont(new Font(Font.SERIF, Font.PLAIN, 30));
            newPanel.add(find);
            find.setBounds(300, 380, 200, 30);
            find.addActionListener(c ->{
                tableFrame.setVisible(false);
                FindFrame<Orders> findFrame = new FindFrame<Orders>(tableFrame, Orders.class);
            });
        });

        JButton product = new JButton("Products");
        product.setFont(new Font(Font.SERIF, Font.PLAIN, 30));
        panel.add(product);
        product.setBounds(625, 270, 200, 60);
        product.addActionListener(e ->{
            JFrame tableFrame = new JFrame("Database Application");
            tableFrame.setBounds(300, 150, 900, 600);
            tableFrame.setResizable(false);
            tableFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            tableFrame.setVisible(true);
            frame.setVisible(false);

            JPanel newPanel = new JPanel(null);
            tableFrame.add(newPanel);

            JLabel newTitle = new JLabel("Products");
            newTitle.setFont(new Font(Font.SERIF, Font.PLAIN, 40));
            newPanel.add(newTitle);
            newTitle.setBounds(350, 70, 600, 60);

            JButton back = new JButton("Back");
            back.setFont(new Font(Font.SERIF, Font.PLAIN, 30));
            newPanel.add(back);
            back.setBounds(750, 450, 100, 60);
            back.addActionListener(c ->{
                tableFrame.dispose();
                frame.setVisible(true);
            });

            JButton insert = new JButton("Insert");
            insert.setFont(new Font(Font.SERIF, Font.PLAIN, 30));
            newPanel.add(insert);
            insert.setBounds(75, 220, 200, 60);
            insert.addActionListener(c ->{
                tableFrame.setVisible(false);
                InsertFrame<Product> insertFrame = new InsertFrame<Product>(tableFrame, Product.class);
            });

            JButton update = new JButton("Update");
            update.setFont(new Font(Font.SERIF, Font.PLAIN, 30));
            newPanel.add(update);
            update.setBounds(350, 220, 200, 60);
            update.addActionListener(c ->{
                tableFrame.setVisible(false);
                UpdateFrame<Product> updateFrame = new UpdateFrame<Product>(tableFrame, Product.class);
            });

            JButton delete = new JButton("Delete");
            delete.setFont(new Font(Font.SERIF, Font.PLAIN, 30));
            newPanel.add(delete);
            delete.setBounds(625, 220, 200, 60);
            delete.addActionListener(c ->{
                tableFrame.setVisible(false);
                DeleteFrame<Product> deleteFrame = new DeleteFrame<Product>(tableFrame, Product.class);
            });

            JButton find = new JButton("Find");
            find.setFont(new Font(Font.SERIF, Font.PLAIN, 30));
            newPanel.add(find);
            find.setBounds(350, 340, 200, 60);
            find.addActionListener(c ->{
                tableFrame.setVisible(false);
                FindFrame<Product> findFrame = new FindFrame<Product>(tableFrame, Product.class);
            });
        });
    }
}
