package br.com.alexandreluchetti.cinealert.dataprovider.entity;

import br.com.alexandreluchetti.cinealert.core.model.enums.ContentType;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentSnapshotEntity {

    private String imdbId;
    private String title;
    private ContentType type;
    private String posterUrl;
    private Integer year;
}
