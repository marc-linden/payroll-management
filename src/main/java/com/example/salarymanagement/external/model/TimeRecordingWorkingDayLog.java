package com.example.salarymanagement.external.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TimeRecordingWorkingDayLog {
  private String capturingTimestamp;
  private String employeeId;
  private String employeeFirstName;
  private String employeeLastName;
  private String employeeEmail;
  private String employeeLocationZip;
  private String employeeLocationStreet;
  private String employeeLocationCity;
  private String employerId;
  private String employerCompanyName;
  private String employerCompanyTaxId;
  private String employerCompanyLocationZip;
  private String employerCompanyLocationStreet;
  private String employerCompanyLocationCity;
  private String logYear;
  private String logMonth;
  private String logTimeHours;
}
