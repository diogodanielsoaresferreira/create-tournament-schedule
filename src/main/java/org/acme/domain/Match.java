package org.acme.domain;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import ai.timefold.solver.core.api.domain.variable.PlanningVariable;

@PlanningEntity
public class Match {

    @PlanningId
    private Long id;

    @PlanningVariable
    private Team homeTeam;

    @PlanningVariable
    private Team awayTeam;

    private int round;

    public Match() {
    }

    public Match(Long id, int round) {
        this.id = id;
        this.round = round;
    }

    public Long getId() {
        return id;
    }
    public Team getHomeTeam() {
        return homeTeam;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public int getRound() {
        return round;
    }
}
