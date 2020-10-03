package app.ccb.services;

import app.ccb.domain.dtos.jsons.ClientImportDto;
import app.ccb.domain.entities.Card;
import app.ccb.domain.entities.Client;
import app.ccb.domain.entities.Employee;
import app.ccb.repositories.ClientRepository;
import app.ccb.repositories.EmployeeRepository;
import app.ccb.util.FileUtil;
import app.ccb.util.ValidationUtil;
import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Transactional
@Service
public class ClientServiceImpl implements ClientService {

    private final static String CLIENTS_JSON_PATH = "E:\\Tai Lopez\\ColonialCouncilBank-skeleton\\ColonialCouncilBank\\src\\main\\resources\\files\\json\\clients.json";

    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private final FileUtil fileUtil;
    private final Gson gson;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;

    @Autowired
    public ClientServiceImpl(ClientRepository clientRepository, EmployeeRepository employeeRepository, FileUtil fileUtil, Gson gson, ModelMapper modelMapper, ValidationUtil validationUtil) {
        this.clientRepository = clientRepository;
        this.employeeRepository = employeeRepository;
        this.fileUtil = fileUtil;
        this.gson = gson;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
    }

    @Override
    public Boolean clientsAreImported() {
        return this.clientRepository.count() > 0;
    }

    @Override
    public String readClientsJsonFile() throws IOException {
        return this.fileUtil.readFile(CLIENTS_JSON_PATH);
    }

    @Override
    public String importClients(String clients) {
        StringBuilder sb = new StringBuilder();

        ClientImportDto[] dtos = this.gson.fromJson(clients, ClientImportDto[].class);
        for (ClientImportDto dto : dtos) {
            Client client = this.modelMapper.map(dto, Client.class);
            client.setFullName(dto.getFirst_name() + " " + dto.getLast_name());

            if (!this.validationUtil.isValid(client)) {
                sb.append("Error: Incorrect Data!").append(System.lineSeparator());
                continue;
            }

            Employee employee = this.employeeRepository.findByFirstNameAndLastName(
                    dto.getAppointed_employee().split("\\s")[0],
                    dto.getAppointed_employee().split("\\s+")[1]);
            if (employee == null) {
                sb.append("Error: Incorrect Data!").append(System.lineSeparator());
                continue;
            }

            Set<Employee> employees = new LinkedHashSet<>();
            employees.add(employee);
            client.setEmployees(employees);


            this.clientRepository.saveAndFlush(client);
            sb.append(String.format("Successfully imported %s - %s.%n",
                    client.getClass().getSimpleName(),
                    client.getFullName()));


        }

        return sb.toString().trim();
    }

    @Override
    public String exportFamilyGuy() {
        StringBuilder sb = new StringBuilder();

        List<Client> clients = this.clientRepository.findAllByMostCards();
        Client familyGuy = clients.get(0);
        sb.append(String.format("Full Name: %s%nAge: %d%nBank Account: %s%n",
                familyGuy.getFullName(),
                familyGuy.getAge(),
                familyGuy.getBankAccount().getAccountNumber()));
        for (Card card : familyGuy.getBankAccount().getCards()) {
            sb.append(String.format("\tCard Number: %s%n", card.getCardNumber()));
        }
        sb.append(System.lineSeparator());


        return sb.toString().trim();
    }
}
