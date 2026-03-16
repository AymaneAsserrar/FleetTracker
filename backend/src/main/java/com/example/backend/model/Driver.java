package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "drivers")
@Getter
@Setter
@NoArgsConstructor
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long age;

    @Column(nullable = false)
    private Boolean isManager;

    @Column(nullable = false)
    private String licenceNumber;

    @Column(nullable = false)
    private String contactPhone;

}
