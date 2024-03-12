package top.cmarco.safechat.api.events;

import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import top.cmarco.safechat.api.checks.ChatCheck;
import top.cmarco.safechat.api.checks.Check;

import java.util.Objects;

/**
 * An abstract event for chat checks.
 * This event is called on the minecraft server main thread.
 */
public abstract class ChatCheckEvent extends Event {

    private final Check check;

    /**
     * This constructor is used to explicitly declare an event as synchronous
     * or asynchronous.
     */
    public ChatCheckEvent(@NotNull Check check, boolean async) {
        super(async);
        this.check = Objects.requireNonNull(check);
    }

    /**
     * Return the {@link ChatCheck} used in the event.
     *
     * @return The chat check.
     */
    @NotNull
    public final Check getCheck() {
        return check;
    }
}
