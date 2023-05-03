package com.lueing.oh.commons.utils;


import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.writer.CsvWriter;
import de.siegmar.fastcsv.writer.LineDelimiter;
import de.siegmar.fastcsv.writer.QuoteStrategy;

public class CsvUtils {
    public static CsvWriter.CsvWriterBuilder createCsvWriter() {
        return CsvWriter.builder()
                .fieldSeparator('\u0001')
                .lineDelimiter(LineDelimiter.CRLF)
                .quoteCharacter('"')
                .quoteStrategy(QuoteStrategy.EMPTY);
    }

    public static CsvReader.CsvReaderBuilder createCsvReader() {
        return CsvReader.builder()
                .fieldSeparator('\u0001')
                .quoteCharacter('"')
                .commentCharacter('#');
    }
}
