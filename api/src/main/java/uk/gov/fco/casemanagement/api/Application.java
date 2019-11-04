package uk.gov.fco.casemanagement.api;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import uk.gov.fco.casemanagement.common.config.MessageQueueConfig;

@SpringBootApplication
@Import({MessageQueueConfig.class})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Case Management API")
                        .version(getClass().getPackage().getImplementationVersion())
                        .description("")
                        .license(new License()
                                .name("MIT")
                                .url("https://raw.githubusercontent.com/CautionYourBlast/case-management/master/LICENSE")));
    }
}
