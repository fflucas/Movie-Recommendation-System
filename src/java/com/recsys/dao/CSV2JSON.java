package com.recsys.dao;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

public class CSV2JSON {

    public void csv2json(String input, String output) throws IOException {
        File fInput = new File(input);
        File fOutput = new File(output);

        CsvSchema csvSchema = CsvSchema.builder().setUseHeader(true).build();
        CsvMapper csvMapper = new CsvMapper();

        // Read data from CSV file
        List<Object> readAll = csvMapper.readerFor(Map.class).with(csvSchema).readValues(fInput).readAll();

        ObjectMapper mapper = new ObjectMapper();

        // Write JSON formatted data to output.json file
        mapper.writerWithDefaultPrettyPrinter().writeValue(fOutput, readAll);

        // Write JSON formatted data to stdout
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(readAll));
    }
}