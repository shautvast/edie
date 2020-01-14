package shautvast.edie.testdomain;

import java.util.ArrayList;
import java.util.List;

public class Company {
    private final List<Employee> employees=new ArrayList<>();

    public Company(List<Employee> employees) {
        this.employees.addAll(employees);
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    @Override
    public String toString() {
        return "Company{" +
                "employees=" + employees +
                '}';
    }
}
