package dao;

import entity.*;
import exception.OrderNotFoundException;
import exception.UserNotFoundException;
import util.DBConnUtil; // Import the DBConnUtil class

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderProcessor implements IOrderManagementRepository {
    private Connection connection;

    public OrderProcessor() throws ClassNotFoundException, SQLException {
        connection = DBConnUtil.getDBConn();  // Ensure DBConnUtil is correctly implemented
    }

  


    private boolean userExists(int userId) throws SQLException {
        String userExistsQuery = "SELECT * FROM User WHERE userId = ?";
        try (PreparedStatement statement = connection.prepareStatement(userExistsQuery)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private boolean orderExists(int orderId, int userId) throws SQLException {
        String orderExistsQuery = "SELECT * FROM OrderTable WHERE orderId = ? AND userId = ?";
        try (PreparedStatement statement = connection.prepareStatement(orderExistsQuery)) {
            statement.setInt(1, orderId);
            statement.setInt(2, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    @Override
    public void createOrder(User user, List<Product> products) throws UserNotFoundException, SQLException {
        if (!userExists(user.getUserId())) {
            throw new UserNotFoundException("User not found with ID: " + user.getUserId());
        }

        String query = "INSERT INTO OrderTable (userId, productId) VALUES (?, ?)";
        for (Product product : products) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, user.getUserId());
                preparedStatement.setInt(2, product.getProductId());
                preparedStatement.executeUpdate();
            }
        }
    }

    @Override
    public void cancelOrder(int userId, int orderId) throws UserNotFoundException, OrderNotFoundException, SQLException {
        if (!userExists(userId)) {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }
        if (!orderExists(orderId, userId)) {
            throw new OrderNotFoundException("Order not found with ID: " + orderId);
        }

        String query = "DELETE FROM OrderTable WHERE userId = ? AND orderId = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, orderId);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void createProduct(User user, Product product) throws UserNotFoundException, SQLException {
        if (!"Admin".equals(user.getRole())) {
            throw new UserNotFoundException("User is not authorized to create products.");
        }

        String query = "INSERT INTO products (productId, productName, description, price, quantityInStock, type) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, product.getProductId());
            preparedStatement.setString(2, product.getProductName());
            preparedStatement.setString(3, product.getDescription());
            preparedStatement.setDouble(4, product.getPrice());
            preparedStatement.setInt(5, product.getQuantityInStock());
            preparedStatement.setString(6, product.getType());
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void createUser(User user) throws SQLException {
        String query = "INSERT INTO User (userId, username, password, role) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, user.getUserId());
            preparedStatement.setString(2, user.getUsername());
            preparedStatement.setString(3, user.getPassword());
            preparedStatement.setString(4, user.getRole());
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public List<Product> getAllProducts() throws SQLException {
        List<Product> productList = new ArrayList<>();
        String query = "SELECT * FROM products";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                Product product = new Product();
                product.setProductId(resultSet.getInt("productId"));
                product.setProductName(resultSet.getString("productName"));
                product.setDescription(resultSet.getString("description"));
                product.setPrice(resultSet.getDouble("price"));
                product.setQuantityInStock(resultSet.getInt("quantityInStock"));
                product.setType(resultSet.getString("type"));
                productList.add(product);
            }
        }
        return productList;
    }

    @Override
    public List<Product> getOrderByUser(User user) throws SQLException {
        List<Product> userProducts = new ArrayList<>();
        String query = "SELECT p.* FROM products p INNER JOIN OrderTable o ON p.productId = o.productId WHERE o.userId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, user.getUserId());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Product product = new Product();
                    product.setProductId(resultSet.getInt("productId"));
                    product.setProductName(resultSet.getString("productName"));
                    product.setDescription(resultSet.getString("description"));
                    product.setPrice(resultSet.getDouble("price"));
                    product.setQuantityInStock(resultSet.getInt("quantityInStock"));
                    product.setType(resultSet.getString("type"));
                    userProducts.add(product);
                }
            }
        }
        return userProducts;
    }
}
