import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class BusReservationSystem {
    private static final String URL = "jdbc:mysql://localhost:3306/bus_reservation";
    private static final String USER = "root";
    private static final String PASSWORD = "12345";

    private static Connection connection;

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);

            Scanner scanner = new Scanner(System.in);
            int choice;
            do {
                System.out.println("Bus Reservation System");
                System.out.println("1. View Available Buses");
                System.out.println("2. Book a Seat");
                System.out.println("3. View Reservations");
                System.out.println("0. Exit");
                System.out.print("Enter your choice: ");
                choice = scanner.nextInt();
                scanner.nextLine();  // Consume newline

                switch (choice) {
                    case 1:
                        viewAvailableBuses();
                        break;
                    case 2:
                        bookSeat(scanner);
                        break;
                    case 3:
                        viewReservations();
                        break;
                    case 0:
                        System.out.println("Exiting...");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } while (choice != 0);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void viewAvailableBuses() throws Exception {
        String query = "SELECT * FROM buses";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        System.out.println("Available Buses:");
        while (resultSet.next()) {
            int busId = resultSet.getInt("bus_id");
            String busName = resultSet.getString("bus_name");
            int seats = resultSet.getInt("seats");
            System.out.println("Bus ID: " + busId + ", Bus Name: " + busName + ", Seats: " + seats);
        }

        resultSet.close();
        statement.close();
    }

    private static void bookSeat(Scanner scanner) throws Exception {
        System.out.print("Enter Bus ID: ");
        int busId = scanner.nextInt();
        scanner.nextLine();  // Consume newline
        System.out.print("Enter Passenger Name: ");
        String passengerName = scanner.nextLine();
        System.out.print("Enter Seat Number: ");
        int seatNumber = scanner.nextInt();

        // Check if the seat is already booked
        String checkQuery = "SELECT * FROM reservations WHERE bus_id = ? AND seat_number = ?";
        PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
        checkStatement.setInt(1, busId);
        checkStatement.setInt(2, seatNumber);
        ResultSet checkResultSet = checkStatement.executeQuery();

        if (checkResultSet.next()) {
            System.out.println("Seat already booked. Please choose a different seat.");
        } else {
            String query = "INSERT INTO reservations (bus_id, passenger_name, seat_number) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, busId);
            preparedStatement.setString(2, passengerName);
            preparedStatement.setInt(3, seatNumber);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            System.out.println("Seat booked successfully.");
        }

        checkResultSet.close();
        checkStatement.close();
    }

    private static void viewReservations() throws Exception {
        String query = "SELECT * FROM reservations";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        System.out.println("Reservations:");
        while (resultSet.next()) {
            int reservationId = resultSet.getInt("reservation_id");
            int busId = resultSet.getInt("bus_id");
            String passengerName = resultSet.getString("passenger_name");
            int seatNumber = resultSet.getInt("seat_number");
            System.out.println("Reservation ID: " + reservationId + ", Bus ID: " + busId + ", Passenger Name: " + passengerName + ", Seat Number: " + seatNumber);
        }

        resultSet.close();
        statement.close();
    }
}
