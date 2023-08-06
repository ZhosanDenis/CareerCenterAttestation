package pro.sky.career_center_attestation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.sql.SQLException;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableJpaRepositories
public class CareerCenterAttestationApplication {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        SpringApplication.run(CareerCenterAttestationApplication.class, args);
    }
}
