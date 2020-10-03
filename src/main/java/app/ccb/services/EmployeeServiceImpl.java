package app.ccb.services;

import app.ccb.domain.dtos.jsons.EmployeeImportDto;
import app.ccb.domain.entities.Branch;
import app.ccb.domain.entities.Client;
import app.ccb.domain.entities.Employee;
import app.ccb.repositories.BranchRepository;
import app.ccb.repositories.EmployeeRepository;
import app.ccb.util.FileUtil;
import app.ccb.util.ValidationUtil;
import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;

@Transactional
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final static String EMPLOYEES_JSON_PATH = "E:\\Tai Lopez\\ColonialCouncilBank-skeleton\\ColonialCouncilBank\\src\\main\\resources\\files\\json\\employees.json";

    private final EmployeeRepository employeeRepository;
    private final BranchRepository branchRepository;
    private final FileUtil fileUtil;
    private final Gson gson;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository, BranchRepository branchRepository, FileUtil fileUtil, Gson gson, ModelMapper modelMapper, ValidationUtil validationUtil) {
        this.employeeRepository = employeeRepository;
        this.branchRepository = branchRepository;
        this.fileUtil = fileUtil;
        this.gson = gson;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
    }

    @Override
    public Boolean employeesAreImported() {
       return this.employeeRepository.count() > 0;
    }

    @Override
    public String readEmployeesJsonFile() throws IOException {
        return this.fileUtil.readFile(EMPLOYEES_JSON_PATH);
    }

    @Override
    public String importEmployees(String employees) {
        StringBuilder sb = new StringBuilder();

        EmployeeImportDto[] dtos = this.gson.fromJson(employees, EmployeeImportDto[].class);
        for (EmployeeImportDto dto : dtos) {
            Employee employee = this.modelMapper.map(dto, Employee.class);
            employee.setFirstName(dto.getFull_name().split("\\s")[0]);
            employee.setLastName(dto.getFull_name().split("\\s")[1]);
            employee.setStartedOn(dto.getStarted_on());

            Branch branch = this.branchRepository.findByName(dto.getBranch_name());
            employee.setBranch(branch);

            if (!this.validationUtil.isValid(employee)) {
                sb.append("Error: Incorrect Data!").append(System.lineSeparator());
                continue;
            }

            this.employeeRepository.saveAndFlush(employee);
            sb.append(String.format("Successfully imported %s - %s.%n",
                    employee.getClass().getSimpleName(),
                    dto.getFull_name()));
        }

        return sb.toString().trim();
    }

    @Override
    public String exportTopEmployees() {
        StringBuilder sb = new StringBuilder();

        List<Employee> employees = this.employeeRepository.findAllByClientsIsNotNullOrderByClientsDescIdAsc();
        for (Employee employee : employees) {
            sb.append(String.format("Full Name: %s%nSalary: %s%nStarted On: %s%nClients:%n",
                    employee.getFirstName() + " " + employee.getLastName(),
                    employee.getSalary(),
                    employee.getStartedOn()));
            for (Client client : employee.getClients()) {
                sb.append(String.format("\t%s%n", client.getFullName()));
            }
            sb.append(System.lineSeparator());
        }

        return sb.toString().trim();
    }
}
