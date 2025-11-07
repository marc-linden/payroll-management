package com.example.payroll.database.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;

@Entity
@Getter
public class Employee extends BaseEntity {
  @Column
  private String firstName;
  @Column
  private String lastName;
  @Column
  private String email;
  @Column
  private String street;
  @Column
  private String city;
  @Column
  private String postalCode;
  @Column
  @Enumerated(EnumType.STRING)
  private Gender gender;
  @Column
  private String phone;
  @Column
  private String salutation;
  @ManyToOne(optional = false)
  @JoinColumn(name = "employer_id", nullable = false)
  private Employer employer;

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
