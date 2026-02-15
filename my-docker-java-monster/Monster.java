/**
 * The Monster class represents a monster with a name and type.
 * It provides a constructor to initialize new monsters with a description.
 */
public class Monster {
    String name;
    String type;

    /**
     * Constructor
     *
     * @param name
     * @param type
     */
    public Monster(String name, String type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Returns a description of the created monster.
     *
     * @return print description statement
     */
    public String getDescription() {
        return "Description: " + name + " is a " + type + " type monster from the Neon Ark training program.";
    }
}
