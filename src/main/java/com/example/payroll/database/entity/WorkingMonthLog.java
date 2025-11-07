package com.example.payroll.database.entity;

import java.time.Instant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;

@Entity
@Getter
public class WorkingMonthLog extends BaseEntity {
  @Column
  private Long insertEmployeeId;
  @Column
  private Instant insertTimestamp;
  @Column
  @Enumerated(EnumType.STRING)
  private WorkingLogSource workingLogSource;
  @ManyToOne(optional = false)
  @JoinColumn(name = "workingLogEmployee_id", nullable = false)
  private Employee workingLogEmployee;
  @Column
  private Integer workingLogYear;
  @Column
  private Integer workingLogMonth; // zero-based month
  @Column
  private Integer workingLogTimeInHours;

  /**
   * Since we do not have transient entities, we solely rely on the id to compare
   *
   * @param o the object to compare
   * @return true if the id is the same
   */
  @Override
  public boolean equals(final Object o) {
    return super.equals(o);
  }

  /**
   * Since we do not have transient entities, we solely rely on the id to compare
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }

}
