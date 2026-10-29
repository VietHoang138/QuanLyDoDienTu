package com.example.demo.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;
import java.io.Serializable;

@Embeddable
@Data
public class CartId implements Serializable {
    private Integer idKh;
    private Integer idSp;
} 