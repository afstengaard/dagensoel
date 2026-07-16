package dk.dagensoel.entities;

import jakarta.persistence.*;
import lombok.*;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Beer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String brewery;
    private String country;
    private double abv;
    private String submittedBy;

    /**
     * Total points for this beer when it comes from a historical/imported
     * result (e.g. the old Microsoft List) rather than from live Vote rows.
     * Null means "no imported result" - live votes are used instead.
     */
    private Integer importedPoints;

    /**
     * A direct URL to a photo of this beer (e.g. hosted on an external
     * image host or in the GitHub repo itself). We deliberately don't
     * store/serve image files on the app server, since Render's free tier
     * has an ephemeral filesystem - anything written to local disk is
     * lost on every redeploy/restart.
     */
    private String imageUrl;

    /**
     * Link to the beer's Untappd page, if known.
     */
    private String untappdLink;

    /**
     * Which evening of a (possibly multi-evening) tasting the beer was
     * served on, e.g. "Første aften" / "Anden aften". Free text rather
     * than an enum since it's just a display label tied to how a given
     * year's event was organized.
     */
    private String evening;

    @ManyToOne(optional = false)
    private Event event;
}
