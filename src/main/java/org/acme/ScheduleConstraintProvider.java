package org.acme;

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.ConstraintProvider;
import org.acme.domain.Match;
import org.acme.domain.Rivalry;

import static ai.timefold.solver.core.api.score.stream.Joiners.equal;

public class ScheduleConstraintProvider implements ConstraintProvider {
    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                teamCannotPlayAgainstItself(constraintFactory),
                twoTeamsCanOnlyFaceOneTime(constraintFactory),
                teamOnlyOnceInRound(constraintFactory),
                // Soft constraints
                eachTeamShouldNotPlayManyConsecutiveGamesAtHomeOrAway(constraintFactory),
                rivalTeamsShouldNotFaceOnFirstRound(constraintFactory)
        };
    }


    private Constraint teamCannotPlayAgainstItself(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Match.class)
                .filter(match -> match.getHomeTeam() == match.getAwayTeam())
                .penalize(HardSoftScore.ofHard(1))
                .asConstraint("A team cannot play against itself");
    }

    private Constraint teamOnlyOnceInRound(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEachUniquePair(Match.class, equal(Match::getRound))
                .filter((match1, match2) -> match1.getHomeTeam() == match2.getHomeTeam()
                        || match1.getAwayTeam() == match2.getHomeTeam()
                        || match1.getHomeTeam() == match2.getAwayTeam()
                        || match1.getAwayTeam() == match2.getAwayTeam()
                )
                .penalize(HardSoftScore.ofHard(1))
                .asConstraint("Each team can only play once per round");
    }

    private Constraint twoTeamsCanOnlyFaceOneTime(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEachUniquePair(Match.class)
                .filter((match1, match2) -> (match1.getHomeTeam() == match2.getHomeTeam() &&
                        match1.getAwayTeam() == match2.getAwayTeam()) ||
                        (match1.getHomeTeam() == match2.getAwayTeam() &&
                                match1.getAwayTeam() == match2.getHomeTeam())
                )
                .penalize(HardSoftScore.ofHard(1))
                .asConstraint("Two teams can only face each other one time");
    }

    private Constraint eachTeamShouldNotPlayManyConsecutiveGamesAtHomeOrAway(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEachUniquePair(Match.class)
                .filter(
                        (match1, match2) -> (match1.getRound() == match2.getRound() + 1 &&
                                (match1.getHomeTeam() == match2.getHomeTeam() || match1.getAwayTeam() == match2.getAwayTeam())) ||
                                (match1.getRound() + 1 == match2.getRound() &&
                                        (match1.getHomeTeam() == match2.getHomeTeam() || match1.getAwayTeam() == match2.getAwayTeam()))
                )
                .penalize(HardSoftScore.ofSoft(1))
                .asConstraint("A team should not play two consecutive house or away games");
    }

    private Constraint rivalTeamsShouldNotFaceOnFirstRound(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Match.class)
                .filter(match -> match.getRound() == 0)
                .join(Rivalry.class)
                .filter((match, rivalry) ->
                        (match.getHomeTeam() == rivalry.getTeamA() && match.getAwayTeam() == rivalry.getTeamB()) ||
                                (match.getHomeTeam() == rivalry.getTeamB() && match.getAwayTeam() == rivalry.getTeamA())
                )
                .penalize(HardSoftScore.ofSoft(1))
                .asConstraint("Rival teams should not face on first round");
    }

}
