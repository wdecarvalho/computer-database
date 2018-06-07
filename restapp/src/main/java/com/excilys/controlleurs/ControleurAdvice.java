package com.excilys.controlleurs;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.excilys.exception.computer.ComputerNotFoundException;
import com.excilys.exceptions.NoContentFoundException;
import com.excilys.exceptions.api.ApiError;

@RestControllerAdvice
public class ControleurAdvice {

    private static final String DEFAULT_CAUSE = "No cause";

    /**
     * Retourne une BadRequest 400 car la requete n'est pas explicite.
     * @param ex
     *            UnsatisfiedServletRequestParameterException
     * @return ResponseEntity<ApiError> 400
     */
    @ExceptionHandler(UnsatisfiedServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleBadRequestMultipleParamPossible(
            UnsatisfiedServletRequestParameterException ex) {
        final ApiError apiError = new ApiError(ex.getClass().toString(), ex.getMessage(),
                ex.getCause() == null ? DEFAULT_CAUSE : ex.getCause().toString());
        return ResponseEntity.badRequest().body(apiError);
    }

    /**
     * Retourne une BadRequest 400 car un parametre n'a pas le type ou le format
     * attendu.
     * @param ex
     *            MethodArgumentTypeMismatchException
     * @return ResponseEntity<ApiError> 400
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleBadRequestTypeMismatch(MethodArgumentTypeMismatchException ex) {
        final String cause = getTheCauseFromException(ex);
        final ApiError apiError = new ApiError(ex.getClass().toString(), ex.getMessage(), cause);
        return ResponseEntity.badRequest().body(apiError);
    }
    
    @ExceptionHandler
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(MethodArgumentNotValidException ex){
        final ApiError apiError = new ApiError(ex.getClass().toString(), ex.getMessage(), ex.getCause() == null ? DEFAULT_CAUSE : ex.getCause().toString());
        return ResponseEntity.badRequest().body(apiError);
    }

    /**
     * Recupere la cause la plus adapté provenant d'une Exception
     * @param ex
     *            Exception
     * @param cause
     *            Throwable
     * @return String cause
     */
    private String getTheCauseFromException(MethodArgumentTypeMismatchException ex) {
        String cause = DEFAULT_CAUSE;
        if (ex.getMostSpecificCause() == null) {
            if (ex.getCause() != null) {
                cause = ex.getCause().toString();
            }
        } else {
            cause = ex.getMostSpecificCause().toString();
        }
        return cause;
    }

    /**
     * Handle and send error.
     * @param ex
     *            NoContentFoundException
     * @return ResponseEntity<ApiError> (NO_CONTENT 204)
     */
    @ExceptionHandler(NoContentFoundException.class)
    public ResponseEntity<ApiError> handleNoContentFoundException(NoContentFoundException ex) {
        final ApiError apiError = new ApiError(ex.toString(), ex.getMessage(),
                ex.getCause() == null ? DEFAULT_CAUSE : ex.getCause().toString());
        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.NO_CONTENT);
    }
    
    @ExceptionHandler(ComputerNotFoundException.class)
    public ResponseEntity<ApiError> handleComputerNotFoundException(ComputerNotFoundException ex){
        final ApiError apiError = new ApiError(ex.toString(), ex.getMessage(),
                ex.getCause() == null ? DEFAULT_CAUSE : ex.getCause().toString());
        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }
}
