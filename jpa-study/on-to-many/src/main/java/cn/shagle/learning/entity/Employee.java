package cn.shagle.learning.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class Employee {
    @Id
    @Column(name = "EMP_ID")
    private long id;
    @OneToMany(mappedBy = "owner")
    private List<Phone> phones;
}