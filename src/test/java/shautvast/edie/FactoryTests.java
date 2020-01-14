package shautvast.edie;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shautvast.edie.testdomain.Company;
import shautvast.edie.testdomain.Employee;
import shautvast.edie.testdomain.Person;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Assertions for the factory and definitions")
public class FactoryTests {
    @Test
    @DisplayName("Should build instance with definition.build()")
    public void simpleTemplate() {
        Definition<Person> personDef = Factory.define(Person.class, () -> new Person("Sander", 48));

        Person person = personDef.build();

        Assertions.assertEquals("Sander", person.getName());
        Assertions.assertEquals(48, person.getAge());
    }

    @Test
    @DisplayName("Should build instance with Factory.build()")
    public void simpleTemplateStatic() {
        Factory.define(Person.class, () -> new Person("Sander", 48));

        Person person = Factory.build(Person.class);

        Assertions.assertEquals("Sander", person.getName());
        Assertions.assertEquals(48, person.getAge());
    }

    @Test
    @DisplayName("Should build a customized instance while the other attributes are according to the template")
    public void simpleTemplateAlteration() {
        Factory.define(Person.class, () -> new Person("Sander", 48));

        Person person = Factory.build(Person.class, p -> p.setName("Harry"));

        Assertions.assertEquals("Harry", person.getName());
        Assertions.assertEquals(48, person.getAge());
    }

    @Test
    @DisplayName("Should create different values for an attribute for every instance")
    public void counters() {
        Definition<Person> personDef = Factory.define(Person.class, () -> new Person("Sander", 48));

        Person person = personDef.build(p ->
                p.setName("Harry" + FactoryHelper.increment())
        );

        Assertions.assertEquals("Harry0", person.getName());
        Assertions.assertEquals(48, person.getAge());
    }

    @Test
    @DisplayName("Should lookup a constructor with a nested type definition and create nested instances")
    public void nestedDefinitionsWithConstructor() {
        Factory.define(Person.class, () -> new Person("Sander", FactoryHelper.randomInt(100)));
        Definition<Employee> employeeDef = Factory.define(Employee.class).withConstructorArgs(Person.class);

        Employee employee = employeeDef.build();

        Assertions.assertEquals("Sander", employee.getPerson().getName());
    }

    @Test
    @DisplayName("Should create nested instances using only suppliers")
    public void nestedDefinitionsWithoutReflection() {
        Definition<Person> personDef = Factory.define(Person.class, () -> new Person("Sander", FactoryHelper.randomInt(100)));
        Definition<Employee> employeeDef = Factory.define(Employee.class, () -> new Employee(personDef.build()));

        Employee employee = employeeDef.build();

        Assertions.assertEquals("Sander", employee.getPerson().getName());
    }

    @Test
    @DisplayName("Should create random attributes for every instance")
    public void randomInts() {
        Definition<Person> personDef = Factory.define(Person.class, () -> new Person("Sander", FactoryHelper.randomInt(100)));

        Person person = personDef.build();

        Assertions.assertEquals("Sander", person.getName());
        assertTrue(person.getAge() < 100);
    }

    @Test
    @DisplayName("Should create an object with a list of factory produced elements")
    public void definitionsList() {
        Factory.define(Person.class, () -> new Person("Sander" + FactoryHelper.increment(), FactoryHelper.randomInt(100)));
        Factory.define(Employee.class).withConstructorArgs(Person.class);
        Factory.define(Company.class).withConstructorArgs(FactoryHelper.listOf(Employee.class, Employee.class));

        Company company = Factory.build(Company.class);

        Assertions.assertEquals(2, company.getEmployees().size());
    }

    @Test
    @DisplayName("Should create an object with a list of factory produced elements without reflection")
    public void definitionsListWithoutReflection() {
        Factory.define(Person.class, () -> new Person("Sander", 10));
        Factory.define(Employee.class).withConstructorArgs(Person.class);
        Factory.define(Company.class, () -> new Company(Arrays.asList(Factory.build(Employee.class), Factory.build(Employee.class))));

        Company company = Factory.build(Company.class);

        Assertions.assertEquals(2, company.getEmployees().size());
        Assertions.assertTrue(company.getEmployees().contains(new Employee(new Person("Sander", 10))));
        Assertions.assertTrue(company.getEmployees().contains(new Employee(new Person("Sander", 10))));
    }

    @Test
    @DisplayName("Should create a list with two elements")
    public void definitionsListWithIncrementInNestedDefinition() {
        Factory.define(Person.class, () -> new Person("Sander" + FactoryHelper.increment("sander"), 10));
        Factory.define(Employee.class).withConstructorArgs(Person.class);
        Factory.define(Company.class, () -> new Company(Arrays.asList(Factory.build(Employee.class), Factory.build(Employee.class))));

        Company company = Factory.build(Company.class);

        Assertions.assertEquals(2, company.getEmployees().size());
        Assertions.assertTrue(company.getEmployees().contains(new Employee(new Person("Sander0", 10))));
        Assertions.assertTrue(company.getEmployees().contains(new Employee(new Person("Sander1", 10))));
    }

    @Test
    @DisplayName("Method withConstructor for a constructor with the wrong types should raise an exception")
    public void constructorNotFound() {
        assertThrows(
                IllegalArgumentException.class, () ->
                        Factory.define(Person.class).withConstructorArgs(String.class));
    }
}