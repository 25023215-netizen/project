package com.nhom4project.auctionweb.data.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "Items")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "item_type", discriminatorType = DiscriminatorType.STRING)
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class Item extends BaseEntity {
    private String name;
    private String description;
    private Double startingPrice;
    private Double currentPrice;
}
