package http.json.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {

    @Override
    public void write(final JsonWriter writer, Duration dur) throws IOException {
        writer.value(dur.toString());
    }

    public Duration read(JsonReader reader) throws IOException {
        return Duration.parse(reader.nextString());
    }
}
