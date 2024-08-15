package exceptions;

public class CorruptedSaveFile extends CS3ServerException {
    public CorruptedSaveFile(String errorMessage) {
        super("Save File is corrupted:\n" + errorMessage);
    }
}
