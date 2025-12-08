package it.greentrails.backend.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
@Getter
@Setter
public class ArchiviazioneProperties {

  private String location = "/var/uploads";

}
