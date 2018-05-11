package eu.jirifrank.springler.api.request;

import eu.jirifrank.springler.api.enums.ServiceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogRequest {
    @NotNull
    private ServiceType serviceType;

    @NotNull
    private String message;
}
