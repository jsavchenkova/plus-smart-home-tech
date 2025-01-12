package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Embeddable
public class Address {

    @Column
    private String country;
    @Column
    private String city;
    @Column
    private String street;
    @Column
    private String house;
    @Column
    private String flat;


}