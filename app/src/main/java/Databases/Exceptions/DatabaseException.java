package Databases.Exceptions;

public class DatabaseException extends Exception {
    public enum Type{ServerUnreachable,LoginDetails,TimeOut,Other}
    private String details;
    private Type type;

    public DatabaseException(String details, Type type){
        this.details = details;
        this.type = type;
    }

    @Override
    public String getMessage() {
        return details;
    }

    public Type getType(){
        return type;
    }
}
