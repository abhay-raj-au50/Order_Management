package dao;

import entity.Product;
import entity.User;
import exception.OrderNotFoundException;
import exception.UserNotFoundException;

import java.sql.SQLException;
import java.util.List;

public interface IOrderManagementRepository {
    void createOrder(User user, List<Product> products) throws UserNotFoundException, SQLException;
    void cancelOrder(int userId, int orderId) throws UserNotFoundException, OrderNotFoundException, SQLException;
    void createProduct(User user, Product product) throws UserNotFoundException, SQLException;
    void createUser(User user) throws SQLException;
    List<Product> getAllProducts() throws SQLException;
    List<Product> getOrderByUser(User user) throws SQLException;
}
