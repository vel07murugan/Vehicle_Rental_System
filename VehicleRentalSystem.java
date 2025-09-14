
import java.util.*;
abstract class Vehicle {
    private String vehicleId;
    private String brand;
    private String model;
    private double rentalPrice;
    private boolean isRented;

    public Vehicle(String vehicleId, String brand, String model, double rentalPrice) {
        this.vehicleId = vehicleId;
        this.brand = brand;
        this.model = model;
        this.rentalPrice = rentalPrice;
        this.isRented = false;
    }

    public boolean rent() {
        if (!isRented) {
            isRented = true;
            return true;
        }
        return false;
    }

    public boolean returnVehicle() {
        if (isRented) {
            isRented = false;
            return true;
        }
        return false;
    }

    public String getVehicleId() { return vehicleId; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public double getRentalPrice() { return rentalPrice; }
    public boolean isRented() { return isRented; }

    @Override
    public String toString() {
        String status = isRented ? "Rented" : "Available";
        return "[" + vehicleId + "] " + brand + " " + model +
               " - $" + rentalPrice + "/day (" + status + ")";
    }
}


class Car extends Vehicle {
    private int seats;

    public Car(String vehicleId, String brand, String model, double rentalPrice, int seats) {
        super(vehicleId, brand, model, rentalPrice);
        this.seats = seats;
    }

    @Override
    public String toString() {
        return super.toString() + " | Seats: " + seats;
    }
}


class Bike extends Vehicle {
    private String type;

    public Bike(String vehicleId, String brand, String model, double rentalPrice, String type) {
        super(vehicleId, brand, model, rentalPrice);
        this.type = type;
    }

    @Override
    public String toString() {
        return super.toString() + " | Type: " + type;
    }
}



class Customer {
    private String username;
    private String password;
    private List<Vehicle> rentedVehicles;

    public Customer(String username, String password) {
        this.username = username;
        this.password = password;
        this.rentedVehicles = new ArrayList<>();
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }

    public void rentVehicle(Vehicle vehicle, int days) {
        if (vehicle.rent()) {
            rentedVehicles.add(vehicle);
            double totalCost = vehicle.getRentalPrice() * days;
            System.out.println(username + " successfully rented " +
                    vehicle.getBrand() + " " + vehicle.getModel() +
                    " for " + days + " days. Total cost: $" + totalCost);
        } else {
            System.out.println("Sorry, " + vehicle.getBrand() + " " + vehicle.getModel() + " is not available.");
        }
    }

    public void returnVehicle(Vehicle vehicle) {
        if (rentedVehicles.contains(vehicle)) {
            if (vehicle.returnVehicle()) {
                rentedVehicles.remove(vehicle);
                System.out.println(username + " returned " + vehicle.getBrand() + " " + vehicle.getModel());
            }
        } else {
            System.out.println(username + " does not have this vehicle rented.");
        }
    }

    public void showMyRentals() {
        if (rentedVehicles.isEmpty()) {
            System.out.println(username + " has not rented any vehicles.");
        } else {
            System.out.println(username + " has rented:");
            for (Vehicle v : rentedVehicles) {
                System.out.println(" - " + v);
            }
        }
    }
}



class RentalSystem {
    private List<Vehicle> vehicles;
    private Map<String, Customer> customers; // username -> customer

    public RentalSystem() {
        vehicles = new ArrayList<>();
        customers = new HashMap<>();
    }

    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
    }

    public void registerCustomer(String username, String password) {
        if (customers.containsKey(username)) {
            System.out.println("Username already exists. Try another.");
        } else {
            customers.put(username, new Customer(username, password));
            System.out.println("Sign up successful! You can now sign in.");
        }
    }

    public Customer loginCustomer(String username, String password) {
        if (customers.containsKey(username) && customers.get(username).getPassword().equals(password)) {
            System.out.println("Login successful! Welcome, " + username);
            return customers.get(username);
        } else {
            System.out.println("Invalid username or password.");
            return null;
        }
    }

    public Vehicle findVehicleById(String vehicleId) {
        for (Vehicle v : vehicles) {
            if (v.getVehicleId().equalsIgnoreCase(vehicleId)) {
                return v;
            }
        }
        return null;
    }

    public void showAvailableVehicles() {
        System.out.println("Available Vehicles:");
        for (Vehicle v : vehicles) {
            if (!v.isRented()) {
                System.out.println(v);
            }
        }
    }
}


public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        RentalSystem system = new RentalSystem();

        // Add Vehicles
        system.addVehicle(new Car("C1", "Toyota", "Camry", 50, 5));
        system.addVehicle(new Car("C2", "Honda", "Civic", 45, 5));
        system.addVehicle(new Bike("B1", "Yamaha", "R15", 20, "Sport"));
        system.addVehicle(new Bike("B2", "Royal Enfield", "Classic 350", 25, "Cruiser"));

        boolean running = true;
        Customer loggedIn = null;

        while (running) {
            if (loggedIn == null) {
                System.out.println("\n=== Vehicle Rental System ===");
                System.out.println("1. Sign Up");
                System.out.println("2. Sign In");
                System.out.println("3. Exit");
                System.out.print("Enter choice: ");
                int choice = sc.nextInt();
                sc.nextLine(); // consume newline

                switch (choice) {
                    case 1:
                        System.out.print("Enter username: ");
                        String newUser = sc.nextLine();
                        System.out.print("Enter password: ");
                        String newPass = sc.nextLine();
                        system.registerCustomer(newUser, newPass);
                        break;
                    case 2:
                        System.out.print("Enter username: ");
                        String user = sc.nextLine();
                        System.out.print("Enter password: ");
                        String pass = sc.nextLine();
                        loggedIn = system.loginCustomer(user, pass);
                        break;
                    case 3:
                        running = false;
                        System.out.println("Exiting system. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
            } else {
                System.out.println("\n=== Welcome " + loggedIn.getUsername() + " ===");
                System.out.println("1. View Available Vehicles");
                System.out.println("2. Rent a Vehicle");
                System.out.println("3. Return a Vehicle");
                System.out.println("4. My Rentals");
                System.out.println("5. Logout");
                System.out.print("Enter choice: ");
                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1:
                        system.showAvailableVehicles();
                        break;
                    case 2:
                        system.showAvailableVehicles();
                        System.out.print("Enter Vehicle ID to rent: ");
                        String vehicleId = sc.nextLine();
                        Vehicle v = system.findVehicleById(vehicleId);
                        if (v != null && !v.isRented()) {
                            System.out.print("Enter number of days: ");
                            int days = sc.nextInt();
                            sc.nextLine();
                            loggedIn.rentVehicle(v, days);
                        } else {
                            System.out.println("Invalid or unavailable vehicle.");
                        }
                        break;
                    case 3:
                        loggedIn.showMyRentals();
                        System.out.print("Enter Vehicle ID to return: ");
                        String returnId = sc.nextLine();
                        Vehicle rv = system.findVehicleById(returnId);
                        if (rv != null) {
                            loggedIn.returnVehicle(rv);
                        } else {
                            System.out.println("Invalid vehicle ID.");
                        }
                        break;
                    case 4:
                        loggedIn.showMyRentals();
                        break;
                    case 5:
                        loggedIn = null;
                        System.out.println("Logged out successfully.");
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
            }
        }
        sc.close();
    }
}
