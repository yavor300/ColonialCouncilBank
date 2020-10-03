package app.ccb.services;

import app.ccb.domain.dtos.jsons.BranchImportDto;
import app.ccb.domain.entities.Branch;
import app.ccb.repositories.BranchRepository;
import app.ccb.util.FileUtil;
import app.ccb.util.ValidationUtil;
import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class BranchServiceImpl implements BranchService {

    private final static String BRANCHES_JSON_PATH = "E:\\Tai Lopez\\ColonialCouncilBank-skeleton\\ColonialCouncilBank\\src\\main\\resources\\files\\json\\branches.json";

    private final BranchRepository branchRepository;
    private final FileUtil fileUtil;
    private final Gson gson;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;

    @Autowired
    public BranchServiceImpl(BranchRepository branchRepository, FileUtil fileUtil, Gson gson, ModelMapper modelMapper, ValidationUtil validationUtil) {
        this.branchRepository = branchRepository;
        this.fileUtil = fileUtil;
        this.gson = gson;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
    }

    @Override
    public Boolean branchesAreImported() {
        return this.branchRepository.count() > 0;
    }

    @Override
    public String readBranchesJsonFile() throws IOException {
        return this.fileUtil.readFile(BRANCHES_JSON_PATH);
    }

    @Override
    public String importBranches(String branchesJson) {
        StringBuilder sb = new StringBuilder();

        BranchImportDto[] dtos = this.gson.fromJson(branchesJson, BranchImportDto[].class);
        for (BranchImportDto dto : dtos) {
            Branch branch = this.modelMapper.map(dto, Branch.class);

            if (!this.validationUtil.isValid(branch)) {
                sb.append("Error: Incorrect Data!").append(System.lineSeparator());
                continue;
            }

            this.branchRepository.saveAndFlush(branch);
            sb.append(String.format("Successfully imported %s - %s.%n",
                    branch.getClass().getSimpleName(),
                    branch.getName()));
        }

        return sb.toString().trim();
    }
}
