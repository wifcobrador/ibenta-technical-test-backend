package au.com.ibenta.test.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class User {
    @NotNull(message = "First name cannot be missing or empty")
    private String firstName;
    @NotNull(message = "Last name cannot be missing or empty")
    private String lastName;
    private String email;
    private String password;
}
