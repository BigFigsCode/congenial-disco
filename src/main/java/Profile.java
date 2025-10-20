

public class Profile {
    private int id;
    private String name;
    private String level;
    private String goal;
    private int daysPerWeek;

    // Constructors
    public Profile() {}

    public Profile(String name, String level, String goal, int daysPerWeek) {
        this.name = name;
        this.level = level;
        this.goal = goal;
        this.daysPerWeek = daysPerWeek;
    }

    public Profile(int id, String name, String level, String goal, int daysPerWeek) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.goal = goal;
        this.daysPerWeek = daysPerWeek;
    }

    // getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getLevel() { return level; }
    public String getGoal() { return goal; }
    public int getDaysPerWeek() { return daysPerWeek; }

    // setters (needed for updates / mapping)
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setLevel(String level) { this.level = level; }
    public void setGoal(String goal) { this.goal = goal; }
    public void setDaysPerWeek(int daysPerWeek) { this.daysPerWeek = daysPerWeek; }

    @Override
    public String toString() {
        return "Profile{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", level='" + level + '\'' +
                ", goal='" + goal + '\'' +
                ", daysPerWeek=" + daysPerWeek +
                '}';
    }
}

