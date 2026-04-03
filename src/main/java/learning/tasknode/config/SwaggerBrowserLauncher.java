package learning.tasknode.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.awt.Desktop;
import java.net.URI;

@Component
@Slf4j
public class SwaggerBrowserLauncher {

    @Value("${server.port:8080}")
    private int serverPort;

    @Value("${springdoc.swagger-ui.path:/swagger-ui.html}")
    private String swaggerPath;

    @EventListener(ApplicationReadyEvent.class)
    public void openSwaggerUI() {
        String url = "http://localhost:" + serverPort + swaggerPath;
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
                log.info("==> Swagger UI opened at: {}", url);
            } else {
                // Fallback for systems where Desktop is not supported
                String os = System.getProperty("os.name").toLowerCase();
                if (os.contains("win")) {
                    Runtime.getRuntime().exec(new String[]{"rundll32", "url.dll,FileProtocolHandler", url});
                } else if (os.contains("mac")) {
                    Runtime.getRuntime().exec(new String[]{"open", url});
                } else if (os.contains("nux") || os.contains("nix")) {
                    Runtime.getRuntime().exec(new String[]{"xdg-open", url});
                }
                log.info("==> Swagger UI opened at: {}", url);
            }
        } catch (Exception e) {
            log.warn("==> Could not open browser automatically. Access Swagger UI at: {}", url);
        }
    }
}
