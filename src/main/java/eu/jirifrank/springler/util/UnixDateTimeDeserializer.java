package eu.jirifrank.springler.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class UnixDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String unixTimestamp = parser.getText().trim();

        return Instant
                .ofEpochSecond(Long.valueOf(unixTimestamp))
                .atZone(TimeUtils.ZONE_ID)
                .toLocalDateTime();
    }
}
