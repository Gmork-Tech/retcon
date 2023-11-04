package tech.gmork.model.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.runtime.PanacheQueryImpl;
import io.quarkus.panache.common.Page;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import lombok.Data;
import org.eclipse.microprofile.config.ConfigProvider;
import tech.gmork.model.entities.Application;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicationListResponse {

    private static final int pageSize = ConfigProvider.getConfig().getValue("application.page.size", int.class);

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
        res.setApplications(query.page(pageNo, pageSize).list());
        return res;
    }
}
