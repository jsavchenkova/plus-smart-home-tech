package ru.yandex.practicum.serialize.hub;

import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.exception.SerializationException;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HexFormat;

public class ScenarioRemovedAvroSerializer implements Serializer<ScenarioAddedEventAvro> {
    private static final Logger log = LoggerFactory.getLogger(ScenarioRemovedAvroSerializer.class);
    private static final HexFormat hexFormat = HexFormat.ofDelimiter(":");


    @Override
    public byte[] serialize(String topic, ScenarioAddedEventAvro event) {
        if (event == null) {
            return null;
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            DatumWriter<ScenarioAddedEventAvro> datumWriter = new SpecificDatumWriter<>(ScenarioAddedEventAvro.class);
            BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null);

            datumWriter.write(event, encoder);

            encoder.flush();

            byte[] bytes = outputStream.toByteArray();

            log.info("Данные сериализованы в формат Avro:\n{}", hexFormat.formatHex(bytes));

            return bytes;

        } catch (IOException e) {
            throw new SerializationException("Ошибка сериализации", e);
        }

    }

}