package com.camel.example.component;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v24.message.ORU_R01;
import ca.uhn.hl7v2.model.v24.segment.PID;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.hl7.fhir.dstu3.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.ProtocolException;


@Component
public class HL7ToJsonConverter extends RouteBuilder {

    @Autowired
    private Environment env;

    @Override
    public void configure() throws Exception {
        from("file://" + env.getProperty("camel.sourcefolder")).routeId("fhir-example")
                .onException(ProtocolException.class)
                .handled(true)
                .log(LoggingLevel.ERROR, "Error connecting to FHIR server with URL:{{serverUrl}}, please check the application.properties file ${exception.message}")
                .end()
                .onException(HL7Exception.class)
                .handled(true)
                .log(LoggingLevel.ERROR, "Error unmarshalling ${file:name} ${exception.message}")
                .end()
                .log("Converting ${file:name}")
                // unmarshall file to hl7 message
                .unmarshal().hl7()
                // very simple mapping from a HLV2 patient to dstu3 patient
                .process(exchange -> {
                    ORU_R01 msg = exchange.getIn().getBody(ORU_R01.class);
                    final PID pid = msg.getPATIENT_RESULT().getPATIENT().getPID();
                    String surname = pid.getPatientName()[0].getFamilyName().getFn1_Surname().getValue();
                    String name = pid.getPatientName()[0].getGivenName().getValue();
                    String patientId = msg.getPATIENT_RESULT().getPATIENT().getPID().getPatientID().getCx1_ID().getValue();
                    Patient patient = new Patient();
                    patient.addName().addGiven(name);
                    patient.getNameFirstRep().setFamily(surname);
                    patient.setId(patientId);
                    exchange.getIn().setBody(patient);
                })
                // marshall to JSON for logging
                .marshal().fhirJson("{{fhirVersion}}")
                .convertBodyTo(String.class)
                .log("Inserting Patient: ${body}")
                // create Patient in our FHIR server
                // .to("fhir://create/resource?inBody=resourceAsString&serverUrl={{serverUrl}}&fhirVersion={{fhirVersion}}")
                // log the outcome
                .log("Patient created successfully: ${body}");


    }
}
