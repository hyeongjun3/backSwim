package com.example.backswim.member.entity;

import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer seq;

    @NotNull
    private String userEmail;

    @Column(length = 1000)
    private String emailTitle;

    @Column(length = 1000)
    private String emailContent;

    @NotNull
    private Integer userId;
}