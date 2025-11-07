package com.example.payroll.database.entity;

import java.util.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;

@Entity
@Getter
public class Employer extends BaseEntity {
  @Column
  private String companyName;
  @Column
  private String taxId;
  @Column
  private String email;
  @Column
  private String country;
  @Column
  private String street;
  @Column
  private String city;
  @Column
  private String postalCode;
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "employer")
  private List<Employee> employees = new ArrayList<>();

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
