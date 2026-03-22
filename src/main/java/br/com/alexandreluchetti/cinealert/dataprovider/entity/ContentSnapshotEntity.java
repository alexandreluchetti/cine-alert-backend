package br.com.alexandreluchetti.cinealert.dataprovider.entity;

import br.com.alexandreluchetti.cinealert.core.model.content.ContentSnapshot;
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

    public static ContentSnapshotEntity fromModel(ContentSnapshot contentSnapshot) {
        return new ContentSnapshotEntity(
                contentSnapshot.getImdbId(),
                contentSnapshot.getTitle(),
                contentSnapshot.getType(),
                contentSnapshot.getPosterUrl(),
                contentSnapshot.getYear()
        );
    }

    public ContentSnapshot toModel() {
        return new ContentSnapshot(imdbId, title, type, posterUrl, year);
    }
}
