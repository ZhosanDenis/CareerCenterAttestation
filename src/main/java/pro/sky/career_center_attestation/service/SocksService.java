package pro.sky.career_center_attestation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.sky.career_center_attestation.dto.SocksDto;
import pro.sky.career_center_attestation.model.SocksColor;
import pro.sky.career_center_attestation.model.SocksEntity;
import pro.sky.career_center_attestation.repository.SocksRepository;

@Service
@RequiredArgsConstructor
public class SocksService {

    private final SocksRepository repository;

    private final SocksMapper mapper;

    /**
     * Регистрация прихода носков на склад:<br>
     * - поиск в БД носков по цвету и содержанию хлопка или создание новой записи при отсутствии<br>
     * - если носки существуют в БД -> добавление количества поступаемых носков и сохранение в БД<br>
     * - если создается новая запись -> поля сущности заполняются из поступаемых значений и сохраняются в БД
     * @param socks поступаемые носки
     */
    @Transactional
    public void socksIncome(SocksDto socks) {
        SocksEntity foundSocks = repository.findByColorAndCottonPart(SocksColor.findByStringColor(socks.getColor().toLowerCase()),
                        socks.getCottonPart())
                .orElse(new SocksEntity());
        if (foundSocks.getId() != 0) {
            foundSocks.setQuantity(foundSocks.getQuantity() + socks.getQuantity());
            repository.save(foundSocks);
        } else if (foundSocks.getId() == 0 && foundSocks.getColor() == null) {
            foundSocks = mapper.updateSocksEntity(foundSocks, socks);
            repository.save(foundSocks);
        }
    }

    /**
     * Регистрация отпуска носков со склада:<br>
     * - поиск в БД носков по цвету и содержанию хлопка или выброс исключения при отсутствии<br>
     * - если количество отпускаемых носков превышает количества на складе, то выброс исключения<br>
     * - уменьшение количества носков на складе на отпускаемое значение и сохранение в БД
     * @param socks отпускаемые носки
     */
    @Transactional
    public void socksOutcome(SocksDto socks) {
        SocksEntity foundSocks = repository.findByColorAndCottonPart(SocksColor.findByStringColor(socks.getColor().toLowerCase()),
                        socks.getCottonPart())
                .orElseThrow(() -> new IllegalArgumentException("По данным параметрам носки отсутствуют на складе"));
        if (foundSocks.getQuantity() < socks.getQuantity()) {
            throw new IllegalArgumentException("Количество отпускаемых носков превышает количество на складе");
        }
        foundSocks.setQuantity(foundSocks.getQuantity() - socks.getQuantity());
        repository.save(foundSocks);
    }

    /**
     * Поиск общего количества носков на складе, соответствующих переданным в параметрах критериям запроса:<br>
     * - поиск цвета из перечисленных значений по переданному значению
     * - в зависимости от переданного оператора сравнения осуществляется поиск в БД или выброс исключения об отсутствии носков по переданным параметрам
     * @param color цвет носков
     * @param operation оператор сравнения значения количества хлопка в составе носков, одно значение из: moreThan, lessThan, equal
     * @param cottonPart значение процента хлопка в составе носков из сравнения
     * @return количество носков с заданными параметрами
     */
    @Transactional(readOnly = true)
    public int getSocksQuantityInfo(String color, String operation, int cottonPart) {
        SocksColor socksColor = SocksColor.findByStringColor(color.toLowerCase());
        switch (operation) {
            case "moreThan" -> {
                SocksEntity foundSocks = repository.findByColorAndCottonPartIsGreaterThan(socksColor, cottonPart)
                        .orElseThrow(() -> new IllegalArgumentException("По данным параметрам носки отсутствуют на складе"));
                return foundSocks.getQuantity();
            }
            case "lessThan" -> {
                SocksEntity foundSocks = repository.findByColorAndCottonPartIsLessThan(socksColor, cottonPart)
                        .orElseThrow(() -> new IllegalArgumentException("По данным параметрам носки отсутствуют на складе"));
                return foundSocks.getQuantity();
            }
            case "equal" -> {
                SocksEntity foundSocks = repository.findByColorAndCottonPart(socksColor, cottonPart)
                        .orElseThrow(() -> new IllegalArgumentException("По данным параметрам носки отсутствуют на складе"));
                return foundSocks.getQuantity();
            }
        }
        return 0;
    }
}
