package si.fri.rsobook.rest;

import com.kumuluz.ee.discovery.annotations.DiscoverService;
import si.fri.rsobook.config.FriendsConfigProperties;
import si.fri.rsobook.core.api.ApiConfiguration;
import si.fri.rsobook.core.api.ApiCore;
import si.fri.rsobook.core.api.client.utility.QueryParamBuilder;
import si.fri.rsobook.core.api.data.response.PagingResponse;
import si.fri.rsobook.core.api.exception.ApiException;
import si.fri.rsobook.core.api.resource.base.CrudApiResource;
import si.fri.rsobook.core.database.dto.AuthEntity;
import si.fri.rsobook.core.database.dto.Paging;
import si.fri.rsobook.core.database.exceptions.BusinessLogicTransactionException;
import si.fri.rsobook.core.database.impl.DatabaseImpl;
import si.fri.rsobook.core.model.User;
import si.fri.rsobook.core.model.UserFriends;
import si.fri.rsobook.core.restComponenets.resource.CrudResource;
import si.fri.rsobook.service.DatabaseService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequestScoped
@Path("Friends")
public class FriendsResource extends CrudResource<UUID, UserFriends> {

    @Inject
    @DiscoverService(value = "ms-user", version = "2.0.x", environment = "dev")
    private URL url;

    @Inject
    private FriendsConfigProperties friendsConfigProperties;

    @Inject
    private DatabaseService databaseService;

    public FriendsResource() {
        super(UserFriends.class);
    }

    @GET
    @Path("resolve/{id}")
    public Response getUserFriendsResolved(@PathParam("id") UUID id) throws ApiException {

        List<UUID> ids = getFriendsUUIDs(id);
        List<User> resolvedFriends = getResolvedList(ids);

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

    private List<UUID> getFriendsUUIDs(UUID id) throws ApiException {

        final UUID qId = id;
        try {
            Paging<UserFriends> friendsList = databaseService.getList(UserFriends.class, (p, cb, r) -> cb.equal(r.get("userId"), qId));

            List<UUID> ids = new ArrayList<>();
            for(UserFriends uf : friendsList.getItems()) {
                ids.add(uf.getFriendsId());
            }

            return ids;
        } catch (BusinessLogicTransactionException e) {
            e.printStackTrace();
            throw new ApiException("Error", e);
        }
    }

    private List<User> getResolvedList(List<UUID> ids) throws ApiException {

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(UUID id : ids){
            sb.append(id.toString());
            sb.append(",");
        }
        sb.append("]");

        QueryParamBuilder queryParamBuilder = new QueryParamBuilder();
        queryParamBuilder.addCond("id:in:" + sb.toString());

        String host = friendsConfigProperties.getUserApiHost();
        if(friendsConfigProperties.getUserApiHostDiscovery() && url != null) {
            host = url.toString();
        }

        ApiConfiguration config = new ApiConfiguration(String.format(
                "%s/api/v1", host));

        ApiCore apiCore = new ApiCore(config, null);
        CrudApiResource<User> resource = new CrudApiResource<>(apiCore, User.class);

        try {
            String query = queryParamBuilder.buildQuery();
            PagingResponse<User> response = resource.get(query);
            if(response.isStatusValid()) {
                return response.getItems();
            }
        } catch (ApiException e) {
            e.printStackTrace();
        }
        throw new ApiException("Error processing users.");
    }
}
