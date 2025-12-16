package dk.dagensoel.dtos;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class ResultDTO {

    public Long beerId;
    public String beerName;
    public int totalPoints;

    public ResultDTO(Long beerId, String beerName, int totalPoints) {
        this.beerId = beerId;
        this.beerName = beerName;
        this.totalPoints = totalPoints;
    }
}
