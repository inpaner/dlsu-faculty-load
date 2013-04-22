package model;

@SuppressWarnings("serial")
public class FLException extends Exception {
    protected FLException() { 
        super(); 
    }
    
    protected FLException(String message) { 
        super(message); 
    }
}
