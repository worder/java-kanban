package http.json.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

    @Override
    public void write(final JsonWriter writer, LocalDateTime ldt) throws IOException {
        if (ldt != null) {
            writer.value(ldt.format(DateTimeFormatter.ISO_DATE_TIME));
        } else {
            writer.nullValue();
        }
    }

    public LocalDateTime read(JsonReader reader) throws IOException {
        return LocalDateTime.parse(reader.nextString(), DateTimeFormatter.ISO_DATE_TIME);
    }
}
