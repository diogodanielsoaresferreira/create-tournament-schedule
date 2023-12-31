package org.acme.domain;

import ai.timefold.solver.core.api.domain.solution.PlanningEntityCollectionProperty;
import ai.timefold.solver.core.api.domain.solution.PlanningScore;
import ai.timefold.solver.core.api.domain.solution.PlanningSolution;
import ai.timefold.solver.core.api.domain.solution.ProblemFactCollectionProperty;
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeProvider;
import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.groupingBy;

@PlanningSolution
public class TournamentSchedule {

    @PlanningEntityCollectionProperty
    private List<Match> matches;

    @ValueRangeProvider
    @ProblemFactCollectionProperty
    private List<Team> teams;

    @ProblemFactCollectionProperty
    private List<Rivalry> rivalries;

    @PlanningScore
    private HardSoftScore score;

    public TournamentSchedule() {
    }

    public TournamentSchedule(List<Team> teams, List<Rivalry> rivalries) {
        this.matches = generateMatches(teams);
        this.teams = teams;
        this.rivalries = rivalries;
    }

    private List<Match> generateMatches(List<Team> teams) {
        int numberOfRounds = (teams.size() % 2 == 0) ? teams.size() - 1 : teams.size();
        int gamesPerRound =  teams.size() / 2;
        List<Match> matches = new ArrayList<>();
        int id = 0;
        for(int round: IntStream.range(0, numberOfRounds).boxed().toList()) {
            for(int game: IntStream.range(0, gamesPerRound).boxed().toList()) {
                matches.add(new Match((long) id++, round));
            }
        }
        return matches;
    }

    public List<Match> getMatches() {
        return matches;
    }

    public List<Rivalry> getRivalries() {
        return rivalries;
    }

    public HardSoftScore getScore() {
        return score;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        matches.stream().collect(Collectors.groupingBy(Match::getRound))
                .forEach((round, matches) -> {
            result.append("Round ").append(round).append(": \n");
            String roundMatchesString = matches.stream()
                    .map(match -> "\t" + match.getHomeTeam().getName() + " vs " + match.getAwayTeam().getName())
                    .collect(Collectors.joining("\n"));
            result.append(roundMatchesString).append("\n\n");
        });
        return result.toString();
    }
}
