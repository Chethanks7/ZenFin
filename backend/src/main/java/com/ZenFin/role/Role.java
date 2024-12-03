package com.ZenFin.role;


import com.ZenFin.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Table(name = "roles") // Specifies the table name in the database.
@Builder
@AllArgsConstructor // Generates a constructor with all arguments.
@NoArgsConstructor // Generates a no-argument constructor.
@Entity// Indicates that this class is a JPA entity.
@EntityListeners(AuditingEntityListener.class) // Enables auditing features for this entity.
public class Role implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L; // Serial version UID for serialization.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Automatically generates the ID.
    private Integer id; // Unique identifier for the role.

    @Column(unique = true, nullable = false) // Specifies that the name must be unique and cannot be null.
    private String name; // Name of the role (e.g., "USER", "ADMIN").

    @ManyToMany(fetch = FetchType.EAGER, // Defines a many-to-many relationship with the User entity.
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, // Specifies cascade operations.
            mappedBy = "roles") // Specifies that this side is the inverse of the relationship.
    @JsonIgnore // Prevents serialization of the users list to avoid circular references.
    private List<User> users; // List of users associated with this role.

    @CreatedDate // Automatically sets the creation time.
    @Column(nullable = false, updatable = false) // Specifies that this field cannot be null or updated.
    private LocalDateTime createdTime; // Timestamp of when the role was created.

    @LastModifiedDate // Automatically updates the last modified time common.
    @Column(insertable = false) // Specifies that this field should not be set during insertion.
    private LocalDateTime lastUpdatedTime; // Timestamp of the last update to the role.
}