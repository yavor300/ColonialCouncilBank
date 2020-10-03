package app.ccb.services;

import app.ccb.domain.dtos.xmls.CardImportDto;
import app.ccb.domain.dtos.xmls.CardImportRootDto;
import app.ccb.domain.entities.BankAccount;
import app.ccb.domain.entities.Card;
import app.ccb.repositories.BankAccountRepository;
import app.ccb.repositories.CardRepository;
import app.ccb.util.FileUtil;
import app.ccb.util.ValidationUtil;
import app.ccb.util.XmlParser;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.io.IOException;

@Service
public class CardServiceImpl implements CardService {

    private final static String CARDS_XML_PATH = "E:\\Tai Lopez\\ColonialCouncilBank-skeleton\\ColonialCouncilBank\\src\\main\\resources\\files\\xml\\cards.xml";

    private final CardRepository cardRepository;
    private final BankAccountRepository bankAccountRepository;
    private final FileUtil fileUtil;
    private final XmlParser xmlParser;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;

    public CardServiceImpl(CardRepository cardRepository, BankAccountRepository bankAccountRepository, FileUtil fileUtil, XmlParser xmlParser, ModelMapper modelMapper, ValidationUtil validationUtil) {
        this.cardRepository = cardRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.fileUtil = fileUtil;
        this.xmlParser = xmlParser;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
    }

    @Override
    public Boolean cardsAreImported() {
        return this.cardRepository.count() > 0;
    }

    @Override
    public String readCardsXmlFile() throws IOException {
        return this.fileUtil.readFile(CARDS_XML_PATH);
    }

    @Override
    public String importCards() throws JAXBException {
        StringBuilder sb = new StringBuilder();

        CardImportRootDto dtos = this.xmlParser.parseXml(CardImportRootDto.class, CARDS_XML_PATH);
        for (CardImportDto cardImportDto : dtos.getCardImportDtos()) {

            Card card = this.modelMapper.map(cardImportDto, Card.class);
            if (!this.validationUtil.isValid(card)) {
                sb.append("Error: Incorrect Data!").append(System.lineSeparator());
                continue;
            }

            BankAccount bankAccount = this.bankAccountRepository.findByAccountNumber(cardImportDto.getAccountNumber());
            if (bankAccount == null) {
                sb.append("Error: Incorrect Data!").append(System.lineSeparator());
                continue;
            }

            card.setBankAccount(bankAccount);
            this.cardRepository.saveAndFlush(card);
            sb.append(String.format("Successfully imported %s - %s.%n",
                    card.getClass().getSimpleName(),
                    card.getCardNumber()));

        }

        return sb.toString().trim();
    }
}
