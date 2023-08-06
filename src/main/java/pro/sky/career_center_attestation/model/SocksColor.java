package pro.sky.career_center_attestation.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SocksColor {
    BLACK("black"),
    WHITE("white"),
    RED("red"),
    BLUE("blue"),
    YELLOW("yellow"),
    GREY("grey"),
    GREEN("green"),
    PINK("pink"),
    STRIPED("striped"),
    PATTERNED("patterned");

    private final String color;

    public static SocksColor findByStringColor(String color) {
        for (SocksColor socksColor : values()) {
            if (socksColor.getColor().equals(color)) {
                return socksColor;
            }
        }
        return null;
    }
}
