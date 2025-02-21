package smbsync;

import jcifs.DialectVersion;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.smb.session.SmbSessionFactory;

@Configuration
public class SessionFactoryConfiguration {

    @Bean
    public SmbSessionFactory smbSessionFactory() {
        final var smbSessionFactory = new SmbSessionFactory();
        smbSessionFactory.setHost("myHost");
        smbSessionFactory.setPort(445);
        smbSessionFactory.setDomain("myDomain");
        smbSessionFactory.setUsername("myUsername");
        smbSessionFactory.setPassword("myPassword");
        smbSessionFactory.setShareAndDir("/myShareAndDir");
        smbSessionFactory.setSmbMinVersion(DialectVersion.SMB210);
        smbSessionFactory.setSmbMaxVersion(DialectVersion.SMB311);
        return smbSessionFactory;
    }
}
