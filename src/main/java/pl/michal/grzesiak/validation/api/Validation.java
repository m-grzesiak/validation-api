package pl.michal.grzesiak.validation.api;

import java.util.function.Predicate;

import static pl.michal.grzesiak.validation.api.ValidationResult.invalid;
import static pl.michal.grzesiak.validation.api.ValidationResult.valid;


public interface Validation<T> {

    static <S> Validation<S> rule(Predicate<S> predicate, String errorMessage) {
        return objectToValidate -> predicate.test(objectToValidate) ? valid() : invalid(new ValidationError(errorMessage));
    }

    ValidationResult validate(T objectToValidate);

    default Validation<T> and(Validation<T> otherValidation) {
        return objectToValidate -> {
            ValidationResult validationResult = this.validate(objectToValidate);
            if (objectToValidate != null) {
                ValidationResult otherValidationResult = otherValidation.validate(objectToValidate);
                return validationResult.concat(otherValidationResult);
            } else {
                return validationResult;
            }
        };
    }
}