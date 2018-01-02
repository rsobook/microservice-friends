package si.fri.rsobook.service;

import com.kumuluz.ee.discovery.annotations.DiscoverService;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;
import si.fri.rsobook.config.FriendsConfigProperties;
import si.fri.rsobook.core.api.ApiConfiguration;
import si.fri.rsobook.core.api.ApiCore;
import si.fri.rsobook.core.api.client.utility.QueryParamBuilder;
import si.fri.rsobook.core.api.data.response.PagingResponse;
import si.fri.rsobook.core.api.resource.base.CrudApiResource;
import si.fri.rsobook.core.database.dto.Paging;
import si.fri.rsobook.core.database.exceptions.BusinessLogicTransactionException;
import si.fri.rsobook.core.model.User;
import si.fri.rsobook.core.model.UserFriends;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.InternalServerErrorException;
import java.net.URL;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequestScoped
public class FriendsBean {

    @Inject
    private FriendsBean friendsBean;

    @Inject
    private FriendsConfigProperties friendsConfigProperties;

    @Inject
    private DatabaseService databaseService;

    @Inject
    @DiscoverService(value = "ms-user", version = "2.0.x", environment = "dev")
    private URL url;

    public List<User> getFriends(UUID id) {
        List<UUID> ids = getFriendsUUIDs(id);
        return getResolvedList(ids);
    }

    public List<UUID> getFriendsUUIDs(UUID id) {

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
            throw new InternalServerErrorException("Error", e);
        }
    }

    @CircuitBreaker(requestVolumeThreshold = 1)
    @Fallback(fallbackMethod = "getResolvedListFallback")
    @Timeout(value = 8, unit = ChronoUnit.SECONDS)
    public List<User> getResolvedList(List<UUID> ids) {

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
            } else {
                throw new InternalServerErrorException("User service is not responding.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerErrorException("Error processing users.", e);
        }
    }

    public List<User> getResolvedListFallback(List<UUID> ids) {

        User user = new User();

        user.setName("Chuck");
        user.setSurname("Norris");
        user.setEmail("chuck@norris.com");

        List<User> userList = new ArrayList<>();
        userList.add(user);

        return userList;
    }

}
