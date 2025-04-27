package com.aryan.userservice.model;


import com.aryan.userservice.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Table(name = "users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
	private String password;
	private String email;
	private UserRole role; // (UserRole.Customer, UserRole.Admin)

	@Lob
	@Column(columnDefinition = "longblob")
	private byte[] img;
}
