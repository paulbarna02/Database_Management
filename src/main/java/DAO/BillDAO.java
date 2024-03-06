package DAO;

import connection.ConnectionFactory;
import model.Bill;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

public class BillDAO extends AbstractDAO<Bill> {
    /**
     * This method inserts a new row in the bill table, with the values of t.
     * @param t
     * @return
     */
    public boolean insert(Bill t){
        Connection connection = null;
        PreparedStatement statement = null;
        try{
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(insertBillStatement());
            PropertyDescriptor propertyDescriptor = null;
            Field[] fields = type.getDeclaredFields();
            System.out.println(statement);
            for(int i = 0; i < fields.length ; i++){
                fields[i].setAccessible(true);
                Integer val = (Integer)fields[i].get(t);
                statement.setInt(i + 1, val);
            }
            statement.executeUpdate();
            return true;
        }catch(SQLException e) {
            e.printStackTrace();
            LOGGER.log(Level.WARNING, type.getName() + "DAO:findAll " + e.getMessage());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return false;
    }

    /**
     * This method creates and returns an SQL insert statement for the bill table.
     * @return
     */
    private String insertBillStatement()
    {
        StringBuilder s = new StringBuilder();
        s.append("INSERT INTO ");
        s.append(type.getSimpleName());
        s.append(" (");
        Field[] fields = type.getDeclaredFields();
        for(int i = 0; i < fields.length; i++)
        {
            s.append(fields[i].getName()).append(", ");
        }
        s.delete(s.length() - 2, s.length());
        s.append(")");
        s.append(" VALUES (");
        for(int i = 0; i < fields.length; i++)
        {
            s.append("?, ");
        }
        s.delete(s.length() - 2, s.length());
        s.append(")");
        return s.toString();
    }
}
