package pl.michal.grzesiak.validation.api

import spock.lang.Specification
import spock.lang.Unroll

import static Validation.rule
import static java.util.Objects.nonNull

class ValidationSpec extends Specification {

	private static final String TEST_PHRASE = "dummy text"

	def "should not return any error when validation has passed"() {
		given:
			def rule = ruleShouldPass(true)
		when:
			def validationResult = rule.validate(TEST_PHRASE)
		then:
			validationResult.isValid()
	}

	def "should return an error when validation has not passed"() {
		given:
			def rule = ruleShouldPass(false)
		when:
			def validationResult = rule.validate(TEST_PHRASE)
		then:
			!validationResult.isValid()
	}

	def "\'AND\' operation - should not execute the second validation when the object to validate is null"() {
		given:
			def firstRuleThatChecksNull = rule({ object -> nonNull(object) }, "error")
		and:
			def secondRule = Mock(Validation)
		and:
			def validation = firstRuleThatChecksNull & secondRule
		when:
			def validationResult = validation.validate(null)
		then:
			!validationResult.isValid()
			0 * secondRule.validate(_)
	}

	@Unroll
	def "\'AND\' operation should return \'#expectedValidationResult\' when the first rule returns \'#hasFirstRulePassed\' and the second: \'#hasSecondRulePassed\'"() {
		given:
			def validation = ruleShouldPass(hasFirstRulePassed) & ruleShouldPass(hasSecondRulePassed)
		when:
			def validationResult = validation.validate(TEST_PHRASE)
		then:
			validationResult.isValid() == expectedValidationResult
		where:
			hasFirstRulePassed | hasSecondRulePassed || expectedValidationResult
			true               | true                || true
			true               | false               || false
			false              | true                || false
			false              | false               || false
	}

	private static Validation<String> ruleShouldPass(boolean shouldPass) {
		rule({ object -> shouldPass }, "error message")
	}
}
