/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.extra.entity.components;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.time.TimerAction;
import javafx.util.Duration;

/**
 * Removes an entity from the world after a certain duration.
 * Useful for special effects or temporary entities.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class ExpireCleanComponent extends Component {

    private Duration expire;

    private boolean animate = false;

    /**
     * The expire duration timer starts when the entity is attached to the world,
     * so it does not start immediately when this component is created.
     *
     * @param expire the duration after entity is removed from the world
     */
    public ExpireCleanComponent(Duration expire) {
        this.expire = expire;
    }

    private TimerAction timerAction;

    @Override
    public void onAdded() {
        entity.activeProperty().addListener((observable, oldValue, isActive) -> {
            if (isActive) {
                timerAction = FXGL.getMasterTimer().runOnceAfter(entity::removeFromWorld, expire);
            } else {
                timerAction.expire();
            }
        });
    }

    @Override
    public void onUpdate(double tpf) {
        if (timerAction == null) {
            timerAction = FXGL.getMasterTimer().runOnceAfter(entity::removeFromWorld, expire);
        } else {

            if (animate) {
                updateOpacity(tpf);
            }
        }
    }

    private double time = 0;

    private void updateOpacity(double tpf) {
        time += tpf;

        getEntity().getView().setOpacity(time >= expire.toSeconds() ? 0 : 1 - time / expire.toSeconds());
    }

    /**
     * Enables diminishing opacity over time.
     *
     * @return this component
     */
    public ExpireCleanComponent animateOpacity() {
        animate = true;
        return this;
    }
}
