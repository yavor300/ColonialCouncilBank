package app.ccb.domain.dtos.jsons;

import com.google.gson.annotations.Expose;

public class BranchImportDto {

    @Expose
    private String name;

    public BranchImportDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
