package com.ozonetel.occ.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Sudhakar
 */
@Entity
@Table (name="location")
public class Location extends BaseObject implements Serializable {

    private Long id;
    private String name;
    private User user;

    /** default constructor */
    public Location() {
    }

    /** full constructor */
    public Location(String name, User user) {
        this.name = name;
        this.user = user;
    }

    @Id @GeneratedValue (strategy=GenerationType.AUTO)
    @Column (name="id")
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @Column (name="name", length=255, nullable=false)
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne (targetEntity=User.class)
    @JoinColumn (name="user_id")
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder().append("Location [")
                .append("name = '").append(name).append("', ")
                .append("user = '").append(user != null ? user.getFullName() : "").append("' ");
        return  sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
	if (o == null || getClass() != o.getClass()) return false;
	if (!(o instanceof Location)) return false;
	final Location location = (Location) o;

	return (id != null ? !id.equals(location.id) : location.id != null)
            && (name != null ? !name.equals(location.name) : location.name != null)
            && (user != null ? !user.equals(location.user) : location.user != null);
    }

    @Override
    public int hashCode() {
        int result = 17;

	result = 37 * result + (id != null ? id.hashCode() : 0);
	result = 37 * result + (name != null ? name.hashCode() : 0);
	result = 37 * result + (user != null ? user.hashCode() : 0);

	return result;
    }
}