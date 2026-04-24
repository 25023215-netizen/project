package com.nhom4project.auctionweb.data.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ART")
public class Art extends Item {
}
