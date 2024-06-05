//package com.camel.example.component;
//
//import java.util.List;
//
//import com.camel.example.model.Student;
//import com.camel.example.repository.StudentRepo;
//import org.apache.camel.Exchange;
//import org.apache.camel.builder.RouteBuilder;
//import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.env.Environment;
//import org.springframework.dao.DataAccessException;
//import org.springframework.stereotype.Component;
//
//@Component
//public class CsvToMongo extends RouteBuilder {
//
//    @Autowired
//    private StudentRepo studentRepo;
//
//    @Autowired
//    private Environment env;
//
//    @Override
//    public void configure() throws Exception {
//
//        onException(Exception.class).log("Error processing CSV file: ${exception.message}\n${exception.stacktrace}")
//                .handled(true).log("Error raised, moving file to Error Folder")
//                .to("file://" + env.getProperty("camel.errorfolder")).log("File moved to Error Folder").end()
//                .log("Attempting to move file back to source folder")
//                .to("file://" + env.getProperty("camel.sourcefolder")).log("File moved back to Source Folder").end();
//
//        from("file://" + env.getProperty("camel.sourcefolder")).routeId("fileRoute").log(" Process Started ")
//                .unmarshal(new BindyCsvDataFormat(Student.class)).process(this::processBulkUserDetails).end()
//                .log("File processed successfully: ${file:name}").process(this::prepareAndSaveProcessedFile)
//                .to("file://" + env.getProperty("camel.destinationfolder")).onException(Exception.class).handled(true)
//                .log("Error moving file: ${exception.message}").to("file://" + env.getProperty("camel.errorfolder"))
//                .end();
//    }
//
//    // processing method
//    private void processBulkUserDetails(Exchange exchange) {
//        List<Student> users = exchange.getIn().getBody(List.class);
//
//        for (Student userdata : users) {
//            try {
//                studentRepo.save(userdata);
//            } catch (DataAccessException e) {
//                e.getMessage();
//            }
//        }
//    }
//
//    // method for move processed file into another folder
//    private void prepareAndSaveProcessedFile(Exchange exchange) {
//        String processedFileName = "processed_" + exchange.getIn().getHeader(Exchange.FILE_NAME, String.class);
//        exchange.getIn().setHeader(Exchange.FILE_NAME, processedFileName);
//        List<Student> users = exchange.getIn().getBody(List.class);
//        StringBuilder processedContent = new StringBuilder();
//        for (Student user : users) {
//            processedContent.append(user.toString()).append("\n");
//        }
//        exchange.getIn().setBody(processedContent.toString().getBytes());
//    }
//}