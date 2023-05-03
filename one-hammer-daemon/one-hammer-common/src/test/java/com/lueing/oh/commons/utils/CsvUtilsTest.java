package com.lueing.oh.commons.utils;

import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.writer.CsvWriter;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

@Ignore
public class CsvUtilsTest {
    @Test
    public void testWriterReader() throws Exception {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        CsvWriter.CsvWriterBuilder writer = CsvUtils.createCsvWriter();

        CsvWriter csvWriter = writer.build(new OutputStreamWriter(buf));
        csvWriter.writeRow("o\r\nne", "two");
        csvWriter.writeRow("th\r\nee", "fo\r\nur");
        csvWriter.close();


        CsvReader.CsvReaderBuilder reader = CsvUtils.createCsvReader();
        CsvReader csvReader = reader.build(new InputStreamReader(new ByteArrayInputStream(buf.toByteArray())));

        csvReader.forEach(System.out::println);
    }

}
