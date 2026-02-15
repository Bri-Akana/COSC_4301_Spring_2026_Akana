/**
 * The Main class creates a Monster object and prints its description.
 */
public class Main {

    /**
     * The main method to initialize a Monster object.
     *
     * @param args the command line arguments.
     */
    public static void main(String[] args) {

        // Create one monster object
        Monster monster = new Monster("Iris", "Void");

        // Prints description to terminal
        System.out.println("Your monster has been created.");
        System.out.println(monster.getDescription());
    }
}
