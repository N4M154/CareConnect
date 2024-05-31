import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import oracle.jdbc.OracleTypes;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;


public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to CareConnect!");
        System.out.println("1. Signup");
        System.out.println("2. Login");
        System.out.print("Choose an option: ");
        int option = scanner.nextInt();
        scanner.nextLine();

        switch (option) {
            case 1:
                signUp(scanner);
                break;
            case 2:
                login(scanner);
                break;
            default:
                System.out.println("Invalid option.");
                break;
        }
    }

    public static void viewPrescriptionsByDoctorId(String doctorId) {
        try (Connection connection = DBConnection.getConnection()) {
            String sql = "{call prescriptions_by_doctor(?, ?)}";
            try (CallableStatement statement = connection.prepareCall(sql)) {
                statement.setString(1, doctorId);
                statement.registerOutParameter(2, OracleTypes.CURSOR);
                statement.execute();
                try (ResultSet resultSet = (ResultSet) statement.getObject(2)) {
                    if (!resultSet.isBeforeFirst()) {
                        System.out.println("No prescriptions found for this doctor.");
                        return;
                    }

                    System.out.println("Prescriptions for Doctor ID: " + doctorId);
                    System.out.printf("%-15s %-15s %-10s %-20s %-10s %-15s %-15s %-15s\n", "Prescription ID", "Appointment ID", "Patient ID", "Patient Name", "Age", "Blood Group", "Symptoms", "Medicine");
                    System.out.println("---------------------------------------------------------------------------------------------");
                    while (resultSet.next()) {
                        String prescriptionId = resultSet.getString("prescription_id");
                        String appointmentId = resultSet.getString("app_id");
                        String patientId = resultSet.getString("patient_id");
                        String patientRealName = resultSet.getString("patient_realname");
                        int age = resultSet.getInt("age");
                        String bloodGroup = resultSet.getString("blood_group");
                        String symptoms = resultSet.getString("symptoms");
                        String medicine = resultSet.getString("medicine");

                        System.out.printf("%-15s %-15s %-10s %-20s %-10s %-15s %-15s %-15s\n", prescriptionId, appointmentId, patientId, patientRealName, age, bloodGroup, symptoms, medicine);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static void signUp(Scanner scanner) {


        int roleOption;
        String role = "";
        do {
            System.out.println("Select role:");
            System.out.println("1. Patient");
            System.out.println("2. Doctor");
            roleOption = Integer.parseInt(scanner.nextLine());
            switch (roleOption) {
                case 1:
                    role = "patient";
                    break;
                case 2:
                    role = "doctor";
                    break;
                default:
                    System.out.println("Invalid option. Please enter 1 for Patient or 2 for Doctor.");
                    break;
            }
        } while (roleOption != 1 && roleOption != 2);
        String username;
        boolean usernameExists;

        do {
            System.out.println("Enter username:");
            username = scanner.nextLine();

            usernameExists = checkUsernameExistence(username,role);
            if (usernameExists) {
                System.out.println("Username already exists. Please choose a different username.");
            }
        } while (usernameExists);


        System.out.println("Enter real name:");
        String realname = scanner.nextLine();

        System.out.println("Enter password:");
        String password = scanner.nextLine();

        Integer age = null;
        String bloodGroup = null;
        String specialty = null;
        String phoneNumber = null;
        String day = null;
        String startTime = null;
        String endTime = null;
        int maxPatientCount = 0;
        if (role.equals("patient")) {
            System.out.println("Enter age:");
            while (true) {
                try {
                    age = Integer.parseInt(scanner.nextLine());
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid age. Please enter a valid number.");
                }
            }
            System.out.println("Enter blood group:");
            bloodGroup = scanner.nextLine();
        } else if (role.equals("doctor")) {
            System.out.println("Enter specialty:");
            specialty = scanner.nextLine();

            System.out.println("Enter phone number:");
            phoneNumber = scanner.nextLine();

            System.out.println("Enter the day for appointments (e.g., Monday):");
            day = scanner.nextLine();


            System.out.println("Enter the number of patients you want to book");
            maxPatientCount = Integer.parseInt(scanner.nextLine());

            System.out.println("Enter the start time for appointments (e.g., 09:00):");
            startTime = scanner.nextLine();

            System.out.println("Enter the end time for appointments (e.g., 12:00):");
            endTime = scanner.nextLine();


        }

        try (Connection connection = DBConnection.getConnection()) {
            String sql;
            if (role.equals("patient")) {
                sql = "INSERT INTO patients (username, realname, password, age, bloodgroup) VALUES (?, ?, ?,?, ?)";
            } else {
                sql = "INSERT INTO doctors (username, realname, password_, specialty, phone_number, day, max_patients, start_time, end_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                statement.setString(2, realname);
                statement.setString(3, password);
                if (role.equals("patient")) {
                    statement.setInt(4, age);
                    statement.setString(5, bloodGroup);
                } else {

                    statement.setString(4, specialty);
                    statement.setString(5, phoneNumber);
                    statement.setString(6, day);
                    statement.setInt(7, maxPatientCount);
                    statement.setString(8, startTime);
                    statement.setString(9, endTime);
                }
                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("Signup successful. You can now login.");
                    login(scanner);
                } else {
                    System.out.println("Signup failed. Please try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static boolean checkUsernameExistence(String username, String role) {
        try (Connection connection = DBConnection.getConnection()) {
            String sql;
            if (role.equals("doctor")) {
                sql = "SELECT COUNT(*) FROM (SELECT 1 FROM patients WHERE username = ? UNION ALL SELECT 1 FROM doctors WHERE username = ?)";
            } else {
                sql = "SELECT COUNT(*) FROM (SELECT 1 FROM patients WHERE username = ? UNION ALL SELECT 1 FROM patients WHERE username = ?)";
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                statement.setString(2, username);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        int count = resultSet.getInt(1);
                        return count > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    private static void login(Scanner scanner) {

        int x=0;
        int roleOption;
        String role = "";
        do {
            System.out.println("Select role:");
            System.out.println("1. Patient");
            System.out.println("2. Doctor");
            roleOption = Integer.parseInt(scanner.nextLine());
            switch (roleOption) {
                case 1:
                    role = "patient";
                    break;
                case 2:
                    role = "doctor";
                    break;
                default:
                    System.out.println("Invalid option. Please enter 1 for Patient or 2 for Doctor.");
                    break;
            }
        } while (roleOption != 1 && roleOption != 2);

        String username;
        boolean usernameExists;

        do {
            System.out.println("Enter username:");
            username = scanner.nextLine();

            usernameExists = checkUsernameExistence(username,role);
            if (!usernameExists) {
                System.out.println("Username doesn't exist. Please enter a correct username.");
            }
        } while (!usernameExists);

        boolean loginSuccessful = false;
        while (!loginSuccessful && x==0) {
            System.out.println("Enter password:");
            String password = scanner.nextLine();

            String tableName = (role.equals("patient")) ? "patients" : "doctors";
            String passwordColumnName = (role.equals("patient")) ? "password" : "password_";
            String idColumnName = (role.equals("patient")) ? "id" : "id";

            try (Connection connection = DBConnection.getConnection()) {
                String sql = "SELECT " + idColumnName + " FROM " + tableName + " WHERE username = ? AND " + passwordColumnName + " = ?";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, username);
                    statement.setString(2, password);
                    try (ResultSet resultSet = statement.executeQuery()) {
                        if (resultSet.next()) {
                            String userId = resultSet.getString(idColumnName);
                            System.out.println("Login successful. Welcome, " + username + "!");
                            x=3;
                            boolean exit = false;
                            while (!exit) {
                                if (role.equals("patient")) {
                                    System.out.println("1. See the list of doctors");
                                    System.out.println("2. Book an appointment");
                                    System.out.println("3. View your appointments");
                                    System.out.println("4. Cancel appointment");
                                    System.out.println("5. View your Prescriptions");
                                    System.out.println("6. View your Bills");
                                    System.out.println("7. Delete account");
                                    System.out.println("8. Exit ");
                                    System.out.print("Choose an option: ");
                                    int option = scanner.nextInt();
                                    scanner.nextLine();
                                    switch (option) {
                                        case 1:
                                            showDoctorsList();
                                            break;
                                        case 2:
                                            bookAppointment(scanner, username, userId, role);
                                            break;
                                        case 3:
                                            viewPatientAppointments(username);
                                            break;
                                        case 4:
                                            cancelAppointment(scanner, username);
                                            break;
                                        case 5:
                                            viewPrescriptions(userId);
                                            break;

                                        case 6:
                                            viewBills(userId);
                                            break;
                                        case 7:
                                            deleteAccount(scanner, username, role);
                                            break;
                                        case 8:
                                            exit=true;
                                            break;
                                        default:
                                            System.out.println("Invalid option.");
                                    }
                                } else if (role.equals("doctor")) {
                                    System.out.println("1. View your appointments");
                                    System.out.println("2. Upload prescriptions");
                                    System.out.println("3. View prescriptions");
                                    System.out.println("4. Delete account");
                                    System.out.println("5. Exit ");

                                    System.out.print("Choose an option: ");

                                    int option = scanner.nextInt();
                                    scanner.nextLine();
                                    switch (option) {
                                        case 1:
                                            viewDoctorAppointments(username);
                                            break;
                                        case 2:
                                            uploadPrescriptions(scanner, username);
                                            break;
                                        case 3:
                                            viewPrescriptionsByDoctorId(userId);
                                            break;
                                        case 4:
                                            deleteAccount(scanner, username, role);
                                            break;
                                        case 5:
                                            exit=true;
                                            break;

                                        default:
                                            System.out.println("Invalid option.");
                                    }
                                }
                            }
                        } else {
                            System.out.println("Invalid username or password.");
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

        public static void cancelAppointment (Scanner scanner, String patientUsername) {
            try (Connection connection = DBConnection.getConnection()) {
                // Fetch the current appointments for the patient
                String fetchAppointmentsSql = "SELECT id AS appointment_id, appointment_date, doctor_id, doctor_name " +
                        "FROM appointments WHERE patient_username = ?";
                try (PreparedStatement fetchAppointmentsStmt = connection.prepareStatement(fetchAppointmentsSql)) {
                    fetchAppointmentsStmt.setString(1, patientUsername);
                    try (ResultSet resultSet = fetchAppointmentsStmt.executeQuery()) {
                        List<String> appointmentIds = new ArrayList<>();
                        while (resultSet.next()) {
                            String appointmentId = resultSet.getString("appointment_id");
                            String appointmentDate = resultSet.getString("appointment_date");
                            String doctorId = resultSet.getString("doctor_id");
                            String doctorRealName = resultSet.getString("doctor_name");
                            System.out.printf("Appointment ID: %s, Date: %s, Doctor ID: %s, Doctor Name: %s\n",
                                    appointmentId, appointmentDate, doctorId, doctorRealName);
                            appointmentIds.add(appointmentId);
                        }

                        if (appointmentIds.isEmpty()) {
                            System.out.println("You have no appointments to cancel.");
                            return;
                        }

                        // Ask the patient to select an appointment to cancel
                        System.out.print("Enter the ID of the appointment you want to cancel: ");
                        String appointmentIdToCancel = scanner.nextLine();

                        // Check if the entered appointment ID is valid
                        if (!appointmentIds.contains(appointmentIdToCancel)) {
                            System.out.println("Invalid appointment ID.");
                            return;
                        }

                        // Call the stored procedure to cancel the appointment
                        String cancelAppointmentSql = "{call cancel_appointment(?, ?)}";
                        try (CallableStatement cancelAppointmentStmt = connection.prepareCall(cancelAppointmentSql)) {
                            cancelAppointmentStmt.setString(1, appointmentIdToCancel);
                            cancelAppointmentStmt.registerOutParameter(2, Types.NUMERIC);
                            cancelAppointmentStmt.executeUpdate();
                            int rowsDeleted = cancelAppointmentStmt.getInt(2);
                            if (rowsDeleted > 0) {
                                System.out.println("Appointment canceled successfully.");
                            } else {
                                System.out.println("Failed to cancel appointment. Please try again.");
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


    private static void viewBills(String patientId) {
        try (Connection connection = DBConnection.getConnection()) {
            String sql = "SELECT * FROM bills WHERE patient_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, patientId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (!resultSet.isBeforeFirst()) {
                        System.out.println("You don't have any bills.");
                    } else {
                        System.out.println("Your Bills:");
                        System.out.printf("%-10s %-10s %-15s %-15s %-20s %-20s %-20s %-20s %-10s\n", "Bill ID", "Appt ID", "Patient ID", "Doctor ID", "Patient Username", "Doctor Name", "Doctor Specialty", "Appointment Day", "Amount");
                        System.out.println("----------------------------------------------------------------------------------------------------------------------------");
                        while (resultSet.next()) {
                            String billId = resultSet.getString("id");
                            String appointmentId = resultSet.getString("app_id");
                            String patientIdFromDB = resultSet.getString("patient_id");
                            String doctorId = resultSet.getString("doctor_id");
                            String patientUsername = resultSet.getString("patient_username");
                            String doctorName = resultSet.getString("doctor_name");
                            String doctorSpecialty = resultSet.getString("doctor_specialty");
                            String appointmentDay = resultSet.getString("appointment_day");
                            BigDecimal amount = resultSet.getBigDecimal("amount");

                            System.out.printf("%-10s %-10s %-15s %-15s %-20s %-20s %-20s %-20s %-10s\n", billId, appointmentId, patientIdFromDB, doctorId, patientUsername, doctorName, doctorSpecialty, appointmentDay, amount);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void viewPrescriptions(String patientId) {
        try (Connection connection = DBConnection.getConnection()) {
            String sql = "{call view_prescriptions(?, ?)}";
            try (CallableStatement statement = connection.prepareCall(sql)) {
                statement.setString(1, patientId);
                statement.registerOutParameter(2, OracleTypes.CURSOR); // Assuming you are using Oracle JDBC driver
                statement.execute();
                try (ResultSet resultSet = (ResultSet) statement.getObject(2)) {
                    if (!resultSet.isBeforeFirst()) {
                        System.out.println("You don't have any prescriptions.");
                    } else {
                        System.out.println("Your Prescriptions:");
                        System.out.printf("%-20s %-10s %-15s %-20s %-20s %-20s\n", "Patient Name", "Age", "Blood Group", "Doctor", "Symptoms", "Medicine");
                        System.out.println("-----------------------------------------------------------------------------");
                        while (resultSet.next()) {
                            String patientRealName = resultSet.getString("patient_realname");
                            int age = resultSet.getInt("age");
                            String bloodGroup = resultSet.getString("bloodgroup");
                            String doctorRealName = resultSet.getString("doctor_realname");
                            String symptoms = resultSet.getString("symptoms");
                            String medicine = resultSet.getString("medicine");
                            System.out.printf("%-20s %-10s %-15s %-20s %-20s %-20s\n", patientRealName, age, bloodGroup, doctorRealName, symptoms, medicine);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static void uploadPrescriptions(Scanner scanner, String doctorUsername) {
        try (Connection connection = DBConnection.getConnection()) {
            // Query to fetch doctor's real name
            String doctorInfoSql = "SELECT id,realname FROM doctors WHERE username = ?";
            try (PreparedStatement doctorInfoStatement = connection.prepareStatement(doctorInfoSql)) {
                doctorInfoStatement.setString(1, doctorUsername);
                try (ResultSet doctorInfoResultSet = doctorInfoStatement.executeQuery()) {
                    String doctorRealName = null;
                    String doctorId = null;
                    if (doctorInfoResultSet.next()) {
                        doctorId = doctorInfoResultSet.getString("id");
                        doctorRealName = doctorInfoResultSet.getString("realname");
                    }


                    if (doctorRealName != null) {

                        String sql = "SELECT a.id AS appointment_id, p.id AS patient_id, p.realname AS patient_realname, p.age, p.bloodgroup, a.appointment_date " +
                                "FROM appointments a " +
                                "JOIN patients p ON a.patient_id = p.id " +
                                "WHERE a.doctor_name = ?";
                        try (PreparedStatement statement = connection.prepareStatement(sql)) {
                            statement.setString(1, doctorUsername);
                            try (ResultSet resultSet = statement.executeQuery()) {
                                if (!resultSet.isBeforeFirst()) {
                                    System.out.println("You don't have any appointments to upload prescriptions.");
                                    return;
                                }

                                System.out.println("Your appointments:");
                                System.out.printf("%-10s %-15s %-10s %-15s %-15s %-15s\n", "Appt ID", "Patient ID", "Patient Name", "Age", "Blood Group", "Appointment Date");
                                while (resultSet.next()) {
                                    String appointmentId = resultSet.getString("appointment_id");
                                    String patientId = resultSet.getString("patient_id");
                                    String patientRealName = resultSet.getString("patient_realname");
                                    int age = resultSet.getInt("age");
                                    String bloodGroup = resultSet.getString("bloodgroup");
                                    Date appointmentDate = resultSet.getDate("appointment_date");

                                    System.out.printf("%-10s %-15s %-10s %-15s %-15s %-15s\n", appointmentId, patientId, patientRealName, age, bloodGroup, appointmentDate);
                                }

                                String appointmentIdToUpload;
                                boolean validAppointmentId = false;
                                do {
                                    System.out.println("Enter the appointment ID to upload prescription:");
                                    appointmentIdToUpload = scanner.nextLine();
                                    if (!isPrescriptionAlreadyUploaded(connection, appointmentIdToUpload)) {
                                        validAppointmentId = true;
                                    } else {
                                        System.out.println("Prescription for this appointment ID already exists. Choose another appointment.");
                                    }
                                } while (!validAppointmentId);

                                System.out.println("Enter symptoms:");
                                String symptoms = scanner.nextLine();

                                System.out.println("Enter medicine:");
                                String medicine = scanner.nextLine();

                                System.out.println("Enter the amount for the bill:");
                                double amount = scanner.nextDouble();
                                scanner.nextLine(); // Consume newline character

                                // Insert prescription into database
                                String insertSql = "INSERT INTO prescriptions (app_id, patient_id, patient_realname, age, blood_group,doctor_id, doctor_realname, symptoms, medicine) " +
                                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                                try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
                                    insertStatement.setString(1, appointmentIdToUpload);
                                    insertStatement.setString(2, getPatientIdByAppointmentId(appointmentIdToUpload));
                                    insertStatement.setString(3, getPatientRealNameByAppointmentId(appointmentIdToUpload));
                                    insertStatement.setInt(4, getPatientAgeByAppointmentId(appointmentIdToUpload));
                                    insertStatement.setString(5, getPatientBloodGroupByAppointmentId(appointmentIdToUpload));
                                    insertStatement.setString(6, doctorId);
                                    insertStatement.setString(7, doctorRealName); // Using doctor's real name
                                    insertStatement.setString(8, symptoms);
                                    insertStatement.setString(9, medicine);
                                    int rowsInserted = insertStatement.executeUpdate();
                                    if (rowsInserted > 0) {
                                        System.out.println("Prescription uploaded successfully.");
                                    } else {
                                        System.out.println("Failed to upload prescription. Please try again.");
                                        return;
                                    }
                                }

                                String insertBillSql = "INSERT INTO bills (app_id, patient_id, doctor_id, patient_username, doctor_name, doctor_specialty, appointment_day, appointment_date, amount) " +
                                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                                try (PreparedStatement insertBillStatement = connection.prepareStatement(insertBillSql)) {
                                    insertBillStatement.setString(1, appointmentIdToUpload);
                                    insertBillStatement.setString(2, getPatientIdByAppointmentId(appointmentIdToUpload));
                                    insertBillStatement.setString(3, getDoctorIdByDoctorName(doctorUsername));
                                    insertBillStatement.setString(4, getPatientRealNameByAppointmentId(appointmentIdToUpload));
                                    insertBillStatement.setString(5, doctorRealName);
                                    insertBillStatement.setString(6, getDoctorSpecialtyByDoctorName(doctorUsername));
                                    insertBillStatement.setString(7, getAppointmentDayByAppointmentId(appointmentIdToUpload));
                                    insertBillStatement.setDate(8, getAppointmentDateByAppointmentId(appointmentIdToUpload));
                                    insertBillStatement.setDouble(9, amount);
                                    int rowsInserted = insertBillStatement.executeUpdate();
                                    if (rowsInserted > 0) {
                                        System.out.println("Bill inserted successfully.");
                                    } else {
                                        System.out.println("Failed to insert bill. Please try again.");

                                    }
                                }
                            }
                        }
                    } else {
                        System.out.println("Failed to fetch doctor's real name.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static boolean isPrescriptionAlreadyUploaded(Connection connection, String appointmentId) throws SQLException {
        String checkSql = "SELECT COUNT(*) AS count FROM prescriptions WHERE app_id = ?";
        try (PreparedStatement checkStatement = connection.prepareStatement(checkSql)) {
            checkStatement.setString(1, appointmentId);
            try (ResultSet resultSet = checkStatement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt("count");
                    return count > 0;
                }
            }
        }
        return false;
    }
    private static String getDoctorIdByDoctorName(String doctorUsername) {
        String doctorId = null;
        try (Connection connection = DBConnection.getConnection()) {
            String sql = "SELECT id FROM doctors WHERE username = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, doctorUsername);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        doctorId = resultSet.getString("id");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return doctorId;
    }
    private static String getDoctorSpecialtyByDoctorName(String doctorUsername) {
        String doctorSpecialty = null;
        try (Connection connection = DBConnection.getConnection()) {
            String sql = "SELECT specialty FROM doctors WHERE username = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, doctorUsername);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        doctorSpecialty = resultSet.getString("specialty");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return doctorSpecialty;
    }
    private static String getAppointmentDayByAppointmentId(String appointmentId) {
        String appointmentDay = null;
        try (Connection connection = DBConnection.getConnection()) {
            String sql = "SELECT appointment_day FROM appointments WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, appointmentId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        appointmentDay = resultSet.getString("appointment_day");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointmentDay;
    }
    private static Date getAppointmentDateByAppointmentId(String appointmentId) {
        Date appointmentDate = null;
        try (Connection connection = DBConnection.getConnection()) {
            String sql = "SELECT appointment_date FROM appointments WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, appointmentId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        appointmentDate = resultSet.getDate("appointment_date");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointmentDate;
    }
    private static String getPatientIdByAppointmentId(String appointmentId) {
        String patientId = "";
        String sql = "SELECT patient_id FROM appointments WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, appointmentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    patientId = resultSet.getString("patient_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patientId;
    }
    private static String getPatientRealNameByAppointmentId(String appointmentId) {
        String realName = "";
        String sql = "SELECT p.realname FROM appointments a JOIN patients p ON a.patient_id = p.id WHERE a.id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, appointmentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    realName = resultSet.getString("realname");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return realName;
    }
    private static int getPatientAgeByAppointmentId(String appointmentId) {
        int age = 0;
        String sql = "SELECT p.age FROM appointments a JOIN patients p ON a.patient_id = p.id WHERE a.id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, appointmentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    age = resultSet.getInt("age");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return age;
    }
    private static String getPatientBloodGroupByAppointmentId(String appointmentId) {
        String bloodGroup = "";
        String sql = "SELECT p.bloodgroup FROM appointments a JOIN patients p ON a.patient_id = p.id WHERE a.id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, appointmentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    bloodGroup = resultSet.getString("bloodgroup");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bloodGroup;
    }
    private static void deleteAccount(Scanner scanner, String username, String role) {
        System.out.println("Are you sure you want to delete your account? This action cannot be undone. (yes/no)");
        String confirmation = scanner.nextLine().toLowerCase();

        if (!confirmation.equals("yes")) {
            System.out.println("Account deletion canceled.");
            return;
        }

        String tableName = (role.equals("patient")) ? "patients" : "doctors";
        String realNameColumn = "realname";  // Assuming 'realname' is the column name in both tables

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);  // Start transaction

            try {
                // Fetch the real name of the user
                String realName = null;
                String fetchRealNameSql = "SELECT " + realNameColumn + " FROM " + tableName + " WHERE username = ?";
                try (PreparedStatement fetchRealNameStmt = connection.prepareStatement(fetchRealNameSql)) {
                    fetchRealNameStmt.setString(1, username);
                    try (ResultSet resultSet = fetchRealNameStmt.executeQuery()) {
                        if (resultSet.next()) {
                            realName = resultSet.getString(realNameColumn);
                        } else {
                            System.out.println("Failed to fetch real name. Please try again.");
                            connection.rollback();
                            return;
                        }
                    }
                }

                // Anonymize user in appointments
                String anonymizeSql;
                if (role.equals("patient")) {
                    anonymizeSql = "UPDATE appointments SET patient_username = NULL, patient_id = NULL WHERE patient_username = ?";
                } else {
                    anonymizeSql = "UPDATE appointments SET doctor_name = NULL, doctor_id = NULL WHERE doctor_name = ?";
                }

                try (PreparedStatement anonymizeStmt = connection.prepareStatement(anonymizeSql)) {
                    anonymizeStmt.setString(1, realName);
                    anonymizeStmt.executeUpdate();
                }

                // Delete user account
                String deleteAccountSql = "DELETE FROM " + tableName + " WHERE username = ?";
                try (PreparedStatement deleteAccountStmt = connection.prepareStatement(deleteAccountSql)) {
                    deleteAccountStmt.setString(1, username);
                    int rowsDeleted = deleteAccountStmt.executeUpdate();
                    if (rowsDeleted > 0) {
                        System.out.println("Account deleted successfully.");
                    } else {
                        System.out.println("Failed to delete account. Please try again.");
                    }
                }

                connection.commit();  // Commit transaction
            } catch (SQLException e) {
                connection.rollback();  // Rollback transaction on error
                e.printStackTrace();
            } finally {
                connection.setAutoCommit(true);  // Reset autocommit
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void showDoctorsList() {
        try (Connection connection = DBConnection.getConnection()) {
            String sql = "{call show_doctors_list(?)}";
            try (CallableStatement statement = connection.prepareCall(sql)) {
                statement.registerOutParameter(1, OracleTypes.CURSOR); // Assuming you are using Oracle JDBC driver
                statement.execute();
                try (ResultSet resultSet = (ResultSet) statement.getObject(1)) {
                    System.out.println("List of Doctors:");
                    System.out.printf("%-5s %-20s %-20s %-15s%n", "ID","Name", "Specialty", "Phone Number");
                    System.out.println("-------------------------------------------------------------------");
                    while (resultSet.next()) {
                        String id = resultSet.getString("id");
                        String username = resultSet.getString("realname");
                        String specialty = resultSet.getString("specialty");
                        String phoneNumber = resultSet.getString("phone_number");
                        System.out.printf("%-5s %-20s %-20s %-15s%n", id, username, specialty, phoneNumber);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static void bookAppointment(Scanner scanner, String patientUsername, String patientId, String role) {
        System.out.println("The appointment booking is for the current week.");
        System.out.println("--------------------------------------------------");
        System.out.println("Select a specialty:");

        try (Connection connection = DBConnection.getConnection()) {
            // Display distinct specialties
            String sqlSpecialties = "SELECT DISTINCT specialty FROM doctors";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sqlSpecialties)) {
                int count = 1;
                while (resultSet.next()) {
                    String specialty = resultSet.getString("specialty");
                    System.out.println(count + ". " + specialty);
                    count++;
                }
            }

            System.out.print("Choose a specialty: ");
            int selectedSpecialtyIndex = scanner.nextInt();
            scanner.nextLine(); // Consume newline character

            // Get the selected specialty
            String selectedSpecialty = "";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sqlSpecialties)) {
                int index = 1;
                while (resultSet.next()) {
                    if (index == selectedSpecialtyIndex) {
                        selectedSpecialty = resultSet.getString("specialty");
                        break;
                    }
                    index++;
                }
            }

            // Retrieve doctors available for the selected specialty
            String sqlDoctors = "SELECT username, id, realname, day, max_patients,start_time, end_time FROM doctors WHERE specialty = ?";
            try (PreparedStatement statement = connection.prepareStatement(sqlDoctors)) {
                statement.setString(1, selectedSpecialty);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (!resultSet.isBeforeFirst()) {
                        // No doctors available for the selected specialty
                        System.out.println("No doctor available with specialty " + selectedSpecialty);
                        return;
                    }
                    int count = 1;
                    System.out.println("Doctors with specialty " + selectedSpecialty + ":");
                    System.out.printf("%-5s %-20s %-15s %-10s %-10s%n", "No.", "Name", "Day", "Start Time", "End Time");
                    System.out.println("---------------------------------------------------------------");
                    while (resultSet.next()) {
                        String doctorusername = resultSet.getString("username");
                        String doctorId = resultSet.getString("id");
                        String doctorName = resultSet.getString("realname");
                        String day = resultSet.getString("day");
                        String startTime = resultSet.getString("start_time");
                        String endTime = resultSet.getString("end_time");
                        int maxCount = resultSet.getInt("max_patients");
                        System.out.printf("%-5d %-20s %-15s %-10s %-10s%n", count, doctorName, day, startTime, endTime);
                        count++;
                    }
                }
            }

            // Prompt the user to choose a doctor by number
            System.out.print("Choose a doctor by number: ");
            int selectedDoctorIndex = scanner.nextInt();
            scanner.nextLine(); // Consume newline character

            // Get the selected doctor's information
            String selectedDoctorUsername = "";
            String selectedDoctorId = "";
            String selectedDay = "";
            String selectedStartTime = "";
            String selectedEndTime = "";
            int selectedDoctorMaxCount = 0;

            try (PreparedStatement statement = connection.prepareStatement(sqlDoctors)) {
                statement.setString(1, selectedSpecialty);
                try (ResultSet rs = statement.executeQuery()) {
                    int index = 1;
                    while (rs.next()) {
                        if (index == selectedDoctorIndex) {
                            selectedDoctorUsername = rs.getString("username");
                            selectedDoctorId = rs.getString("id");
                            selectedDay = rs.getString("day");
                            selectedStartTime = rs.getString("start_time");
                            selectedEndTime = rs.getString("end_time");
                            selectedDoctorMaxCount = rs.getInt("max_patients");
                            break;
                        }
                        index++;
                    }
                }
            }

            LocalDate appointmentDate = getNextAppointmentDate(selectedDay);


            // Check patient count
            String checkPatientCountSql = "SELECT COUNT(*) AS patient_count FROM appointments WHERE doctor_id = ? AND appointment_date = ?";
            int currentPatientCount = 0;

            try (PreparedStatement checkStatement = connection.prepareStatement(checkPatientCountSql)) {
                checkStatement.setString(1, selectedDoctorId);
                checkStatement.setDate(2, java.sql.Date.valueOf(appointmentDate));
                try (ResultSet rs = checkStatement.executeQuery()) {
                    if (rs.next()) {
                        currentPatientCount = rs.getInt("patient_count");
                    }
                }
            }

            if (currentPatientCount >= selectedDoctorMaxCount) {
                System.out.println("The selected doctor has reached the maximum patient count for " + selectedDay + ".");
                return;
            }

            // Confirm appointment booking
            System.out.println("Confirm booking appointment with " + selectedDoctorUsername + " on " + selectedDay + " (yes/no):");
            String confirmation = scanner.nextLine().toLowerCase();

            if (confirmation.equals("yes")) {
                // Insert appointment into database and get generated ID
                String insertSql = "INSERT INTO appointments (patient_id, doctor_id, patient_username, doctor_username, doctor_specialty, appointment_day, appointment_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
                String returningSql = "{CALL insert_appointment(?, ?, ?, ?, ?, ?, ?, ?)}";

                try (CallableStatement callableStatement = connection.prepareCall(returningSql)) {
                    callableStatement.setString(1, patientId);
                    callableStatement.setString(2, selectedDoctorId);
                    callableStatement.setString(3, patientUsername);
                    callableStatement.setString(4, selectedDoctorUsername);
                    callableStatement.setString(5, selectedSpecialty);
                    callableStatement.setString(6, selectedDay);
                    callableStatement.setDate(7, java.sql.Date.valueOf(appointmentDate));

                    // Register the output parameter
                    callableStatement.registerOutParameter(8, java.sql.Types.VARCHAR);

                    callableStatement.executeUpdate();
                    String appointmentId = callableStatement.getString(8);

                    System.out.println("Appointment for " + patientUsername + " booked successfully with " + selectedDoctorUsername + " on " + selectedDay + " (" + appointmentDate + ")");
                    System.out.println("Your appointment ID is: " + appointmentId);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Appointment booking canceled.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static LocalDate getNextAppointmentDate(String dayOfWeek) {
        DayOfWeek day = DayOfWeek.valueOf(dayOfWeek.toUpperCase());
        LocalDate now = LocalDate.now();
        int daysUntilNextAppointment = day.getValue() - now.getDayOfWeek().getValue();
        if (daysUntilNextAppointment < 0) {
            daysUntilNextAppointment += 7;
        }
        return now.plusDays(daysUntilNextAppointment);
    }
    public static void viewDoctorAppointments(String doctorUsername) {
        try (Connection connection = DBConnection.getConnection()) {
            String sql = "{call view_doctor_appointments(?, ?)}";
            try (CallableStatement statement = connection.prepareCall(sql)) {
                statement.setString(1, doctorUsername);
                statement.registerOutParameter(2, OracleTypes.CURSOR); // Assuming you are using Oracle JDBC driver
                statement.execute();
                try (ResultSet resultSet = (ResultSet) statement.getObject(2)) {
                    if (!resultSet.isBeforeFirst()) {
                        System.out.println("You don't have any appointments.");
                    } else {
                        System.out.println("Your appointments:");
                        System.out.printf("%-10s %-10s %-15s %-15s %-15s%n", "Appt ID", "Patient ID", "Patient Name", "Day", "Date");
                        System.out.println("------------------------------------------------------------");
                        while (resultSet.next()) {
                            String appointmentId = resultSet.getString("appointment_id");
                            String patientId = resultSet.getString("patient_id");
                            String patientName = resultSet.getString("patient_name");
                            String appointmentDay = resultSet.getString("appointment_day");
                            Date appointmentDate = resultSet.getDate("appointment_date");

                            System.out.printf("%-10s %-10s %-15s %-15s %-15s%n", appointmentId, patientId, patientName, appointmentDay, appointmentDate);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static void viewPatientAppointments(String username) {
        try (Connection connection = DBConnection.getConnection()) {

            String fetchAppointmentsSql = "SELECT id AS appointment_id, appointment_date, doctor_id, doctor_name " +
                    "FROM appointments WHERE patient_username = ?";
            try (PreparedStatement fetchAppointmentsStmt = connection.prepareStatement(fetchAppointmentsSql)) {
                fetchAppointmentsStmt.setString(1,username);
                try (ResultSet resultSet = fetchAppointmentsStmt.executeQuery()) {
                    List<String> appointmentIds = new ArrayList<>();
                    while (resultSet.next()) {
                        String appointmentId = resultSet.getString("appointment_id");
                        String appointmentDate = resultSet.getString("appointment_date");
                        String doctorId = resultSet.getString("doctor_id");
                        String doctorRealName = resultSet.getString("doctor_name");
                        System.out.printf("Appointment ID: %s, Date: %s, Doctor ID: %s, Doctor Name: %s\n",
                                appointmentId, appointmentDate, doctorId, doctorRealName);
                        appointmentIds.add(appointmentId);
                    }

                    if (appointmentIds.isEmpty()) {
                        System.out.println("You have no appointments to cancel.");

                    }

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




}
