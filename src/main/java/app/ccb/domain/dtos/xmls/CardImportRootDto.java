package app.ccb.domain.dtos.xmls;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "cards")
@XmlAccessorType(XmlAccessType.FIELD)
public class CardImportRootDto {

    @XmlElement(name = "card")
    private List<CardImportDto> cardImportDtos;

    public CardImportRootDto() {
    }

    public List<CardImportDto> getCardImportDtos() {
        return cardImportDtos;
    }

    public void setCardImportDtos(List<CardImportDto> cardImportDtos) {
        this.cardImportDtos = cardImportDtos;
    }
}
