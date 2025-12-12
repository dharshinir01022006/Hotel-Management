package dharsh;

import java.sql.*;
import java.util.Scanner;

public class HotelManagement {

    static final String url = "jdbc:mysql://localhost:3306/hotel_db";
    static final String user = "root";
    static final String pass = ""; // change if needed

    public static void main(String[] args) throws Exception {

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== HOTEL MANAGEMENT SYSTEM =====");
            System.out.println("1. Add Room");
            System.out.println("2. Book Room");
            System.out.println("3. Checkout");
            System.out.println("4. View All Rooms");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");

            int ch = sc.nextInt();

            switch (ch) {
                case 1: addRoom(); break;
                case 2: bookRoom(); break;
                case 3: checkout(); break;
                case 4: viewRooms(); break;
                case 5: System.exit(0);
                default: System.out.println("Invalid choice!");
            }
        }
    }

    // ------------------ ADD ROOM --------------------
    public static void addRoom() throws Exception {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter Room ID: ");
        int id = sc.nextInt();
        System.out.print("Enter Room Type (AC/Non-AC): ");
        String type = sc.next();
        System.out.print("Enter Price: ");
        double price = sc.nextDouble();

        Connection con = DriverManager.getConnection(url, user, pass);

        PreparedStatement ps = con.prepareStatement(
        	    "INSERT INTO rooms(room_id, room_type, price, status) VALUES (?, ?, ?, 'Available')");
        	ps.setInt(1, id);
        	ps.setString(2, type);
        	ps.setDouble(3, price);

      

        System.out.println("Room added successfully!");
        con.close();
    }

    // ------------------ BOOK ROOM --------------------
    public static void bookRoom() throws Exception {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter Room ID to Book: ");
        int rid = sc.nextInt();
        sc.nextLine();

        System.out.print("Customer Name: ");
        String name = sc.nextLine();
        System.out.print("Customer Mobile: ");
        String mobile = sc.nextLine();
        System.out.print("Check-in Date (YYYY-MM-DD): ");
        String in = sc.next();
        System.out.print("Check-out Date (YYYY-MM-DD): ");
        String out = sc.next();

        Connection con = DriverManager.getConnection(url, user, pass);

        // Check room availability
        PreparedStatement check = con.prepareStatement(
                "SELECT status FROM rooms WHERE room_id=?");
        check.setInt(1, rid);

        ResultSet rs = check.executeQuery();

        if (!rs.next()) {
            System.out.println("Room does not exist!");
            return;
        }

        if (rs.getString("status").equals("Booked")) {
            System.out.println("Room already booked!");
            return;
        }

        // Insert into bookings
        PreparedStatement ps = con.prepareStatement(
                "INSERT INTO bookings(room_id, customer_name, customer_mobile, checkin_date, checkout_date) VALUES (?, ?, ?, ?, ?)"
        );

        ps.setInt(1, rid);
        ps.setString(2, name);
        ps.setString(3, mobile);
        ps.setString(4, in);
        ps.setString(5, out);

        ps.executeUpdate();

        // Update room status
        PreparedStatement update = con.prepareStatement(
                "UPDATE rooms SET status='Booked' WHERE room_id=?");
        update.setInt(1, rid);
        update.executeUpdate();

        System.out.println("Room booked successfully!");
        con.close();
    }

    // ------------------ CHECKOUT --------------------
    public static void checkout() throws Exception {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter Room ID to Checkout: ");
        int rid = sc.nextInt();

        Connection con = DriverManager.getConnection(url, user, pass);

        // Delete booking
        PreparedStatement del = con.prepareStatement(
                "DELETE FROM bookings WHERE room_id=?");
        del.setInt(1, rid);

        int rows = del.executeUpdate();

        if (rows > 0) {
            PreparedStatement update = con.prepareStatement(
                    "UPDATE rooms SET status='Available' WHERE room_id=?");
            update.setInt(1, rid);
            update.executeUpdate();

            System.out.println("Checkout successful!");
        } else {
            System.out.println("Room not booked!");
        }

        con.close();
    }

    // ---------------- VIEW ALL ROOMS -----------------
    public static void viewRooms() throws Exception {
        Connection con = DriverManager.getConnection(url, user, pass);

        PreparedStatement ps = con.prepareStatement("SELECT * FROM rooms");
        ResultSet rs = ps.executeQuery();

        System.out.println("\nROOM_ID | TYPE | PRICE | STATUS");
        System.out.println("--------------------------------");

        while (rs.next()) {
            System.out.println(
                    rs.getInt("room_id") + " | "
                            + rs.getString("room_type") + " | "
                            + rs.getDouble("price") + " | "
                            + rs.getString("status"));
        }

        con.close();
    }
}