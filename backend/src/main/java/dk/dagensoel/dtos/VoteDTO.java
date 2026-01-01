package dk.dagensoel.dtos;

import dk.dagensoel.entities.VoteType;
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
public class VoteDTO {
    public Long favoriteBeerId;
    public Long secondBeerId;
}
