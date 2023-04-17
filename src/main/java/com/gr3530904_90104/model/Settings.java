package com.gr3530904_90104.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Table(name = "settings")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Settings {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "settings_id_seq")
    @SequenceGenerator(name = "settings_id_seq", sequenceName = "settings_id_seq", allocationSize = 1)
    Integer id;

    @Column(name = "user_id")
    Integer userId;

    @Column(name = "user_login")
    String userLogin;

    @Column(name = "host")
    String host;

    @Column(name = "port")
    Integer port;

    @Column(name = "\"password\"")
    String password;
}
