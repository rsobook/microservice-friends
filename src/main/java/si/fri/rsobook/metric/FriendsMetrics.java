package si.fri.rsobook.metric;

import com.codahale.metrics.Counter;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FriendsMetrics {

    /*@Inject
    @Metric(name = "friends_returned")*/
    private Counter friendsReturned;


    public void addFriendsReturned(int count){
        //friendsReturned.inc(count);
    }

    public boolean isHealthy(){
        return true;
    }

}
