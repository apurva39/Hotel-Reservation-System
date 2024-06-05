import java.sql.*;
import java.util.Scanner;

import static java.lang.Class.forName;

public class hotel_reservation_system {
    private static final String url="jdbc:postgresql://localhost:5432/hotel_db";
    private static final String username="postgres";
    private static final String password="root";

    public static void main(String[] args) throws ClassNotFoundException, SQLException{
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e)
        {

            System.out.println(e.getMessage());
        }

        try
        {
            Connection connection=DriverManager.getConnection(url,username,password);
            Statement statement=connection.createStatement();
            while(true)
            {
                System.out.println("----------------++++----------------");
                Scanner scanner=new Scanner(System.in);
                System.out.println("1.Reserve a Room ");
                System.out.println("2.View Reservation");
                System.out.println("3.Get Room Number");
                System.out.println("4.Update Reservation");
                System.out.println("5.Delete Reservation");
                System.out.println("0.Exit");
                System.out.println("Choose an Option: ");
                int choice=scanner.nextInt();

                switch (choice)
                {
                    case 1:
                        reserveRoom(connection,scanner,statement);
                        break;
                    case 2:
                        viewReservation(connection,scanner,statement);
                        break;
                    case 3:
                        getRoomNumber(connection,scanner,statement);
                        break;

                    case 4:
                        updateReservation(connection,scanner,statement);
                        break;

                    case 5:
                        deleteReservation(connection,scanner,statement);
                        break;

                    case 0:
                        exit();
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid choice. Try again.");
                }

            }

        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }

    private static void reserveRoom(Connection connection,Scanner scanner,Statement statement)
    {
        String name;
        int roomNumber;
        int contactNo;
        String reservationDate;
        System.out.print("Enter the Guest name: ");
        name=scanner.next();
        scanner.nextLine();
        System.out.print("Enter the Room Number: ");
        roomNumber=scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter the Contact Number: ");
        contactNo=scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter the Reservation Date: ");
        reservationDate=scanner.next();
        scanner.nextLine();

        String sql="INSERT INTO reservations (guest_name,room_no,phone_no,reservation_date)" + "VALUES('"+name+"','"+roomNumber+"','"+contactNo+"','" + reservationDate+ "')";
        try
        {
            int affectRows=statement.executeUpdate(sql);

            if(affectRows>0)
            {
                System.out.println("Reservation Sucessful");
            }
            else {
                System.out.println("Reservation Failed!!!");
            }

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    private static void viewReservation(Connection connection,Scanner scanner,Statement statement) throws SQLException {

        String sql="Select reservation_id,guest_name,room_no,phone_no,reservation_date from reservations";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = ps.executeQuery()) {

                System.out.println("Current Reservations: ");

                while (resultSet.next()) {
                    int reservationId = resultSet.getInt("reservation_id");
                    String guestName = resultSet.getString("guest_name");
                    int roomNumber = resultSet.getInt("room_no");
                    int contactNumber = resultSet.getInt("phone_no");
                    String reservationDate = resultSet.getString("reservation_date");

                    System.out.println("Reservation_ID :" + reservationId);
                    System.out.println("Guest Name:" + guestName);
                    System.out.println("Room No : " + roomNumber);
                    System.out.println("Phone No :" + contactNumber);
                    System.out.println("Reservation date: " + reservationDate);


    //                System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n",
    //                        reservationId, guestName, roomNumber, contactNumber, reservationDate);
                }

            }
        }

    }

    private static void getRoomNumber(Connection connection,Scanner scanner,Statement statement) {

            System.out.println("Enter reservation ID: ");
            int reservationId=scanner.nextInt();

            System.out.println("Enter Guest Name: ");
            String guestName=scanner.next();


            String sql = "SELECT room_no FROM reservations WHERE reservation_id = ? AND guest_name = ?";


            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, reservationId);
                preparedStatement.setString(2, guestName);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int roomNumber = resultSet.getInt("room_no");
                        System.out.println("Room number for Reservation ID " + reservationId +
                                " and Guest " + guestName + " is: " + roomNumber);
                    } else {
                        System.out.println("Reservation not found for the given ID and guest name.");
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
    }

    private static void updateReservation(Connection connection,Scanner scanner,Statement statement){
        try
        {

            System.out.println("Enter reservation ID to Update: ");
            int reservationId=scanner.nextInt();

            if(!reservationIdExist(connection,reservationId))
            {
                System.out.println("Reservation ID not found!");
                return;

            }

            String guestName;
            int roomNumber;
            int phoneNumber;

            System.out.println("Enter the Guest Name: ");
            guestName=scanner.next();

            System.out.println("Enter the updated Room Number: ");
            roomNumber=scanner.nextInt();

            System.out.println("Enter the updated Phone Number: ");
            phoneNumber=scanner.nextInt();

            String updatedSql="UPDATE reservations SET guest_name= ?, room_no = ?, phone_no = ? WHERE reservation_id = ? ";



            try(PreparedStatement preparedStatement=connection.prepareStatement(updatedSql))
            {
                preparedStatement.setString(1,guestName);
                preparedStatement.setInt(2,roomNumber);
                preparedStatement.setInt(3,phoneNumber);
                preparedStatement.setInt(4,reservationId);


                int affectedRow=preparedStatement.executeUpdate();

                if(affectedRow>0)
                {
                    System.out.println("Reservations Updated Successfully!");
                }else {
                    System.out.println("Reservations Updated Failed!!!!");

                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private static void deleteReservation(Connection connection,Scanner scanner,Statement statement)
    {
        try{
            int reservationId;
            System.out.println("Enter the Reservation Id to delete: ");
            reservationId=scanner.nextInt();

            if(!reservationIdExist(connection,reservationId))
            {
                System.out.println("Reservation not found for the given Id");
                return;
            }


            String deleteSql="DELETE FROM reservations WHERE reservation_id= ?";

            try(PreparedStatement preparedStatement=connection.prepareStatement(deleteSql))
            {
                preparedStatement.setInt(1,reservationId);

                int affectedRows=preparedStatement.executeUpdate();

                if (affectedRows > 0) {
                    System.out.println("Reservation deleted successfully!");
                } else {
                    System.out.println("Reservation deletion failed.");
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public static Boolean reservationIdExist(Connection connection, int reservationId) {
        String sql = "SELECT reservation_id FROM reservations WHERE reservation_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, reservationId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public static void exit() throws InterruptedException {
        System.out.println("Exiting System: ");
        int i=5;
        while(i!=0)
        {
            System.out.print("Bye ");
            Thread.sleep(1000);
            i--;
        }
        System.out.println();
        System.out.println("ThankYou For Using Hotel Reservation System!!!");
    }

}
