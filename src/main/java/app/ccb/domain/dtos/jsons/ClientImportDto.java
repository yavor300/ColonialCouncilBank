package app.ccb.domain.dtos.jsons;

import com.google.gson.annotations.Expose;

public class ClientImportDto {

    @Expose
    private String first_name;

    @Expose
    private String last_name;

    @Expose
    private Integer age;

    @Expose
    private String appointed_employee;

    public ClientImportDto() {
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getAppointed_employee() {
        return appointed_employee;
    }

    public void setAppointed_employee(String appointed_employee) {
        this.appointed_employee = appointed_employee;
    }
}
