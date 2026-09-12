package io.github.cogitowl.chatimage;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
public final class SmokeClient implements ClientModInitializer {
    public void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STARTED.register(SmokeTest::start);
    }
}
