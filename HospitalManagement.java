import java.sql.*;
import java.util.Scanner;

public class HospitalManagement {
    private static final String url = "jdbc:mysql://localhost:3306/hospital";
    private static final String username = "root";
    private static final String password = "Sandy@2003";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Scanner scanner = new Scanner(System.in);
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Patient patient = new Patient(connection, scanner);
            Doctor doctor = new Doctor(connection);
            while (true) {
                System.out.println(" HOSPITAL MANAGEMENT SYSTEM  ");
                System.out.println("---------------------------------------");
                System.out.println("1. Add Patient");
                System.out.println("2. View Patient");
                System.out.println("3. View Doctors");
                System.out.println("4. Book Appointment");
                System.out.println("5. View Appointment");
                System.out.println("6. Exit");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        patient.addPatient();
                        System.out.println();
                        break;
                    case 2:
                        patient.viewPatient();
                        System.out.println();
                        break;
                    case 3:
                        doctor.viewDoctor();
                        System.out.println();
                        break;
                    case 4:
                        bookAppointment(patient,doctor,connection,scanner);
                        System.out.println();
                        break;
                    case 5:
                        viewAppointment(connection);
                        break;
                    case 6:
                        return;

                    default:
                        System.out.println("Enter valid choice!!!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void bookAppointment(Patient patient, Doctor doctor, Connection connection, Scanner scanner) {
        System.out.print("Enter Patient Id: ");
        int patientId = scanner.nextInt();
        System.out.println("Enter Doctor Id: ");
        int doctorId = scanner.nextInt();
        System.out.print("Enter appointment date (YYYY-MM-DD): ");
        String appDate = scanner.next();
        if (patient.getPatientById(patientId) && doctor.getDoctorById(doctorId)) {
            if (checkDocAvailable(doctorId, appDate, connection)) {
                String appQuery = "INSERT INTO appointments(patient_id,doctor_id,appointment_date) VALUES(?,?,?)";
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(appQuery);
                    preparedStatement.setInt(1, patientId);
                    preparedStatement.setInt(2, doctorId);
                    preparedStatement.setString(3, appDate);
                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Appointment Booked!!");
                    } else {
                        System.out.println("Failed to Book Appointment");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Doctor Unavailable on this date!!!");
            }
        } else {
            System.out.println("Either patient or doctor does not exist!!!");
        }


    }
    public static void viewAppointment(Connection connection)
    {
        String query="Select * from appointments;";
        try
        {
            Statement smt= connection.createStatement();
            ResultSet resultSet=smt.executeQuery(query);
            System.out.println("| Appointment Id |  Patient Id |  Doctor Id |     Appointment Date     |");
            System.out.println("+------------+----------------------------------+---------+----------------+");
            while(resultSet.next())
            {
                int id =resultSet.getInt("appointment_id");
                int pid = resultSet.getInt("patient_id");
                int did=resultSet.getInt("doctor_id");
                Date date=resultSet.getDate("appointment_date");
                System.out.printf("|%-16s|%-13s|%-12s|%-26s|\n",id,pid,did,date);
                System.out.println("+------------+----------------------------------+---------+----------------+");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    public static boolean checkDocAvailable(int doctorId, String appDate, Connection connection) {
        String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id=? AND appointment_date=?;";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, doctorId);
            preparedStatement.setString(2, appDate);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                if (count == 0) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}