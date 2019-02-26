package com.dyescape.dyescapebot.unit.verticle.deployment;

import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

import com.dyescape.dyescapebot.constant.Config;
import com.dyescape.dyescapebot.core.connectivity.BotConnection;
import com.dyescape.dyescapebot.verticle.BotGatewayConnectorVerticle;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.stubbing.Answer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

@ExtendWith(VertxExtension.class)
@DisplayName("When deploying the BotGatewayConnectorVerticle with an unexpected connection error")
public class WhenDeployingTheBotGatewayConnectorVerticleWithAnUnexpectedConnectionErrorTest {

    private static boolean deploymentSucceeded;

    @BeforeAll
    public static void setup(Vertx vertx, VertxTestContext context) {

        // Mock the configuration values
        JsonObject config = new JsonObject();
        config.put(Config.API_TOKEN, "i-am-a-valid-token-i-swear");

        // Mock the bot connection
        BotConnection connectionMock = mock(BotConnection.class);
        AsyncResult<Void> mockResult = Future.failedFuture(new RuntimeException("hello"));
        doAnswer((Answer<AsyncResult<Void>>) arg0 -> {
            ((Handler<AsyncResult<Void>>) arg0.getArguments()[1]).handle(mockResult);
            return null;
        }).when(connectionMock).connect(any(), any());

        // Deploy the Verticle
        BotGatewayConnectorVerticle verticle = new BotGatewayConnectorVerticle(connectionMock);
        vertx.deployVerticle(verticle, new DeploymentOptions().setConfig(config), handler -> {

            deploymentSucceeded = handler.succeeded();
            context.completeNow();
        });
    }

    @Test
    @DisplayName("It should fail the deployment")
    public void itShouldFailTheDeployment() {
        assertFalse(deploymentSucceeded);
    }
}
