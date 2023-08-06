package pro.sky.career_center_attestation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pro.sky.career_center_attestation.dto.SocksDto;
import pro.sky.career_center_attestation.service.SocksService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/socks")
public class SocksController {
    public static final List<String> OPERATIONS = new ArrayList<>(List.of("moreThan", "lessThan", "equal"));

    private static final String BAD_REQUEST_ERROR = "Incorrect socks input parameters are presented." +
            " You need to check the color, cotton part or number of pairs.";

    private final SocksService service;

    /**
     * Регистрация прихода носков на склад.
     * @param socks поступаемые носки с параметрами: id, цвет, содержание хлопка, количество
     * @return статус ответа в зависимости от выполнения запроса
     */
    @Operation(
            summary = "Поступление носков на склад",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Удалось добавить приход", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "400", description = "Параметры запроса отсутствуют или имеют некорректный формат", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "500", description = "Произошла ошибка, не зависящая от вызывающей стороны", content = @Content(schema = @Schema(hidden = true)))
            },
            tags = "Склад носков"

    )
    @PostMapping("/income")
    public ResponseEntity<?> socksIncome(@RequestBody SocksDto socks) {
        if (socksNotValid(socks)) {
            log.error(BAD_REQUEST_ERROR);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BAD_REQUEST_ERROR);
        }
        service.socksIncome(socks);
        return ResponseEntity.ok().body("Socks income");
    }

    /**
     * Регистрация отпуска носков со склада.
     * @param socks отпускааемые носки с параметрами: id, цвет, содержание хлопка, количество
     * @return статус ответа в зависимости от выполнения запроса
     */
    @Operation(
            summary = "Отгрузка носков со склада",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Удалось провести отпуск", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "400", description = "Параметры запроса отсутствуют или имеют некорректный формат", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "500", description = "Произошла ошибка, не зависящая от вызывающей стороны", content = @Content(schema = @Schema(hidden = true)))
            },
            tags = "Склад носков"

    )
    @PostMapping("/outcome")
    public ResponseEntity<?> socksOutcome(@RequestBody SocksDto socks) {
        if (socksNotValid(socks)) {
            log.error(BAD_REQUEST_ERROR);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BAD_REQUEST_ERROR);
        }
        service.socksOutcome(socks);
        return ResponseEntity.ok().body("Socks outgo");
    }

    /**
     * Поиск общего количества носков на складе, соответствующих переданным в параметрах критериям запроса.
     * @param color цвет носков
     * @param operation оператор сравнения значения количества хлопка в составе носков, одно значение из: moreThan, lessThan, equal
     * @param cottonPart значение процента хлопка в составе носков из сравнения
     * @return количество носков с заданными параметрами
     */
    @Operation(
            summary = "Поиск носков на складе по заданным параметрам",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Запрос выполнен, результат в теле ответа в виде строкового представления целого числа", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "400", description = "Параметры запроса отсутствуют или имеют некорректный формат", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "500", description = "Произошла ошибка, не зависящая от вызывающей стороны", content = @Content(schema = @Schema(hidden = true)))
            },
            tags = "Склад носков"

    )
    @GetMapping
    public ResponseEntity<String> getSocksQuantityInfo(@RequestParam String color,
                                                       @RequestParam String operation,
                                                       @RequestParam int cottonPart) {
        if (color == null ||
                color.isEmpty() ||
                color.isBlank() ||
                operation == null ||
                operation.isEmpty() ||
                operation.isBlank() ||
                cottonPart < 0 ||
                cottonPart > 100 ||
                !OPERATIONS.contains(operation)) {
            log.error(BAD_REQUEST_ERROR + " Также необходимо проверить правильность указанной операции.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BAD_REQUEST_ERROR);
        }
        String comparing = null;
        if (operation.equals(OPERATIONS.get(0))) {
            comparing = "более";
        } else if (operation.equals(OPERATIONS.get(1))) {
            comparing = "менее";
        } else if (operation.equals(OPERATIONS.get(2))) {
            comparing = "равным";
        }
        return ResponseEntity.ok("Количество носков цвета " + color.toLowerCase() +
                " с процентным содержанием хлопка " + comparing + " " + cottonPart +
                "% составляет " + service.getSocksQuantityInfo(color, operation, cottonPart) + " шт.");
    }

    /**
     * Перехватывает исключения, возникающие при работе приложения для передачи сообщения данных исключений в качестве ответа на запрос
     */
    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    /**
     * Вспомогательный метод для проверки некорректности передаваемых параметров поступаемых или отпускаемых носков
     * @param socks поступаемые или отпускаемые носки
     * @return true или false
     */
    private boolean socksNotValid(SocksDto socks) {
        return socks == null ||
                socks.getColor() == null ||
                socks.getColor().isEmpty() ||
                socks.getColor().isBlank() ||
                socks.getCottonPart() < 0 ||
                socks.getCottonPart() > 100 ||
                socks.getQuantity() < 0;
    }
}
