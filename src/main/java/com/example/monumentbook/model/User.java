package com.example.monumentbook.model;

import com.example.monumentbook.enums.Role;
import com.example.monumentbook.model.dto.UserDto;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@ToString(exclude = "creditCard")
@Table(name = "user_tb")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "name")
    private String name;
    private String phoneNumber;
    private String coverImg;
    @Column(unique = true)
    private String email;
    private String address;
    private String password;
    @OneToMany (mappedBy="user", fetch = FetchType.LAZY )
//    @JoinColumn(name = "creditCardId")
    private List<CreditCard> creditCard;
    @Enumerated(EnumType.STRING)
    private Role role;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role != null) {
            return Collections.singletonList(new SimpleGrantedAuthority(this.role.toString().toUpperCase()));
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public String getUsername() {
        // email in our case
        return email ;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    public UserDto toDto(){
       return new UserDto(this.id,this.name,this.phoneNumber,this.email, this.coverImg, this.address);
    }
}