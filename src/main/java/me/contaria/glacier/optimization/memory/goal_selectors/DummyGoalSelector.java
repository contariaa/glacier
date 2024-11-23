package me.contaria.glacier.optimization.memory.goal_selectors;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.PrioritizedGoal;

import java.util.stream.Stream;

public class DummyGoalSelector extends GoalSelector {
    public static final DummyGoalSelector INSTANCE = new DummyGoalSelector();

    private DummyGoalSelector() {
        super(null);
    }

    @Override
    public void add(int priority, Goal goal) {
    }

    @Override
    public void remove(Goal goal) {
    }

    @Override
    public void tick() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Stream<PrioritizedGoal> getRunningGoals() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void disableControl(Goal.Control control) {
    }

    @Override
    public void enableControl(Goal.Control control) {
    }

    @Override
    public void setControlEnabled(Goal.Control control, boolean enabled) {
    }
}
