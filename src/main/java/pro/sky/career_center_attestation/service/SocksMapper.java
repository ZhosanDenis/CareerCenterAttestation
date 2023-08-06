package pro.sky.career_center_attestation.service;

import org.springframework.stereotype.Component;
import pro.sky.career_center_attestation.dto.SocksDto;
import pro.sky.career_center_attestation.model.SocksColor;
import pro.sky.career_center_attestation.model.SocksEntity;

@Component
public class SocksMapper {

    /**
     * Преобразование полей найденной сущности значениями из тела запроса
     * @param found найденная сущность носков
     * @param incoming объект, передаваемый в запросе
     * @return преобразованную сущность носков
     */
    public SocksEntity updateSocksEntity(SocksEntity found, SocksDto incoming) {
        found.setColor(SocksColor.findByStringColor(incoming.getColor().toLowerCase()));
        found.setCottonPart(incoming.getCottonPart());
        found.setQuantity(incoming.getQuantity());
        return found;
    }
}
