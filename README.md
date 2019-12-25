# validation-api
The `rule` is a basic validation unit, which contains a predicate and an error message - the second one is returned if
validation failed. In order to create a new rule, you have to use the `Validation.rule()` method and pass necessary parameters.

```java
Predicate<String> hasMoreThanTenSigns = word -> word.length() > 10;
Validation.rule(hasMoreThanTenSigns, "word has less than 10 signs");
```
The `rule()` method returns the instance of `Validation`, which contains among others a `validate()` method - it allows validating your object.

```java
Predicate<String> hasMoreThanTenSigns = word -> word.length() > 10;
ValidationResult validationResult = Validation.rule(hasMoreThanTenSigns, "word has not more than 10 signs")
                                              .validate("Lech");
```
By performing validation, you obtain the `ValidationResult` object - it contains the operation result and has a couple of
methods you can use to handle the whole process. Each method depends on the validation result - for instance: 
if validation has finished without any errors, the passed consumer to`ifAnyErrorOccurred()` method will not be invoked.

```java
Validation.rule(hasMoreThanTenSigns, "word has not more than 10 signs")
          .validate("Lech")
          .ifAnyErrorOccurred(errors -> errors.forEach(System.out::println);
```

### The rules chaining
If you have a more complicated validation logic, you can join as rules as you need into one chain by invoking the `and()` method.
Remember that each rule accepts `Predicate<T>` - as you surely know, this interface offers a couple of usable methods
like `or()` and `not()` - you can use them to create a more advanced rule.

```java
Validation<String> isNotEmpty = rule(not(String::isEmpty), "a word is empty");
Validation<String> startsWithL = rule(word -> word.startsWith("L"), "a word does not start with L");
Validation<String> endsWithH = rule(word -> word.endsWith("h"), "a word does not end with h");

ValidationResult validationResult = isNotEmpty.and(startsWithL)
                                              .and(endsWithH)
                                              .validate("Lech")
```
Remember that `ValidationResult` will aggregate all errors which will occurred during validation.

### Merging different validation results
When you validate the different type of objects, you can merge their results and handle in one way, by invoking `concat()` method.

```java
ValidationResult validationResult1 = isNotEmpty.and(startsWithL).validate("Lee");
ValidationResult validationResult2 = greaterThan10.and(multipleOfTwo).validate(16);

validationResult1.concat(validationResult2)
                 .ifAnyErrorOccurred(...);
```