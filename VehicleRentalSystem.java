import java.util.*;

abstract class User {
    String id, name, email, password, role;
    double securityDeposit;

    User(String id, String name, String email, String password, String role, double securityDeposit) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.securityDeposit = securityDeposit;
    }
}

class Admin extends User {
    Admin(String id, String name, String email, String password) {
        super(id, name, email, password, "Admin", 0);
    }
}

class Borrower extends User {
    String rentedCarId = null;
    String rentedBikeId = null;
    List<String> rentalHistory = new ArrayList<>();

    Borrower(String id, String name, String email, String password) {
        super(id, name, email, password, "Borrower", 30000); 
    }
}

class Vehicle {
    String id, name, numberPlate, type;
    double rentalPrice;
    int kmsRun, availableCount;
    boolean needsService = false;

    Vehicle(String id, String name, String numberPlate, String type, double rentalPrice, int kmsRun, int availableCount) {
        this.id = id;
        this.name = name;
        this.numberPlate = numberPlate;
        this.type = type;
        this.rentalPrice = rentalPrice;
        this.kmsRun = kmsRun;
        this.availableCount = availableCount;
    }
}

class Rental {
    String rentalId, borrowerId, vehicleId, date;
    String damageLabel = "NONE";
    boolean returned = false;
    double totalCharges = 0.0;

    Rental(String rentalId, String borrowerId, String vehicleId, String date) {
        this.rentalId = rentalId;
        this.borrowerId = borrowerId;
        this.vehicleId = vehicleId;
        this.date = date;
    }
}

public class VehicleRentalSystem {
    static Scanner sc = new Scanner(System.in);

    static Map<String, User> users = new HashMap<>();
    static Map<String, Vehicle> vehicles = new HashMap<>();
    static Map<String, Rental> rentals = new HashMap<>();

    static double minDepositCar = 10000;
    static double minDepositBike = 3000;

    public static void main(String[] args) {
        seedData();
        while (true) {
            System.out.println("\n--- Vehicle Rental System ---");
            System.out.println("1. Sign In");
            System.out.println("2. Sign Up (Borrower)");
            System.out.println("3. Exit");
            int ch = sc.nextInt();
            sc.nextLine();
            switch (ch) {
                case 1 :
                    login();
                    break;
                case 2 :
                    signUp();
                    break;
                case 3 :
                    System.exit(0);
                    break;
                default :
                System.out.println("Invalid!");
                break;
            }
        }
    }
    // initial data
    static void seedData() {
        Admin admin = new Admin("A1", "Admin", "admin@mail.com", "admin");
        users.put(admin.email, admin);

        vehicles.put("V1", new Vehicle("V1", "Swift", "TN01AB1234", "Car", 1500, 1000, 2));
        vehicles.put("V2", new Vehicle("V2", "Activa", "TN01XY9876", "Bike", 500, 500, 5));
    }

    static void login() {
        while(true){
            System.out.print("Email: ");
            String email = sc.nextLine();
            System.out.print("Password: ");
            String pass = sc.nextLine();

            User user = users.get(email);
            if (user != null && user.password.equals(pass)) {
                if (user.role.equals("Admin")) adminMenu((Admin) user);
                else if(user.role.equals("Borrower")) borrowerMenu((Borrower)user);
                break;
            } else {
                System.out.println("Invalid credentials!");
            }
        }
    }

    static void signUp() {
        System.out.print("Name: ");
        String name = sc.nextLine();
        System.out.print("Email: ");
        String email = sc.nextLine();
        System.out.print("Password: ");
        String pass = sc.nextLine();

        if (users.containsKey(email)) {
            System.out.println("Email already exists!");
            login();
        }
        Borrower b = new Borrower(UUID.randomUUID().toString(), name, email, pass);
        users.put(email, b);
        System.out.println("SignUp Successful!");
    }

    static void adminMenu(Admin admin) {
        while (true) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. Add Vehicle");
            System.out.println("2. Modify Vehicle");
            System.out.println("3. Delete Vehicle");
            System.out.println("4. View All Vehicles");
            System.out.println("5. Change Security Deposit");
            System.out.println("6. Reports");
            System.out.println("7. Logout");
            int ch = sc.nextInt();
            sc.nextLine();
            switch (ch) {
                case 1 -> addVehicle();
                case 2 -> modifyVehicle();
                case 3 -> deleteVehicle();
                case 4 -> listVehicles();
                case 5 -> changeSecurityDeposit();
                case 6 -> reports();
                case 7 -> { return; }
            }
        }
    }

    static void addVehicle() {
        System.out.print("Name: ");
        String name = sc.nextLine();
        System.out.print("Number Plate: ");
        String numberPlate = sc.nextLine();
        System.out.print("Type (Car/Bike): ");
        String type = sc.nextLine();
        System.out.print("Rental Price: ");
        double price = sc.nextDouble();
        System.out.print("KMs run: ");
        int kms = sc.nextInt();
        System.out.print("Available Count: ");
        int count = sc.nextInt();
        sc.nextLine();
        Vehicle v = new Vehicle(UUID.randomUUID().toString(), name, numberPlate, type, price, kms, count);
        vehicles.put(v.id, v);
        System.out.println("Vehicle Added!");
    }

    static void modifyVehicle() {
        System.out.print("Enter Vehicle ID: ");
        String id = sc.nextLine();
        if (!vehicles.containsKey(id)) {
            System.out.println("Not found!");
            return;
        }
        Vehicle v = vehicles.get(id);
        System.out.print("New Available Count: ");
        v.availableCount = sc.nextInt();
        sc.nextLine();
        System.out.println("Updated!");
    }

    static void deleteVehicle() {
        System.out.print("Enter Vehicle ID: ");
        String id = sc.nextLine();
        if (vehicles.remove(id) != null) System.out.println("Deleted!");
        else System.out.println("Not Found!");
    }

    static void listVehicles() {
        vehicles.values().stream()
                .sorted(Comparator.comparing(v -> v.name))
                .forEach(v -> System.out.println(v.id + " " + v.name + " " + v.type + " Avl: " + v.availableCount));
    }

    static void changeSecurityDeposit() {
        System.out.print("New Min Car Deposit: ");
        minDepositCar = sc.nextDouble();
        System.out.print("New Min Bike Deposit: ");
        minDepositBike = sc.nextDouble();
        sc.nextLine();
        System.out.println("Updated!");
    }

    static void reports() {
        System.out.println("1. Vehicles needing service");
        System.out.println("2. Vehicles sorted by rental price");
        System.out.println("3. Search Vehicle by name/type");
        int ch = sc.nextInt();
        sc.nextLine();
        switch (ch) {
            case 1 -> vehicles.values().stream().filter(v -> v.needsService)
                    .forEach(v -> System.out.println(v.name));
            case 2 -> vehicles.values().stream()
                    .sorted(Comparator.comparingDouble(v -> v.rentalPrice))
                    .forEach(v -> System.out.println(v.name + " " + v.rentalPrice));
            case 3 -> {
                System.out.print("Enter name: ");
                String name = sc.nextLine();
                vehicles.values().stream()
                        .filter(v -> v.name.toLowerCase().contains(name.toLowerCase()))
                        .forEach(v -> System.out.println(v.name + " " + v.type));
            }
        }
    }

    static void borrowerMenu(Borrower borrower) {
        while (true) {
            System.out.println("\n--- Borrower Menu ---");
            System.out.println("1. View Catalog");
            System.out.println("2. Rent Vehicle");
            System.out.println("3. View Rental History");
            System.out.println("4. Logout");
            int ch = sc.nextInt();
            sc.nextLine();
            switch (ch) {
                case 1 -> viewCatalog();
                case 2 -> rentVehicle(borrower);
                case 3 -> borrower.rentalHistory.forEach(System.out::println);
                case 4 -> { return; }
            }
        }
    }

    static void viewCatalog() {
        vehicles.values().stream()
                .filter(v -> !v.needsService)
                .forEach(v -> System.out.println(v.id + " " + v.name + " " + v.type + " Price: " + v.rentalPrice + " Avl: " + v.availableCount));
    }

    static void rentVehicle(Borrower borrower) {
        System.out.print("Enter Vehicle ID: ");
        String id = sc.nextLine();
        if (!vehicles.containsKey(id)) {
            System.out.println("Vehicle Not Found!");
            return;
        }
        Vehicle v = vehicles.get(id);
        if (v.availableCount <= 0) {
            System.out.println("Not available!");
            return;
        }
        if (v.type.equalsIgnoreCase("Car") && borrower.rentedCarId != null) {
            System.out.println("You already rented a Car!");
            return;
        }
        if (v.type.equalsIgnoreCase("Bike") && borrower.rentedBikeId != null) {
            System.out.println("You already rented a Bike!");
            return;
        }
        if ((v.type.equalsIgnoreCase("Car") && borrower.securityDeposit < minDepositCar) ||
            (v.type.equalsIgnoreCase("Bike") && borrower.securityDeposit < minDepositBike)) {
            System.out.println("Insufficient Security Deposit!");
            return;
        }
        v.availableCount--;
        if (v.type.equalsIgnoreCase("Car")) borrower.rentedCarId = v.id;
        else borrower.rentedBikeId = v.id;

        String rentalId = UUID.randomUUID().toString();
        Rental r = new Rental(rentalId, borrower.id, v.id, new Date().toString());
        rentals.put(rentalId, r);
        borrower.rentalHistory.add("Rented: " + v.name + " on " + r.date);
        System.out.println("Vehicle Rented Successfully!");
    }
}
