package com.sammidev.student;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@JsonIgnoreProperties(allowGetters = true)
public class Student {

    @Id
    private UUID id;

    @NotBlank
    @Column(nullable = false, length = 30)
    private String name;

    @NotBlank
    @Column(nullable = false, unique = true, length = 10)
    private String nim;

    @Column(unique = true)
    private String email;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String phone;
}