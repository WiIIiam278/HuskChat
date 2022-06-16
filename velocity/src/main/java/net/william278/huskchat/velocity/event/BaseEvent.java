package net.william278.huskchat.velocity.event;

import com.velocitypowered.api.event.ResultedEvent;
import net.william278.huskchat.event.EventBase;

import java.util.Objects;

public class BaseEvent implements ResultedEvent<ResultedEvent.GenericResult>, EventBase {
    private ResultedEvent.GenericResult result = ResultedEvent.GenericResult.allowed();

    @Override
    public GenericResult getResult() {
        return result;
    }

    @Override
    public void setResult(GenericResult result) {
        this.result = Objects.requireNonNull(result);
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.result = cancelled ? GenericResult.denied() : GenericResult.allowed();
    }

    @Override
    public boolean isCancelled() {
        return !result.isAllowed();
    }
}
