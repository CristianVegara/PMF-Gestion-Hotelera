package com.gestionmediterraneo.hotel.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "habitacion")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
   
    @Column(name = "numero", nullable = false, unique = true)
    private String number;

    @Column(name = "tipo", nullable = false)
    private String type;

    @Column(name = "estado")
    private String status;

    @Column(name = "precio_por_noche")
    private Double price;

    public Room() {}

    public Room(String number, String type, String status, Double price) {
        this.number = number;
        this.type = type;
        this.status = status;
        this.price = price;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
}