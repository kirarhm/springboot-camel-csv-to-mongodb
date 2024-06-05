package com.camel.example.model;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "studentdata")
@CsvRecord(separator = ",", skipField = true, skipFirstLine = true)
public class Student {
    @Id
    @DataField(pos = 1)
    private String id;

    @DataField(pos = 2)
    private String name;

    @DataField(pos = 3)
    private String age;
}