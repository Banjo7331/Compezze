package com.cmze.spi.helpers.room;

import lombok.Data;

@Data
public class LeaderboardEntryDto {
    private String username;
    private int score;
    private int rank;

    public LeaderboardEntryDto(String username, int score, int rank) {
        this.username = username;
        this.score = score;
        this.rank = rank;
    }
}
