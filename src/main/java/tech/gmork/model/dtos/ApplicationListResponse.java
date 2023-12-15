package tech.gmork.model.dtos;

import io.quarkus.logging.Log;
import io.quarkus.panache.common.Page;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import lombok.Data;
import org.eclipse.microprofile.config.ConfigProvider;
import tech.gmork.model.entities.Application;

import java.util.List;

@Data
public class ApplicationListResponse {

    private static final int pageSize = ConfigProvider.getConfig().getValue("entities.default.page.size", int.class);

    private List<Application> applications;
    private int totalPages;

    public static ApplicationListResponse byPageNumber(int pageNo) {
        var res = new ApplicationListResponse();
        var query = Application.findAll();
        query.page(Page.ofSize(pageSize));
        res.setTotalPages(query.pageCount());
        if (res.getTotalPages() < pageNo || pageNo < 1) {
            throw new WebApplicationException("The requested page does not exist.", Response.Status.BAD_REQUEST);
        }
        res.setApplications(query.page(pageNo - 1, pageSize).list());
        return res;
    }
}
