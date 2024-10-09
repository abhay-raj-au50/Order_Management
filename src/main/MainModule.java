package main;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import dao.OrderProcessor;
import entity.Product;
import entity.User;
import exception.OrderNotFoundException;
import exception.UserNotFoundException;
import util.DBConnUtil;
import util.DBPropertyUtil;

public class MainModule {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        User user = new User(1, "admin", "admin_password", "admin");
        String propertyFileName = "db.properties"; // Ensure this file exists with proper DB config
        String connectionString = DBPropertyUtil.getConnectionString();
        
        if (connectionString == null) {
            System.out.println("Error loading database configuration.");
            return;
        }

        DBConnUtil.getDBConn();
        OrderProcessor orderProcessor = new OrderProcessor();

        while (true) {
            System.out.println("Menu:");
            System.out.println("1. Create User");
            System.out.println("2. Create Product");
            System.out.println("3. Create Order");
            System.out.println("4. Cancel Order");
            System.out.println("5. Get All Products");
            System.out.println("6. Get Orders by User");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");
            
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    // Create user
                    System.out.print("Enter User ID: ");
				scanner.nextInt();
                    System.out.print("Enter Username: ");
				scanner.next();
                    System.out.print("Enter Password: ");
				scanner.next();
                    System.out.print("Enter Role (User/Admin): ");
				scanner.next();
                   
                    try {
                        orderProcessor.createUser(user);
                        System.out.println("User created successfully.");
                    } catch (SQLException e) {
                        System.out.println("Error creating user: " + e.getMessage());
                    }
                    break;

                case 2:
                    // Create product
                    System.out.print("Enter Product ID: ");
                    int productId = scanner.nextInt();
                    System.out.print("Enter Product Name: ");
                    String productName = scanner.next();
                    System.out.print("Enter Description: ");
                    String description = scanner.next();
                    System.out.print("Enter Price: ");
                    double price = scanner.nextDouble();
                    System.out.print("Enter Quantity in Stock: ");
                    int quantityInStock = scanner.nextInt();
                    System.out.print("Enter Product Type: ");
                    String type = scanner.next();
                    Product product = new Product(productId, productName, description, price, quantityInStock, type);
                    try {
                        orderProcessor.createProduct(user, product); 
                        System.out.println("Product created successfully.");
                    } catch (UserNotFoundException | SQLException e) {
                        System.out.println("Error creating product: " + e.getMessage());
                    }
                    break;

                case 3:
                    // Create order
                    System.out.print("Enter User ID for the order: ");
                    int orderUserId = scanner.nextInt();
                    System.out.print("Enter Product ID: ");
                    int orderProductId = scanner.nextInt();
                    // For now, using a dummy product instead of fetching from DB
                    Product orderProduct = new Product(orderProductId, "Dummy Product", "Dummy Description", 0.0, 0, "Dummy Type");
                    List<Product> orderProducts = new ArrayList<>();
                    orderProducts.add(orderProduct);
                    try {
                        User orderUser = new User(orderUserId, "", "", ""); // Assuming User object exists
                        orderProcessor.createOrder(orderUser, orderProducts);
                        System.out.println("Order created successfully.");
                    } catch (UserNotFoundException | SQLException e) {
                        System.out.println("Error creating order: " + e.getMessage());
                    }
                    break;

                case 4:
                    // Cancel order
                    System.out.print("Enter User ID: ");
                    int cancelUserId = scanner.nextInt();
                    System.out.print("Enter Order ID: ");
                    int orderId = scanner.nextInt();
                    try {
                        orderProcessor.cancelOrder(cancelUserId, orderId);
                        System.out.println("Order cancelled successfully.");
                    } catch (UserNotFoundException | OrderNotFoundException e) {
                        System.out.println("Error cancelling order: " + e.getMessage());
                    }
                    break;

                case 5:
                    // Get all products
                    try {
                        List<Product> products = orderProcessor.getAllProducts();
                        System.out.println("Products:");
                        for (Product p : products) {
                            System.out.println(p);
                        }
                    } catch (SQLException e) {
                        System.out.println("Error fetching products: " + e.getMessage());
                    }
                    break;

                case 6:
                    // Get orders by user
                    System.out.print("Enter User ID to fetch orders: ");
                    int fetchUserId = scanner.nextInt();
                    User fetchUser = new User(fetchUserId, "", "", ""); // Assuming User object exists
                    try {
                        List<Product> userProducts = orderProcessor.getOrderByUser(fetchUser);
                        System.out.println("Orders for User ID " + fetchUserId + ":");
                        for (Product p : userProducts) {
                            System.out.println(p);
                        }
                    } catch (SQLException e) {
                        System.out.println("Error fetching orders: " + e.getMessage());
                    }
                    break;

                case 7:
                    // Exit
                    System.out.println("Exiting...");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }
}
