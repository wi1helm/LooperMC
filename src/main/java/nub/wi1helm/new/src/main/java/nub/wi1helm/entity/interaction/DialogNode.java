package nub.wi1helm.entity.interaction;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.timer.TaskSchedule;

import java.util.function.ToIntFunction;

/**
 * A dialog node that displays text in the action bar and plays a sound.
 * Typically used with autoAdvance=true to create dialog sequences.
 */
public class DialogNode extends InteractionNode {

    private DialogNode(Component text,
                       Sound sound,
                       ToIntFunction<PlayerEntityInteractEvent> priority,
                       boolean autoAdvance,
                       TaskSchedule delay) {
        super(
                event -> {
                    // Show text
                    event.getPlayer().sendActionBar(text);

                    // Play sound if specified
                    if (sound != null) {
                        Pos pos = event.getPlayer().getPosition();
                        event.getPlayer().playSound(
                                sound,
                                pos.x(), pos.y(), pos.z()
                        );
                    }
                },
                priority,
                autoAdvance,
                delay
        );
    }

    // ------------------------------------------------------------------------
    // Builder
    // ------------------------------------------------------------------------

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Component text;
        private Sound sound;
        private SoundEvent type;
        private float volume = 1.0f;
        private float pitch = 1.0f;
        private ToIntFunction<PlayerEntityInteractEvent> priority = InteractionPriority.always(0);
        private boolean autoAdvance = true; // Dialogs typically auto-advance
        private TaskSchedule delay = TaskSchedule.seconds(2); // Default 2 second pause

        public Builder text(Component text) {
            this.text = text;
            return this;
        }

        public Builder text(String text) {
            this.text = Component.text(text);
            return this;
        }

        public Builder sound(Sound sound) {
            this.sound = sound;
            return this;
        }

        public Builder type(SoundEvent type) {
            this.type = type;
            return this;
        }

        public Builder volume(Float volume) {
            this.volume = volume;
            return this;
        }
        public Builder pitch(Float pitch) {
            this.pitch = pitch;
            return this;
        }

        public Builder priority(ToIntFunction<PlayerEntityInteractEvent> priority) {
            this.priority = priority;
            return this;
        }

        public Builder autoAdvance(boolean autoAdvance) {
            this.autoAdvance = autoAdvance;
            return this;
        }

        public Builder delay(TaskSchedule delay) {
            this.delay = delay;
            return this;
        }

        /**
         * Convenience: set delay in seconds
         */
        public Builder delaySeconds(long seconds) {
            this.delay = TaskSchedule.seconds(seconds);
            return this;
        }

        /**
         * Convenience: no delay (instant)
         */
        public Builder instant() {
            this.delay = TaskSchedule.immediate();
            return this;
        }

        /**
         * Convenience: manual advancement (requires click)
         */
        public Builder manual() {
            this.autoAdvance = false;
            return this;
        }

        public DialogNode build() {
            if (text == null) {
                throw new IllegalStateException("Dialog text must be set");
            }
            if (sound == null) {
                return new DialogNode(text, Sound.sound().type(type).volume(volume).pitch(pitch).build(), priority, autoAdvance, delay);
            }
            return new DialogNode(text, sound, priority, autoAdvance, delay);
        }
    }
}