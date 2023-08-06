package pro.sky.career_center_attestation;

import org.assertj.core.api.Assertions;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pro.sky.career_center_attestation.controller.SocksController;
import pro.sky.career_center_attestation.model.SocksColor;
import pro.sky.career_center_attestation.model.SocksEntity;
import pro.sky.career_center_attestation.repository.SocksRepository;
import pro.sky.career_center_attestation.service.SocksMapper;
import pro.sky.career_center_attestation.service.SocksService;
import pro.sky.career_center_attestation.dto.SocksDto;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = SocksController.class)
class CareerCenterAttestationApplicationTests {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private SocksRepository repository;
    @SpyBean
    private SocksMapper mapper;
    @SpyBean
    private SocksService service;
    @InjectMocks
    private SocksController controller;

    private final long whiteSocksId = 1L;
    private final String whiteSocksColor = "white";
    private final int whiteSocksCottonPart = 80;
    private final int whiteSocksQuantity = 20;
    private final long pinkSocksId = 2L;
    private final String pinkSocksColor = "";
    private final int pinkSocksCottonPart = 60;
    private final int pinkSocksQuantity = -30;
    private final String moreThanOperation = "moreThan";
    private final String lessThanOperation = "lessThan";
    private final String equalOperation = "equal";

    private SocksDto createWhiteSocksDto() {
        SocksDto whiteSocksDto = new SocksDto();
        whiteSocksDto.setId(whiteSocksId);
        whiteSocksDto.setColor(whiteSocksColor);
        whiteSocksDto.setCottonPart(whiteSocksCottonPart);
        whiteSocksDto.setQuantity(whiteSocksQuantity);
        return whiteSocksDto;
    }

    private SocksDto createPinkSocksDto() {
        SocksDto pinkSocksDto = new SocksDto();
        pinkSocksDto.setId(pinkSocksId);
        pinkSocksDto.setColor(pinkSocksColor);
        pinkSocksDto.setCottonPart(pinkSocksCottonPart);
        pinkSocksDto.setQuantity(pinkSocksQuantity);
        return pinkSocksDto;
    }

    private JSONObject createSocksJSON(SocksDto dto) throws Exception {
        JSONObject socksObject = new JSONObject();
        socksObject.put("id", dto.getId());
        socksObject.put("color", dto.getColor());
        socksObject.put("cottonPart", dto.getCottonPart());
        socksObject.put("quantity", dto.getQuantity());
        return socksObject;
    }

    @Test
    void contextLoads() throws Exception {
        Assertions.assertThat(controller).isNotNull();
    }

    @Test
    void socksIncomeTest() throws Exception {
        SocksDto whiteSocksDto = createWhiteSocksDto();

        JSONObject whiteSocksObject = createSocksJSON(whiteSocksDto);

        SocksEntity whiteSocksEntity = mapper.updateSocksEntity(new SocksEntity(), whiteSocksDto);

        when(repository.save(any(SocksEntity.class))).thenReturn(whiteSocksEntity);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/socks/income")
                        .content(whiteSocksObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Socks income"));
    }

    @Test
    void socksInvalidIncomeTest() throws Exception {
        SocksDto pinkSocksDto = createPinkSocksDto();

        JSONObject pinkSocksObject = createSocksJSON(pinkSocksDto);

        SocksEntity pinkSocksEntity = mapper.updateSocksEntity(new SocksEntity(), pinkSocksDto);

        when(repository.save(any(SocksEntity.class))).thenReturn(pinkSocksEntity);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/socks/income")
                        .content(pinkSocksObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Incorrect socks input parameters are presented. You need to check the color, cotton part or number of pairs."));
    }

    @Test
    void socksOutcomeTest() throws Exception {
        SocksDto whiteSocksDto = createWhiteSocksDto();

        JSONObject whiteSocksObject = createSocksJSON(whiteSocksDto);

        SocksEntity whiteSocksEntity = mapper.updateSocksEntity(new SocksEntity(), whiteSocksDto);

        when(repository.findByColorAndCottonPart(SocksColor.findByStringColor(whiteSocksColor), whiteSocksCottonPart)).thenReturn(Optional.of(whiteSocksEntity));
        when(repository.save(any(SocksEntity.class))).thenReturn(whiteSocksEntity);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/socks/outcome")
                        .content(whiteSocksObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Socks outgo"));
    }

    @Test
    void socksInvalidOutcomeTest() throws Exception {
        SocksDto pinkSocksDto = createPinkSocksDto();

        JSONObject pinkSocksObject = createSocksJSON(pinkSocksDto);

        SocksEntity pinkSocksEntity = mapper.updateSocksEntity(new SocksEntity(), pinkSocksDto);

        when(repository.save(any(SocksEntity.class))).thenReturn(pinkSocksEntity);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/socks/outcome")
                        .content(pinkSocksObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Incorrect socks input parameters are presented. You need to check the color, cotton part or number of pairs."));
    }

    @Test
    void getSocksQuantityMoreThanTest() throws Exception {
        SocksDto whiteSocksDto = createWhiteSocksDto();

        SocksEntity whiteSocksEntity = mapper.updateSocksEntity(new SocksEntity(), whiteSocksDto);

        when(repository.findByColorAndCottonPartIsGreaterThan(SocksColor.findByStringColor(whiteSocksColor), whiteSocksCottonPart)).thenReturn(Optional.of(whiteSocksEntity));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/socks" + "?color=" + whiteSocksColor + "&operation=" + moreThanOperation + "&cottonPart=" + whiteSocksCottonPart)
                        .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string("Количество носков цвета white с процентным содержанием хлопка более 80% составляет 20 шт."));
    }

    @Test
    void getSocksQuantityLessThanTest() throws Exception {
        SocksDto whiteSocksDto = createWhiteSocksDto();

        SocksEntity whiteSocksEntity = mapper.updateSocksEntity(new SocksEntity(), whiteSocksDto);

        when(repository.findByColorAndCottonPartIsLessThan(SocksColor.findByStringColor(whiteSocksColor), whiteSocksCottonPart)).thenReturn(Optional.of(whiteSocksEntity));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/socks" + "?color=" + whiteSocksColor + "&operation=" + lessThanOperation + "&cottonPart=" + whiteSocksCottonPart)
                        .accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().string("Количество носков цвета white с процентным содержанием хлопка менее 80% составляет 20 шт."));
    }

    @Test
    void getSocksQuantityEqualTest() throws Exception {
        SocksDto whiteSocksDto = createWhiteSocksDto();

        SocksEntity whiteSocksEntity = mapper.updateSocksEntity(new SocksEntity(), whiteSocksDto);

        when(repository.findByColorAndCottonPart(SocksColor.findByStringColor(whiteSocksColor), whiteSocksCottonPart)).thenReturn(Optional.of(whiteSocksEntity));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/socks" + "?color=" + whiteSocksColor + "&operation=" + equalOperation + "&cottonPart=" + whiteSocksCottonPart)
                        .accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().string("Количество носков цвета white с процентным содержанием хлопка равным 80% составляет 20 шт."));
    }
}
