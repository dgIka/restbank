package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Role extends AbstractEntity<Long>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name")
    private RoleName name;

    @Override
    public Long getId() {return id;}
}
