package org.acme;

import ai.timefold.solver.core.api.solver.SolverJob;
import ai.timefold.solver.core.api.solver.SolverManager;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.acme.domain.Rivalry;
import org.acme.domain.Team;
import org.acme.domain.TournamentSchedule;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Path("/solve")
public class SolverResource {

    @Inject
    SolverManager<TournamentSchedule, UUID> solverManager;

    @GET
    public String solve() {
        UUID problemId = UUID.randomUUID();
        List<Team> teams = List.of(
                new Team("A"),
                new Team("B"),
                new Team("C"),
                new Team("D")
        );

        List<Rivalry> rivalries = List.of(
                new Rivalry(teams.get(0), teams.get(1)),
                new Rivalry(teams.get(0), teams.get(2))
        );

        TournamentSchedule problem = new TournamentSchedule(teams, rivalries);
        // Submit the problem to start solving
        SolverJob<TournamentSchedule, UUID> solverJob = solverManager.solve(problemId, problem);
        TournamentSchedule solution;
        try {
            // Wait until the solving ends
            solution = solverJob.getFinalBestSolution();
        } catch (InterruptedException | ExecutionException e) {
            throw new IllegalStateException("Solving failed.", e);
        }
        return solution.toString();
    }

}
