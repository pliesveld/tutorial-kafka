package tutorial;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.info.ProjectInfoAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.shell.boot.ShellRunnerAutoConfiguration;
import org.springframework.shell.boot.StandardCommandsAutoConfiguration;
import org.springframework.shell.boot.TerminalUIAutoConfiguration;
import org.springframework.shell.standard.ShellComponent;
import tutorial.config.BrokerConfig;

@Log4j2
//@SpringBootApplication
@Configuration
@Import({ // explicitly specify AutoConfiguration to speed up load time
        org.springframework.shell.boot.SpringShellAutoConfiguration.class,
        org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration.class,
        org.springframework.shell.boot.ExitCodeAutoConfiguration.class,
        org.springframework.shell.boot.ThemingAutoConfiguration.class,
        org.springframework.boot.autoconfigure.ssl.SslAutoConfiguration.class,
        org.springframework.boot.autoconfigure.context.LifecycleAutoConfiguration.class,
        org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration.class,
        org.springframework.shell.boot.JLineShellAutoConfiguration.class,
        org.springframework.shell.boot.ShellContextAutoConfiguration.class,
        org.springframework.shell.boot.CompleterAutoConfiguration.class,
        org.springframework.shell.boot.ShellRunnerAutoConfiguration.class,
        org.springframework.shell.boot.ApplicationRunnerAutoConfiguration.class,
        org.springframework.shell.boot.LineReaderAutoConfiguration.class,
        org.springframework.shell.boot.CommandCatalogAutoConfiguration.class,
        org.springframework.shell.boot.ParameterResolverAutoConfiguration.class,
        org.springframework.shell.boot.UserConfigAutoConfiguration.class,
        org.springframework.boot.autoconfigure.availability.ApplicationAvailabilityAutoConfiguration.class,
        org.springframework.shell.boot.JLineAutoConfiguration.class,
        org.springframework.shell.boot.StandardAPIAutoConfiguration.class,
        org.springframework.boot.autoconfigure.info.ProjectInfoAutoConfiguration.class,
        TerminalUIAutoConfiguration.class,
        TaskSchedulingAutoConfiguration.class,
        TaskExecutionAutoConfiguration.class,
        StandardCommandsAutoConfiguration.class,
        ShellRunnerAutoConfiguration.class,
        ProjectInfoAutoConfiguration.class
})
@ComponentScan
@ShellComponent
public class Application {

    @Autowired
    private BrokerConfig brokerConfig;


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
