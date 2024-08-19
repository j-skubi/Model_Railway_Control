package exceptions;

public abstract class ModelException extends CS3ServerException{
    public ModelException(String errorMessage) {
        super(errorMessage);
    }
}
