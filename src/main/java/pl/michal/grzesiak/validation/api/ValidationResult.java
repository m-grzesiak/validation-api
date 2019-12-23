package pl.michal.grzesiak.validation.api;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

public class ValidationResult {

    private static final ValidationResult VALID = new ValidationResult(emptyList());

    private final List<ValidationError> validationErrors;

    private ValidationResult(List<ValidationError> validationErrors) {
        this.validationErrors = validationErrors;
    }

    static ValidationResult valid() {
        return VALID;
    }

    static ValidationResult invalid(ValidationError validationError) {
        return new ValidationResult(singletonList(validationError));
    }

    public ValidationResult concat(ValidationResult otherValidationResult) {
        if (isValid()) {
            return otherValidationResult;
        } else {
            List<ValidationError> mergedValidationErrors = Stream.concat(validationErrors.stream(), otherValidationResult.getValidationErrors().stream())
                    .collect(toList());
            return new ValidationResult(mergedValidationErrors);
        }
    }

    public void ifAnyErrorOccurred(Consumer<List<ValidationError>> consumer) {
        if (!isValid()) {
            consumer.accept(validationErrors);
        }
    }

    public <T extends Exception> void throwIfAnyErrorOccurred(Supplier<T> supplier) throws T {
        if (!isValid()) {
            throw supplier.get();
        }
    }

    public <T extends Exception> void throwIfAnyErrorOccurred(Function<List<ValidationError>, T> action) throws T {
        if (!isValid()) {
            throw action.apply(validationErrors);
        }
    }

    public void ifValidOrElse(Runnable action, Consumer<List<ValidationError>> alternativeAction) {
        if (isValid()) {
            action.run();
        } else {
            alternativeAction.accept(validationErrors);
        }
    }

    public <T extends Exception> void ifValidOrElseThrow(Runnable action, Function<List<ValidationError>, T> alternativeAction) throws T {
        if (isValid()) {
            action.run();
        } else {
            throw alternativeAction.apply(validationErrors);
        }
    }

    public boolean isValid() {
        return validationErrors.isEmpty();
    }

    public List<ValidationError> getValidationErrors() {
        return new ArrayList<>(validationErrors);
    }
}
