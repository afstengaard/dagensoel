package dk.dagensoel.dtos;

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
public class VoteRequestDTO {
    public Long favoriteBeerId;
    public Long secondBeerId;
}
