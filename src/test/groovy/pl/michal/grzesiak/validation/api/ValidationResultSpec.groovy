package pl.michal.grzesiak.validation.api


import spock.lang.Specification

import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Supplier

class ValidationResultSpec extends Specification {

	private static final ValidationError ERROR = new ValidationError("error")

	def "should invoke action if the validation result contains error"() {
		given:
			Consumer<List<ValidationError>> consumer = Mock()
			def validationResult = ValidationResult.invalid(ERROR)
		when:
			validationResult.ifAnyErrorOccurred(consumer)
		then:
			1 * consumer.accept(_)
	}

	def "should not invoke action if the validation result does not contain any error"() {
		given:
			Consumer<List<ValidationError>> consumer = Mock()
			def validationResult = ValidationResult.valid()
		when:
			validationResult.ifAnyErrorOccurred(consumer)
		then:
			0 * consumer.accept(_)
	}

	def "should invoke exception supplier if the validation result contains error"() {
		given:
			Supplier<IllegalArgumentException> expectedException = Mock()
			expectedException.get() >> { new RuntimeException() }
			def validationResult = ValidationResult.invalid(ERROR)
		when:
			validationResult.throwIfAnyErrorOccurred(expectedException)
		then:
			thrown(RuntimeException)
	}

	def "should not invoke exception supplier if the validation result does not contain any error"() {
		given:
			Supplier<IllegalArgumentException> expectedException = Mock()
			def validationResult = ValidationResult.valid()
		when:
			validationResult.throwIfAnyErrorOccurred(expectedException)
		then:
			0 * expectedException.get()
	}

	def "should throw an exception if validation result contains error"() {
		given:
			Function<List<ValidationError>, IllegalArgumentException> expectedException = Mock()
			expectedException.apply(_ as List<ValidationError>) >> { new IllegalArgumentException() }
			def validationResult = ValidationResult.invalid(ERROR)
		when:
			validationResult.throwIfAnyErrorOccurred(expectedException)
		then:
			thrown(IllegalArgumentException)
	}

	def "should not throw an exception if the validation result does not contain any error"() {
		given:
			Function<List<ValidationError>, IllegalArgumentException> expectedException = Mock()
			def validationResult = ValidationResult.valid()
		when:
			validationResult.throwIfAnyErrorOccurred(expectedException)
		then:
			0 * expectedException.apply(_)
	}

	def "should invoke action if the validation result does not contain any error"() {
		given:
			Runnable action = Mock()
			Consumer<List<ValidationError>> alternativeAction = Mock()
			def validationResult = ValidationResult.valid()
		when:
			validationResult.ifValidOrElse(action, alternativeAction)
		then:
			1 * action.run()
			0 * alternativeAction.accept(_)
	}

	def "should invoke alternative action if the validation result contains error"() {
		given:
			Runnable action = Mock()
			Consumer<List<ValidationError>> alternativeAction = Mock()
			def validationResult = ValidationResult.invalid(ERROR)
		when:
			validationResult.ifValidOrElse(action, alternativeAction)
		then:
			0 * action.run()
			1 * alternativeAction.accept(_)
	}

	def "should invoke action when validation passed"() {
		given:
			Runnable action = Mock()
			Function<List<ValidationError>, RuntimeException> exceptionSupplier = Mock()
			def validationResult = ValidationResult.valid()
		when:
			validationResult.ifValidOrElseThrow(action, exceptionSupplier)
		then:
			1 * action.run()
			0 * exceptionSupplier.apply(_)
	}

	def "should throw exception from exception supplier when validation has not passed"() {
		given:
			Runnable action = Mock()
			Function<List<ValidationError>, RuntimeException> exceptionSupplier = Mock()
			exceptionSupplier.apply(_ as List<ValidationError>) >> { return new IllegalArgumentException() }
			def validationResult = ValidationResult.invalid(ERROR)
		when:
			validationResult.ifValidOrElseThrow(action, exceptionSupplier)
		then:
			0 * action.run()
			thrown(IllegalArgumentException)
	}

	def "concatenation should return an error only from the second validation result when the first one is valid"() {
		given:
			def withoutError = ValidationResult.valid()
		and:
			def validationError = new ValidationError("")
			def withError = ValidationResult.invalid(validationError)
		when:
			def validationResult = withoutError.concat(withError)
		then:
			!validationResult.isValid()
			validationResult.getValidationErrors().contains(validationError)
			validationResult.getValidationErrors().size() == 1
	}

	def "concatenation should return errors from both validation results when they are invalid"() {
		given:
			def firstValidationError = new ValidationError("test1")
			def firstResultWithError = ValidationResult.invalid(firstValidationError)
		and:
			def secondValidationError = new ValidationError("test2")
			def secondResultWithError = ValidationResult.invalid(secondValidationError)
		when:
			def validationResult = firstResultWithError.concat(secondResultWithError)
		then:
			!validationResult.isValid()
			validationResult.getValidationErrors().size() == 2
	}

	def "concatenation should return an error only from the first validation result when the second is valid"() {
		given:
			def validationError = new ValidationError("test1")
			def firstResultWithError = ValidationResult.invalid(validationError)
		and:
			def secondResultWithError = ValidationResult.valid()
		when:
			def validationResult = firstResultWithError.concat(secondResultWithError)
		then:
			!validationResult.isValid()
			validationResult.getValidationErrors().contains(validationError)
			validationResult.getValidationErrors().size() == 1
	}
}
