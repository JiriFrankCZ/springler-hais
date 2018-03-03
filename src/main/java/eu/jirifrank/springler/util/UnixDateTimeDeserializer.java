package eu.jirifrank.springler.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class UnixDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    private static final ZoneId ZONE_ID = ZoneId.of("Europe/Prague");

    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String unixTimestamp = parser.getText().trim();

        return Instant
                .ofEpochSecond(Long.valueOf(unixTimestamp))
                .atZone(ZONE_ID)
                .toLocalDateTime();
    }
}
