package org.acme.domain;

public class Rivalry {

    private Team teamA;
    private Team teamB;

    public Rivalry(Team teamA, Team teamB) {
        this.teamA = teamA;
        this.teamB = teamB;
    }

    public Team getTeamA() {
        return teamA;
    }

    public Team getTeamB() {
        return teamB;
    }
}
