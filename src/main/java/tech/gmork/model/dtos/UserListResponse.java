package tech.gmork.model.dtos;

import io.quarkus.panache.common.Page;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import lombok.Data;
import org.eclipse.microprofile.config.ConfigProvider;
import tech.gmork.model.entities.User;

import java.util.List;

@Data
public class UserListResponse {

    private static final int pageSize = ConfigProvider.getConfig().getValue("entities.default.page.size", int.class);

    private List<User> users;
    private int totalPages;

    public static UserListResponse byPageNumber(int pageNo) {
        var res = new UserListResponse();
        var query = User.findAll();
        query.page(Page.ofSize(pageSize));
        res.setTotalPages(query.pageCount());
        if (res.getTotalPages() < pageNo || pageNo < 1) {
            throw new WebApplicationException("The requested page does not exist.", Response.Status.BAD_REQUEST);
        }
        res.setUsers(query.page(pageNo, pageSize).list());
        return res;
    }

}
