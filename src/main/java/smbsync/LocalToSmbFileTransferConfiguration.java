package smbsync;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.integration.handler.advice.ExpressionEvaluatingRequestHandlerAdvice;
import org.springframework.integration.smb.dsl.Smb;
import org.springframework.integration.smb.session.SmbSessionFactory;

import java.io.File;

@Configuration
public class LocalToSmbFileTransferConfiguration {

    private final SmbSessionFactory smbSessionFactory;

    public LocalToSmbFileTransferConfiguration(SmbSessionFactory smbSessionFactory) {
        this.smbSessionFactory = smbSessionFactory;
    }

    @Bean
    public IntegrationFlow fileToSmbSyncFlow() {
        return IntegrationFlow.from(
                        Files.inboundAdapter(new File("myLocalExportDirectory"))
                                .preventDuplicates(false),
                        e -> e.poller(Pollers.fixedDelay(1000).maxMessagesPerPoll(-1))
                )
                .transform(Files.toStringTransformer())
                .handle(Smb.outboundAdapter(smbSessionFactory, FileExistsMode.REPLACE)
                                .useTemporaryFileName(true)
                                .remoteDirectory("/remoteExportDirectory"),
                        e -> e.advice(originalFileDeletionAdvice())
                )
                .get();
    }

    @Bean
    public ExpressionEvaluatingRequestHandlerAdvice originalFileDeletionAdvice() {
        final var advice = new ExpressionEvaluatingRequestHandlerAdvice();
        advice.setOnSuccessExpressionString("headers['" + FileHeaders.ORIGINAL_FILE + "'].delete()");
        advice.setOnFailureExpressionString("headers['" + FileHeaders.ORIGINAL_FILE + "'].delete()");
        return advice;
    }
}
