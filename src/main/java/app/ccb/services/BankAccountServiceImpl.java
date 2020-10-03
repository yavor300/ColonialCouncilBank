package app.ccb.services;

import app.ccb.domain.dtos.xmls.BankAccountDto;
import app.ccb.domain.dtos.xmls.BankAccountRootDto;
import app.ccb.domain.entities.BankAccount;
import app.ccb.domain.entities.Client;
import app.ccb.repositories.BankAccountRepository;
import app.ccb.repositories.ClientRepository;
import app.ccb.util.FileUtil;
import app.ccb.util.ValidationUtil;
import app.ccb.util.XmlParser;
import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;

@Service
public class BankAccountServiceImpl implements BankAccountService {

    private final static String BANK_ACCOUNTS_XML_PATH = "E:\\Tai Lopez\\ColonialCouncilBank-skeleton\\ColonialCouncilBank\\src\\main\\resources\\files\\xml\\bank-accounts.xml";

    private final BankAccountRepository bankAccountRepository;
    private final ClientRepository clientRepository;
    private final FileUtil fileUtil;
    private final Gson gson;
    private final XmlParser xmlParser;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;

    public BankAccountServiceImpl(BankAccountRepository bankAccountRepository, ClientRepository clientRepository, FileUtil fileUtil, Gson gson, XmlParser xmlParser, ModelMapper modelMapper, ValidationUtil validationUtil) {
        this.bankAccountRepository = bankAccountRepository;
        this.clientRepository = clientRepository;
        this.fileUtil = fileUtil;
        this.gson = gson;
        this.xmlParser = xmlParser;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
    }

    @Override
    public Boolean bankAccountsAreImported() {
        return this.bankAccountRepository.count() > 0;
    }

    @Override
    public String readBankAccountsXmlFile() throws IOException {
        return this.fileUtil.readFile(BANK_ACCOUNTS_XML_PATH);
    }

    @Override
    public String importBankAccounts() throws JAXBException {
        StringBuilder sb = new StringBuilder();

        BankAccountRootDto dtos = this.xmlParser.parseXml(BankAccountRootDto.class, BANK_ACCOUNTS_XML_PATH);
        for (BankAccountDto dto : dtos.getBankAccountDtos()) {

            BankAccount bankAccount = this.modelMapper.map(dto, BankAccount.class);
            if (!this.validationUtil.isValid(bankAccount)) {
                sb.append("Error: Incorrect Data!").append(System.lineSeparator());
                continue;
            }

            List<Client> clients = this.clientRepository.findAllByFullName(dto.getClient());
            if (clients == null || clients.size() == 0) {
                sb.append("Error: Incorrect Data!").append(System.lineSeparator());
                continue;
            }

            bankAccount.setClient(clients.get(0));
            this.bankAccountRepository.saveAndFlush(bankAccount);
            sb.append(String.format("Successfully imported %s - %s.%n",
                    bankAccount.getClass().getSimpleName(),
                    bankAccount.getAccountNumber()));
        }

        return sb.toString().trim();
    }
}
