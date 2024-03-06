package DAO;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import connection.ConnectionFactory;

public class AbstractDAO<T> {
    protected static final Logger LOGGER = Logger.getLogger(AbstractDAO.class.getName());

    protected final Class<T> type;

    @SuppressWarnings("unchecked")
    public AbstractDAO() {
        this.type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

    }

    /**
     * This method creates and returns a String containing an SQL SELECT query, depending on the field given.
     * @param field
     * @return
     */
    private String createSelectQuery(String field) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        sb.append(" * ");
        sb.append(" FROM ");
        sb.append(type.getSimpleName());
        sb.append(" WHERE ").append(field).append(" =?");
        return sb.toString();
    }

    /**
     * This method creates and returns a String containing an SQL SELECT query.
     * @return
     */
    private String createSelectQuery() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        sb.append(" * ");
        sb.append(" FROM ");
        sb.append(type.getSimpleName());
        return sb.toString();
    }

    /**
     * This method creates and returns an SQL UPDATE query.
     * @return
     */
    private String updateStatement()
    {
        StringBuilder s = new StringBuilder();
        s.append("UPDATE ");
        s.append(type.getSimpleName());
        s.append(" SET ");
        Field[] fields = type.getDeclaredFields();
        for(int i = 1; i < fields.length; i++)
        {
            s.append(fields[i].getName());
            s.append(" = ?, ");
        }
        s.delete(s.length() - 2, s.length());
        s.append(" WHERE id = ");
        s.append("?");
        return s.toString();
    }

    /**
     * This method returns a list containing all objects from a table.
     * @return
     */
    public List<T> findAll() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String query = createSelectQuery();
        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            return createObjects(resultSet);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:findAll" + e.getMessage());
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return null;
    }

    public Class<T> getType() {
        return type;
    }

    /**
     * This method returns an object from a table which has the value of the id field equal to the id parameter.
     * @param id
     * @return
     */
    public T findById(int id) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String query = createSelectQuery("id");
        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            System.out.println(statement);
            resultSet = statement.executeQuery();

            return createObjects(resultSet).get(0);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:findById " + e.getMessage());
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return null;
    }

    private List<T> createObjects(ResultSet resultSet) {
        List<T> list = new ArrayList<T>();
        Constructor[] ctors = type.getDeclaredConstructors();
        Constructor ctor = null;
        for (int i = 0; i < ctors.length; i++) {
            ctor = ctors[i];
            if (ctor.getGenericParameterTypes().length == 0)
                break;
        }
        try {
            while (resultSet.next()) {
                ctor.setAccessible(true);
                T instance = (T)ctor.newInstance();
                for (int i = 0; i < type.getDeclaredFields().length; i++) {
                    String fieldName = type.getDeclaredFields()[i].getName();
                    Object value = resultSet.getObject(fieldName);
                    PropertyDescriptor propertyDescriptor = new PropertyDescriptor(fieldName, type);
                    Method method = propertyDescriptor.getWriteMethod();
                    method.invoke(instance, value);
                }
                list.add(instance);
            }
        } catch (InstantiationException | IntrospectionException | SQLException | InvocationTargetException |
                 IllegalArgumentException | SecurityException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * This method creates and returns an SQL INSERT statement.
     * @return
     */
    public String insertStatement()
    {
        StringBuilder s = new StringBuilder();
        s.append("INSERT INTO ");
        s.append(type.getSimpleName());
        s.append(" (");
        Field[] fields = type.getDeclaredFields();
        for(int i = 1; i < fields.length; i++)
        {
            s.append(fields[i].getName()).append(", ");
        }
        s.delete(s.length() - 2, s.length());
        s.append(")");
        s.append(" VALUES (");
        for(int i = 1; i < fields.length; i++)
        {
            s.append("?, ");
        }
        s.delete(s.length() - 2, s.length());
        s.append(")");
        return s.toString();
    }

    /**
     * This method returns an array of Strings containing the name of the fields of the parameter t. Can be used to create a JTable header.
     * @param t
     * @return
     */
    public String[] fieldsToString(T t)
    {
        String[] s = new String[type.getDeclaredFields().length];
        Field[] fields = type.getDeclaredFields();
        PropertyDescriptor propertyDescriptor = null;
        for(int i = 0; i < type.getDeclaredFields().length; i++){
            try {
                propertyDescriptor = new PropertyDescriptor(fields[i].getName(), type);
                s[i] = String.valueOf(propertyDescriptor.getReadMethod().invoke(t));
            } catch (IntrospectionException | InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return s;
    }

    /**
     * This method inserts a new row with the fields of the parameter t.
     * @param t
     * @return
     */
    public boolean insert(T t) {
        Connection connection = null;
        PreparedStatement statement = null;
        try{
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(insertStatement());
            PropertyDescriptor propertyDescriptor = null;
            Field[] fields = type.getDeclaredFields();
            for(int i = 1; i < fields.length; i++){
                String s = fields[i].getType().getSimpleName();
                propertyDescriptor = new PropertyDescriptor(fields[i].getName(), type);
                switch (s) {
                    case "String" -> statement.setString(i, (String) propertyDescriptor.getReadMethod().invoke(t));
                    case "int" -> statement.setInt(i, (int) propertyDescriptor.getReadMethod().invoke(t));
                }
            }
            System.out.println(statement);
            statement.executeUpdate();
            return true;
        }catch(SQLException e) {
            e.printStackTrace();
            LOGGER.log(Level.WARNING, type.getName() + "DAO:findAll " + e.getMessage());
        } catch (IntrospectionException e) {
            e.printStackTrace();
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return false;
    }

    /**
     * This method updates the row with the id field equal to the parameter t's id, inserting the values of t.
     * @param t
     * @return
     */
    public boolean update(T t) {
        Connection connection = null;
        PreparedStatement statement = null;
        try{
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(updateStatement());
            PropertyDescriptor propertyDescriptor = null;
            Field[] fields = type.getDeclaredFields();
            for(int i = 1; i <= fields.length ; i++){
                if(i < fields.length) {
                    String s = fields[i].getType().getSimpleName();
                    propertyDescriptor = new PropertyDescriptor(fields[i].getName(), type);
                    switch (s) {
                        case "String" -> statement.setString(i, (String) propertyDescriptor.getReadMethod().invoke(t));
                        case "int" -> statement.setInt(i, (int) propertyDescriptor.getReadMethod().invoke(t));
                    }
                }
                else{
                    System.out.println(statement);
                    propertyDescriptor = new PropertyDescriptor(fields[0].getName(), type);
                    statement.setInt(i, (int)propertyDescriptor.getReadMethod().invoke(t));
                }
            }
               statement.executeUpdate();
            return true;
        }catch(SQLException e) {
            e.printStackTrace();
            LOGGER.log(Level.WARNING, type.getName() + "DAO:findAll " + e.getMessage());
        } catch (IntrospectionException e) {
            e.printStackTrace();
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }

        return false;
    }

    /**
     * This method creates and returns an SQL DELETE statement.
     * @return
     */
    public String deleteStatement(){
        StringBuilder s = new StringBuilder();
        s.append("DELETE FROM ");
        s.append(type.getSimpleName());
        s.append(" WHERE id = ?");
        return s.toString();
    }

    /**
     * This method deletes the row with the id field equal to the parameter t's id.
     * @param t
     * @return
     */
    public boolean delete(T t)
    {
        Connection connection = null;
        PreparedStatement statement = null;
        try{
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(deleteStatement());
            Field[] fields = type.getDeclaredFields();
            PropertyDescriptor propertyDescriptor = new PropertyDescriptor(fields[0].getName(), type);
            statement.setInt(1, (int)propertyDescriptor.getReadMethod().invoke(t));
            statement.executeUpdate();
            return true;
        }catch(SQLException e) {
            e.printStackTrace();
            LOGGER.log(Level.WARNING, type.getName() + "DAO:findAll " + e.getMessage());
        } catch (IntrospectionException e) {
            e.printStackTrace();
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }

        return false;
    }
}
