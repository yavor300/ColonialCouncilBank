package app.ccb.domain.dtos.jsons;

import com.google.gson.annotations.Expose;

import java.math.BigDecimal;

public class EmployeeImportDto {

    @Expose
    private String full_name;

    @Expose
    private BigDecimal salary;

    @Expose
    private String started_on;

    @Expose
    private String branch_name;

    public EmployeeImportDto() {
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public String getStarted_on() {
        return started_on;
    }

    public void setStarted_on(String started_on) {
        this.started_on = started_on;
    }

    public String getBranch_name() {
        return branch_name;
    }

    public void setBranch_name(String branch_name) {
        this.branch_name = branch_name;
    }
}
