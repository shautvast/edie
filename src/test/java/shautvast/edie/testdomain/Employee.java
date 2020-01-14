package shautvast.edie.testdomain;

import java.util.Objects;

public class Employee {
    private Person person;

    public Employee(Person person) {
        this.person = person;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(person, employee.person);
    }

    @Override
    public int hashCode() {
        return Objects.hash(person);
    }

    @Override
    public String toString() {
        return "Employee{" +
                "person=" + person +
                '}';
    }
}
