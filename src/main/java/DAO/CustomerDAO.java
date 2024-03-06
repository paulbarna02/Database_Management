package DAO;

import model.Customer;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class CustomerDAO extends AbstractDAO<Customer>{
    public Integer[] getIds() {
        Connection connection = null;
        PreparedStatement statement = null;
        PropertyDescriptor propertyDescriptor = null;
        Integer[] arr = new Integer[this.findAll().size()];
        for(int i = 0; i < arr.length; i++)
        {
            try {
                propertyDescriptor = new PropertyDescriptor("id", getType());
                arr[i] = (Integer)propertyDescriptor.getReadMethod().invoke(findAll().get(i));
            } catch (IllegalAccessException | InvocationTargetException | IntrospectionException e) {
                throw new RuntimeException(e);
            }
        }
        return arr;
    }
}
