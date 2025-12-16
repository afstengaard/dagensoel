package dk.dagensoel.dtos;

import dk.dagensoel.entities.AdminUser;
import lombok.*;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUserDTO {
    public Long id;
    public String username;
}

