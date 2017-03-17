package com.thoughtworks.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by pyang on 15/03/2017.
 */
@Entity
public class Computer {

    @Id
    @GeneratedValue
    private Integer id;

    private Integer count;

    @Temporal(TemporalType.DATE)
    private Date created;

    public Computer() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
