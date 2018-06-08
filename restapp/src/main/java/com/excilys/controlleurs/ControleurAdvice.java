package com.excilys.controlleurs;

import javax.persistence.EntityNotFoundException;

import org.apiguardian.api.API;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.excilys.exception.CompanyException;
import com.excilys.exception.company.CompanyNotFoundException;
import com.excilys.exception.computer.ComputerNotDeletedException;
import com.excilys.exception.computer.ComputerNotFoundException;
import com.excilys.exception.computer.DateIntroShouldBeMinorthanDisconException;
import com.excilys.exception.date.DateTruncationException;
import com.excilys.exceptions.ConflictException;
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        final ApiError apiError = new ApiError(ex.getClass().toString(), ex.getMessage(),
                ex.getCause() == null ? DEFAULT_CAUSE : ex.getCause().toString());
        return ResponseEntity.badRequest().body(apiError);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        final ApiError apiError = new ApiError(ex.getClass().toString(), ex.getMessage(),
                ex.getCause() == null ? DEFAULT_CAUSE : ex.getCause().toString());
        return ResponseEntity.badRequest().body(apiError);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleEntityNotFoundException(EntityNotFoundException ex) {
        final ApiError apiError = new ApiError(ex.getClass().toString(), ex.getMessage(),
                ex.getCause() == null ? DEFAULT_CAUSE : ex.getCause().toString());
        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.PRECONDITION_FAILED);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException ex) {
        final ApiError apiError = new ApiError(ex.getClass().toString(), ex.getMessage(),
                ex.getCause() == null ? DEFAULT_CAUSE : ex.getCause().toString());
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
        final ApiError apiError = new ApiError(ex.getClass().toString(), ex.getMessage(),
                ex.getCause() == null ? DEFAULT_CAUSE : ex.getCause().toString());
        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(ComputerNotFoundException.class)
    public ResponseEntity<ApiError> handleComputerNotFoundException(ComputerNotFoundException ex) {
        final ApiError apiError = new ApiError(ex.getClass().toString(), ex.getMessage(),
                ex.getCause() == null ? DEFAULT_CAUSE : ex.getCause().toString());
        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DateIntroShouldBeMinorthanDisconException.class)
    public ResponseEntity<ApiError> handleDateIntroMinorThanDateDisconException(
            DateIntroShouldBeMinorthanDisconException ex) {
        final ApiError apiError = new ApiError(ex.getClass().toString(), ex.getMessage(),
                ex.getCause() == null ? DEFAULT_CAUSE : ex.getCause().toString());
        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.PRECONDITION_FAILED);
    }

    @ExceptionHandler(DateTruncationException.class)
    public ResponseEntity<ApiError> handleDateTruncationException(DateTruncationException ex) {
        final ApiError apiError = new ApiError(ex.getClass().toString(), ex.getMessage(),
                ex.getCause() == null ? DEFAULT_CAUSE : ex.getCause().toString());
        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.PRECONDITION_FAILED);
    }

    @ExceptionHandler(CompanyNotFoundException.class)
    public ResponseEntity<ApiError> handleCompanyNotFoundException(CompanyNotFoundException ex) {
        final ApiError apiError = new ApiError(ex.getClass().toString(), ex.getMessage(),
                ex.getCause() == null ? DEFAULT_CAUSE : ex.getCause().toString());
        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> handleConflitException(ConflictException ex) {
        final ApiError apiError = new ApiError(ex.getClass().toString(), ex.getMessage(),
                ex.getCause() == null ? DEFAULT_CAUSE : ex.getCause().toString());
        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.CONFLICT);
    }
    
    @ExceptionHandler(ComputerNotDeletedException.class)
    public ResponseEntity<ApiError> handleComputerNotDeletedException(ComputerNotDeletedException ex){
        final ApiError apiError = new ApiError(ex.getClass().toString(), ex.getMessage(),
                ex.getCause() == null ? DEFAULT_CAUSE : ex.getCause().toString());
        return ResponseEntity.accepted().body(apiError);
    }
}
