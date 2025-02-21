package smbsync;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.NullChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.filters.AcceptAllFileListFilter;
import org.springframework.integration.smb.dsl.Smb;
import org.springframework.integration.smb.session.SmbSessionFactory;

import java.io.File;

@Configuration
public class SmbToLocalFileTransferConfiguration {

    private final SmbSessionFactory smbSessionFactory;

    public SmbToLocalFileTransferConfiguration(SmbSessionFactory smbSessionFactory) {
        this.smbSessionFactory = smbSessionFactory;
    }

    @Bean
    public IntegrationFlow smbToFileSyncFlow() {
        return IntegrationFlow.from(
                        Smb.inboundAdapter(smbSessionFactory)
                                .remoteDirectory("/remoteImportDirectory")
                                .filter(new AcceptAllFileListFilter<>())
                                .deleteRemoteFiles(true)
                                .localDirectory(new File("myLocalImportDirectory"))
                                .autoCreateLocalDirectory(true)
                                .preserveTimestamp(true),
                        e -> e.poller(Pollers
                                .fixedDelay(10000).maxMessagesPerPoll(-1)
                        )
                )
                .channel(new NullChannel()) // ignore payload
                .get();
    }
}
