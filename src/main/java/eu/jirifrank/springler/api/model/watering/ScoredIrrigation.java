package eu.jirifrank.springler.api.model.watering;

import eu.jirifrank.springler.api.entity.Irrigation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ScoredIrrigation {
    private Irrigation irrigation;
    private Double score;
}
