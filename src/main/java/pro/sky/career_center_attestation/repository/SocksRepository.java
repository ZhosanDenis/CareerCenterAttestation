package pro.sky.career_center_attestation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.career_center_attestation.model.SocksColor;
import pro.sky.career_center_attestation.model.SocksEntity;

import java.util.Optional;

public interface SocksRepository extends JpaRepository<SocksEntity, Long> {
    Optional<SocksEntity> findByColorAndCottonPart(SocksColor color, int cottonPart);

    Optional<SocksEntity> findByColorAndCottonPartIsGreaterThan(SocksColor color, int cottonPart);

    Optional<SocksEntity> findByColorAndCottonPartIsLessThan(SocksColor color, int cottonPart);
}
