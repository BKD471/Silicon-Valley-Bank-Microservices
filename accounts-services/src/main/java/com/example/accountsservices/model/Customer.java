package com.example.accountsservices.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer extends Audit{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;

    @Column(nullable = false,name="cust_name")
    private String name;

    @Column(name="dob",nullable = false)
    private LocalDate DateOfBirth;

    private int age;

    @Email
    @Column(nullable = false,unique = true)
    private String email;

    @Column(nullable = false,length = 512)
    private String password;

    @Column(nullable = false,unique = true,name="mobile_num")
    private String phoneNumber;

    @Column(name = "adhar_num",unique = true, nullable = false)
    private String adharNumber;

    @Column(name = "pan_num",unique = true, nullable = false)
    private String panNumber;

    @Column(name = "voter_id",unique = true)
    private String voterId;

    @Column(name = "driving_license",unique = true)
    private String drivingLicense;

    @Column(name = "passport",unique = true)
    private String passportNumber;

    @Column(name = "img_name",length = 256)
    private String imageName;

    @Column(name = "address",length = 1000)
    private String address;

    @OneToMany(mappedBy = "customer",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Accounts> accounts=new ArrayList<>();
}
