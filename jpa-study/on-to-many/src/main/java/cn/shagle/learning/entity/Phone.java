package cn.shagle.learning.entity;

import javax.persistence.*;

@Entity
public class Phone {
    @Id
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OWNER_ID")
    private Employee owner;

}