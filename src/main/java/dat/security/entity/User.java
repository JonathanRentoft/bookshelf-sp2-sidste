package dat.security.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Getter
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "user_name", length = 25, nullable = false, unique = true)
    private String userName;

    @Column(name = "user_password", nullable = false)
    private String userPassword;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = {
            @JoinColumn(name = "user_name", referencedColumnName = "user_name")}, inverseJoinColumns = {
            @JoinColumn(name = "role_name", referencedColumnName = "role_name")})
    private Set<Role> roles = new HashSet<>();

    public User(String userName, String userPassword) {
        this.userName = userName;
        this.userPassword = BCrypt.hashpw(userPassword, BCrypt.gensalt());
    }

    public boolean verifyPassword(String pw) {
        return BCrypt.checkpw(pw, this.userPassword);
    }

    public void addRole(Role role) {
        roles.add(role);
    }
}