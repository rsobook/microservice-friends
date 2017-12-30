package si.fri.rsobook.rest;

import com.kumuluz.ee.logs.cdi.Log;
import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.annotation.Metric;
import si.fri.rsobook.core.api.exception.ApiException;
import si.fri.rsobook.core.database.dto.AuthEntity;
import si.fri.rsobook.core.database.impl.DatabaseImpl;
import si.fri.rsobook.core.model.User;
import si.fri.rsobook.core.model.UserFriends;
import si.fri.rsobook.core.restComponenets.resource.CrudResource;
import si.fri.rsobook.service.DatabaseService;
import si.fri.rsobook.service.FriendsBean;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@RequestScoped
@Path("Friends")
public class FriendsResource extends CrudResource<UUID, UserFriends> {

    @Inject
    private DatabaseService databaseService;

    @Inject
    private FriendsBean friends;

    @Inject
    @Metric(name = "friends_resolved")
    private Counter friendsResolvedCounter;

    public FriendsResource() {
        super(UserFriends.class);
    }

    @Log
    @GET
    @Path("resolve/{id}")
    public Response getUserFriendsResolved(@PathParam("id") UUID id) throws ApiException {

        List<User> resolvedFriends = friends.getFriends(id);
        friendsResolvedCounter.inc(resolvedFriends.size());

        return Response.ok(resolvedFriends).build();
    }

    @Override
    protected UUID parseId(String s) {
        return UUID.fromString(s);
    }

    @Override
    protected AuthEntity getAuthorizedEntity() {
        return null;
    }

    @Override
    protected DatabaseImpl getDatabaseService() {
        return databaseService;
    }

}
