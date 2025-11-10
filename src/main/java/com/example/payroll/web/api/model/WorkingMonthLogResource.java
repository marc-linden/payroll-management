package com.example.payroll.web.api.model;

import java.time.Instant;
import com.example.payroll.database.entity.WorkingLogSource;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkingMonthLogResource {
  private Long insertEmployeeId;
  private Instant insertTimestamp;
  private WorkingLogSource workingLogSource;
  @NotNull
  private Long employeeId;
  @NotNull
  @DecimalMin("1970")
  private Integer year;
  @NotNull
  @DecimalMin("0")
  @DecimalMax("11")
  private Integer month;
  @NotNull
  @PositiveOrZero
  private Integer workingHours;
}
