package utils;

public class IDGenerator {
    private int ID;
    public IDGenerator(int ID) {
        this.ID = ID;
    }
    public int generateID() {
        return ID++;
    }
    public int save() { return this.ID;}
}
